package luckytntlib.config.common;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ServerConfig extends Config {

	ServerConfig(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		super(modid, configValues, packetCreator);
	}

	public void init() {
		Path path = FabricLoader.getInstance().getConfigDir();
		File file = new File(path.toString() + "\\" + modid + "-server-config.json");

		LuckyTNTLib.LOGGER.info("Init server config for " + modid + " from file " + file.toString());
		
		if(!file.exists()) {
			createConfigFile(file);
		} else {
			loadConfigValues(file);
		}
	}
	
	public void save(World world) {
		if(world instanceof ServerWorld sworld) {
			Path path = FabricLoader.getInstance().getConfigDir();
			File file = new File(path.toString() + "\\" + modid + "-server-config.json");
	
			LuckyTNTLib.LOGGER.info("Saving server config for " + modid + " to file " + file.toString());
			
			if(!file.exists()) {
				createConfigFile(file);
			} else {
				file.delete();
				createConfigFile(file);
			}
			
			if(!packetCreator.isEmpty()) {
				for(ServerWorld sw : sworld.getServer().getWorlds()) {
					for(ServerPlayerEntity player : sw.getPlayers()) {
						ServerPlayNetworking.send(player, packetCreator.get().getPacket(configValues).getName(), packetCreator.get().getPacket(configValues).toByteBuf());
					}
				}
			}
		}
	}
}
