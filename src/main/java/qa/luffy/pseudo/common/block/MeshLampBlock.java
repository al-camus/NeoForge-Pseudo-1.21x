package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class MeshLampBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public MeshLampBlock(Properties properties, boolean defaultOn) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ACTIVATED, defaultOn)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                     Level level,
                                                     @NotNull BlockPos pos,
                                                     @NotNull Player player,
                                                     @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        level.setBlock(pos, state.cycle(ACTIVATED), Block.UPDATE_ALL);
        return InteractionResult.CONSUME;
    }
}
