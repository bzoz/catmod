package dev.catmod.client;

import dev.catmod.CatMod;
import dev.catmod.network.MeowPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class CatModClient implements ClientModInitializer {
	private static final int MEOW_COOLDOWN_TICKS = 10;

	private static KeyBinding meowKey;
	private static int meowCooldown;

	@Override
	public void onInitializeClient() {
		KeyBinding.Category category = KeyBinding.Category.create(CatMod.id("cat"));
		meowKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.catmod.meow", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (meowCooldown > 0) {
				meowCooldown--;
			}
			while (meowKey.wasPressed()) {
				if (client.player == null || !CatMod.isCat(client.player) || meowCooldown > 0) {
					continue;
				}
				meowCooldown = MEOW_COOLDOWN_TICKS;
				client.player.playSound(SoundEvents.ENTITY_CAT_AMBIENT, 1.0f, CatMod.randomPitch(client.player));
				if (ClientPlayNetworking.canSend(MeowPayload.ID)) {
					ClientPlayNetworking.send(MeowPayload.INSTANCE);
				}
			}
		});
	}
}
