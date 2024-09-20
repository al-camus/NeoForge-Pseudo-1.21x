package qa.luffy.pseudo.common.util.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EnergyStorageBlock {
    PseudoEnergyStorage getEnergyStorage(@Nullable Direction direction);
}
