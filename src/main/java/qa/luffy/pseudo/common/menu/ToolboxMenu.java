package qa.luffy.pseudo.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.entity.ToolboxBlockEntity;
import qa.luffy.pseudo.common.item.ToolboxItem;
import qa.luffy.pseudo.common.util.ContainerItemGuards;

public class ToolboxMenu extends AbstractContainerMenu {

    public static final byte MODE_ITEM = 0;
    public static final byte MODE_BLOCK = 1;

    private final IItemHandler handler;
    private final ContainerLevelAccess access;
    private final boolean isBlock;

    // Client ctor (called by IMenuTypeExtension factory)
    public ToolboxMenu(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        super(PseudoMenus.TOOLBOX_MENU_TYPE.get(), windowId);

        byte mode = data.readByte();
        this.isBlock = (mode == MODE_BLOCK);

        if (this.isBlock) {
            BlockPos ignored = data.readBlockPos();
            this.access = ContainerLevelAccess.NULL;
            this.handler = new ItemStackHandler(ToolboxItem.SLOTS);
        } else {
            boolean offhand = data.readBoolean();
            this.access = ContainerLevelAccess.NULL;
            this.handler = clientReadOnlyHandlerFromHeld(playerInv, offhand);
        }

        addSlots(playerInv);
    }

    // Server ctor: item-mode
    public ToolboxMenu(int windowId, Inventory playerInv, InteractionHand hand, ItemStack toolboxStack) {
        super(PseudoMenus.TOOLBOX_MENU_TYPE.get(), windowId);
        this.isBlock = false;
        this.access = ContainerLevelAccess.NULL;
        this.handler = ToolboxItem.getInventory(toolboxStack);
        addSlots(playerInv);
    }

    // Server ctor: block-mode
    public ToolboxMenu(int windowId, Inventory playerInv, ToolboxBlockEntity be, ContainerLevelAccess access) {
        super(PseudoMenus.TOOLBOX_MENU_TYPE.get(), windowId);
        this.isBlock = true;
        this.access = access;
        this.handler = be.getItems();
        addSlots(playerInv);
    }

    private void addSlots(Inventory playerInv) {
        // Toolbox 3x3
        int startX = 62;
        int startY = 17;
        int slot = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(handler, slot++, startX + col * 18, startY + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return !ContainerItemGuards.isBlockedContainerItem(stack);
                    }
                });
            }
        }

        // Player inventory (3 rows)
        int invStartY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, invStartY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = invStartY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (isBlock) {
            return stillValid(access, player, PseudoBlocks.TOOLBOX_BLOCK.get());
        }
        return player.isAlive() && !findToolbox(player.getInventory()).isEmpty();
    }

    private static ItemStack findToolbox(Inventory inv) {
        for (ItemStack s : inv.items) {
            if (s.getItem() instanceof ToolboxItem) return s;
        }
        for (ItemStack s : inv.offhand) {
            if (s.getItem() instanceof ToolboxItem) return s;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack ret = stack.copy();

        int toolboxSlots = ToolboxItem.SLOTS;
        int playerEnd = toolboxSlots + 36;

        if (index < toolboxSlots) {
            // toolbox -> player
            if (!this.moveItemStackTo(stack, toolboxSlots, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // player -> toolbox
            if (ContainerItemGuards.isBlockedContainerItem(stack)) {
                return ItemStack.EMPTY;
            }
            if (!this.moveItemStackTo(stack, 0, toolboxSlots, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return ret;
    }

    // Client-only helper: prefill UI from held toolbox contents (read-only; no persistence)
    private static IItemHandler clientReadOnlyHandlerFromHeld(Inventory inv, boolean offhand) {
        ItemStack stack = offhand ? inv.player.getOffhandItem() : inv.player.getMainHandItem();

        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> list = NonNullList.withSize(ToolboxItem.SLOTS, ItemStack.EMPTY);
        contents.copyInto(list);

        ItemStackHandler handler = new ItemStackHandler(ToolboxItem.SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                // client prediction only; server is authoritative
            }
        };

        for (int i = 0; i < ToolboxItem.SLOTS; i++) {
            handler.setStackInSlot(i, list.get(i));
        }
        return handler;
    }
}
