package dev.catmod.mixin;

import dev.catmod.CatMod;
import net.minecraft.sounds.SoundEvent;
import dev.catmod.CatSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Cat sounds when hurt or dying. */
@Mixin(Player.class)
public abstract class PlayerEntityMixin {
	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
	private void catmod$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
		if (CatMod.isCat((Player) (Object) this)) {
			cir.setReturnValue(CatSounds.hurt(((Player) (Object) this).level()));
		}
	}

	@Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
	private void catmod$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
		if (CatMod.isCat((Player) (Object) this)) {
			cir.setReturnValue(CatSounds.death(((Player) (Object) this).level()));
		}
	}
}
