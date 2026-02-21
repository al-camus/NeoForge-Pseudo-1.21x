package qa.luffy.pseudo.client.input;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.network.payload.OpenPocketCrafterPayload;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class PocketCrafterKeyHandler {

    private PocketCrafterKeyHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        while (PseudoKeyMappings.OPEN_POCKET_CRAFTER.consumeClick()) {
            OpenPocketCrafterPayload.sendToServer();
        }
    }
}
