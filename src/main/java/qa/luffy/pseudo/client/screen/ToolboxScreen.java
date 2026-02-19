package qa.luffy.pseudo.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import qa.luffy.pseudo.common.menu.ToolboxMenu;

public class ToolboxScreen extends ToolboxBaseScreen<ToolboxMenu> {

    public ToolboxScreen(ToolboxMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }
}
