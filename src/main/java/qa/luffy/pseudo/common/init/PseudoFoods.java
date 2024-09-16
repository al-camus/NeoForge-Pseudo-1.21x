package qa.luffy.pseudo.common.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class PseudoFoods {
    public static final FoodProperties SCULK_FRUIT = new FoodProperties.Builder().nutrition(3).saturationModifier(2f).effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 2000), 1f).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2000, 1), 1f).build();
}
