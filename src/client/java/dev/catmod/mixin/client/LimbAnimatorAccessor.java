package dev.catmod.mixin.client;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbAnimator.class)
public interface LimbAnimatorAccessor {
	@Accessor("speed")
	float catmod$getSpeed();

	@Accessor("speed")
	void catmod$setSpeed(float value);

	@Accessor("lastSpeed")
	float catmod$getLastSpeed();

	@Accessor("lastSpeed")
	void catmod$setLastSpeed(float value);

	@Accessor("animationProgress")
	float catmod$getAnimationProgress();

	@Accessor("animationProgress")
	void catmod$setAnimationProgress(float value);
}
