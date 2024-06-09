package luckytntlib.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface LuckyTNTPacket {
	
	PacketByteBuf toByteBuf();
	
	Identifier getName();
}
