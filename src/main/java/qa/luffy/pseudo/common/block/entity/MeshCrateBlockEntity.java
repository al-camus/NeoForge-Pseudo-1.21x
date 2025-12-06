// qa/luffy/pseudo/common/blockentity/MeshCrateBlockEntity.java
package qa.luffy.pseudo.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import qa.luffy.pseudo.common.menu.MeshCrateMenu;

public class MeshCrateBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOTS = 81; // 9x9

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    public MeshCrateBlockEntity(BlockPos pos, BlockState state) {
        super(PseudoBlockEntities.MESH_CRATE_TYPE.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        // 🔹 NEW: pass the registries into the handler
        CompoundTag itemsTag = items.serializeNBT(registries);
        nbt.put("Items", itemsTag);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("Items", Tag.TAG_COMPOUND)) {
            items.deserializeNBT(registries, nbt.getCompound("Items"));
        }
    }

    public ItemStack createStackWithContents(ItemStack base) {
        NonNullList<ItemStack> list = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < SLOTS; i++) {
            list.set(i, items.getStackInSlot(i));
        }
        ItemContainerContents contents = ItemContainerContents.fromItems(list);
        base.set(DataComponents.CONTAINER, contents);
        return base;
    }

    /** Load inventory from an ItemStack’s CONTAINER component into this block entity. */
    public void loadFromStack(ItemStack stack) {
        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        NonNullList<ItemStack> list = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        contents.copyInto(list);

        for (int i = 0; i < SLOTS; i++) {
            items.setStackInSlot(i, list.get(i));
        }
        setChanged();
    }

    // --- MenuProvider for opening GUI ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pseudo.mesh_crate");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new MeshCrateMenu(containerId, inv, this);
    }
}
