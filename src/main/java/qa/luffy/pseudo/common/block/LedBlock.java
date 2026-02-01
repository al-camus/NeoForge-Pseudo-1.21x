package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LedBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    // Smaller, slightly less wide than previous; ~2:1 aspect ratio, centered at (8, 8), 2px thick
    private static final VoxelShape SHAPE_UP =
            Block.box(4.0D, 0.0D, 6.0D, 12.0D, 2.0D, 10.0D);
    private static final VoxelShape SHAPE_DOWN =
            Block.box(4.0D, 14.0D, 6.0D, 12.0D, 16.0D, 10.0D);
    private static final VoxelShape SHAPE_NORTH =
            Block.box(4.0D, 6.0D, 14.0D, 12.0D, 10.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH =
            Block.box(4.0D, 6.0D, 0.0D, 12.0D, 10.0D, 2.0D);
    private static final VoxelShape SHAPE_WEST =
            Block.box(14.0D, 6.0D, 4.0D, 16.0D, 10.0D, 12.0D);
    private static final VoxelShape SHAPE_EAST =
            Block.box(0.0D, 6.0D, 4.0D, 2.0D, 10.0D, 12.0D);

    public LedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        BlockState state = this.defaultBlockState().setValue(FACING, face);
        return state.canSurvive(level, pos) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return Block.canSupportCenter(level, supportPos, facing);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state,
                                           @NotNull Direction neighborDir,
                                           @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (neighborDir == facing.getOpposite() && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, neighborDir, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state,
                                                 @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos) {
        return Shapes.empty();
    }
}
