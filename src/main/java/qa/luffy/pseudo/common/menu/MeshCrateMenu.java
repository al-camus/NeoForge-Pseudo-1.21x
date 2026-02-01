// qa.luffy.pseudo.common.menu.MeshCrateMenu
package qa.luffy.pseudo.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.entity.MeshCrateBlockEntity;
import qa.luffy.pseudo.common.item.PocketCrafterItem;
import qa.luffy.pseudo.common.item.ToolboxItem;

public class MeshCrateMenu extends AbstractContainerMenu {

    private final MeshCrateBlockEntity crate;

    private static final int CRATE_ROWS = 9;
    private static final int CRATE_COLUMNS = 9;
    private static final int CRATE_SLOT_COUNT = CRATE_ROWS * CRATE_COLUMNS;

    // exact GOLD shulker GUI size
    public static final int GUI_WIDTH = 256;
    public static final int GUI_HEIGHT = 276;

    public MeshCrateMenu(int containerId, Inventory playerInv, MeshCrateBlockEntity crate) {
        super(PseudoMenus.MESH_CRATE_MENU_TYPE.get(), containerId);
        this.crate = crate;

        // --- Crate inventory (9x9), same as GOLD shulker: x=12+col*18, y=18+row*18
        int index = 0;
        for (int row = 0; row < CRATE_ROWS; ++row) {
            for (int col = 0; col < CRATE_COLUMNS; ++col) {
                int x = 12 + col * 18;
                int y = 18 + row * 18;
                this.addSlot(new SlotItemHandler(crate.getItems(), index++, x, y));
            }
        }

        // --- Player inventory + hotbar ---

        // ALIGN with crate grid horizontally: same X start as crate (12)
        int leftCol = 12;

        // player inventory (3x9)
        for (int playerInvRow = 0; playerInvRow < 3; ++playerInvRow) {
            for (int playerInvCol = 0; playerInvCol < 9; ++playerInvCol) {
                int x = leftCol + playerInvCol * 18;
                int y = GUI_HEIGHT - (4 - playerInvRow) * 18 - 10;
                this.addSlot(new Slot(
                        playerInv,
                        playerInvCol + playerInvRow * 9 + 9,
                        x, y
                ));
            }
        }

        // hotbar (1x9)
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            int x = leftCol + hotbarSlot * 18;
            int y = GUI_HEIGHT - 24;
            this.addSlot(new Slot(playerInv, hotbarSlot, x, y));
        }
    }

    // client-side ctor
    public MeshCrateMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, getCrateFromBuf(playerInv.player.level(), buf));
    }

    private static MeshCrateBlockEntity getCrateFromBuf(Level level, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MeshCrateBlockEntity crate)) {
            throw new IllegalStateException("MeshCrateMenu: expected MeshCrateBlockEntity at " + pos);
        }
        return crate;
    }


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

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        // Prevent interacting with *any* container item (crafter, toolbox, mesh crate, shulker)
        // while THIS menu is open.
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                if (isBlockedContainerItem(stack)) {
                    // Completely ignore the click for these items
                    return;
                }
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        if ((player.level() != crate.getLevel()) || crate.isRemoved()) {
            return false;
        }
        return player.distanceToSqr(
                crate.getBlockPos().getX() + 0.5,
                crate.getBlockPos().getY() + 0.5,
                crate.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            original = stackInSlot.copy();

            int crateEnd = CRATE_SLOT_COUNT; // 0..80 = crate

            if (index < crateEnd) {
                // crate -> player
                if (!this.moveItemStackTo(stackInSlot, crateEnd, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // player -> crate
                if (!this.moveItemStackTo(stackInSlot, 0, crateEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return original;
    }
}
