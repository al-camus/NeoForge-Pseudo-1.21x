package qa.luffy.pseudo.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import qa.luffy.pseudo.client.init.PseudoItemProperties;
import qa.luffy.pseudo.client.render.ber.ClipboardBER;
import qa.luffy.pseudo.client.screen.CapacitorScreen;
import qa.luffy.pseudo.client.screen.MeshCrateScreen;
import qa.luffy.pseudo.client.screen.PocketCrafterScreen;
import qa.luffy.pseudo.client.screen.ToolboxBlockScreen;
import qa.luffy.pseudo.client.screen.ToolboxScreen;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.entity.PseudoBlockEntities;
import qa.luffy.pseudo.common.menu.PseudoMenus;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PseudoClientModEvents {
    private PseudoClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PseudoItemProperties::addCustomItemProperties);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PseudoMenus.CAPACITOR_MENU_TYPE.get(), CapacitorScreen::new);
        event.register(PseudoMenus.TOOLBOX_MENU_TYPE.get(), ToolboxScreen::new);
        event.register(PseudoMenus.TOOLBOX_BLOCK_MENU_TYPE.get(), ToolboxBlockScreen::new);
        event.register(PseudoMenus.MESH_CRATE_MENU_TYPE.get(), MeshCrateScreen::new);
        event.register(PseudoMenus.POCKET_CRAFTER_MENU.get(), PocketCrafterScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PseudoBlockEntities.CLIPBOARD_TYPE.get(), ClipboardBER::new);
    }
}
