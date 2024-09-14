package qa.luffy.pseudo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.energy.EnergyStorage;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.entity.CapacitorBlockEntity;
import qa.luffy.pseudo.common.menu.CapacitorMenu;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CapacitorScreen extends AbstractContainerScreen<CapacitorMenu> {

    public static final ResourceLocation TEXTURE = Pseudo.resource("textures/gui/capacitor_gui.png");
    public static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    public static final ResourceLocation BURN_PROGRESS_REVERSE_SPRITE = Pseudo.resource("util/burn_progress_reverse");
    public static final ResourceLocation ENERGY_SPRITE = Pseudo.resource("util/energy");

    private final CapacitorBlockEntity blockEntity = getMenu().getBlockEntity();

    private EnergyStorage energyStorage;

    public CapacitorScreen(CapacitorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity==null) return;

        energyStorage = blockEntity.getEnergyStorage(null);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        //will be moved to another class for convenience
        int textureWidth = 15;
        int textureHeight = 46;
        if (energyStorage != null) {
            int energyAmount = energyStorage.getEnergyStored();
            int energyTotalAmount = energyStorage.getMaxEnergyStored();
            int i = Mth.ceil(getProgress(energyAmount, energyTotalAmount) * (textureHeight - 1));
            guiGraphics.blitSprite(ENERGY_SPRITE, textureWidth, textureHeight, 0, textureHeight - i, this.leftPos + 126, this.topPos + 19 + textureHeight - i, textureWidth, i);
        }

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        if (x >= this.leftPos + 126 && y >= this.topPos + 19 ) {
            if(x <= this.leftPos + 126 + width && y <= this.topPos + 19 + this.height) {
                guiGraphics.renderComponentTooltip(
                        font, List.of(Component.literal(
                                energyStorage.getEnergyStored() + " / " + energyStorage.getMaxEnergyStored() + "FE"))
                        , x, y);
            }
        }
    }

    float getProgress(int amount, int capacity) {
        return Mth.clamp((float)amount / (float)capacity, 0.0F, 1.0F);
    }

}
