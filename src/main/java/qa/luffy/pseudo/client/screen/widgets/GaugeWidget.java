package qa.luffy.pseudo.client.screen.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

public class GaugeWidget extends AbstractWidget {

    private int capacity;
    private int amount = 0;

    private final ResourceLocation SPRITE;

    public GaugeWidget(int x, int y, int width, int height, int capacity, ResourceLocation sprite) {
        super(x, y, width, height, Component.empty());
        this.SPRITE = sprite;
        this.capacity = capacity;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
        guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, getHeight() - i, getX(), getY() + getHeight() - i, getWidth(), i);
    }

    public void renderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        if (mouseX >= getX() && mouseX <= getX() + width && mouseY <= getY() + height && mouseY >= getY()) {
            guiGraphics.renderComponentTooltip(
                    font, List.of(
                            Component.literal(amount + " / " + capacity + "FE")),
                    mouseX, mouseY);
        }
    }

    public void updateAmount(int value) {
        this.amount = value;
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    private float getProgress(int amount, int capacity) {
        return Mth.clamp((float)amount / (float)capacity, 0.0F, 1.0F);
    }
}
