package qa.luffy.pseudo.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.item.EnderKnapsackItem;

/**
 * 3x3 ender chest menu for the Ender Knapsack item.
 * Layout is identical to a normal 3-row chest, but we block
 * interaction with the knapsack item itself while this menu is open.
 */
public class EnderKnapsackMenu extends ChestMenu {

    public EnderKnapsackMenu(int containerId, Inventory playerInv, Container enderChest) {
        // Use vanilla 3-row chest menu type so we get the normal ChestScreen
        super(MenuType.GENERIC_9x3, containerId, playerInv, enderChest, 3);
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        // Only lock the EnderKnapsackItem itself; everything else is allowed
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                if (stack.getItem() instanceof EnderKnapsackItem) {
                    // Can't pick up/move the knapsack while its GUI is open
                    return;
                }
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }
}
