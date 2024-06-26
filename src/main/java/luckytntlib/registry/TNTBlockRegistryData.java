package luckytntlib.registry;

import net.minecraft.block.MapColor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * The TNTBlockRegistryData only serves the purpose of bringing together relatively simple and repetitive properties that a TNT block/item may have.
 * It is currently only used by the {@link RegistryHelper} in some registering methods of TNT blocks.
 * A TNTBlockRegistryData can be created by using the Builder subclass.
 */
public class TNTBlockRegistryData {

	private final String registryName;
	private final boolean makeItem;
	private final boolean addDispenseBehavior;
	private final boolean randomizedFuseUponExploded;
	private final boolean addToTNTLists;
	private final MutableText description;
	private final String tab;
	private final MapColor color;
	
	
	private TNTBlockRegistryData(String registryName, boolean makeItem, boolean addDispenseBehavior, boolean randomizedFuseUponExploded, boolean addToTNTLists, MutableText description, String tab, MapColor color) {
		this.registryName = registryName;
		this.makeItem = makeItem;
		this.addDispenseBehavior = addDispenseBehavior;
		this.randomizedFuseUponExploded = randomizedFuseUponExploded;
		this.addToTNTLists = addToTNTLists;
		this.description = description;
		this.tab = tab;
		this.color = color;
	}

	public String getRegistryName() {
		return registryName;
	}

	public boolean makeItem() {
		return makeItem;
	}

	public boolean addDispenseBehavior() {
		return addDispenseBehavior;
	}

	public boolean randomizedFuseUponExploded() {
		return randomizedFuseUponExploded;
	}
	
	public boolean addToTNTLists() {
		return addToTNTLists;
	}
	
	public MutableText getDescription() {
		return description;
	}

	public String getTab() {
		return tab;
	}

	public MapColor getColor() {
		return color;
	}
	
	public static class Builder {
		
		private final String registryName;
		private boolean makeItem = true;
		private boolean addDispenseBehavior = true;
		private boolean randomizedFuseUponExploded = true;
		private boolean addToTNTLists = true;
		private MutableText description = Text.translatable("");
		private String tab = "none";
		private MapColor color = MapColor.RED;
		
		public Builder(String registryName) {
			this.registryName = registryName;
		}

		public Builder makeItem(boolean makeItem) {
			this.makeItem = makeItem;
			return this;
		}

		public Builder addDispenseBehavior(boolean addDispenseBehavior) {
			this.addDispenseBehavior = addDispenseBehavior;
			return this;
		}

		public Builder randomizedFuseUponExploded(boolean randomizedFuseUponExploded) {
			this.randomizedFuseUponExploded = randomizedFuseUponExploded;
			return this;
		}
		
		public Builder addToTNTLists(boolean addToTNTLists) {
			this.addToTNTLists = addToTNTLists;
			return this;
		}
		
		public Builder description(MutableText description) {
			this.description = description;
			return this;
		}

		public Builder tab(String tab) {
			this.tab = tab;
			return this;
		}

		public Builder color(MapColor color) {
			this.color = color;
			return this;
		}
		
		public TNTBlockRegistryData build() {
			return new TNTBlockRegistryData(registryName, makeItem, addDispenseBehavior, randomizedFuseUponExploded, addToTNTLists, description, tab, color);
		}
	}
}
