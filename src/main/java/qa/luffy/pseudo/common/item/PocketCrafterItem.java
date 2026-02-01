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
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.menu.PocketCrafterMenu;

public class PocketCrafterItem extends Item {

    // 3×3 crafting grid only
    public static final int SLOTS = 9;

    public PocketCrafterItem(Properties props) {
        super(props);
    }

    public static NonNullList<ItemStack> getStoredItems(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);
        return items;
    }

    public static void setStoredItems(ItemStack stack, NonNullList<ItemStack> items) {
        // Expect items.size() == SLOTS
        ItemContainerContents contents = ItemContainerContents.fromItems(items);
        stack.set(DataComponents.CONTAINER, contents);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInv, ply) ->
                            new PocketCrafterMenu(containerId, playerInv, stack),
                    Component.translatable("container.pseudo.pocket_crafter")
            );
            player.openMenu(provider);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
