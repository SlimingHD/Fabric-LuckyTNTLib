package luckytntlib.registry;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config;
import luckytntlib.network.ClientReadyC2SPacket;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class NetworkRegistry {
	
	private static final PlayChannelHandler UPDATE_C2S = new PlayChannelHandler() {
		
		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			NbtCompound tag = buf.readNbt();
			
			Config.writeToValues(tag, LuckyTNTLibConfigValues.CONFIG.getConfigValues());
			
			LuckyTNTLibConfigValues.CONFIG.save(server.getWorld(World.OVERWORLD));
		}
	};
	private static final PlayChannelHandler READY_C2S = new PlayChannelHandler() {
		
		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			for(ServerWorld world : server.getWorlds()) {
				for(ServerPlayerEntity entity : world.getPlayers()) {
					LuckyTNTLib.RH.sendS2CPacket(entity, new UpdateConfigValuesPacket(LuckyTNTLibConfigValues.CONFIG.getConfigValues()));
				}
			}
		}
	};

	public static void init() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientNetworkRegistry.init();
		}
		
		ServerPlayNetworking.registerGlobalReceiver(UpdateConfigValuesPacket.NAME, UPDATE_C2S);
		ServerPlayNetworking.registerGlobalReceiver(ClientReadyC2SPacket.NAME, READY_C2S);
	}
}
