package dev.catmod.network;

import dev.catmod.CatMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** Client -> server: "I meowed". Carries no data. */
public record MeowPayload() implements CustomPayload {
	public static final MeowPayload INSTANCE = new MeowPayload();
	public static final CustomPayload.Id<MeowPayload> ID = new CustomPayload.Id<>(CatMod.id("meow"));
	public static final PacketCodec<RegistryByteBuf, MeowPayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
