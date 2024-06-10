package luckytntlib.registry;

import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config.BooleanValue;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.config.common.Config.DoubleValue;
import luckytntlib.config.common.Config.EnumValue;
import luckytntlib.config.common.Config.IntValue;
import luckytntlib.network.UpdateConfigValuesS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworkRegistry {

private static final PlayChannelHandler UPDATE_S2C = new PlayChannelHandler() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			NbtCompound tag = buf.readNbt();
			
			client.execute(() -> {
				System.out.println("packet handling on client");
				
				for(ConfigValue<?> value : LuckyTNTLibConfigValues.CONFIG.getConfigValues()) {
					if(tag.contains(value.getName())) {
						if(value instanceof IntValue intval && tag.get(value.getName()) instanceof NbtInt nbt) {
							intval.set(nbt.intValue());
						} else if(value instanceof DoubleValue dval && tag.get(value.getName()) instanceof NbtDouble nbt) {
							dval.set(nbt.doubleValue());
						} else if(value instanceof BooleanValue bval && tag.get(value.getName()) instanceof NbtByte nbt) {
							bval.set(nbt.byteValue() == 0 ? false : true);
						} else if(value instanceof EnumValue eval && tag.get(value.getName()) instanceof NbtString nbt) {
							eval.set(eval.getValueByName(nbt.asString()));
						}
					}
				}
			});
		}
	};
	
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(UpdateConfigValuesS2CPacket.NAME, UPDATE_S2C);
	}
}
