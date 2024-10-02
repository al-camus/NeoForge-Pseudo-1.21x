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
    private final Direction4 direction;

    public GaugeWidget(int x, int y, int width, int height, int capacity, Direction4 direction, ResourceLocation sprite) {
        super(x, y, width, height, Component.empty());
        this.SPRITE = sprite;
        this.capacity = capacity;
        this.direction = direction;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        switch (direction) { //TODO finish all directions
            case DOWN_UP -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, getHeight() - i, getX(), getY() + getHeight() - i, getWidth(), i);
            }
            case UP_DOWN -> {
                //int i = (int)(28.0F * (1.0F - getProgress(amount, capacity)));
                //guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, 0, getX(), getY(), getWidth(), i);

                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, 0, getX(), getY(), getWidth(), i);
            }
            case LEFT_RIGHT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, 0, getX(), getY(), i, getHeight());
                //guiGraphics.blitSprite(SPRITE, getX(), getY(), i, getHeight());
            }
            case RIGHT_LEFT -> { //make
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), getWidth() -i, 0, getX() + getWidth() -i, getY(), i, getHeight());
            }
        }
//        int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
//        guiGraphics.blitSprite(SPRITE, getWidth(), getHeight(), 0, getHeight() - i, getX(), getY() + getHeight() - i, getWidth(), i);
    }

    public void renderTooltip(String unit, GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        if (mouseX >= getX() && mouseX <= getX() + width && mouseY <= getY() + height && mouseY >= getY()) {
            guiGraphics.renderComponentTooltip(
                    font, List.of(
                            Component.literal(amount + " §e/§r " + capacity + " §3" + unit +"§r")),
                    mouseX, mouseY);
        }
    }

    public void updateAmount(int value) {
        this.amount = Math.clamp(value, 0, capacity);
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
        this.amount = Math.min(this.amount, capacity);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    private float getProgress(int amount, int capacity) {
        return Mth.clamp((float)amount / (float)capacity, 0.0F, 1.0F);
    }

    public enum Direction4 {
        DOWN_UP,
        UP_DOWN,
        LEFT_RIGHT,
        RIGHT_LEFT
    }
}
