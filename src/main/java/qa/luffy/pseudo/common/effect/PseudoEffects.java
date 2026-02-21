package qa.luffy.pseudo.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;

public class PseudoEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Pseudo.MODID);

    public static final Holder<MobEffect> BLEEDING_EFFECT =
            MOB_EFFECTS.register("bleeding", () -> new BleedingEffect(MobEffectCategory.HARMFUL, 0x780606));

    public static final Holder<MobEffect> WARMING_EFFECT =
            MOB_EFFECTS.register("warming", () -> new WarmingEffect(MobEffectCategory.BENEFICIAL, 0xFF4D00));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}