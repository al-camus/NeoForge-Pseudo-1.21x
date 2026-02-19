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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LedBlock extends Block implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // PICK/OUTLINE box: 4x4 centered, 1px thick
    private static final VoxelShape BIG_UP    = Block.box(6, 0, 6, 10, 1, 10);
    private static final VoxelShape BIG_DOWN  = Block.box(6, 15, 6, 10, 16, 10);
    private static final VoxelShape BIG_NORTH = Block.box(6, 6, 15, 10, 10, 16);
    private static final VoxelShape BIG_SOUTH = Block.box(6, 6, 0,  10, 10, 1);
    private static final VoxelShape BIG_WEST  = Block.box(15, 6, 6, 16, 10, 10);
    private static final VoxelShape BIG_EAST  = Block.box(0,  6, 6, 1,  10, 10);

    // COLLISION: visible model size, 2x2 centered, 1px thick
    private static final VoxelShape SMALL_UP    = Block.box(7, 0, 7, 9, 1, 9);
    private static final VoxelShape SMALL_DOWN  = Block.box(7, 15, 7, 9, 16, 9);
    private static final VoxelShape SMALL_NORTH = Block.box(7, 7, 15, 9, 9, 16);
    private static final VoxelShape SMALL_SOUTH = Block.box(7, 7, 0,  9, 9, 1);
    private static final VoxelShape SMALL_WEST  = Block.box(15, 7, 7, 16, 9, 9);
    private static final VoxelShape SMALL_EAST  = Block.box(0,  7, 7, 1,  9, 9);

    public LedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.UP)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        FluidState fluid = level.getFluidState(pos);
        BlockState state = this.defaultBlockState()
                .setValue(FACING, face)
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);

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

        // If our support is gone, pop off.
        Direction facing = state.getValue(FACING);
        if (neighborDir == facing.getOpposite() && !state.canSurvive(level, pos)) {
            // If we were waterlogged, leave water behind instead of air.
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        // Keep water ticking properly when waterlogged.
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, neighborDir, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // Pick/outline/break shape (easy to click): BIG
    @Override
    public @NotNull VoxelShape getShape(BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> BIG_UP;
            case DOWN -> BIG_DOWN;
            case NORTH -> BIG_NORTH;
            case SOUTH -> BIG_SOUTH;
            case WEST -> BIG_WEST;
            case EAST -> BIG_EAST;
        };
    }

    // Physical collision: SMALL (matches the 2x2x1 model)
    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state,
                                                 @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> SMALL_UP;
            case DOWN -> SMALL_DOWN;
            case NORTH -> SMALL_NORTH;
            case SOUTH -> SMALL_SOUTH;
            case WEST -> SMALL_WEST;
            case EAST -> SMALL_EAST;
        };
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state,
                                                 @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos) {
        return Shapes.empty();
    }
}
