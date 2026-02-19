package qa.luffy.pseudo.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.block.entity.CapacitorBlockEntity;
import qa.luffy.pseudo.common.block.entity.PseudoBlockEntities;
import qa.luffy.pseudo.common.data.PseudoDataComponents;

import java.util.List;
import java.util.Objects;

public class CapacitorBlock extends BaseEntityBlock {

    protected CapacitorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CapacitorBlock::new);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CapacitorBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(Objects.requireNonNull(state.getMenuProvider(level, pos)), pos);

        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState state,
                            LivingEntity placer,
                            @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CapacitorBlockEntity capacitor) {
            capacitor.loadFromItem(stack);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType,PseudoBlockEntities.CAPACITOR_TYPE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick());
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        BlockEntity be = level.getBlockEntity(pos);

        if (!level.isClientSide && player.isCreative() && be instanceof CapacitorBlockEntity capacitor) {
            if (hasStoredData(capacitor)) {
                ItemStack drop = new ItemStack(this.asItem());
                capacitor.saveToItem(drop);

                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        drop
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        ItemStack drop = new ItemStack(this);

        if (be instanceof CapacitorBlockEntity capacitor) {
            capacitor.saveToItem(drop);
        }

        return List.of(drop);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (!flag.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.pseudo.hold_shift_for_info")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        int storedEnergy = stack.getOrDefault(PseudoDataComponents.ENERGY.get(), 0);
        int capacity = CapacitorBlockEntity.ENERGY_CAPACITY;
        int pct = (int) Math.round((storedEnergy / (double) capacity) * 100.0D);
        if (pct < 0) pct = 0;
        if (pct > 100) pct = 100;

        ChatFormatting color;
        if (pct >= 75) color = ChatFormatting.GREEN;
        else if (pct >= 35) color = ChatFormatting.YELLOW;
        else color = ChatFormatting.RED;

        Component energyLine = Component.literal(String.valueOf(storedEnergy))
                .append(Component.literal(" "))
                .append(Component.literal("/").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" "))
                .append(Component.literal(String.valueOf(capacity)))
                .append(Component.literal(" FE").withStyle(ChatFormatting.DARK_AQUA));

        MutableComponent header = Component.translatable("tooltip.pseudo.capacitor.fill", pct)
                .withStyle(color);
        header.append(Component.literal(" (").withStyle(ChatFormatting.YELLOW))
                .append(energyLine)
                .append(Component.literal(")").withStyle(ChatFormatting.YELLOW));

        tooltip.add(header);

        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        java.util.ArrayList<ItemStack> stacks = new java.util.ArrayList<>();
        for (ItemStack s : contents.nonEmptyItems()) {
            stacks.add(s);
        }

        if (stacks.isEmpty()) return;

        stacks.sort((a, b) -> {
            int c = Integer.compare(b.getCount(), a.getCount());
            if (c != 0) return c;
            return a.getHoverName().getString().compareToIgnoreCase(b.getHoverName().getString());
        });

        int maxShown = 2;
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

        int remaining = stacks.size() - shown;
        if (remaining > 0) {
            tooltip.add(Component.translatable("container.shulkerBox.more", remaining)
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }

    private static boolean hasStoredData(CapacitorBlockEntity capacitor) {
        if (capacitor.getEnergyStorage(null).getEnergyStored() > 0) return true;

        ItemStackHandler handler = capacitor.getItemHandler(null);
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) return true;
        }

        return false;
    }
}
