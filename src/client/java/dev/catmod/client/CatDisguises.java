package dev.catmod.client;

import dev.catmod.mixin.client.WalkAnimationStateAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.feline.Cat;
import org.jspecify.annotations.Nullable;

/**
 * One invisible, never-spawned cat per player. At render time we copy the player's
 * position, rotation and walk animation onto it and let vanilla's cat renderer draw it.
 */
public final class CatDisguises {
	private static final Map<UUID, Cat> CATS = new HashMap<>();
	private static @Nullable ClientLevel catsLevel;

	private CatDisguises() {
	}

	public static @Nullable Cat forPlayer(AbstractClientPlayer player) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return null;
		}
		if (level != catsLevel) {
			CATS.clear(); // new world or dimension: drop old cats
			catsLevel = level;
		}
		Cat cat = CATS.computeIfAbsent(player.getUUID(), uuid -> EntityType.CAT.create(level, EntitySpawnReason.LOAD));
		if (cat != null) {
			copyPose(player, cat);
		}
		return cat;
	}

	private static void copyPose(AbstractClientPlayer player, Cat cat) {
		cat.setPos(player.getX(), player.getY(), player.getZ());
		cat.xo = player.xo;
		cat.yo = player.yo;
		cat.zo = player.zo;
		cat.xOld = player.xOld;
		cat.yOld = player.yOld;
		cat.zOld = player.zOld;

		cat.setYRot(player.getYRot());
		cat.yRotO = player.yRotO;
		cat.setXRot(player.getXRot());
		cat.xRotO = player.xRotO;
		cat.yBodyRot = player.yBodyRot;
		cat.yBodyRotO = player.yBodyRotO;
		cat.yHeadRot = player.yHeadRot;
		cat.yHeadRotO = player.yHeadRotO;

		cat.tickCount = player.tickCount;
		cat.hurtTime = player.hurtTime;
		cat.setOnGround(player.onGround());

		// Sneak = sit, like a proper cat.
		cat.setInSittingPose(player.isCrouching());

		WalkAnimationStateAccessor from = (WalkAnimationStateAccessor) player.walkAnimation;
		WalkAnimationStateAccessor to = (WalkAnimationStateAccessor) cat.walkAnimation;
		to.catmod$setSpeed(from.catmod$getSpeed());
		to.catmod$setSpeedOld(from.catmod$getSpeedOld());
		to.catmod$setPosition(from.catmod$getPosition());
	}
}
