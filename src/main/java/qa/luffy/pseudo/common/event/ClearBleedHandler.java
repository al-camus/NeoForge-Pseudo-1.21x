package qa.luffy.pseudo.common.event;

import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.effect.PseudoEffects;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ClearBleedHandler {

    private ClearBleedHandler() {}

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getAmount() <= 0.0F) return;

        if (event.getEntity().hasEffect(PseudoEffects.BLEEDING_EFFECT)) {
            event.getEntity().removeEffect(PseudoEffects.BLEEDING_EFFECT);
        }
    }

    @SubscribeEvent
    public static void onDamagePost(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;

        // Only cauterize if health actually went down
        if (event.getNewDamage() <= 0.0F) return;

        if (!event.getEntity().hasEffect(PseudoEffects.BLEEDING_EFFECT)) return;

        // Any fire-type damage cauterizes
        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.getEntity().removeEffect(PseudoEffects.BLEEDING_EFFECT);
        }
    }
}
