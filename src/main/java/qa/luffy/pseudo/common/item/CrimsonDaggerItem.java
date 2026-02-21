package qa.luffy.pseudo.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.effect.PseudoEffects;

public class CrimsonDaggerItem extends SwordItem {

    private final float bleedChance;       // 0.0 - 1.0
    private final int bleedDurationTicks;  // e.g. 160
    private final int bleedAmplifier;      // 0 = Bleed I, 1 = Bleed II, etc.

    public CrimsonDaggerItem(Tier tier, Properties properties, float bleedChance, int bleedDurationTicks, int bleedAmplifier) {
        super(tier, properties);
        this.bleedChance = bleedChance;
        this.bleedDurationTicks = bleedDurationTicks;
        this.bleedAmplifier = bleedAmplifier;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean ok = super.hurtEnemy(stack, target, attacker);
        if (!ok) return false;

        if (attacker.level().isClientSide) return true;
        if (attacker instanceof Player p && (p.isCreative() || p.isSpectator())) return true;

        if (attacker.getRandom().nextFloat() >= this.bleedChance) return true;

        var bleed = PseudoEffects.BLEEDING_EFFECT;

        MobEffectInstance existing = target.getEffect(bleed);
        int amp = this.bleedAmplifier;
        int dur = this.bleedDurationTicks;

        if (existing != null) {
            amp = Math.max(existing.getAmplifier(), amp);
            dur = Math.max(existing.getDuration(), dur);
        }

        // Stealthy: no particles, no icon (your HUD wiggle can still react to the effect existing)
        target.addEffect(new MobEffectInstance(
                bleed,
                dur,
                amp,
                false, // ambient
                true, // showParticles
                true  // showIcon
        ));

        return true;
    }
}
