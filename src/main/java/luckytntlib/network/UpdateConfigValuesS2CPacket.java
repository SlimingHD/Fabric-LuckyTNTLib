package luckytntlib.network;

import java.util.List;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config.BooleanValue;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.config.common.Config.DoubleValue;
import luckytntlib.config.common.Config.EnumValue;
import luckytntlib.config.common.Config.IntValue;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateConfigValuesS2CPacket implements LuckyTNTPacket {
	
	public static Identifier NAME = new Identifier(LuckyTNTLib.MODID, "update_config_values_s2c");
	
	public final List<ConfigValue<?>> configValues;

	public UpdateConfigValuesS2CPacket(List<ConfigValue<?>> configValues) {
		this.configValues = configValues;
	}
	
	@Override
	public PacketByteBuf toByteBuf() {
		NbtCompound tag = new NbtCompound();
		
		for(ConfigValue<?> value : configValues) {
			if(value instanceof IntValue intval) {
				tag.putInt(value.getName(), intval.get().intValue());
			} else if(value instanceof DoubleValue dval) {
				tag.putDouble(value.getName(), dval.get().doubleValue());
			} else if(value instanceof BooleanValue bval) {
				tag.putBoolean(value.getName(), bval.get().booleanValue());
			} else if(value instanceof EnumValue eval) {
				tag.putString(value.getName(), eval.getNameOfValue());
			}
		}
		
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeNbt(tag);
		return buf;
	}

	@Override
	public Identifier getName() {
		return NAME;
	}
}
