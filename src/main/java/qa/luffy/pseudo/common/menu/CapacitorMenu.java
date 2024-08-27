package qa.luffy.pseudo.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CapacitorMenu extends AbstractContainerMenu {

    private final Container inventory;

    public CapacitorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2));
    }

    public CapacitorMenu(int containerId, Inventory playerInventory, Container container) {
        super(PseudoMenus.CAPACITOR_MENU_TYPE.get(), containerId);
        this.inventory = container;

        this.addSlot(new Slot(inventory, 0, 50 , 50)); //TODO change slot placement according to screen texture
        this.addSlot(new Slot(inventory, 1, 60 , 60)); //TODO change slot placement according to screen texture

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    protected Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }


    //Taken from Stellaris CC BY-NC-SA 4.0
    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(quickMovedSlotIndex);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (quickMovedSlotIndex < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
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
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (84 + i * 18) + 58));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 200));
        }
    }
}
