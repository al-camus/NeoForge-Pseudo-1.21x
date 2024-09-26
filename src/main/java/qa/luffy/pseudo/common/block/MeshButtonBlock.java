package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.util.energy.EnergyUtil;

public class MeshButtonBlock extends ButtonBlock {

    private final BlockSetType type;
    private final int ticksToStayPressed;

    public MeshButtonBlock(BlockSetType type, int ticksToStayPressed, BlockBehaviour.Properties properties) {
        super(type, ticksToStayPressed, properties);
        this.type = type;
        this.ticksToStayPressed = ticksToStayPressed;
    }

    @Override
    public void checkPressed(BlockState state, Level level, BlockPos pos) {
        AbstractArrow abstractarrow = this.type.canButtonBeActivatedByArrows()
                ? level.getEntitiesOfClass(AbstractArrow.class, state.getShape(level, pos).bounds().move(pos)).stream().findFirst().orElse(null)
                : null;
        boolean flag = abstractarrow != null;
        boolean flag1 = state.getValue(POWERED);
        if (flag != flag1) {
            level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag)), 3);
            this.updateNeighbours(state, level, pos);
            this.playSound(null, level, pos, flag);
            level.gameEvent(abstractarrow, flag ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
            EnergyUtil.generateEnergyNearby(level, pos, 5);
        }

        if (flag) {
            level.scheduleTick(new BlockPos(pos), this, this.ticksToStayPressed);
        }
    }

    public void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }
}