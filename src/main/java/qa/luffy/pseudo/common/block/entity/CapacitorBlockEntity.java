package qa.luffy.pseudo.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.menu.CapacitorMenu;
import qa.luffy.pseudo.common.recipe.PseudoCustomRecipes;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipeInput;
import qa.luffy.pseudo.common.util.ItemHandlerBlock;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;
import qa.luffy.pseudo.common.util.energy.EnergyUtil;
import qa.luffy.pseudo.common.util.energy.PseudoEnergyStorage;

import java.util.Optional;

public class CapacitorBlockEntity extends BlockEntity implements MenuProvider, EnergyStorageBlock, ItemHandlerBlock {

    final int INPUT_SLOT = 0;
    final int OUTPUT_SLOT = 1;
    final int DEFAULT_MAX_PROGRESS=100;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };
    private final PseudoEnergyStorage energyStorage = new PseudoEnergyStorage(96000, 256 ,256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
            //PacketDistributor.sendToAllPlayers(new EnergyData(this.energy, getBlockPos()));
        }
    };
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = DEFAULT_MAX_PROGRESS;

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(PseudoBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CapacitorBlockEntity.this.progress;
                    case 1 -> CapacitorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: CapacitorBlockEntity.this.progress = value;
                    case 1: CapacitorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private int energyPerTick = 1;
    public void tick() {
        if(!level.isClientSide()) {
            //slot 0 - charge capacitor
            IEnergyStorage itemEnergy0 = Capabilities.EnergyStorage.ITEM.getCapability(itemHandler.getStackInSlot(INPUT_SLOT), null);
            if (itemEnergy0 != null) {
                if (itemEnergy0.getEnergyStored() != 0) {
                    EnergyUtil.transferEnergy(itemEnergy0, energyStorage, level, 256);
                }
            }
            //slot 1 - charge item
            IEnergyStorage itemEnergy1 = Capabilities.EnergyStorage.ITEM.getCapability(itemHandler.getStackInSlot(OUTPUT_SLOT), null);
            if (itemEnergy1 != null) {
                if (itemEnergy1.getEnergyStored() != itemEnergy1.getMaxEnergyStored()) {
                    EnergyUtil.transferEnergy(energyStorage, itemEnergy1, level, 256);
                }
            }

            if (hasRecipe() && !isProcessing()) {
                Optional<RecipeHolder<CapacitorRecipe>> recipe = getCurrentRecipes();
                recipe.ifPresent(capacitorRecipeRecipeHolder -> maxProgress = getProcessTimeForEnergy(capacitorRecipeRecipeHolder.value().inputEnergy()));
            }
            if (hasRecipe() && isOutputSlotEmptyOrRecievable()) {
                increaseProgress();
                this.energyStorage.extractEnergy(1,false);
                setChanged();

                if (hasCraftingFinished()) {
                    craftItem();
                    resetProgress();
                }
            }
            else {
                resetProgress();
            }

        }

    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        Optional<RecipeHolder<CapacitorRecipe>> recipe = getCurrentRecipes();
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(), itemHandler.getStackInSlot(OUTPUT_SLOT).getCount()+ output.getCount()));
    }

    private boolean hasCraftingFinished() {
        return this.progress >= maxProgress;
    }

    private void increaseProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrRecievable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private int getProcessTimeForEnergy(int energy) {
        return Math.round( (float) energy / energyPerTick );
    }
    private boolean isProcessing() {
        return progress > 0;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<CapacitorRecipe>> recipe = getCurrentRecipes();
        if (recipe.isEmpty()) return false;
        ItemStack output = recipe.get().value().getResultItem(null);

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output) && this.energyStorage.getEnergyStored()>0;
    }

    private Optional<RecipeHolder<CapacitorRecipe>> getCurrentRecipes() {
        return this.level.getRecipeManager().getRecipeFor(PseudoCustomRecipes.CAPACITOR_TYPE.get(),
                new CapacitorRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT), energyStorage.getEnergyStored()), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergy(tag.getInt("energy"));
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.pseudo.capacitor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CapacitorMenu(containerId, playerInventory, this, this.data);
    }

    public PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }

    @Override
    public ItemStackHandler getItemHandler(@Nullable Direction direction) {
        return itemHandler;
    }

}
