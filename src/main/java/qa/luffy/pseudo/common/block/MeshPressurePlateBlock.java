package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.util.energy.EnergyUtil;

public class MeshPressurePlateBlock extends PressurePlateBlock {

    public MeshPressurePlateBlock(BlockSetType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public void checkPressed(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, int currentSignal) {
        int i = this.getSignalStrength(level, pos);
        boolean flag = currentSignal > 0;
        boolean flag1 = i > 0;
        if (currentSignal != i) {
            BlockState blockstate = this.setSignalForState(state, i);
            level.setBlock(pos, blockstate, 2);
            this.updateNeighbours(level, pos);
            level.setBlocksDirty(pos, state, blockstate);
        }

        if (!flag1 && flag) {
            level.playSound(null, pos, this.type.pressurePlateClickOff(), SoundSource.BLOCKS);
            level.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (flag1 && !flag) {
            level.playSound(null, pos, this.type.pressurePlateClickOn(), SoundSource.BLOCKS);
            level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
            EnergyUtil.generateEnergyNearby(level, pos, 5);
        }

        if (flag1) {
            level.scheduleTick(new BlockPos(pos), this, this.getPressedTime());
        }
    }
}
