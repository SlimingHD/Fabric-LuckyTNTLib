package luckytntlib.registry;

import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworkRegistry {

private static final PlayChannelHandler UPDATE_S2C = new PlayChannelHandler() {
		
		@Override
		public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			NbtCompound tag = buf.readNbt();
			
			client.execute(() -> {
				Config.writeToValues(tag, LuckyTNTLibConfigValues.CONFIG.getConfigValues());
			});
		}
	};
	
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(UpdateConfigValuesPacket.NAME, UPDATE_S2C);
	}
}
