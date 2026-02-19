// qa.luffy.pseudo.client.screen.MeshCrateScreen
package qa.luffy.pseudo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.menu.MeshCrateMenu;

public class MeshCrateScreen extends AbstractContainerScreen<MeshCrateMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/mesh_crate.png");

    // Texture size on disk
    private static final int TEXTURE_WIDTH  = 256;
    private static final int TEXTURE_HEIGHT = 276;

    // Visible GUI (frame + slots) only uses the left ~184 px of the texture.
    // We treat this as the "effective" width for centering.
    private static final int VISIBLE_WIDTH = 184;

    public MeshCrateScreen(MeshCrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth  = TEXTURE_WIDTH;
        this.imageHeight = TEXTURE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        // Center the *visible* 184px-wide area, not the full 256px.
        // This keeps all existing slot offsets correct (they're relative
        // to leftPos) but visually centers the grey frame.
        this.leftPos = (this.width  - VISIBLE_WIDTH) / 2;

        // Vanilla-style vertical centering; no clamping, so at very
        // high GUI scales the tiny overflow is split top/bottom.
        this.topPos  = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // title
        guiGraphics.drawString(this.font, this.title,
                8, 6,
                0xF0F0F0, false);

        // "Inventory" label – same formula as IronShulkerBoxScreen
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 96 + 2,
                0xF0F0F0, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Use leftPos/topPos so background and slots stay in sync
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(
                TEXTURE,
                x, y,
                0, 0,
                this.imageWidth, this.imageHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }
}
