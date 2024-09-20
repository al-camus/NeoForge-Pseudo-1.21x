package qa.luffy.pseudo.common.util.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyUtil {

    //TODO better logic
    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount) {
        Direction.stream().forEach(direction -> {
            IEnergyStorage energyFrom = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
            IEnergyStorage energyTo = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if(!level.isClientSide() && energyFrom != null && energyTo != null) {
                int newAmount = Math.min(energyFrom.extractEnergy(amount, true), energyTo.receiveEnergy(amount, true));
                energyTo.receiveEnergy(newAmount, false);
                energyFrom.extractEnergy(newAmount, false);
            }
        });
    }

    //TODO better logic
    public static void generateEnergyNearby(Level level, BlockPos pos, int amount) {
        Direction.stream().forEach(direction -> {
            IEnergyStorage energyTo = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if(!level.isClientSide() && energyTo != null) {
                energyTo.receiveEnergy(amount, false);
            }
        });
    }

    /**
     * @param energyFrom
     * @param energyTo
     * @param level
     * @param amount
     * @return Amount of energy transferred
     */
    public static int transferEnergy(IEnergyStorage energyFrom, IEnergyStorage energyTo, Level level, int amount) {
        if(!level.isClientSide() && energyFrom != null && energyTo != null) {
            int newAmount = Math.min(energyFrom.extractEnergy(amount, true), energyTo.receiveEnergy(amount, true));
            energyTo.receiveEnergy(newAmount, false);
            energyFrom.extractEnergy(newAmount, false);
            return newAmount;
        }
        return 0;
    }
}
