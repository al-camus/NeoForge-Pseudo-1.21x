package qa.luffy.pseudo.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CapacitorMenu extends AbstractContainerMenu {

//    public CapacitorMenu(int containerId, Inventory playerInventory) { //Client
//        this(containerId, playerInventory);
//    }

    public CapacitorMenu(int containerId, Inventory playerInventory) { //Server
        super(PseudoMenus.CAPACITOR_MENU_TYPE.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    protected Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }
}
