package luckytntlib.registry;

import java.util.function.Supplier;

import luckytntlib.LuckyTNTLib;
import luckytntlib.item.TNTConfigItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry {
	
	public static final Supplier<Item> CONFIG_ITEM = registerItem("tnt_config", new TNTConfigItem());

	public static Supplier<Item> registerItem(String name, Item item) {
		Item ritem = Registry.register(Registries.ITEM, new Identifier(LuckyTNTLib.MODID, name), item);
		return () -> ritem;
	}
	
	public static void init() {}
}
