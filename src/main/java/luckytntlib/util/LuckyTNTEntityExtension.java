package luckytntlib.util;

import net.minecraft.nbt.NbtCompound;

public interface LuckyTNTEntityExtension {
	NbtCompound getAdditionalPersistentData();
	
	void setAdditionalPersistentData(NbtCompound nbt);
}
