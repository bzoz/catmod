package dev.catmod.mixin.client;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccessor {
	@Accessor("speed")
	float catmod$getSpeed();

	@Accessor("speed")
	void catmod$setSpeed(float value);

	@Accessor("speedOld")
	float catmod$getSpeedOld();

	@Accessor("speedOld")
	void catmod$setSpeedOld(float value);

	@Accessor("position")
	float catmod$getPosition();

	@Accessor("position")
	void catmod$setPosition(float value);
}
