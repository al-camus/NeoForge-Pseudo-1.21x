package qa.luffy.pseudo.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.ToolboxBlock;
import qa.luffy.pseudo.common.block.entity.ToolboxBlockEntity;
import qa.luffy.pseudo.common.util.ContainerItemGuards;

public class ToolboxBlockMenu extends AbstractContainerMenu {

    private static final int TOOLBOX_SLOTS = ToolboxBlock.SLOTS; // 9
    private static final int TOOLBOX_START = 0;
    private static final int TOOLBOX_END = TOOLBOX_START + TOOLBOX_SLOTS; // exclusive
    private static final int PLAYER_SLOTS = 36;
    private static final int PLAYER_START = TOOLBOX_END;
    private static final int PLAYER_END = PLAYER_START + PLAYER_SLOTS; // exclusive

    private final ContainerLevelAccess access;
    private final ItemStackHandler handler;
    private final BlockPos pos;

    // SERVER ctor (opened from BE)
    public ToolboxBlockMenu(int containerId, Inventory playerInv, ToolboxBlockEntity be) {
        super(PseudoMenus.TOOLBOX_BLOCK_MENU_TYPE.get(), containerId);
        this.pos = be.getBlockPos();

        Level level = be.getLevel() != null ? be.getLevel() : playerInv.player.level();
        this.access = ContainerLevelAccess.create(level, this.pos);
        this.handler = be.getInventory();

        addToolboxSlots();
        addPlayerSlots(playerInv);
    }

    // CLIENT ctor (opened from buf)
    public ToolboxBlockMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(PseudoMenus.TOOLBOX_BLOCK_MENU_TYPE.get(), containerId);

        Level level = playerInv.player.level();
        this.pos = buf.readBlockPos();

        BlockEntity be = level.getBlockEntity(this.pos);
        if (be instanceof ToolboxBlockEntity toolbox) {
            this.handler = toolbox.getInventory();
        } else {
            this.handler = new ItemStackHandler(TOOLBOX_SLOTS);
        }

        this.access = ContainerLevelAccess.create(level, this.pos);

        addToolboxSlots();
        addPlayerSlots(playerInv);
    }

    private void addToolboxSlots() {
        int startX = 62;
        int startY = 17;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = col + row * 3;
                int x = startX + col * 18;
                int y = startY + row * 18;

                this.addSlot(new SlotItemHandler(handler, idx, x, y) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return !ContainerItemGuards.isBlockedContainerItem(stack);
                    }
                });
            }
        }
    }

    private void addPlayerSlots(Inventory playerInv) {
        int invStartY = 84;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, invStartY + row * 18));
            }
        }

        int hotbarY = 142;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, hotbarY));
        }
    }

    private static boolean isToolboxSlot(int slotId) {
        return slotId >= TOOLBOX_START && slotId < TOOLBOX_END;
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);

            // Don’t allow placing blocked container items INTO toolbox slots
            if (isToolboxSlot(slotId)) {
                ItemStack carried = this.getCarried();

                // If player is trying to insert a blocked item, block it.
                // Exception: if the *slot itself* already contains a blocked item,
                // allow PICKUP with empty cursor so the player can recover it.
                if (!carried.isEmpty() && ContainerItemGuards.isBlockedContainerItem(carried)) {
                    return;
                }

                if (slot.hasItem() && ContainerItemGuards.isBlockedContainerItem(slot.getItem())) {
                    boolean emptyCursor = carried.isEmpty();
                    boolean pickup = clickType == ClickType.PICKUP || clickType == ClickType.PICKUP_ALL;
                    if (!(emptyCursor && pickup)) {
                        return;
                    }
                }
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, PseudoBlocks.TOOLBOX_BLOCK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack ret = stack.copy();

        // Toolbox -> Player
        if (index < TOOLBOX_END) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, ret);
        }
        // Player -> Toolbox
        else {
            if (ContainerItemGuards.isBlockedContainerItem(stack)) return ItemStack.EMPTY;
            if (!moveItemStackTo(stack, TOOLBOX_START, TOOLBOX_END, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return ret;
    }
}
