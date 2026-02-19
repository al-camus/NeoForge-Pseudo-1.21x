package qa.luffy.pseudo.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.entity.ToolboxBlockEntity;
import qa.luffy.pseudo.common.util.ContainerItemGuards;

import java.util.ArrayList;
import java.util.List;

public class ToolboxBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final int SLOTS = 9;
    public static final MapCodec<ToolboxBlock> CODEC = simpleCodec(ToolboxBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final double MIN_X = 0.95D, MAX_X = 15.05D;
    private static final double MIN_Y = 0.00D, MAX_Y = 10.05307D;
    private static final double MIN_Z = 3.50D, MAX_Z = 11.55D;

    private static final VoxelShape SHAPE_NORTH = box(MIN_X, MIN_Y, MIN_Z, MAX_X, MAX_Y, MAX_Z);
    private static final VoxelShape SHAPE_SOUTH = box(16.0D - MAX_X, MIN_Y, 16.0D - MAX_Z, 16.0D - MIN_X, MAX_Y, 16.0D - MIN_Z);
    private static final VoxelShape SHAPE_EAST  = box(16.0D - MAX_Z, MIN_Y, MIN_X, 16.0D - MIN_Z, MAX_Y, MAX_X);
    private static final VoxelShape SHAPE_WEST  = box(MIN_Z, MIN_Y, 16.0D - MAX_X, MAX_Z, MAX_Y, 16.0D - MIN_X);

    public ToolboxBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state,
                                           @NotNull Direction direction,
                                           @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state,
                                                 @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull CollisionContext context) {
        return shapeFor(state);
    }

    private static VoxelShape shapeFor(BlockState state) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ToolboxBlockEntity(pos, state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack held,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.sidedSuccess(true);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ToolboxBlockEntity toolbox)) return ItemInteractionResult.FAIL;

        if (player.isShiftKeyDown()) swapWithHotbar(player, toolbox);
        else player.openMenu(toolbox, buf -> buf.writeBlockPos(pos));

        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        @NotNull Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ToolboxBlockEntity toolbox)) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) swapWithHotbar(player, toolbox);
        else player.openMenu(toolbox, buf -> buf.writeBlockPos(pos));

        return InteractionResult.CONSUME;
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide && player.isShiftKeyDown() && player.mayBuild() && !player.isSpectator()) {
            boolean drop = !player.isCreative();
            level.destroyBlock(pos, drop, player);
            return;
        }
        super.attack(state, level, pos, player);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        BlockEntity be = level.getBlockEntity(pos);

        if (!level.isClientSide && player.isCreative() && be instanceof ToolboxBlockEntity toolbox) {
            if (!isEmpty(toolbox)) {
                ItemStack drop = new ItemStack(this.asItem());
                toolbox.saveToItem(drop);
                popResource(level, pos, drop);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private static boolean isEmpty(ToolboxBlockEntity toolbox) {
        ItemStackHandler handler = toolbox.getInventory();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState state,
                            LivingEntity placer,
                            @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ToolboxBlockEntity toolbox) {
            toolbox.loadFromItem(stack);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));

        if (be instanceof ToolboxBlockEntity toolbox) {
            for (int i = 0; i < drops.size(); i++) {
                ItemStack d = drops.get(i);
                if (d.is(this.asItem())) {
                    toolbox.saveToItem(d);
                    return drops;
                }
            }

            ItemStack fallback = new ItemStack(this.asItem());
            toolbox.saveToItem(fallback);
            return List.of(fallback);
        }

        if (drops.isEmpty()) return List.of(new ItemStack(this.asItem()));
        return drops;
    }

    private static void swapWithHotbar(Player player, ToolboxBlockEntity toolbox) {
        var inv = player.getInventory();
        ItemStackHandler handler = toolbox.getInventory();

        for (int i = 0; i < SLOTS; i++) {
            ItemStack internal = handler.getStackInSlot(i);
            ItemStack external = inv.getItem(i);

            if (ContainerItemGuards.isBlockedContainerItem(internal) || ContainerItemGuards.isBlockedContainerItem(external)) {
                continue;
            }

            handler.setStackInSlot(i, external.copy());
            inv.setItem(i, internal.copy());
        }

        inv.setChanged();
        player.inventoryMenu.broadcastChanges();
        toolbox.setChanged();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
