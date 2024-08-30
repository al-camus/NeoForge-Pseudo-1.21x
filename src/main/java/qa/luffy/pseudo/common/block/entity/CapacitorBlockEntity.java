package qa.luffy.pseudo.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.menu.CapacitorMenu;
import qa.luffy.pseudo.common.recipe.PseudoRecipeTypes;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipeInput;
import qa.luffy.pseudo.common.util.ImplementedInventory;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;
import qa.luffy.pseudo.common.util.energy.ModEnergyStorage;

import java.util.Optional;

public class CapacitorBlockEntity extends BaseContainerBlockEntity implements EnergyStorageBlock, ImplementedInventory {

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final ModEnergyStorage energyStorage = new ModEnergyStorage(64000, 256 ,256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
        }
    };

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(PseudoBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
    }

    public void tick() {
        RecipeManager recipes = this.level.getRecipeManager();
        Optional<RecipeHolder<CapacitorRecipe>> optionalRecipes = recipes.getRecipeFor(
                PseudoRecipeTypes.CAPACITOR.get(),
                new CapacitorRecipeInput(energyStorage.getEnergyStored(),
                        getItem(0)), level);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStorage.getEnergyStored());
        energyStorage.serializeNBT(registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergy(tag.getInt("energy"));
        energyStorage.deserializeNBT(registries, tag);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new CapacitorMenu(containerId, inventory, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.pseudo.capacitor");
    }

    public EnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }
}
