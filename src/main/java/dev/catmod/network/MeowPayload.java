package dev.catmod.network;

import dev.catmod.CatMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client -> server: "I meowed". Carries no data. */
public record MeowPayload() implements CustomPacketPayload {
	public static final MeowPayload INSTANCE = new MeowPayload();
	public static final CustomPacketPayload.Type<MeowPayload> TYPE = new CustomPacketPayload.Type<>(CatMod.id("meow"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MeowPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
