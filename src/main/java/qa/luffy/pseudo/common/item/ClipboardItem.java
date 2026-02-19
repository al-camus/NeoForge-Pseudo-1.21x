package qa.luffy.pseudo.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;

import java.lang.reflect.Method;
import java.util.Objects;

public class ClipboardItem extends BlockItem {
    public ClipboardItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            openClientScreen(stack, hand);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        return context.getPlayer() != null && context.getPlayer().isSecondaryUseActive()
                ? super.useOn(context)
                : InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) return;
        if (!(entity instanceof Player)) return;

        // If renamed in an anvil (or any other way), CUSTOM_NAME wins and updates the clipboard title.
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            pushCustomNameIntoTitle(stack);
            return;
        }

        // Otherwise, if the clipboard title is non-blank, ensure the item is named accordingly.
        ClipboardContent content = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
        applyTitleToStackName(stack, content);
    }

    /**
     * Title -> item name rule (authoritative when content changes from the GUI):
     * - blank/whitespace title: remove CUSTOM_NAME so vanilla name ("Mesh Clipboard") shows
     * - non-blank title: set CUSTOM_NAME to that title
     */
    public static void applyTitleToStackName(ItemStack stack, ClipboardContent content) {
        String title = normalizeTitle(content == null ? "" : content.title());
        if (title.isEmpty()) {
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                stack.remove(DataComponents.CUSTOM_NAME);
            }
        } else {
            Component desired = Component.literal(title);
            Component current = stack.get(DataComponents.CUSTOM_NAME);
            if (current == null || !Objects.equals(current.getString(), desired.getString())) {
                stack.set(DataComponents.CUSTOM_NAME, desired);
            }
        }
    }

    /**
     * Custom name -> title rule (authoritative when renamed externally, e.g. anvil).
     * Keeps only the clipboard title in sync; leaves pages/checkboxes intact.
     */
    public static void pushCustomNameIntoTitle(ItemStack stack) {
        Component custom = stack.get(DataComponents.CUSTOM_NAME);
        ClipboardContent content = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);

        String desiredTitle = normalizeTitle(custom == null ? "" : custom.getString());

        // If the item has no custom name anymore, clear the title so it falls back to default behavior.
        if (custom == null || desiredTitle.isEmpty()) {
            if (!normalizeTitle(content.title()).isEmpty()) {
                stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), content.setTitle(""));
            }
            return;
        }

        // If renamed, update stored title to match.
        if (!Objects.equals(normalizeTitle(content.title()), desiredTitle)) {
            stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), content.setTitle(desiredTitle));
        }
    }

    private static String normalizeTitle(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }

    private static void openClientScreen(ItemStack stack, InteractionHand hand) {
        try {
            Class<?> hooks = Class.forName("qa.luffy.pseudo.client.screen.hooks.ClipboardHook");
            Method m = hooks.getMethod("openClipboardScreen", ItemStack.class, InteractionHand.class);
            m.invoke(null, stack, hand);
        } catch (Throwable ignored) {
        }
    }
}
