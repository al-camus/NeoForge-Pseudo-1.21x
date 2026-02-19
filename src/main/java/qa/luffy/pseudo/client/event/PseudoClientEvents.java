package qa.luffy.pseudo.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PseudoItems;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class PseudoClientEvents {
    private PseudoClientEvents() {}

    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        var player = event.getPlayer();
        if (!player.isUsingItem()) return;
        if (player.getUseItem().getItem() != PseudoItems.SLINGSHOT.get()) return;

        float draw = Math.min(player.getTicksUsingItem() / 20.0F, 1.0F);
        event.setNewFovModifier(1.0F - (draw * 0.15F));
    }
}
