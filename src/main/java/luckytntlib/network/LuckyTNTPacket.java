package luckytntlib.network;

import luckytntlib.registry.ClientNetworkRegistry;
import luckytntlib.registry.NetworkRegistry;
import luckytntlib.registry.RegistryHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

/**
 * An Interface that makes the creation and the sending of {@link Packet}s easier when using the {@link RegistryHelper}.
 * <p>
 * @see RegistryHelper#sendC2SPacket(LuckyTNTPacket)
 * @see RegistryHelper#sendS2CPacket(net.minecraft.server.network.ServerPlayerEntity, LuckyTNTPacket)
 * @see NetworkRegistry
 * @see ClientNetworkRegistry
 */
public interface LuckyTNTPacket {
	
	/**
	 * write the data of this {@link Packet} to a {@link PacketByteBuf}
	 * @return a {@link PacketByteBuf} with the data written to it
	 */
	PacketByteBuf toByteBuf();
	
	/**
	 * the unique name of the packet
	 * @return an {@link Identifier} that represents the unique name
	 */
	Identifier getName();
}
