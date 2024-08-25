package qa.luffy.pseudo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EmptyEnergyStorage;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.block.entity.energy.EnergyStorageBlock;
import qa.luffy.pseudo.block.entity.energy.ModEnergyStorage;

public class CapacitorBlockEntity extends BlockEntity implements EnergyStorageBlock {

    private ModEnergyStorage energyStorage = new ModEnergyStorage(64000, 256 ,256) {
        @Override
        public void setEnergyChanged() {
            setChanged();
        }
    };

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CAPACITOR_TYPE.get(), pos, blockState);
    }

    public EnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
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
}
