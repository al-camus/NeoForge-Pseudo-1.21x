package qa.luffy.pseudo.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import qa.luffy.pseudo.client.screen.widgets.SpriteButton;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.CheckboxState;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;
import qa.luffy.pseudo.common.network.payload.ClipboardSyncPayload;
import qa.luffy.pseudo.common.network.payload.TodoSyncClipboardPayload;

import java.util.ArrayList;
import java.util.List;

public class ClipboardScreen extends Screen {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/clipboard.png");

    private static final ResourceLocation CHECK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/sprites/check.png");

    private static final ResourceLocation X_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/sprites/x.png");

    private static final int BG_W = 192;
    private static final int BG_H = 192;

    private final ItemStack stack;

    @Nullable
    private final InteractionHand hand; // when opened from hand

    private final int slot; // when opened from inventory slot, else -1
    private final boolean openedFromTodo;

    private ClipboardContent data;

    private final CheckboxButton[] checkboxes = new CheckboxButton[ClipboardContent.MAX_LINES];
    private final EditBox[] lines = new EditBox[ClipboardContent.MAX_LINES];

    private EditBox titleBox;
    private PageButton forwardButton;
    private PageButton backButton;

    private int leftPos;
    private int topPos;

    /** Normal open: right-click item in hand. */
    public ClipboardScreen(ItemStack stack, @Nullable InteractionHand hand) {
        super(stack.getHoverName());
        this.stack = stack;
        this.hand = hand;
        this.slot = -1;
        this.openedFromTodo = false;
        this.data = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
    }

    private ClipboardScreen(ItemStack stack, int slot) {
        super(stack.getHoverName());
        this.stack = stack;
        this.hand = null;
        this.slot = slot;
        this.openedFromTodo = true;
        this.data = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
    }

    private ClipboardScreen(ItemStack stack, @Nullable InteractionHand hand, boolean openedFromTodo) {
        super(stack.getHoverName());
        this.stack = stack;
        this.hand = hand;
        this.slot = -1;
        this.openedFromTodo = openedFromTodo;
        this.data = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
    }

    /**
     * Client-only helper (called reflectively by TodoOpenClipboardPayload).
     * slot >= 0 => open that inventory slot
     * slot < 0  => open the provided hand
     */
    public static void openForTodo(int slot, InteractionHand hand, ClipboardContent content) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack;

        if (slot >= 0) {
            if (slot >= mc.player.getInventory().items.size()) return;
            stack = mc.player.getInventory().getItem(slot);
            if (stack.isEmpty() || !stack.is(PseudoBlocks.CLIPBOARD_BLOCK.get().asItem())) return;
            stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), content);
            mc.setScreen(new ClipboardScreen(stack, slot));
            return;
        }

        // slot < 0 => use hand (including OFF_HAND)
        stack = mc.player.getItemInHand(hand);
        if (stack.isEmpty() || !stack.is(PseudoBlocks.CLIPBOARD_BLOCK.get().asItem())) return;
        stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), content);
        mc.setScreen(new ClipboardScreen(stack, hand, true));
    }

    @Override
    public void onClose() {
        super.onClose();

        stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), data);

        if (openedFromTodo) {
            InteractionHand h = hand == null ? InteractionHand.MAIN_HAND : hand;
            PacketDistributor.sendToServer(new TodoSyncClipboardPayload(slot, h, data));
            return;
        }

        if (hand != null) {
            PacketDistributor.sendToServer(new ClipboardSyncPayload(data, hand));
        }
    }

    @Override
    protected void init() {
        this.leftPos = (width - BG_W) / 2;

        int extraY = height - BG_H;
        this.topPos = Math.max(0, extraY / 4);

        titleBox = addRenderableWidget(new EditBox(getMinecraft().font, leftPos + 57, topPos + 12, 72, 8, Component.empty()));
        titleBox.setTextColor(0);
        titleBox.setBordered(false);
        titleBox.setTextShadow(false);
        titleBox.setResponder(e -> data = data.setTitle(e));

        for (int i = 0; i < ClipboardContent.MAX_LINES; i++) {
            final int j = i;

            checkboxes[i] = addRenderableWidget(new CheckboxButton(leftPos + 30, topPos + (15 * i + 24), e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<CheckboxState> cb = new ArrayList<>(page.checkboxes());
                cb.set(j, ((CheckboxButton) e).getState());
                pages.set(data.active(), page.setCheckboxes(cb));
                data = data.setPages(pages);
            }));

            lines[i] = addRenderableWidget(new EditBox(getMinecraft().font, leftPos + 45, topPos + (15 * i + 26), 109, 8, Component.empty()));
            lines[i].setTextColor(0);
            lines[i].setBordered(false);
            lines[i].setTextShadow(false);
            lines[i].setResponder(e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<String> ls = new ArrayList<>(page.lines());
                ls.set(j, e);
                pages.set(data.active(), page.setLines(ls));
                data = data.setPages(pages);
            });
        }

        forwardButton = addRenderableWidget(new PageButton(leftPos + 116, topPos + 157, true, $ -> {
            data = data.nextPage();
            updateContents();
        }, false));

        backButton = addRenderableWidget(new PageButton(leftPos + 43, topPos + 157, false, $ -> {
            data = data.prevPage();
            updateContents();
        }, false));

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $ -> onClose())
                .bounds(width / 2 - 100, topPos + BG_H + 4, 200, 20)
                .build());

        updateContents();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // Placeholder: when title is blank/default, still show the item's default name visually.
        if (titleBox != null && !titleBox.isFocused() && titleBox.getValue().trim().isEmpty()) {
            Component hint = Component.translatable(stack.getItem().getDescriptionId());
            graphics.drawString(this.font, hint, leftPos + 57, topPos + 12, 0x7F7F7F, false);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, BG_W, BG_H);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0 && forwardButton.visible) {
            forwardButton.onPress();
            return true;
        }
        if (scrollY > 0 && backButton.visible) {
            backButton.onPress();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        return switch (keyCode) {
            case GLFW.GLFW_KEY_PAGE_UP -> {
                backButton.onPress();
                yield true;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN -> {
                forwardButton.onPress();
                yield true;
            }
            default -> false;
        };
    }

    private void updateContents() {
        backButton.visible = data.active() > 0;

        titleBox.setValue(data.title());

        ClipboardContent.Page page = data.pages().get(data.active());
        for (int i = 0; i < checkboxes.length; i++) {
            checkboxes[i].setState(page.checkboxes().get(i));
            lines[i].setValue(page.lines().get(i));
        }
    }

    private static class CheckboxButton extends SpriteButton {
        private CheckboxState state = CheckboxState.EMPTY;

        public CheckboxButton(int x, int y, Button.OnPress onPress) {
            super(x, y, 14, 14, onPress);
        }

        public CheckboxState getState() {
            return state;
        }

        public void setState(CheckboxState state) {
            this.state = state;
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {
            state = switch (state) {
                case EMPTY -> CheckboxState.CHECK;
                case CHECK -> CheckboxState.X;
                case X -> CheckboxState.EMPTY;
            };
            super.onClick(mouseX, mouseY, button);
        }

        @Override
        @Nullable
        protected ResourceLocation getSprite() {
            return switch (state) {
                case EMPTY -> null;
                case CHECK -> CHECK_TEXTURE;
                case X -> X_TEXTURE;
            };
        }
    }
}
