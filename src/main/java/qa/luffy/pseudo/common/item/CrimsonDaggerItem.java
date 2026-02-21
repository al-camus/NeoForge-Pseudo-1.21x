package qa.luffy.pseudo.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
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

        // Require fully-charged hit for players (mobs don't have cooldown)
        if (attacker instanceof Player p) {
            if (p.isCreative() || p.isSpectator()) return true;

            // "Fully charged" (use 1.0F if you want strict, 0.99F is practically strict but reliable)
            if (p.getAttackStrengthScale(0.0F) < 0.99F) return true;
        }

        // Proc roll (proc if roll < chance)
        if (attacker.getRandom().nextFloat() >= this.bleedChance) return true;

        // IMPORTANT: this is a Holder<MobEffect> in your setup
        Holder<MobEffect> bleed = PseudoEffects.BLEEDING_EFFECT;

        // Refresh/upgrade behavior
        MobEffectInstance existing = target.getEffect(bleed);
        int amp = this.bleedAmplifier;
        int dur = this.bleedDurationTicks;

        if (existing != null) {
            amp = Math.max(existing.getAmplifier(), amp);
            dur = Math.max(existing.getDuration(), dur);
        }

        target.addEffect(new MobEffectInstance(bleed, dur, amp, true, true, true), attacker);
        return true;
    }
}
