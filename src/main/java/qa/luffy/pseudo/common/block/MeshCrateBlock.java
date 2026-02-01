package qa.luffy.pseudo.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.entity.MeshCrateBlockEntity;

import java.util.List;

public class MeshCrateBlock extends Block implements EntityBlock {

    // dynamic drop key (matches shulker convention)
    public static final ResourceLocation CONTENTS =
            ResourceLocation.withDefaultNamespace("contents");

    private static final Component UNKNOWN_CONTENTS =
            Component.translatable("container.shulkerBox.unknownContents")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    public MeshCrateBlock(Properties props) {
        super(props);
    }

    // --- Block entity ---

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MeshCrateBlockEntity(pos, state);
    }

    // --- 1.21.1 interaction: useWithoutItem only ---

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

    // --- Placement: load contents from item ---

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

    // --- Breaking: always drop crate item with embedded inventory ---

    @Override
    public @NotNull BlockState playerWillDestroy(Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide && blockEntity instanceof MeshCrateBlockEntity crate) {
            boolean crateEmpty = isEmpty(crate);

            // Survival: always drop the crate (even if empty).
            // Creative: only drop if it has contents.
            if (!player.isCreative() || !crateEmpty) {
                ItemStack stack = new ItemStack(this.asItem());
                stack = crate.createStackWithContents(stack);

                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        stack
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        // Loot table for mesh_crate is empty, so vanilla dropResources won't add anything.
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static boolean isEmpty(MeshCrateBlockEntity crate) {
        ItemStackHandler handler = crate.getItems();
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // --- Dynamic drops (for loot tables / hoppers etc.) ---

    @Override
    @Deprecated
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof MeshCrateBlockEntity crate) {
            ItemStackHandler handler = crate.getItems();
            params = params.withDynamicDrop(CONTENTS, consumer -> {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack s = handler.getStackInSlot(i);
                    if (!s.isEmpty()) {
                        consumer.accept(s);
                    }
                }
            });
        }

        return super.getDrops(state, params);
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

    // --- Tooltip (same UX as shulker / Iron Shulker) ---

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

        int shown = 0;
        int total = 0;

        for (ItemStack itemStack : stack
                .getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
                .nonEmptyItems()) {
            total++;
            if (shown <= 4) {
                shown++;
                tooltip.add(Component.translatable(
                        "container.shulkerBox.itemCount",
                        itemStack.getHoverName(),
                        itemStack.getCount()
                ));
            }
        }

        if (total - shown > 0) {
            tooltip.add(
                    Component.translatable("container.shulkerBox.more", total - shown)
                            .withStyle(ChatFormatting.ITALIC)
            );
        }
    }
}
