package qa.luffy.pseudo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BleedingEffect extends MobEffect {

    public BleedingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        if (entity instanceof Player p && (p.isCreative() || p.isSpectator())) return true;

        // Poison-style floor: stop at 1.0F health (½ heart)
        final float floor = 1.0F;
        float health = entity.getHealth();
        if (health <= floor) return true;

        float drain = 2.0F + (1.0F * amplifier);

        entity.setHealth(Math.max(floor, health - drain));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Math.max(10, 40 - (amplifier * 10));
        return duration % interval == 0;
    }
}
