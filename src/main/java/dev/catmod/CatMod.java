package dev.catmod;

import dev.catmod.network.MeowPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

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
		return Identifier.of(MOD_ID, path);
	}

	/** Single switch for "is this player a cat". Everyone is, except spectators. */
	public static boolean isCat(PlayerEntity player) {
		return !player.isSpectator();
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(MeowPayload.ID, MeowPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(MeowPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			if (isCat(player)) {
				// Broadcasts to everyone else; the meowing client plays its own copy locally.
				player.playSound(SoundEvents.ENTITY_CAT_AMBIENT, 1.0f, randomPitch(player));
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (isCat(player)) {
					applyCatPowers(player);
				} else {
					removeCatPowers(player);
				}
			}
		});
	}

	public static float randomPitch(PlayerEntity player) {
		return 0.9f + player.getRandom().nextFloat() * 0.3f;
	}

	private static void applyCatPowers(ServerPlayerEntity player) {
		ensureModifier(player, EntityAttributes.SCALE, SCALE_ID,
				CAT_SCALE - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		ensureModifier(player, EntityAttributes.MOVEMENT_SPEED, SPEED_ID,
				CAT_SPEED_BONUS, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		// Multiplier of 0 => cats always land on their feet.
		ensureModifier(player, EntityAttributes.FALL_DAMAGE_MULTIPLIER, FALL_ID,
				-1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

		StatusEffectInstance nightVision = player.getStatusEffect(StatusEffects.NIGHT_VISION);
		if (nightVision == null || nightVision.getDuration() < NIGHT_VISION_REFRESH_BELOW) {
			// ambient, no particles, no HUD icon
			player.addStatusEffect(new StatusEffectInstance(
					StatusEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, true, false, false));
		}
	}

	private static void removeCatPowers(ServerPlayerEntity player) {
		removeModifier(player, EntityAttributes.SCALE, SCALE_ID);
		removeModifier(player, EntityAttributes.MOVEMENT_SPEED, SPEED_ID);
		removeModifier(player, EntityAttributes.FALL_DAMAGE_MULTIPLIER, FALL_ID);
	}

	// Temporary modifiers are not saved to disk, so uninstalling the mod leaves the world clean.
	private static void ensureModifier(PlayerEntity player, RegistryEntry<EntityAttribute> attribute,
			Identifier modifierId, double amount, EntityAttributeModifier.Operation operation) {
		EntityAttributeInstance instance = player.getAttributeInstance(attribute);
		if (instance != null && !instance.hasModifier(modifierId)) {
			instance.addTemporaryModifier(new EntityAttributeModifier(modifierId, amount, operation));
		}
	}

	private static void removeModifier(PlayerEntity player, RegistryEntry<EntityAttribute> attribute,
			Identifier modifierId) {
		EntityAttributeInstance instance = player.getAttributeInstance(attribute);
		if (instance != null && instance.hasModifier(modifierId)) {
			instance.removeModifier(modifierId);
		}
	}
}
