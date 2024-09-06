package qa.luffy.pseudo.common.util.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EnergyStorageBlock {
    PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction);
}
