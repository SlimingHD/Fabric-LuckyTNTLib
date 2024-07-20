package luckytntlib.util.dispenser;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.item.LTNTMinecartItem;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * Used to register default {@link DispenserBehavior}s for {@link LTNTBlock}s, {@link LDynamiteItem}s and {@link LTNTMinecartItem}s
 */
public class DispenserBehaviorHelper {
	
	public static void registerTNTBlockDispenserBehavior(Supplier<LTNTBlock> tnt) {
		LTNTBlock block = tnt.get();
		
		DispenserBehavior behaviour = new DispenserBehavior() {

			@Override
			public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				block.explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
				world.emitGameEvent(null, GameEvent.ENTITY_PLACE, pos);
				stack.decrement(1);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(block, behaviour);
	}

	public static void registerDynamiteDispenserBehavior(Supplier<LDynamiteItem> dynamite) {
    		LDynamiteItem item = dynamite.get();
    		
			DispenserBehavior behaviour = new DispenserBehavior() {

				@Override
				public ItemStack dispense(BlockPointer source, ItemStack stack) {
					World level = source.getWorld();
					Vec3d dispenserPos = new Vec3d(source.getPos().getX() + 0.5f, source.getPos().getY() + 0.5f, source.getPos().getZ() + 0.5f);
					Position pos = DispenserBlock.getOutputLocation(source);
					item.shoot(level, pos.getX(), pos.getY(), pos.getZ(), new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(-dispenserPos.getX(), -dispenserPos.getY(), -dispenserPos.getZ()), 2, null);
					stack.decrement(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
	}
	
	public static void registerMinecartDispenserBehavior(Supplier<LTNTMinecartItem> minecart) {
		LTNTMinecartItem item = minecart.get();
		
		DispenserBehavior behaviour = new DispenserBehavior() {
			
			@Override
			public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				World world = pointer.getWorld();
				double d = pointer.getX() + (double) direction.getOffsetX() * 1.125;
				double e = Math.floor(pointer.getY()) + (double) direction.getOffsetY();
				double f = pointer.getZ() + (double) direction.getOffsetZ() * 1.125;
				BlockPos blockPos = pointer.getPos().offset(direction);
				BlockState blockState = world.getBlockState(blockPos);
				RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
				double g;
				if (blockState.isIn(BlockTags.RAILS)) {
					if (railShape.isAscending()) {
						g = 0.6;
					} else {
						g = 0.1;
					}
				} else {
					if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS)) {
						return dispense(pointer, stack);
					}

					BlockState blockState2 = world.getBlockState(blockPos.down());
					RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock
							? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty())
							: RailShape.NORTH_SOUTH;
					if (direction != Direction.DOWN && railShape2.isAscending()) {
						g = -0.4;
					} else {
						g = -0.9;
					}
				}

				LTNTMinecart cart = item.createMinecart(world, d, e + g, f, null);
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
