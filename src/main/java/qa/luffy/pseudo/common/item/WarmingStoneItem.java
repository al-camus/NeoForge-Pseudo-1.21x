package qa.luffy.pseudo.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.effect.PseudoEffects;

public class WarmingStoneItem extends Item {

    private final int durationTicks;
    private final int amplifier;

    public WarmingStoneItem(Properties properties, int durationTicks, int amplifier) {
        super(properties);
        this.durationTicks = durationTicks;
        this.amplifier = amplifier;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // apply effect
            player.addEffect(new MobEffectInstance(PseudoEffects.WARMING_EFFECT, durationTicks, amplifier));

            // glass break at player position
            level.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GLASS_BREAK,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );

            player.awardStat(Stats.ITEM_USED.get(this));

            // consume 1 unless creative
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        // success on both sides so the hand swing/feedback feels correct
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}