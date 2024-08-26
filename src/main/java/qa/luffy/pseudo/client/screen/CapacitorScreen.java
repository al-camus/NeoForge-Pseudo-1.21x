package qa.luffy.pseudo.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapacitorScreen extends Screen {

    protected CapacitorScreen(Component title) {
        super(title);
    }

}
