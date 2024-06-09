package luckytntlib.config.common;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public class ServerConfig extends Config {

	ServerConfig(String modid, List<ConfigValue<?>> configValues) {
		super(modid, configValues);
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
		Path path = FabricLoader.getInstance().getConfigDir();
		File file = new File(path.toString() + "\\" + modid + "-server-config.json");

		LuckyTNTLib.LOGGER.info("Saving server config for " + modid + " to file " + file.toString());
		
		if(!file.exists()) {
			createConfigFile(file);
		} else {
			file.delete();
			createConfigFile(file);
		}
	}
}
