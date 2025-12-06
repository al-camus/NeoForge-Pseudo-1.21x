// qa.luffy.pseudo/common/menu/MeshCrateMenu.java
package qa.luffy.pseudo.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import qa.luffy.pseudo.common.block.entity.MeshCrateBlockEntity;

public class MeshCrateMenu extends AbstractContainerMenu {

    private final MeshCrateBlockEntity crate;

    private static final int CRATE_ROWS = 9;
    private static final int CRATE_COLUMNS = 9;
    private static final int CRATE_SLOT_COUNT = CRATE_ROWS * CRATE_COLUMNS;

    // Server-side constructor
    public MeshCrateMenu(int containerId, Inventory playerInv, MeshCrateBlockEntity crate) {
        super(PseudoMenus.MESH_CRATE_MENU_TYPE.get(), containerId);
        this.crate = crate;

        // --- Crate slots (9×9) ---
        // Layout: you will design your texture to match this (or tweak these numbers)
        int startX = 8;   // left margin
        int startY = 18;  // top margin

        int index = 0;
        for (int row = 0; row < CRATE_ROWS; ++row) {
            for (int col = 0; col < CRATE_COLUMNS; ++col) {
                int x = startX + col * 18;
                int y = startY + row * 18;
                this.addSlot(new SlotItemHandler(crate.getItems(), index++, x, y));
            }
        }

        // --- Player inventory (3×9) below crate ---
        int playerInvStartY = startY + CRATE_ROWS * 18 + 14; // a bit of spacing
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = playerInvStartY + row * 18;
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, x, y));
            }
        }

        // --- Hotbar (1×9) ---
        int hotbarY = playerInvStartY + 58;
        for (int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            this.addSlot(new Slot(playerInv, col, x, hotbarY));
        }
    }

    // Client-side factory ctor (reads block pos from the network)
    public MeshCrateMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, getCrateFromBuf(playerInv.player.level(), buf));
    }

    private static MeshCrateBlockEntity getCrateFromBuf(Level level, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MeshCrateBlockEntity crate)) {
            throw new IllegalStateException("MeshCrateMenu: block entity is not a MeshCrateBlockEntity");
        }
        return crate;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level() != crate.getLevel() || crate.isRemoved()) {
            return false;
        }
        return player.distanceToSqr(
                crate.getBlockPos().getX() + 0.5,
                crate.getBlockPos().getY() + 0.5,
                crate.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            original = stackInSlot.copy();

            int crateEnd = CRATE_SLOT_COUNT; // 0..80 are crate

            if (index < crateEnd) {
                // From crate -> player
                if (!this.moveItemStackTo(stackInSlot, crateEnd, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player -> crate
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
