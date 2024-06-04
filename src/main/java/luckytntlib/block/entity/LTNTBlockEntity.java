package luckytntlib.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class LTNTBlockEntity extends BlockEntity {
	
	protected NbtCompound persistentData = new NbtCompound();
	private String PERSISTENT_DATA_TAG = "PersistentData";

	public LTNTBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.put(PERSISTENT_DATA_TAG, persistentData);
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		persistentData = nbt.getCompound(PERSISTENT_DATA_TAG);
	}
	
	public NbtCompound getPresistentData() {
		return persistentData;
	}
}
