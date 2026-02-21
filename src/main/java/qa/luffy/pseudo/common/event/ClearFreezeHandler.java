package qa.luffy.pseudo.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.effect.PseudoEffects;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ClearFreezeHandler {

    private ClearFreezeHandler() {}

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity e = event.getEntity();
        if (!(e instanceof LivingEntity living)) return;
        if (living.level().isClientSide) return;

        if (living.hasEffect(PseudoEffects.WARMING_EFFECT)) {
            if (living.getTicksFrozen() > 0) {
                living.setTicksFrozen(0);
            }
        }
    }
}