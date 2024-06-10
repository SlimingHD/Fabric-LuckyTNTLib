package luckytntlib.config.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import luckytntlib.network.LuckyTNTPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Config {
	
	protected final String modid;
	protected final List<ConfigValue<?>> configValues;
	protected final Optional<UpdatePacketCreator> packetCreator;
	
	protected Config(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		this.modid = modid;
		this.configValues = configValues;
		this.packetCreator = packetCreator;
	}
	
	public abstract void init();
	
	public abstract void save(World world);
	
	protected void createConfigFile(File file) {
		try {
			file.createNewFile();
			
			FileWriter writer = new FileWriter(file);
			JsonWriter json = new JsonWriter(writer);
			
			json.setIndent("\t");
			json.beginObject();
			
			for(ConfigValue<?> value : configValues) {
				if(value instanceof IntValue intval) {
					json.name(value.getName()).value(intval.get());
				} else if(value instanceof DoubleValue dval) {
					json.name(value.getName()).value(dval.get());
				} else if(value instanceof BooleanValue bval) {
					json.name(value.getName()).value(bval.get());
				} else if(value instanceof EnumValue eval) {
					json.name(value.getName()).value(eval.getNameOfValue());
				}
			}
			
			json.endObject();
			
			json.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void loadConfigValues(File file) {
		try {
			FileReader reader = new FileReader(file);
			JsonReader json = new JsonReader(reader);
			
			json.beginObject();
			
			while(json.hasNext()) {
				String name = json.nextName();
				
				for(ConfigValue<?> value : configValues) {
					if(value.getName().equals(name)) {
						if(value instanceof IntValue intval) {
							intval.set(json.nextInt());
						} else if(value instanceof DoubleValue dval) {
							dval.set(json.nextDouble());
						} else if(value instanceof BooleanValue bval) {
							bval.set(json.nextBoolean());
						} else if(value instanceof EnumValue eval) {
							eval.set(eval.getValueByName(json.nextString()));
						}
					}
				}
			}
			
			json.endObject();
			
			json.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<ConfigValue<?>> getConfigValues() {
		return configValues;
	}

	public static class Builder {
		
		private String id = "";
		private List<ConfigValue<?>> values = new ArrayList<>();
		private Optional<UpdatePacketCreator> packetCreator = Optional.empty();
		
		private Builder() {
		}
		
		public static Builder of(String modid) {
			Builder builder = new Builder();
			builder.id = modid;
			return builder;
		}
		
		public Builder addConfigValue(ConfigValue<?> value) {
			values.add(value);
			return this;
		}
		
		public Builder setPacketCreator(UpdatePacketCreator creator) {
			packetCreator = Optional.of(creator);
			return this;
		}
		
		public ServerConfig buildServer() {
			return new ServerConfig(id, values, packetCreator);
		}
		
		public ClientConfig buildClient() {
			return new ClientConfig(id, values, packetCreator);
		}
	}
	
	public static abstract class ConfigValue<T> implements Supplier<T> {
		
		protected final T defaultValue;
		protected T value;
		protected final String name;
		
		private ConfigValue(T defaultValue, String name) {
			this.defaultValue = defaultValue;
			this.name = name;
		}
		
		public void set(T value) {
			this.value = value;
		}

		@Override
		public T get() {
			return value != null ? value : defaultValue;
		}
		
		public T getDefault() {
			return defaultValue;
		}

		public String getName() {
			return name;
		}
	}
	
	public static class IntValue extends ConfigValue<Integer> {
		
		protected final int minValue;
		protected final int maxValue;
		
		public IntValue(int defaultValue, int minValue, int maxValue, String name) {
			super(defaultValue, name);
			this.minValue = minValue;
			this.maxValue = maxValue;
			
			if(defaultValue > maxValue || defaultValue < minValue || minValue > maxValue) {
				throw new IllegalArgumentException("Value bounds are arbitrary for Config Value \"" + name + "\"");
			}
		}
		
		@Override
		public void set(Integer value) {
			this.value = MathHelper.clamp(value, minValue, maxValue);
		}
	}
	
	public static class DoubleValue extends ConfigValue<Double> {
		
		protected final double minValue;
		protected final double maxValue;
		
		public DoubleValue(double defaultValue, double minValue, double maxValue, String name) {
			super(defaultValue, name);
			this.minValue = minValue;
			this.maxValue = maxValue;
			
			if(defaultValue > maxValue || defaultValue < minValue || minValue > maxValue) {
				throw new IllegalArgumentException("Value bounds are arbitrary for Config Value \"" + name + "\"");
			}
		}
		
		@Override
		public void set(Double value) {
			this.value = MathHelper.clamp(value, minValue, maxValue);
		}
	}
	
	public static class BooleanValue extends ConfigValue<Boolean> {
		
		public BooleanValue(boolean defaultValue, String name) {
			super(defaultValue, name);
		}
	}
	
	public static class EnumValue<T extends Enum<T> & StringRepresentable> extends ConfigValue<T> {
		
		protected T[] values;
		
		public EnumValue(T defaultValue, String name, T[] values) {
			super(defaultValue, name);
			this.values = values;
		}
		
		public String getNameOfValue() {
			return get().getString();
		}
		
		public T getValueByName(String name) {
			for(T t : values) {
				if(t.getString().equals(name)) {
					return t;
				}
			}
			return defaultValue;
		}
	}
	
	public interface UpdatePacketCreator {
		public LuckyTNTPacket getPacket(List<ConfigValue<?>> configValues);
	}
}
