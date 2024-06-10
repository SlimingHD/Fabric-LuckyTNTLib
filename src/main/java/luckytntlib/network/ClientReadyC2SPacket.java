package luckytntlib.network;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientReadyC2SPacket implements LuckyTNTPacket {
	
	public static Identifier NAME = new Identifier(LuckyTNTLib.MODID, "client_ready_c2s");
	
	public ClientReadyC2SPacket() {
	}

	@Override
	public PacketByteBuf toByteBuf() {
		return PacketByteBufs.empty();
	}

	@Override
	public Identifier getName() {
		return NAME;
	}

}
