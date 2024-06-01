package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 
 * A PrimedLTNT is an extension of Minecraft's {@link TntEntity}
 * and uses a {@link PrimedTNTEffect} to easily customize the explosion effect and other parameters.
 * It implements {@link IExplosiveEntity}.
 */
public class PrimedLTNT extends TntEntity implements IExplosiveEntity{

	@Nullable
	private LivingEntity igniter;
	private PrimedTNTEffect effect;
	
	public PrimedLTNT(EntityType<PrimedLTNT> type, World level, PrimedTNTEffect effect) {
		super(type, level);
		this.effect = effect;
	    double movement = level.random.nextDouble() * (double)(Math.PI * 2F);
	    this.setVelocity(-Math.sin(movement) * 0.02D, 0.2F, -Math.cos(movement) * 0.02D);
	    this.setTNTFuse(effect.getDefaultFuse(this));
	}
	
	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.MASTER;
	}
	
	public void setOwner(@Nullable LivingEntity igniter) {
		this.igniter = igniter;
	}
	
	@Override
	@Nullable
	public LivingEntity getOwner() {
		return igniter;
	}
	
	@Override
	public PrimedTNTEffect getEffect() {
		return effect;
	}
	
	@Override
	public void tick() {
		if (!hasNoGravity()) {
			setVelocity(getVelocity().add(0.0D, -0.04D, 0.0D));
			updateWaterState();
		}
		move(MovementType.SELF, getVelocity());
		setVelocity(getVelocity().multiply(0.98D));
		if (isOnGround()) {
			setVelocity(getVelocity().multiply(0.7D, -0.5D, 0.7D));
		}
		effect.baseTick(this);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		if(igniter != null) {
			tag.putInt("igniterID", igniter.getId());
		}
		super.writeCustomDataToNbt(tag);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		if(getWorld().getEntityById(tag.getInt("igniterID")) instanceof LivingEntity lEnt) {
			igniter = lEnt;
		}
		super.readCustomDataFromNbt(tag);
	}
	
	@Override
	public void setTNTFuse(int fuse) {
		setFuse(fuse);
	}
	
	@Override
	public int getTNTFuse() {
		return getFuse();
	}

	@Override
	public Vec3d getPos() {
		return getLerpedPos(1);
	}

	@Override
	public void destroy() {
		discard();
	}
	
	@Override
	public World getLevel() {
		return getWorld();
	}

	@Override
	public double x() {
		return getX();
	}

	@Override
	public double y() {
		return getY();
	}

	@Override
	public double z() {
		return getZ();
	}
	
	@Override
	public LivingEntity owner() {
		return getOwner();
	}
}
