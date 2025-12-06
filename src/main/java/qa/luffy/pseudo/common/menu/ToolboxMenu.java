// qa/luffy/pseudo/menu/ToolboxMenu.java
package qa.luffy.pseudo.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import qa.luffy.pseudo.common.item.ToolboxItem;

public class ToolboxMenu extends AbstractContainerMenu {

    public static final int TOOLBOX_SLOTS = ToolboxItem.SLOTS;

    public ToolboxMenu(int containerId, Inventory playerInv) {
        super(PseudoMenus.TOOLBOX_MENU_TYPE.get(), containerId);

        ItemStack toolboxStack = findToolbox(playerInv);
        if (toolboxStack.isEmpty() || !(toolboxStack.getItem() instanceof ToolboxItem)) {
            throw new IllegalStateException("ToolboxMenu opened without a toolbox in inventory");
        }

        ItemStackHandler handler = ToolboxItem.getInventory(toolboxStack);

        // 3×3 toolbox
        int startX = 62;
        int startY = 17;
        int index = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new SlotItemHandler(handler, index++,
                        startX + col * 18,
                        startY + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        // No toolboxes inside toolboxes
                        return !(stack.getItem() instanceof ToolboxItem);
                    }
                });
            }
        }

        int playerInvStartY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int slotIndex = col + row * 9 + 9;
                this.addSlot(new Slot(playerInv, slotIndex,
                        8 + col * 18,
                        playerInvStartY + row * 18) {

                    @Override
                    public boolean mayPickup(Player player) {
                        ItemStack stack = this.getItem();
                        // ❌ Don’t allow picking up *any* toolbox while this menu is open
                        if (stack.getItem() instanceof ToolboxItem) {
                            return false;
                        }
                        return super.mayPickup(player);
                    }
                });
            }
        }

        int hotbarY = 142;
        for (int col = 0; col < 9; ++col) {
            int slotIndex = col;
            this.addSlot(new Slot(playerInv, slotIndex,
                    8 + col * 18,
                    hotbarY) {

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack stack = this.getItem();
                    if (stack.getItem() instanceof ToolboxItem) {
                        return false;
                    }
                    return super.mayPickup(player);
                }
            });
        }
    }

    // Find a toolbox in the player's hands (or inventory as fallback)
    private static ItemStack findToolbox(Inventory inv) {
        Player player = inv.player;

        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof ToolboxItem) return main;

        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof ToolboxItem) return off;

        // Fallback: scan whole inventory (if player somehow opened it another way)
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof ToolboxItem) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        // Close the GUI if the player no longer has a toolbox at all
        return player.isAlive() && player.getInventory().items.stream()
                .anyMatch(s -> s.getItem() instanceof ToolboxItem);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            original = stackInSlot.copy();

            int toolboxEnd = TOOLBOX_SLOTS; // 0–8 are toolbox slots

            if (stackInSlot.getItem() instanceof ToolboxItem) {
                return ItemStack.EMPTY;
            }

            if (index < toolboxEnd) {
                // Move from toolbox -> player inventory
                if (!this.moveItemStackTo(stackInSlot, toolboxEnd, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory -> toolbox
                if (!this.moveItemStackTo(stackInSlot, 0, toolboxEnd, false)) {
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
