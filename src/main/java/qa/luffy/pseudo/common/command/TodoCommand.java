package qa.luffy.pseudo.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;
import qa.luffy.pseudo.common.network.payload.TodoOpenClipboardPayload;

public final class TodoCommand {
    private TodoCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("todo")
                .executes(ctx -> execute(ctx.getSource())));
    }

    private static int execute(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        Item clipboardItem = PseudoBlocks.CLIPBOARD_BLOCK.get().asItem();

        // 1) Main hand first
        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty() && main.is(clipboardItem)) {
            ClipboardContent content = main.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
            TodoOpenClipboardPayload.sendForHand(player, InteractionHand.MAIN_HAND, content);
            return 1;
        }

        // 2) Offhand second
        ItemStack off = player.getOffhandItem();
        if (!off.isEmpty() && off.is(clipboardItem)) {
            ClipboardContent content = off.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
            TodoOpenClipboardPayload.sendForHand(player, InteractionHand.OFF_HAND, content);
            return 1;
        }

        // 3) Hotbar next (0..8)
        int slot = findClipboardInRange(player, clipboardItem, 0, 8);
        if (slot >= 0) {
            openSlot(player, slot);
            return 1;
        }

        // 4) Main inventory last (9..end)
        slot = findClipboardInRange(player, clipboardItem, 9, player.getInventory().items.size() - 1);
        if (slot >= 0) {
            openSlot(player, slot);
            return 1;
        }

        source.sendFailure(Component.literal("No clipboard found in your inventory."));
        return 0;
    }

    private static int findClipboardInRange(ServerPlayer player, Item clipboardItem, int start, int endInclusive) {
        if (start > endInclusive) return -1;

        for (int i = start; i <= endInclusive; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(clipboardItem)) {
                return i;
            }
        }
        return -1;
    }

    private static void openSlot(ServerPlayer player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        ClipboardContent content = stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT);
        TodoOpenClipboardPayload.sendForSlot(player, slot, content);
    }
}
