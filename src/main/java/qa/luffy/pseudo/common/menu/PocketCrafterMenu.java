package qa.luffy.pseudo.common.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PocketCrafterItem;
import qa.luffy.pseudo.common.item.ToolboxItem;

import java.util.Objects;
import java.util.Optional;

public class PocketCrafterMenu extends AbstractContainerMenu {

    private final Player player;
    private final Level level;
    private final ItemStack crafterStack;

    // 3×3 crafting grid (backed logically by the item)
    private final TransientCraftingContainer craftingGrid;
    // Result container for the output slot
    private final ResultContainer resultSlot;

    // Top-left of the 3x3 crafting grid (item area)
    private static final int GRID_X = 26;
    private static final int GRID_Y = 17;
    private static final int SLOT_SPACING = 18;

    // Output slot (item area) – tuned values
    private static final int OUTPUT_X = 134;
    private static final int OUTPUT_Y = 35;

    // Player inventory + hotbar
    private static final int INV_X = 8;
    private static final int INV_Y = 84;
    private static final int HOTBAR_Y = 142;

    public PocketCrafterMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, findPocketCrafter(playerInv.player));
    }

    public PocketCrafterMenu(int containerId, Inventory playerInv, ItemStack stack) {
        super(PseudoMenus.POCKET_CRAFTER_MENU.get(), containerId);
        this.player = playerInv.player;
        this.level = playerInv.player.level();
        this.crafterStack = stack;

        // 3×3 transient grid owned by this menu
        this.craftingGrid = new TransientCraftingContainer(this, 3, 3);
        this.resultSlot = new ResultContainer();

        // Load stored items from the crafter into the 3×3 grid
        NonNullList<ItemStack> stored = PocketCrafterItem.getStoredItems(stack);
        for (int i = 0; i < PocketCrafterItem.SLOTS; i++) {
            this.craftingGrid.setItem(i, stored.get(i));
        }

        // 3×3 crafting grid slots (indices 0..8)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = col + row * 3;
                this.addSlot(new Slot(this.craftingGrid, idx,
                        GRID_X + col * SLOT_SPACING,
                        GRID_Y + row * SLOT_SPACING));
            }
        }

        // Output slot (menu index 9, result container index 0)
        this.addSlot(new ResultSlot(playerInv.player, this.craftingGrid, this.resultSlot, 0,
                OUTPUT_X, OUTPUT_Y));

        // Player inventory (27 slots, indices 10..36)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = col + row * 9 + 9;
                this.addSlot(new Slot(playerInv, idx,
                        INV_X + col * 18,
                        INV_Y + row * 18));
            }
        }

        // Hotbar (9 slots, indices 37..45)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col,
                    INV_X + col * 18,
                    HOTBAR_Y));
        }

        // Compute initial result from loaded grid (server only)
        this.updateResult();
    }

    private static ItemStack findPocketCrafter(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof PocketCrafterItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    // ---------------------------------------------------------------------
    // Crafting result + persistence
    // ---------------------------------------------------------------------

    @Override
    public void slotsChanged(@NotNull Container container) {
        super.slotsChanged(Objects.requireNonNull(container));
        if (container == this.craftingGrid && !this.level.isClientSide) {
            updateResult();
            saveGrid();
        }
    }

    private void updateResult() {
        if (this.level.isClientSide) {
            return;
        }

        CraftingInput input = this.craftingGrid.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipeOpt =
                this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, this.level);

        if (recipeOpt.isPresent()) {
            CraftingRecipe recipe = recipeOpt.get().value();
            ItemStack result = recipe.assemble(input, this.level.registryAccess());
            this.resultSlot.setItem(0, result);
        } else {
            this.resultSlot.setItem(0, ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    private void saveGrid() {
        if (this.level.isClientSide) {
            return; // save only on server
        }
        if (!(this.crafterStack.getItem() instanceof PocketCrafterItem)) {
            return;
        }

        NonNullList<ItemStack> items =
                NonNullList.withSize(PocketCrafterItem.SLOTS, ItemStack.EMPTY);

        for (int i = 0; i < PocketCrafterItem.SLOTS; i++) {
            items.set(i, this.craftingGrid.getItem(i).copy());
        }

        PocketCrafterItem.setStoredItems(this.crafterStack, items);
    }

    // ---------------------------------------------------------------------
    // Lifecycle / Shift-click
    // ---------------------------------------------------------------------

    @Override
    public void removed(@NotNull Player player) {
        super.removed(Objects.requireNonNull(player));
        saveGrid(); // once more on close, server only
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            // Result slot (menu index 9)
            if (index == 9) {
                if (!this.moveItemStackTo(stack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            }
            // Player inventory / hotbar -> crafting grid
            else if (index >= 10 && index < 46) {
                if (!this.moveItemStackTo(stack, 0, 9, false)) {
                    if (index < 37) {
                        if (!this.moveItemStackTo(stack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            // Crafting grid -> player inventory / hotbar
            else if (index >= 0 && index < 10) {
                if (!this.moveItemStackTo(stack, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            if (index == 9) {
                slot.onTake(player, stack);
            }

            this.broadcastChanges();
        }

        return itemstack;
    }


    private static boolean isBlockedContainerItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        // Your container items
        switch (item) {
            case PocketCrafterItem pocketCrafterItem -> {
                return true;
            }
            case ToolboxItem toolboxItem -> {
                return true;
            }

            // Mesh Crate block item and all shulker boxes
            case BlockItem blockItem -> {
                if (blockItem.getBlock() == PseudoBlocks.MESH_CRATE.get()) {
                    return true;
                }
                if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                    return true;
                }
            }
            default -> {
            }
        }

        return false;
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        // Prevent interacting with *any* container item (crafter, toolbox, mesh crate, shulker)
        // while THIS menu is open.
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                if (isBlockedContainerItem(stack)) {
                    // Completely ignore the click for these items
                    return;
                }
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
