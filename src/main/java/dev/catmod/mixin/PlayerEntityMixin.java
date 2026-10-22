package dev.catmod.mixin;

import dev.catmod.CatMod;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Cat sounds when hurt or dying. */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
	private void catmod$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
		if (CatMod.isCat((PlayerEntity) (Object) this)) {
			cir.setReturnValue(SoundEvents.ENTITY_CAT_HURT);
		}
	}

	@Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
	private void catmod$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
		if (CatMod.isCat((PlayerEntity) (Object) this)) {
			cir.setReturnValue(SoundEvents.ENTITY_CAT_DEATH);
		}
	}
}
