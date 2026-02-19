package qa.luffy.pseudo.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
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

    public static final int ENERGY_CAPACITY = 96_000;

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int DEFAULT_MAX_PROGRESS = 100;

    private static final int ENERGY_PER_TICK = 1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            Level lvl = CapacitorBlockEntity.this.level;
            if (lvl != null && !lvl.isClientSide()) {
                lvl.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final PseudoEnergyStorage energyStorage = new PseudoEnergyStorage(ENERGY_CAPACITY, 256, 256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
        }
    };

    private int progress = 0;
    private int maxProgress = DEFAULT_MAX_PROGRESS;

    // data[0]=progress, data[1]=maxProgress, data[2]=energy low16, data[3]=energy high16
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CapacitorBlockEntity.this.progress;
                case 1 -> CapacitorBlockEntity.this.maxProgress;
                case 2 -> CapacitorBlockEntity.this.energyStorage.getEnergyStored() & 0xFFFF;
                case 3 -> (CapacitorBlockEntity.this.energyStorage.getEnergyStored() >>> 16) & 0xFFFF;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> CapacitorBlockEntity.this.progress = value;
                case 1 -> CapacitorBlockEntity.this.maxProgress = value;
                // 2/3 are server->client only
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(PseudoBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
    }

    public void tick() {
        final Level level = this.level;
        if (level == null || level.isClientSide()) return;

        EnergyUtil.distributeEnergyNearby(level, getBlockPos(), 256);

        IEnergyStorage inputEnergy = getItemEnergy(itemHandler.getStackInSlot(INPUT_SLOT));
        if (inputEnergy != null && inputEnergy.getEnergyStored() > 0) {
            EnergyUtil.transferEnergy(inputEnergy, energyStorage, level, 256);
        }

        IEnergyStorage outputEnergy = getItemEnergy(itemHandler.getStackInSlot(OUTPUT_SLOT));
        if (outputEnergy != null && outputEnergy.getEnergyStored() < outputEnergy.getMaxEnergyStored()) {
            EnergyUtil.transferEnergy(energyStorage, outputEnergy, level, 256);
        }

        Optional<RecipeHolder<CapacitorRecipe>> recipeOpt = getCurrentRecipe(level);
        if (recipeOpt.isEmpty()) {
            resetProgress();
            return;
        }

        CapacitorRecipe recipe = recipeOpt.get().value();
        ItemStack output = recipe.output();

        if (!canInsertAmountIntoOutputSlot(output.getCount())
                || !canInsertItemIntoOutputSlot(output)
                || energyStorage.getEnergyStored() <= 0) {
            resetProgress();
            return;
        }

        if (!isProcessing()) {
            maxProgress = getProcessTimeForEnergy(recipe.inputEnergy());
        }

        progress++;
        energyStorage.extractEnergy(ENERGY_PER_TICK, false);
        setChanged();

        if (progress >= maxProgress) {
            craftItem(recipe);
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem(CapacitorRecipe recipe) {
        ItemStack out = recipe.output().copy();
        itemHandler.extractItem(INPUT_SLOT, 1, false);

        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int newCount = existing.isEmpty() ? out.getCount() : existing.getCount() + out.getCount();
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(out.getItem(), newCount));
    }

    private int getProcessTimeForEnergy(int energy) {
        return Math.round((float) energy / ENERGY_PER_TICK);
    }

    private boolean isProcessing() {
        return progress > 0;
    }

    private Optional<RecipeHolder<CapacitorRecipe>> getCurrentRecipe(Level level) {
        return level.getRecipeManager().getRecipeFor(
                PseudoCustomRecipes.CAPACITOR_TYPE.get(),
                new CapacitorRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT), energyStorage.getEnergyStored()),
                level
        );
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        return existing.isEmpty() || existing.getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int maxCount = existing.isEmpty() ? 64 : existing.getMaxStackSize();
        return maxCount >= existing.getCount() + count;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergy(tag.getInt("energy"));
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.pseudo.capacitor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new CapacitorMenu(containerId, playerInventory, this, this.data);
    }

    public void loadFromItem(ItemStack stack) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        contents.copyInto(items);

        for (int i = 0; i < items.size(); i++) {
            itemHandler.setStackInSlot(i, items.get(i));
        }

        int storedEnergy = stack.getOrDefault(PseudoDataComponents.ENERGY.get(), 0);
        energyStorage.setEnergy(storedEnergy);
        setChanged();
    }

    public void saveToItem(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, itemHandler.getStackInSlot(i));
        }
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        stack.set(PseudoDataComponents.ENERGY.get(), energyStorage.getEnergyStored());
    }

    @Override
    public PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }

    @Override
    public ItemStackHandler getItemHandler(@Nullable Direction direction) {
        return itemHandler;
    }

    private static @Nullable IEnergyStorage getItemEnergy(ItemStack stack) {
        if (stack.isEmpty()) return null;
        // Prefer the public API: stack.getCapability(...)
        return stack.getCapability(Capabilities.EnergyStorage.ITEM, null);
    }
}
