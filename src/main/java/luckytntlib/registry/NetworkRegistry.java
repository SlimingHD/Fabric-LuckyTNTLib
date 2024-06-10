package luckytntlib.registry;

import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config.BooleanValue;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.config.common.Config.DoubleValue;
import luckytntlib.config.common.Config.EnumValue;
import luckytntlib.config.common.Config.IntValue;
import luckytntlib.network.UpdateConfigValuesC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class NetworkRegistry {
	
	private static final PlayChannelHandler UPDATE_C2S = new PlayChannelHandler() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			NbtCompound tag = buf.readNbt();
			
			System.out.println("packet handling on server");
			
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
			
			LuckyTNTLibConfigValues.CONFIG.save(server.getWorld(World.OVERWORLD));
		}
	};

	public static void init() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientNetworkRegistry.init();
		}
		
		ServerPlayNetworking.registerGlobalReceiver(UpdateConfigValuesC2SPacket.NAME, UPDATE_C2S);
		
	}
}
