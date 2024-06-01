package luckytntlib;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.datafixers.util.Pair;

import luckytntlib.block.LTNTBlock;
import luckytntlib.client.ClientAccess;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.item.LTNTMinecartItem;
import luckytntlib.registry.ItemGroupModification;
import luckytntlib.registry.ItemRegistry;
import luckytntlib.registry.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class LuckyTNTLib implements ModInitializer {
    
	public static final String MODID = "luckytntlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final RegistryHelper RH = new RegistryHelper(MODID);
    
    @Override
	public void onInitialize() {
    	ItemRegistry.init();
    	ItemGroupModification.init();
    	
    	if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
    		RH.registerConfigScreenFactory(Text.literal("Lucky TNT Lib"), ClientAccess.getFactory());
    	}
    	
    	LuckyTNTLibConfigValues.registerConfig();
    	
    	changeFlintAndSteelDispenserBehavior();
    	registerTNTBlockDispenserBehavior();
    	registerDynamiteDispenserBehavior();
    	registerMinecartDispenserBehavior();
    }
    
	private void changeFlintAndSteelDispenserBehavior() {
		DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior() {

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				ServerWorld world = pointer.world();
				setSuccess(true);
				Direction direction = pointer.state().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.pos().offset(direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (AbstractFireBlock.canPlaceAt(world, blockPos, direction)) {
					world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
					world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
				} else if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
					world.setBlockState(blockPos, (BlockState) blockState.with(Properties.LIT, true));
					world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
				} else if (blockState.getBlock() instanceof TntBlock tnt) {
					tnt.onDestroyedByExplosion(world, blockPos, new Explosion(world, null, null, null, 0, 0, 0, 0, false, DestructionType.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.ENTITY_GENERIC_EXPLODE));
					world.removeBlock(blockPos, false);
				} else {
					setSuccess(false);
				}
				if (isSuccess() && stack.damage(1, world.random, null)) {
					stack.setCount(0);
				}
				return stack;
			}
		});
	}
    
    private void registerTNTBlockDispenserBehavior() {
    	for(Pair<Supplier<LTNTBlock>, Supplier<Item>> pair : RegistryHelper.TNT_DISPENSER_REGISTRY_LIST) {
    		LTNTBlock block = pair.getFirst().get();
    		Item item = pair.getSecond().get();
			DispenserBehavior behaviour = new DispenserBehavior() {
				
				@Override
				public ItemStack dispense(BlockPointer source, ItemStack stack) {
					World level = source.world();
					Position p = DispenserBlock.getOutputLocation(source);
					BlockPos pos = new BlockPos(MathHelper.floor(p.getX()), MathHelper.floor(p.getY()), MathHelper.floor(p.getZ()));
					block.explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
					stack.decrement(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
    	}
    }
    
    private void registerDynamiteDispenserBehavior() {
    	for(Supplier<LDynamiteItem> dynamite : RegistryHelper.DYNAMITE_DISPENSER_REGISTRY_LIST) {
    		LDynamiteItem item = dynamite.get();
    		DispenserBehavior behaviour = new DispenserBehavior() {
				
				@Override
				public ItemStack dispense(BlockPointer source, ItemStack stack) {
					World level = source.world();
					Vec3d dispenserPos = new Vec3d(source.pos().getX() + 0.5f, source.pos().getY() + 0.5f, source.pos().getZ() + 0.5f);
					Position pos = DispenserBlock.getOutputLocation(source);
					item.shoot(level, pos.getX(), pos.getY(), pos.getZ(), new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(-dispenserPos.getX(), -dispenserPos.getY(), -dispenserPos.getZ()), 2, null);
					stack.decrement(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
    	}
    }
    
    private void registerMinecartDispenserBehavior() {
    	for(Supplier<LTNTMinecartItem> minecart : RegistryHelper.MINECART_DISPENSER_REGISTRY_LIST) {
    		LTNTMinecartItem item = minecart.get();
			DispenserBehavior behaviour = new DispenserBehavior() {
				
				@Override
				public ItemStack dispense(BlockPointer source, ItemStack stack) {
					Direction direction = source.state().get(DispenserBlock.FACING);
					World level = source.world();
					double x = source.centerPos().getX() + (double) direction.getOffsetX() * 1.125D;
					double y = Math.floor(source.centerPos().getY()) + (double) direction.getOffsetY();
					double z = source.centerPos().getZ() + (double) direction.getOffsetZ() * 1.125D;
					BlockPos pos = source.pos().offset(direction);
					BlockState state = level.getBlockState(pos);
					RailShape rail = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
					double railHeight;
					if (state.isIn(BlockTags.RAILS)) {
						if (rail.isAscending()) {
							railHeight = 0.6D;
						} else {
							railHeight = 0.1D;
						}
					} else {
						if (!state.isAir() || !level.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
							return new ItemDispenserBehavior().dispense(source, stack);
						}

						BlockState stateDown = level.getBlockState(pos.down());
						RailShape railDown = stateDown.getBlock() instanceof AbstractRailBlock ? stateDown.get(((AbstractRailBlock)stateDown.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
						if (direction != Direction.DOWN && railDown.isAscending()) {
							railHeight = -0.4D;
						} else {
							railHeight = -0.9D;
						}
					}

					LTNTMinecart cart = item.createMinecart(level, x, y + railHeight, z, null);
					if (stack.hasCustomName()) {
						cart.setCustomName(stack.getName());
					}
					stack.decrement(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
    	}
    }
}
