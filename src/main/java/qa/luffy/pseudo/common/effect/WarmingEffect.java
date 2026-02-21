package qa.luffy.pseudo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WarmingEffect extends MobEffect {

    public WarmingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        if (entity instanceof Player p && (p.isCreative() || p.isSpectator())) return true;

        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(2.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Vanilla Regeneration interval: 50 >> amplifier (50, 25, 12, 6, 3, ...)
        int interval = 50 >> amplifier;
        return interval <= 0 || duration % interval == 0;
    }
}