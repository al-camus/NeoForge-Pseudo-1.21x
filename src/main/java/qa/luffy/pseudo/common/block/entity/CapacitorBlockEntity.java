package qa.luffy.pseudo.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.menu.CapacitorMenu;
import qa.luffy.pseudo.common.recipe.PseudoRecipeTypes;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipeInput;
import qa.luffy.pseudo.common.util.ImplementedInventory;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;
import qa.luffy.pseudo.common.util.energy.EnergyUtil;
import qa.luffy.pseudo.common.util.energy.PseudoEnergyStorage;

import java.util.Optional;

public class CapacitorBlockEntity extends BaseContainerBlockEntity implements EnergyStorageBlock, ImplementedInventory {

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final PseudoEnergyStorage energyStorage = new PseudoEnergyStorage(96000, 256 ,256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
            //PacketDistributor.sendToAllPlayers(new EnergyData(this.energy, getBlockPos()));
        }
    };
    private final RecipeManager.CachedCheck<CapacitorRecipeInput, CapacitorRecipe> quickCheck = RecipeManager.createCheck(PseudoRecipeTypes.CAPACITOR.get());

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(PseudoBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
    }

    private int energyPerTick = 1;
    private int processTime;
    private int totalProcessTime;
    public void tick() {
        if(!level.isClientSide()) {
            IEnergyStorage itemEnergy = Capabilities.EnergyStorage.ITEM.getCapability(getItem(0), null);
            if (itemEnergy != null) {
                if (itemEnergy.getEnergyStored()==itemEnergy.getMaxEnergyStored() && getItems().get(1).isEmpty()) {
                    setItem(1, getItem(0));
                    setItem(0, ItemStack.EMPTY);
                }
                EnergyUtil.transferEnergy(energyStorage, itemEnergy, level, 256);
            }


            Optional<RecipeHolder<CapacitorRecipe>> optionalRecipes = quickCheck.getRecipeFor(
                    new CapacitorRecipeInput(this.energyStorage.getEnergyStored(), getItem(0)), this.level);
            if (optionalRecipes.isPresent()) {

                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("ACTIVE"));

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
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergy(tag.getInt("energy"));
        ContainerHelper.loadAllItems(tag, items, registries);
        //energyStorage.deserializeNBT(registries, tag);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
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

    public PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }
}
