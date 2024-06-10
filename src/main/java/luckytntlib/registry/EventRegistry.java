package luckytntlib.registry;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.network.UpdateConfigValuesS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventRegistry {
	
	private static final Join PLAYER_JOIN = new Join() {
		
		@Override
		public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
			for(ServerWorld world : server.getWorlds()) {
				for(ServerPlayerEntity entity : world.getPlayers()) {
					LuckyTNTLib.RH.sendS2CPacket(entity, new UpdateConfigValuesS2CPacket(LuckyTNTLibConfigValues.CONFIG.getConfigValues()));
				}
			}
		}
	};
	
	public static void init() {
		ServerPlayConnectionEvents.JOIN.register(PLAYER_JOIN);
	}
}
