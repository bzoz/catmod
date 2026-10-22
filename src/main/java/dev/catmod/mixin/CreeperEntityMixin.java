package dev.catmod.mixin;

import dev.catmod.CatMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Creepers are scared of cats in vanilla; now they are scared of cat players too. */
@Mixin(Creeper.class)
public abstract class CreeperEntityMixin extends Monster {
	protected CreeperEntityMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "registerGoals", at = @At("TAIL"))
	private void catmod$fleeFromCatPlayers(CallbackInfo ci) {
		// Same priority and distances vanilla uses for fleeing from real cats.
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(
				(Creeper) (Object) this, Player.class, 6.0f, 1.0, 1.2,
				entity -> entity instanceof Player player && CatMod.isCat(player) && !player.isCreative()));
	}
}
