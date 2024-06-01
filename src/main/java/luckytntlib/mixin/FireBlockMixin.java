package luckytntlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
	
	@Shadow
	protected abstract BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age);
	
	@Shadow
	protected abstract int getSpreadChance(BlockState state);
	
	@Inject(method = "trySpreadingFire", at = @At(value = "HEAD"), cancellable = true)
	private void redirectPrimeTnt(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci) {
		BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        
        if (!(block instanceof TntBlock)) {
        	return;
        }
        
		int i = getSpreadChance(world.getBlockState(pos));
        if (random.nextInt(spreadFactor) < i) {
            if (random.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
                int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                world.setBlockState(pos, getStateWithAge(world, pos, j), Block.NOTIFY_ALL);
            } else {
                world.removeBlock(pos, false);
            }
            
            if (block instanceof TntBlock tnt) {
            	Explosion explosion = new Explosion(world, null, null, null, 0, 0, 0, 0, false, DestructionType.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.ENTITY_GENERIC_EXPLODE);
    			tnt.onDestroyedByExplosion(world, pos, explosion);
    			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
        }
        ci.cancel();
	}
}
