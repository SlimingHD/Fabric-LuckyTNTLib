package luckytntlib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityMixin implements LuckyTNTEntityExtension {

	@Shadow
	@Final
	protected DataTracker dataTracker;
	
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void injectionConstructor(EntityType<?> type, World world, CallbackInfo info) {
		dataTracker.startTracking(PERSISTENT_DATA, new NbtCompound());
	}
	
	@Inject(method = "readNbt", at = @At("HEAD"))
	private void injectionReadNbt(NbtCompound tag, CallbackInfo info) {
		setAdditionalPersistentData(tag.getCompound("AdditionalData"));
	}
	
	@Inject(method = "writeNbt", at = @At("HEAD"))
	private void injectionWriteNbt(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
		tag.put("AdditionalData", getAdditionalPersistentData());
	}

	@Unique
	public NbtCompound getAdditionalPersistentData() {
		return dataTracker.get(PERSISTENT_DATA);
	}

	@Unique
	public void setAdditionalPersistentData(NbtCompound nbt) {
		dataTracker.set(PERSISTENT_DATA, nbt, true);
	}
}
