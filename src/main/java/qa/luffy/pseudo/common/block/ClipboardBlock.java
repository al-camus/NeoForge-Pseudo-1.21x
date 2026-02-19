package qa.luffy.pseudo.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.block.entity.ClipboardBlockEntity;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;

import java.util.ArrayList;
import java.util.List;

public class ClipboardBlock extends BaseEntityBlock {
    public static final MapCodec<ClipboardBlock> CODEC = simpleCodec(ClipboardBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Shapes.box(0.1875, 0.125, 0.9375, 0.8125, 0.8125, 1.0),
            Shapes.box(0.4375, 0.8125, 0.9375, 0.5625, 0.875, 1.0)
    );

    private static final VoxelShape EAST_SHAPE  = rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE  = rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public ClipboardBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ClipboardBlockEntity(pos, state);
    }

    /**
     * Place like an item frame: must click a horizontal face.
     * The clipboard faces outward (same direction as clicked face).
     */
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();
        if (face.getAxis().isVertical()) return null; // no floor/ceiling

        BlockState placed = defaultBlockState().setValue(FACING, face);
        return canSurvive(placed, ctx.getLevel(), ctx.getClickedPos()) ? placed : null;
    }

    /**
     * Needs a sturdy face on the block behind it.
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.isFaceSturdy(level, supportPos, facing);
    }

    /**
     * If the supporting block changes, re-check and pop off if needed.
     * NOTE: 1.21.1 signature uses LevelAccessor.
     */
    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            @NotNull Direction direction,
            @NotNull BlockState neighborState,
            @NotNull LevelAccessor level,
            @NotNull BlockPos pos,
            @NotNull BlockPos neighborPos
    ) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite() && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * Extra safety: if something updates without going through updateShape.
     */
    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, net.minecraft.world.level.block.@NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof ClipboardBlockEntity clipboard) {
            clipboard.setContent(stack.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT));
            clipboard.setComponents(stack.getComponents());
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    // Minimal rotate helper (no external ShapeUtil)
    private static VoxelShape rotate(VoxelShape shape, Rotation rotation) {
        if (rotation == Rotation.NONE) return shape;

        List<VoxelShape> parts = new ArrayList<>();
        for (AABB bb : shape.toAabbs()) {
            parts.add(Shapes.create(rotateAabbY(bb, rotation)));
        }

        VoxelShape out = Shapes.empty();
        for (VoxelShape p : parts) out = Shapes.or(out, p);
        return out.optimize();
    }

    // Rotates AABB around Y axis inside 0..1 block space
    private static AABB rotateAabbY(AABB bb, Rotation rotation) {
        return switch (rotation) {
            case NONE -> bb;
            case CLOCKWISE_90 -> new AABB(
                    1.0 - bb.maxZ, bb.minY, bb.minX,
                    1.0 - bb.minZ, bb.maxY, bb.maxX
            );
            case CLOCKWISE_180 -> new AABB(
                    1.0 - bb.maxX, bb.minY, 1.0 - bb.maxZ,
                    1.0 - bb.minX, bb.maxY, 1.0 - bb.minZ
            );
            case COUNTERCLOCKWISE_90 -> new AABB(
                    bb.minZ, bb.minY, 1.0 - bb.maxX,
                    bb.maxZ, bb.maxY, 1.0 - bb.minX
            );
        };
    }
}
