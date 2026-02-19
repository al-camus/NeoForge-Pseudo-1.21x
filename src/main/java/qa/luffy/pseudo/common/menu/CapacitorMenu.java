package qa.luffy.pseudo.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
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
import qa.luffy.pseudo.common.block.entity.CapacitorBlockEntity;
import qa.luffy.pseudo.common.item.PocketCrafterItem;
import qa.luffy.pseudo.common.item.ToolboxItem;
import qa.luffy.pseudo.common.menu.slot.EnergyResultSlot;

public class CapacitorMenu extends AbstractContainerMenu {

    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int BE_SLOTS = 2;

    public final CapacitorBlockEntity entity;
    private final Level level;
    private final ContainerData data;

    public CapacitorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                playerInventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(4));
    }

    public CapacitorMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(PseudoMenus.CAPACITOR_MENU_TYPE.get(), containerId);

        if (!(entity instanceof CapacitorBlockEntity capacitor)) {
            throw new IllegalStateException("CapacitorMenu opened with non-capacitor block entity: " + entity);
        }

        this.entity = capacitor;
        this.level = playerInventory.player.level();
        this.data = data;

        // BE slots FIRST so quick-move logic is sane.
        this.addSlot(new SlotItemHandler(this.entity.getItemHandler(null), SLOT_INPUT, 37, 21) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return !isBlockedContainerItem(stack);
            }
        });
        this.addSlot(new EnergyResultSlot(this.entity.getItemHandler(null), SLOT_OUTPUT, 37, 47));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addDataSlots(this.data);
    }

    public CapacitorBlockEntity getBlockEntity() {
        return entity;
    }

    public int getProgressAmount() {
        return data.get(0);
    }

    public int getProgressCapacity() {
        return data.get(1);
    }

    public int getEnergyStored() {
        int lo = data.get(2) & 0xFFFF;
        int hi = data.get(3) & 0xFFFF;
        return (hi << 16) | lo;
    }

    public int getEnergyCapacity() {
        return CapacitorBlockEntity.ENERGY_CAPACITY;
    }

    private static boolean isBlockedContainerItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();

        switch (item) {
            case PocketCrafterItem pocketCrafterItem -> {
                return true;
            }
            case ToolboxItem toolboxItem -> {
                return true;
            }
            case BlockItem blockItem -> {
                if (blockItem.getBlock() == PseudoBlocks.MESH_CRATE.get()) return true;
                if (blockItem.getBlock() instanceof ShulkerBoxBlock) return true;
            }
            default -> {
            }
        }

        return false;
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.hasItem() && isBlockedContainerItem(slot.getItem())) return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, entity.getBlockPos()), player, PseudoBlocks.CAPACITOR_BLOCK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack ret = stack.copy();

        // BE slots -> player inventory/hotbar
        if (index < BE_SLOTS) {
            if (!this.moveItemStackTo(stack, BE_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, ret);
        }
        // player inventory/hotbar -> BE input only
        else {
            if (isBlockedContainerItem(stack)) return ItemStack.EMPTY;

            if (!this.moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return ret;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int idx = col + row * 9 + 9;
                this.addSlot(new Slot(playerInventory, idx, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
}
