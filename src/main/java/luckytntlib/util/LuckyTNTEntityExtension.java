package luckytntlib.util;

import net.minecraft.nbt.NbtCompound;

public interface LuckyTNTEntityExtension {
	NbtCompound getPersistentData();
	
	void setPersistentData(NbtCompound nbt);
}
