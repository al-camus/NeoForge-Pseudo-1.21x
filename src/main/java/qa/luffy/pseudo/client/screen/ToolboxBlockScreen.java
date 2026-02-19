package qa.luffy.pseudo.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import qa.luffy.pseudo.common.menu.ToolboxBlockMenu;

public class ToolboxBlockScreen extends ToolboxBaseScreen<ToolboxBlockMenu> {

    public ToolboxBlockScreen(ToolboxBlockMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }
}
