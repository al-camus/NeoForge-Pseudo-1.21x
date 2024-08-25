package qa.luffy.pseudo.block.entity.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface EnergyStorageBlock {

    EnergyStorage getEnergyStorage(@Nullable Direction direction);

}
