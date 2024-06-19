package luckytntlib.network;

import java.util.List;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateConfigValuesPacket implements LuckyTNTPacket {
	
	public static Identifier NAME = new Identifier(LuckyTNTLib.MODID, "update_config_values");
	
	public final List<ConfigValue<?>> configValues;

	public UpdateConfigValuesPacket(List<ConfigValue<?>> configValues) {
		this.configValues = configValues;
	}
	
	@Override
	public PacketByteBuf toByteBuf() {
		return Config.valuesToPacketByteBuf(configValues);
	}

	@Override
	public Identifier getName() {
		return NAME;
	}
}
