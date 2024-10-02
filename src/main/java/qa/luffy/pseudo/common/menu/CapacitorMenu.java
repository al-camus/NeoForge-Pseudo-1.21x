package qa.luffy.pseudo.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.entity.CapacitorBlockEntity;
import qa.luffy.pseudo.common.networking.packet.EnergyData;

public class CapacitorMenu extends AbstractContainerMenu {

    public final CapacitorBlockEntity entity;
    private final Level level;
    private final ContainerData data;

    public CapacitorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
    }

    public CapacitorMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(PseudoMenus.CAPACITOR_MENU_TYPE.get(), containerId);
        this.entity = ((CapacitorBlockEntity) entity);
        this.level = playerInventory.player.level();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.addSlot(new SlotItemHandler(this.entity.getItemHandler(null), 0, 37, 21));
        this.addSlot(new SlotItemHandler(this.entity.getItemHandler(null), 1, 37 , 47));

        addDataSlots(data);
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

    @Override
    public boolean stillValid(Player player) {
        if (!player.isLocalPlayer()) PacketDistributor.sendToAllPlayers(new EnergyData(entity.getEnergyStorage(null).getEnergyStored(), entity.getBlockPos()));
        return stillValid(ContainerLevelAccess.create(level, entity.getBlockPos()), player, PseudoBlocks.CAPACITOR_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(quickMovedSlotIndex);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (quickMovedSlotIndex < this.entity.getItemHandler(null).getSlots()) {
                if (!this.moveItemStackTo(originalStack, this.entity.getItemHandler(null).getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.entity.getItemHandler(null).getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (84 + i * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
