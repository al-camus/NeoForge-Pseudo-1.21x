package qa.luffy.pseudo.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
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
import qa.luffy.pseudo.common.block.entity.MeshCrateBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class MeshCrateBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final double MIN = 0.5D, MAX = 15.5D;
    private static final double MIN_Y = 0.0D, MAX_Y = 14.5D;

    private static final VoxelShape SHAPE = box(MIN, MIN_Y, MIN, MAX, MAX_Y, MAX);

    private static final Component UNKNOWN_CONTENTS =
            Component.translatable("container.shulkerBox.unknownContents")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    public MeshCrateBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state,
                                                 @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        // Avoid deprecated BlockState#rotate(Rotation) by mirroring the Direction directly.
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MeshCrateBlockEntity(pos, state);
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide && player.isShiftKeyDown() && player.mayBuild() && !player.isSpectator()) {
            if (player.isCreative()) {
                this.playerWillDestroy(level, pos, state, player);
                level.destroyBlock(pos, false, player);
            } else {
                level.destroyBlock(pos, true, player);
            }
            return;
        }
        super.attack(state, level, pos, player);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(provider, buf -> buf.writeBlockPos(pos));
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState state,
                            LivingEntity placer,
                            @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeshCrateBlockEntity crate) {
                crate.loadFromStack(stack);
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide && player.isCreative() && blockEntity instanceof MeshCrateBlockEntity crate) {
            if (!isEmpty(crate)) {
                ItemStack stack = crate.createStackWithContents(new ItemStack(this.asItem()));
                popResource(level, pos, stack);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private static boolean isEmpty(MeshCrateBlockEntity crate) {
        ItemStackHandler handler = crate.getItems();
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    @Deprecated
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));

        if (blockEntity instanceof MeshCrateBlockEntity crate) {
            for (int i = 0; i < drops.size(); i++) {
                ItemStack d = drops.get(i);
                if (d.is(this.asItem())) {
                    drops.set(i, crate.createStackWithContents(d));
                    return drops;
                }
            }
            return List.of(crate.createStackWithContents(new ItemStack(this.asItem())));
        }

        if (drops.isEmpty()) return List.of(new ItemStack(this.asItem()));
        return drops;
    }

    @Override
    @Deprecated
    public void onRemove(BlockState state,
                         @NotNull Level level,
                         @NotNull BlockPos pos,
                         BlockState newState,
                         boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MeshCrateBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            tooltip.add(UNKNOWN_CONTENTS);
            return;
        }

        if (!flag.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.pseudo.hold_shift_for_info")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        final int maxSlots = 81;
        final int maxItems = 81 * 64;
        final int maxShown = 8;

        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        int usedSlots = 0;
        int totalItems = 0;
        ArrayList<ItemStack> stacks = new ArrayList<>();

        for (ItemStack s : contents.nonEmptyItems()) {
            usedSlots++;
            totalItems += s.getCount();
            stacks.add(s);
        }

        int pct = (int) Math.round((totalItems / (double) maxItems) * 100.0D);
        if (pct < 0) pct = 0;
        if (pct > 100) pct = 100;

        ChatFormatting color;
        if (pct >= 75) color = ChatFormatting.RED;
        else if (pct >= 35) color = ChatFormatting.YELLOW;
        else color = ChatFormatting.GREEN;

        tooltip.add(Component.translatable(
                "tooltip.pseudo.mesh_crate.fill",
                pct, usedSlots, maxSlots
        ).withStyle(color));

        if (usedSlots == 0) return;

        stacks.sort((a, b) -> {
            int c = Integer.compare(b.getCount(), a.getCount());
            if (c != 0) return c;
            return a.getHoverName().getString().compareToIgnoreCase(b.getHoverName().getString());
        });

        int shown = 0;
        for (ItemStack s : stacks) {
            if (shown >= maxShown) break;
            tooltip.add(Component.translatable(
                    "container.shulkerBox.itemCount",
                    s.getHoverName(),
                    s.getCount()
            ));
            shown++;
        }

        int remaining = usedSlots - shown;
        if (remaining > 0) {
            tooltip.add(Component.translatable("container.shulkerBox.more", remaining)
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
}
