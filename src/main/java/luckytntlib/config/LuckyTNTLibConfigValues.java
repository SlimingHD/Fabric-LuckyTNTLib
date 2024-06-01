package luckytntlib.config;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config;

public class LuckyTNTLibConfigValues {
	
	public static Config CONFIG;
	
	public static Config.BooleanValue PERFORMANT_EXPLOSION = new Config.BooleanValue(true, "performantExplosion");
	public static Config.DoubleValue EXPLOSION_PERFORMANCE_FACTOR = new Config.DoubleValue(0.3d, 0.3d, 0.6d, "explosionPerformanceFactor");
	
	public static void registerConfig() {
		CONFIG = Config.Builder.of(LuckyTNTLib.MODID).addConfigValue(PERFORMANT_EXPLOSION).addConfigValue(EXPLOSION_PERFORMANCE_FACTOR).build();
		CONFIG.init();
	}
}
