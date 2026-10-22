package dev.catmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.catmod.CatMod;
import dev.catmod.network.MeowPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import dev.catmod.CatSounds;

public class CatModClient implements ClientModInitializer {
	private static final int MEOW_COOLDOWN_TICKS = 10;

	private static KeyMapping meowKey;
	private static int meowCooldown;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(CatMod.id("cat"));
		meowKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.catmod.meow", InputConstants.Type.KEYSYM, InputConstants.KEY_M, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (meowCooldown > 0) {
				meowCooldown--;
			}
			while (meowKey.consumeClick()) {
				if (client.player == null || !CatMod.isCat(client.player) || meowCooldown > 0) {
					continue;
				}
				meowCooldown = MEOW_COOLDOWN_TICKS;
				client.player.playSound(CatSounds.meow(client.player.level()), 1.0f, CatMod.randomPitch(client.player));
				if (ClientPlayNetworking.canSend(MeowPayload.TYPE)) {
					ClientPlayNetworking.send(MeowPayload.INSTANCE);
				}
			}
		});
	}
}
