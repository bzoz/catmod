package dev.catmod;

import dev.catmod.network.MeowPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CatMod implements ModInitializer {
	public static final String MOD_ID = "catmod";

	// Tweak these to taste.
	/** Player size relative to normal (1.0). 0.5 gives roughly a cat's eye level. */
	public static final double CAT_SCALE = 0.5;
	/** Extra walking speed, as a fraction of base speed. */
	public static final double CAT_SPEED_BONUS = 0.25;

	private static final Identifier SCALE_ID = id("cat_scale");
	private static final Identifier SPEED_ID = id("cat_speed");
	private static final Identifier FALL_ID = id("cat_no_fall_damage");

	private static final int NIGHT_VISION_TICKS = 400;
	private static final int NIGHT_VISION_REFRESH_BELOW = 220; // vanilla flickers under 200

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/** Single switch for "is this player a cat". Everyone is, except spectators. */
	public static boolean isCat(Player player) {
		return !player.isSpectator();
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundPlay().register(MeowPayload.TYPE, MeowPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(MeowPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			if (isCat(player)) {
				// Broadcasts to everyone else; the meowing client plays its own copy locally.
				player.playSound(CatSounds.meow(player.level()), 1.0f, randomPitch(player));
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (isCat(player)) {
					applyCatPowers(player);
				} else {
					removeCatPowers(player);
				}
			}
		});
	}

	public static float randomPitch(Player player) {
		return 0.9f + player.getRandom().nextFloat() * 0.3f;
	}

	private static void applyCatPowers(ServerPlayer player) {
		ensureModifier(player, Attributes.SCALE, SCALE_ID,
				CAT_SCALE - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		ensureModifier(player, Attributes.MOVEMENT_SPEED, SPEED_ID,
				CAT_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		// Multiplier of 0 => cats always land on their feet.
		ensureModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, FALL_ID,
				-1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

		MobEffectInstance nightVision = player.getEffect(MobEffects.NIGHT_VISION);
		if (nightVision == null || nightVision.getDuration() < NIGHT_VISION_REFRESH_BELOW) {
			// ambient, no particles, no HUD icon
			player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, true, false, false));
		}
	}

	private static void removeCatPowers(ServerPlayer player) {
		removeModifier(player, Attributes.SCALE, SCALE_ID);
		removeModifier(player, Attributes.MOVEMENT_SPEED, SPEED_ID);
		removeModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, FALL_ID);
	}

	// Transient modifiers are not saved to disk, so uninstalling the mod leaves the world clean.
	private static void ensureModifier(Player player, Holder<Attribute> attribute,
			Identifier modifierId, double amount, AttributeModifier.Operation operation) {
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance != null && !instance.hasModifier(modifierId)) {
			instance.addTransientModifier(new AttributeModifier(modifierId, amount, operation));
		}
	}

	private static void removeModifier(Player player, Holder<Attribute> attribute, Identifier modifierId) {
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance != null && instance.hasModifier(modifierId)) {
			instance.removeModifier(modifierId);
		}
	}
}
