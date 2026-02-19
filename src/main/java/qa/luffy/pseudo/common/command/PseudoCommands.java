package qa.luffy.pseudo.common.command;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import qa.luffy.pseudo.common.Pseudo;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class PseudoCommands {
    private PseudoCommands() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        TodoCommand.register(event.getDispatcher());
    }
}
