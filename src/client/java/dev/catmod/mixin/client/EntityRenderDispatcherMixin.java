package dev.catmod.mixin.client;

import dev.catmod.CatMod;
import dev.catmod.client.CatDisguises;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When the game extracts the render state for a player, hand back the state of that
 * player's disguise cat instead. The renderer is picked from the state's entity type,
 * so vanilla's cat renderer draws it.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow
	public abstract <E extends Entity> EntityRenderState extractEntity(E entity, float partialTick);

	@Inject(method = "extractEntity", at = @At("HEAD"), cancellable = true)
	private <E extends Entity> void catmod$renderPlayerAsCat(E entity, float partialTick,
			CallbackInfoReturnable<EntityRenderState> cir) {
		if (entity instanceof AbstractClientPlayer player && CatMod.isCat(player)) {
			Cat cat = CatDisguises.forPlayer(player);
			if (cat != null) {
				cir.setReturnValue(this.extractEntity(cat, partialTick));
			}
		}
	}
}
