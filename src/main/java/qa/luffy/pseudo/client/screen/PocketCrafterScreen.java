package qa.luffy.pseudo.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.menu.PocketCrafterMenu;

public class PocketCrafterScreen extends AbstractContainerScreen<PocketCrafterMenu>
        implements MenuAccess<PocketCrafterMenu> {

    // Full texture size (your PNG)
    private static final ResourceLocation BG_TEXTURE =
            Pseudo.resource("textures/gui/pocket_crafter.png");
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 256;

    // Actual GUI area size (like vanilla crafting table)
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    public PocketCrafterScreen(PocketCrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        // Draw only the 176×166 GUI region from the 256×256 texture.
        // Assumes the GUI artwork sits at the top-left of the texture (u = v = 0).
        graphics.blit(
                BG_TEXTURE,
                left, top,          // where on the screen
                0, 0,               // u, v in texture
                GUI_WIDTH, GUI_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title near top-left
        graphics.drawString(this.font, this.title, 8, 6, 0xF0F0F0, false);
        // "Inventory" above player inventory slots
        graphics.drawString(this.font, this.playerInventoryTitle, 8, 74, 0xF0F0F0, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
