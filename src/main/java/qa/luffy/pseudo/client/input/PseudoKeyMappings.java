package qa.luffy.pseudo.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;
import qa.luffy.pseudo.common.Pseudo;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PseudoKeyMappings {

    public static final String CATEGORY = "key.categories.pseudo";

    public static final KeyMapping OPEN_POCKET_CRAFTER = new KeyMapping(
            "key.pseudo.open_pocket_crafter",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    );

    private PseudoKeyMappings() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_POCKET_CRAFTER);
    }
}
