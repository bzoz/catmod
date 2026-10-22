package dev.catmod.client;

import dev.catmod.mixin.client.LimbAnimatorAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import org.jetbrains.annotations.Nullable;

/**
 * One invisible, never-spawned cat per player. At render time we copy the player's
 * position, rotation and walk animation onto it and let vanilla's cat renderer draw it.
 */
public final class CatDisguises {
	private static final Map<UUID, CatEntity> CATS = new HashMap<>();
	private static @Nullable ClientWorld catsWorld;

	private CatDisguises() {
	}

	public static @Nullable CatEntity forPlayer(AbstractClientPlayerEntity player) {
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return null;
		}
		if (world != catsWorld) {
			CATS.clear(); // new world or dimension: drop old cats
			catsWorld = world;
		}
		CatEntity cat = CATS.computeIfAbsent(player.getUuid(), uuid -> EntityType.CAT.create(world, SpawnReason.LOAD));
		if (cat != null) {
			copyPose(player, cat);
		}
		return cat;
	}

	private static void copyPose(AbstractClientPlayerEntity player, CatEntity cat) {
		cat.setPosition(player.getX(), player.getY(), player.getZ());
		cat.lastX = player.lastX;
		cat.lastY = player.lastY;
		cat.lastZ = player.lastZ;
		cat.lastRenderX = player.lastRenderX;
		cat.lastRenderY = player.lastRenderY;
		cat.lastRenderZ = player.lastRenderZ;

		cat.setYaw(player.getYaw());
		cat.lastYaw = player.lastYaw;
		cat.setPitch(player.getPitch());
		cat.lastPitch = player.lastPitch;
		cat.bodyYaw = player.bodyYaw;
		cat.lastBodyYaw = player.lastBodyYaw;
		cat.headYaw = player.headYaw;
		cat.lastHeadYaw = player.lastHeadYaw;

		cat.age = player.age;
		cat.hurtTime = player.hurtTime;
		cat.setOnGround(player.isOnGround());

		// Sneak = sit, like a proper cat.
		cat.setInSittingPose(player.isInSneakingPose());

		LimbAnimatorAccessor from = (LimbAnimatorAccessor) player.limbAnimator;
		LimbAnimatorAccessor to = (LimbAnimatorAccessor) cat.limbAnimator;
		to.catmod$setSpeed(from.catmod$getSpeed());
		to.catmod$setLastSpeed(from.catmod$getLastSpeed());
		to.catmod$setAnimationProgress(from.catmod$getAnimationProgress());
	}
}
