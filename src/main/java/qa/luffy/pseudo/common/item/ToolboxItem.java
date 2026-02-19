package qa.luffy.pseudo.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.menu.ToolboxMenu;
import qa.luffy.pseudo.common.util.ContainerItemGuards;

public class ToolboxItem extends BlockItem {

    public static final int SLOTS = 9;

    public ToolboxItem(Block block, Properties props) {
        super(block, props);
    }

    public static ItemStackHandler getInventory(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);

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
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < SLOTS; i++) {
            items.set(i, handler.getStackInSlot(i));
        }
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    private static NonNullList<ItemStack> readContents(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(items);
        return items;
    }

    private static void writeContents(ItemStack stack, NonNullList<ItemStack> items) {
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp) {
            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInv, p) -> new ToolboxMenu(containerId, playerInv, hand, stack),
                    Component.translatable("container.pseudo.toolbox")
            );

            sp.openMenu(provider, buf -> {
                buf.writeByte(ToolboxMenu.MODE_ITEM);
                buf.writeBoolean(hand == InteractionHand.OFF_HAND);
            });
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void swapWithHotbar(Player player, InteractionHand hand, ItemStack toolboxStack) {
        var inv = player.getInventory();

        NonNullList<ItemStack> oldToolbox = readContents(toolboxStack);
        NonNullList<ItemStack> newToolbox = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

        int[] hotbarIndexForPos = new int[SLOTS];
        boolean[] useOffhandForPos = new boolean[SLOTS];

        for (int i = 0; i < SLOTS; i++) {
            hotbarIndexForPos[i] = -1;
            useOffhandForPos[i] = false;
        }

        if (hand == InteractionHand.OFF_HAND) {
            for (int i = 0; i < SLOTS; i++) {
                hotbarIndexForPos[i] = i;
            }
        } else {
            int toolboxHotbarIndex = inv.selected;

            int pos = 0;
            for (int i = 0; i < 9 && pos < SLOTS; i++) {
                if (i == toolboxHotbarIndex) continue;
                hotbarIndexForPos[pos++] = i;
            }

            if (pos < SLOTS) {
                useOffhandForPos[pos] = true;
            }
        }

        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack internal = oldToolbox.get(slot);
            ItemStack external;

            if (useOffhandForPos[slot]) {
                external = inv.offhand.getFirst();
            } else if (hotbarIndexForPos[slot] >= 0) {
                external = inv.getItem(hotbarIndexForPos[slot]);
            } else {
                external = ItemStack.EMPTY;
            }

            if (ContainerItemGuards.isBlockedContainerItem(internal) || ContainerItemGuards.isBlockedContainerItem(external)) {
                newToolbox.set(slot, internal);
                continue;
            }

            newToolbox.set(slot, external.copy());

            if (useOffhandForPos[slot]) {
                inv.offhand.set(0, internal.copy());
            } else if (hotbarIndexForPos[slot] >= 0) {
                inv.setItem(hotbarIndexForPos[slot], internal.copy());
            }
        }

        writeContents(toolboxStack, newToolbox);
        inv.setChanged();
        player.inventoryMenu.broadcastChanges();
    }
}
