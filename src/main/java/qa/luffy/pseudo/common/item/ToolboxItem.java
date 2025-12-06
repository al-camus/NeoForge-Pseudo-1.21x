// qa/luffy/pseudo/item/ToolboxItem.java
package qa.luffy.pseudo.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import qa.luffy.pseudo.common.menu.ToolboxMenu;

public class ToolboxItem extends Item {

    public static final int SLOTS = 9;

    public ToolboxItem(Properties props) {
        super(props);
    }

    // === INVENTORY STORAGE VIA DATA COMPONENTS ===

    public static ItemStackHandler getInventory(ItemStack stack) {
        // 1) Read current contents from the built-in CONTAINER component
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        // 2) Copy into a temporary list with exactly SLOTS entries
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);

        // 3) Build a handler backed by that list, and auto-save on change
        ItemStackHandler handler = new ItemStackHandler(SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                saveInventory(stack, this);
            }
        };

        for (int i = 0; i < SLOTS; i++) {
            handler.setStackInSlot(i, items.get(i));
        }

        return handler;
    }

    public static void saveInventory(ItemStack stack, ItemStackHandler handler) {
        // Copy handler contents into a list
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < SLOTS; i++) {
            items.set(i, handler.getStackInSlot(i));
        }

        // Write back into the built-in CONTAINER component
        ItemContainerContents contents = ItemContainerContents.fromItems(items);
        stack.set(DataComponents.CONTAINER, contents);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInv, p) -> new ToolboxMenu(containerId, playerInv),
                    Component.translatable("container.pseudo.toolbox")
            );

            // Vanilla / NeoForge way, no NetworkHooks needed
            player.openMenu(provider);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
