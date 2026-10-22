package dev.catmod.mixin.client;

import dev.catmod.CatMod;
import dev.catmod.client.CatDisguises;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When the game builds the render state for a player, hand back the state of that
 * player's disguise cat instead. The renderer is picked from the state's entity type,
 * so vanilla's cat renderer draws it.
 */
@Mixin(EntityRenderManager.class)
public abstract class EntityRenderManagerMixin {
	@Shadow
	public abstract <E extends Entity> EntityRenderState getAndUpdateRenderState(E entity, float tickProgress);

	@Inject(method = "getAndUpdateRenderState", at = @At("HEAD"), cancellable = true)
	private <E extends Entity> void catmod$renderPlayerAsCat(E entity, float tickProgress,
			CallbackInfoReturnable<EntityRenderState> cir) {
		if (entity instanceof AbstractClientPlayerEntity player && CatMod.isCat(player)) {
			CatEntity cat = CatDisguises.forPlayer(player);
			if (cat != null) {
				cir.setReturnValue(this.getAndUpdateRenderState(cat, tickProgress));
			}
		}
	}
}
