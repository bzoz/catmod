package dev.catmod.mixin;

import dev.catmod.CatMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Creepers are scared of cats in vanilla; now they are scared of cat players too. */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {
	protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void catmod$fleeFromCatPlayers(CallbackInfo ci) {
		// Same priority and distances vanilla uses for fleeing from real cats.
		this.goalSelector.add(3, new FleeEntityGoal<>(
				(CreeperEntity) (Object) this, PlayerEntity.class, 6.0f, 1.0, 1.2,
				entity -> entity instanceof PlayerEntity player && CatMod.isCat(player) && !player.isCreative()));
	}
}
