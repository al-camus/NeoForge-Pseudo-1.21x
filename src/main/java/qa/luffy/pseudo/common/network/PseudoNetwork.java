package qa.luffy.pseudo.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.network.packet.EnergyData;
import qa.luffy.pseudo.common.network.payload.ClipboardSyncPayload;
import qa.luffy.pseudo.common.network.payload.OpenPocketCrafterPayload;
import qa.luffy.pseudo.common.network.payload.TodoOpenClipboardPayload;
import qa.luffy.pseudo.common.network.payload.TodoSyncClipboardPayload;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class PseudoNetwork {

    private static final String PROTOCOL_VERSION = "1";

    private PseudoNetwork() {}

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                OpenPocketCrafterPayload.TYPE,
                OpenPocketCrafterPayload.STREAM_CODEC,
                OpenPocketCrafterPayload::handle
        );

        registrar.playToServer(
                ClipboardSyncPayload.TYPE,
                ClipboardSyncPayload.STREAM_CODEC,
                ClipboardSyncPayload::handle
        );

        registrar.playToServer(
                TodoSyncClipboardPayload.TYPE,
                TodoSyncClipboardPayload.STREAM_CODEC,
                TodoSyncClipboardPayload::handle
        );

        registrar.playToClient(
                TodoOpenClipboardPayload.TYPE,
                TodoOpenClipboardPayload.STREAM_CODEC,
                TodoOpenClipboardPayload::handle
        );

        registrar.playToClient(
                EnergyData.TYPE,
                EnergyData.STREAM_CODEC,
                EnergyData::handleData
        );
    }
}
