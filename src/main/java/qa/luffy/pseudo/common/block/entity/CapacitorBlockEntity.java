package qa.luffy.pseudo.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.menu.CapacitorMenu;
import qa.luffy.pseudo.common.networking.packet.EnergyData;
import qa.luffy.pseudo.common.recipe.PseudoRecipeTypes;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipeInput;
import qa.luffy.pseudo.common.util.ImplementedInventory;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;
import qa.luffy.pseudo.common.util.energy.PseudoEnergyStorage;

import java.util.Optional;

public class CapacitorBlockEntity extends BaseContainerBlockEntity implements EnergyStorageBlock, ImplementedInventory {

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final PseudoEnergyStorage energyStorage = new PseudoEnergyStorage(64000, 256 ,256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
            PacketDistributor.sendToAllPlayers(new EnergyData(this.energy, getBlockPos()));
        }
    };
    protected final ContainerData data;
    private final RecipeManager.CachedCheck<CapacitorRecipeInput, CapacitorRecipe> quickCheck = RecipeManager.createCheck(PseudoRecipeTypes.CAPACITOR.get());

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(PseudoBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> totalProcessTime;
                    case 1 -> processTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        totalProcessTime = value;
                    case 1:
                        processTime = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private int energyPerTick = 1;
    private int processTime;
    private int totalProcessTime;
    public void tick() {
        if(!level.isClientSide()) {
            Optional<RecipeHolder<CapacitorRecipe>> optionalRecipes = quickCheck.getRecipeFor(
                    new CapacitorRecipeInput(this.energyStorage.getEnergyStored(), getItem(0)), this.level);
            if (optionalRecipes.isPresent()) {
                CapacitorRecipe recipe = optionalRecipes.get().value();
                if (!isProcessing()) {
                    totalProcessTime = getProcessTimeForEnergy(recipe.getInputEnergy());
                    processTime = totalProcessTime;
                }
                if (isProcessing()) {
                    energyStorage.setEnergy(energyStorage.getEnergyStored() - energyPerTick);
                    if (--processTime == 0) {
                        setItem(0, ItemStack.EMPTY);
                        setItem(1, recipe.getResult());
                    }
                }
            }
        }

    }
    private int getProcessTimeForEnergy(int energy) {
        return Math.round( (float) energy / energyPerTick );
    }
    private boolean isProcessing() {
        return processTime > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStorage.getEnergyStored());
        //energyStorage.serializeNBT(registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergy(tag.getInt("energy"));
        //energyStorage.deserializeNBT(registries, tag);
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
        return new CapacitorMenu(containerId, inventory, this, data);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.pseudo.capacitor");
    }

    public PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }
}
