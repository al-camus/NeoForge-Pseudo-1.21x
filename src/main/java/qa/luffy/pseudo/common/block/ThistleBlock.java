package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThistleBlock extends FlowerBlock {

    private static final Vec3 SLOW = new Vec3(0.8D, 0.75D, 0.8D);
    private static final int HUNGER_TICKS = 8 * 20;

    public ThistleBlock(Holder<MobEffect> stewEffect, int stewEffectDuration, Properties properties) {
        super(stewEffect, stewEffectDuration, properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        EntityType<?> type = living.getType();
        if (type == EntityType.BEE || type == EntityType.FOX) return;

        entity.makeStuckInBlock(state, SLOW);
        if (level.isClientSide) return;

        double dx = Math.abs(entity.getX() - entity.xOld);
        double dz = Math.abs(entity.getZ() - entity.zOld);
        if (dx >= 0.003D || dz >= 0.003D) {
            living.hurt(level.damageSources().sweetBerryBush(), 1.0F);

            if (!living.hasEffect(MobEffects.HUNGER) && level.random.nextFloat() < 0.043F) {
                living.addEffect(new MobEffectInstance(MobEffects.HUNGER, HUNGER_TICKS, 0));
            }
        }
    }
}
