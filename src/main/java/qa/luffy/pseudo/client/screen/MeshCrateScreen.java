// qa/luffy/pseudo/client/screen/MeshCrateScreen.java
package qa.luffy.pseudo.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.menu.MeshCrateMenu;

public class MeshCrateScreen extends AbstractContainerScreen<MeshCrateMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/mesh_crate.png");

    public MeshCrateScreen(MeshCrateMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);

        // Adjust to match your texture:
        this.imageWidth = 256;   // example for a big 9x9 UI
        this.imageHeight = 256;  // tweak to your art

        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int color = 0xFFFFFF;
        guiGraphics.drawString(this.font, this.title,
                this.titleLabelX, this.titleLabelY,
                color, true);
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                this.inventoryLabelX, this.inventoryLabelY,
                color, true);
    }
}
