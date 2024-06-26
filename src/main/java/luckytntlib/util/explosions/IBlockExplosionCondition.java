package luckytntlib.util.explosions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An IBlockExplosionCondition is used by different explosions to check whether a block is suited for further actions or not.
 * Further actions include blocks getting added to a List or passed to an {@link IForEachBlockExplosionEffect}.
 * It is usually used as a parameter of a function.
 */
@FunctionalInterface
public interface IBlockExplosionCondition {

	/**
	 * Decides which blocks are considered to be passed to an {@link IForEachBlockExplosionEffect} or not.
	 * @param level  the current level
	 * @param pos  the position of the block
	 * @param state  the state of the block
	 * @param distance  the distance of the block to the explosion origin
	 */
	public boolean conditionMet(World level, BlockPos pos, BlockState state, double distance);
	
}
