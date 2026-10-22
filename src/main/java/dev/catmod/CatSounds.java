package dev.catmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.feline.CatSoundVariant;
import net.minecraft.world.entity.animal.feline.CatSoundVariants;
import net.minecraft.world.level.Level;

/**
 * Cat sounds are data-driven variants since 26.x. The player uses the classic kitten set.
 */
public final class CatSounds {
	private CatSounds() {
	}

	private static CatSoundVariant.CatSoundSet set(Level level) {
		return level.registryAccess()
				.lookupOrThrow(Registries.CAT_SOUND_VARIANT)
				.getOrThrow(CatSoundVariants.CLASSIC)
				.value()
				.babySounds();
	}

	public static SoundEvent meow(Level level) {
		return set(level).ambientSound().value();
	}

	public static SoundEvent hurt(Level level) {
		return set(level).hurtSound().value();
	}

	public static SoundEvent death(Level level) {
		return set(level).deathSound().value();
	}
}
