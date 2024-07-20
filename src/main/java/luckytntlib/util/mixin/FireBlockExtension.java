package luckytntlib.util.mixin;

import net.minecraft.block.Block;

public interface FireBlockExtension {
	public void registerBurnableBlock(Block block, int burnChance, int spreadChance);
}
