package qa.luffy.pseudo.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.menu.ToolboxMenu;

public class ToolboxItem extends Item {

    public static final int SLOTS = 9;

    public ToolboxItem(Properties props) {
        super(props);
    }

    // === INVENTORY STORAGE VIA DATA COMPONENTS ===

    public static ItemStackHandler getInventory(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);

        ItemStackHandler handler = new ItemStackHandler(SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                saveInventory(stack, this);
            }
        };

        for (int i = 0; i < SLOTS; i++) {
            handler.setStackInSlot(i, items.get(i));
        }

        return handler;
    }

    public static void saveInventory(ItemStack stack, ItemStackHandler handler) {
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < SLOTS; i++) {
            items.set(i, handler.getStackInSlot(i));
        }

        ItemContainerContents contents = ItemContainerContents.fromItems(items);
        stack.set(DataComponents.CONTAINER, contents);
    }

    // Convenience for working directly with NonNullList instead of handlers
    private static NonNullList<ItemStack> readContents(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);
        return items;
    }

    private static void writeContents(ItemStack stack, NonNullList<ItemStack> items) {
        ItemContainerContents contents = ItemContainerContents.fromItems(items);
        stack.set(DataComponents.CONTAINER, contents);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                // Sneak-right-click: open the 3x3 toolbox GUI
                MenuProvider provider = new SimpleMenuProvider(
                        (containerId, playerInv, p) -> new ToolboxMenu(containerId, playerInv),
                        Component.translatable("container.pseudo.toolbox")
                );
                player.openMenu(provider);
            } else {
                // Normal right-click: hotbar <-> toolbox swap logic
                swapWithHotbar(player, hand, stack);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    // === SWAP LOGIC ===

    /**
     * Swap up to 9 slots between the toolbox and the player's hotbar, using offhand as
     * a single "overflow" / extra mapped slot when the toolbox is in the hotbar.
     * Rules:
     * - Each toolbox slot maps 1:1 to an external slot (hotbar or offhand).
     * - Blocked container items (toolboxes, pocket crafters, mesh crates, shulkers) are never moved.
     * - The active toolbox stack itself is never moved.
     * - If the toolbox is in offhand: map toolbox[0..8] <-> hotbar[0..8].
     * - If the toolbox is in hotbar slot H: map 8 hotbar slots != H, plus the offhand.
     */
    private static void swapWithHotbar(Player player, InteractionHand hand, ItemStack toolboxStack) {
        var inv = player.getInventory();

        // Read current toolbox contents as a fixed-size list.
        NonNullList<ItemStack> oldToolbox = readContents(toolboxStack);
        NonNullList<ItemStack> newToolbox = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

        // For each toolbox index we store where its mapped external slot is.
        int[] hotbarIndexForPos = new int[SLOTS];   // -1 if not a hotbar slot
        boolean[] useOffhandForPos = new boolean[SLOTS];

        // Initialize with "unused" sentinel
        for (int i = 0; i < SLOTS; i++) {
            hotbarIndexForPos[i] = -1;
            useOffhandForPos[i] = false;
        }

        if (hand == InteractionHand.OFF_HAND) {
            // Toolbox in offhand: straightforward 9:9 swap with hotbar
            for (int i = 0; i < SLOTS; i++) {
                hotbarIndexForPos[i] = i;
                useOffhandForPos[i] = false;
            }
        } else {
            // Toolbox in main hand: skip its own hotbar slot, use offhand as the 9th external slot.
            int toolboxHotbarIndex = inv.selected; // 0..8

            int pos = 0;
            for (int i = 0; i < 9 && pos < SLOTS; i++) {
                if (i == toolboxHotbarIndex) {
                    continue; // never map the active toolbox slot
                }
                hotbarIndexForPos[pos] = i;
                useOffhandForPos[pos] = false;
                pos++;
            }

            // Use offhand as the last mapped external slot (overflow).
            if (pos < SLOTS) {
                hotbarIndexForPos[pos] = -1;
                useOffhandForPos[pos] = true;
                pos++;
            }
        }

        // Perform per-slot swaps, respecting blocked container items.
        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack internal = oldToolbox.get(slot);
            ItemStack external;

            if (useOffhandForPos[slot]) {
                external = inv.offhand.getFirst();
            } else if (hotbarIndexForPos[slot] >= 0) {
                external = inv.getItem(hotbarIndexForPos[slot]);
            } else {
                external = ItemStack.EMPTY;
            }

            // Never move blocked container items (either side).
            if (isBlockedContainerItem(internal) || isBlockedContainerItem(external)) {
                // Leave both as they are.
                newToolbox.set(slot, internal);
                if (useOffhandForPos[slot]) {
                    inv.offhand.set(0, external);
                } else if (hotbarIndexForPos[slot] >= 0) {
                    inv.setItem(hotbarIndexForPos[slot], external);
                }
                continue;
            }

            // Normal swap: toolbox <-> external slot
            newToolbox.set(slot, external.copy());
            if (useOffhandForPos[slot]) {
                inv.offhand.set(0, internal.copy());
            } else if (hotbarIndexForPos[slot] >= 0) {
                inv.setItem(hotbarIndexForPos[slot], internal.copy());
            }
        }

        // Write back toolbox contents and flag inventory as changed
        writeContents(toolboxStack, newToolbox);
        inv.setChanged();
        player.inventoryMenu.broadcastChanges();
    }

    // === CONTAINER ITEM BLOCK LIST ===

    /**
     * Matches the same "blocked container" rules you showed for menus.
     * These stacks are never moved during swaps.
     */
    private static boolean isBlockedContainerItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        // Your container items
        switch (item) {
            case PocketCrafterItem pocketCrafterItem -> {
                return true;
            }
            case ToolboxItem toolboxItem -> {
                return true;
            }

            // Mesh Crate block item and all shulker boxes
            case BlockItem blockItem -> {
                if (blockItem.getBlock() == PseudoBlocks.MESH_CRATE.get()) {
                    return true;
                }
                if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                    return true;
                }
            }
            default -> {
            }
        }

        return false;
    }
}
