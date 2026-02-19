package qa.luffy.pseudo.common.item;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.Foods;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThistleItem extends BlockItem {

    private static final int HUNGER_TICKS = 8 * 20;

    private static final String KEY_PRICK_TARGET = "pseudo_thistle_prick_target";
    private static final String KEY_PRICK_START_TICK = "pseudo_thistle_prick_start_tick";

    public ThistleItem(Block block, Properties properties) {
        super(block, properties.food(Foods.SWEET_BERRIES));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity living, @NotNull ItemStack stack, int remainingUseTicks) {
        super.onUseTick(level, living, stack, remainingUseTicks);
        if (level.isClientSide) return;

        int duration = this.getUseDuration(stack, living);
        if (duration <= 0) return;

        CompoundTag tag = living.getPersistentData();

        if (remainingUseTicks >= duration - 1) {
            int tick = living.tickCount;
            if (tag.getInt(KEY_PRICK_START_TICK) != tick) {
                tag.putInt(KEY_PRICK_START_TICK, tick);

                if (level.random.nextFloat() < 0.10F) {
                    tag.putInt(KEY_PRICK_TARGET, Math.max(2, duration / 2));
                } else {
                    tag.remove(KEY_PRICK_TARGET);
                }
            }
        }

        if (tag.contains(KEY_PRICK_TARGET) && remainingUseTicks == tag.getInt(KEY_PRICK_TARGET)) {
            tag.remove(KEY_PRICK_TARGET);
            living.hurt(level.damageSources().sweetBerryBush(), 1.0F);
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, Level level, @NotNull LivingEntity living, int timeLeft) {
        if (!level.isClientSide) {
            living.getPersistentData().remove(KEY_PRICK_TARGET);
        }
        super.releaseUsing(stack, level, living, timeLeft);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity living) {
        ItemStack out = super.finishUsingItem(stack, level, living);
        if (level.isClientSide) return out;

        living.getPersistentData().remove(KEY_PRICK_TARGET);

        if (level.random.nextFloat() < 0.30F) {
            if (level.random.nextBoolean()) {
                living.addEffect(new MobEffectInstance(MobEffects.HUNGER, HUNGER_TICKS, 0));
            } else {
                living.addEffect(new MobEffectInstance(MobEffects.SATURATION, HUNGER_TICKS, 0));
            }
        }

        clearOneRandomEffect(living, level);
        return out;
    }

    private static void clearOneRandomEffect(LivingEntity living, Level level) {
        ArrayList<Holder<MobEffect>> removable = new ArrayList<>();
        for (MobEffectInstance inst : living.getActiveEffects()) {
            Holder<MobEffect> eff = inst.getEffect();
            if (eff == MobEffects.HUNGER || eff == MobEffects.SATURATION) continue;
            removable.add(eff);
        }
        if (removable.isEmpty()) return;

        Holder<MobEffect> pick = removable.get(level.random.nextInt(removable.size()));
        living.removeEffect(pick);
    }
}
