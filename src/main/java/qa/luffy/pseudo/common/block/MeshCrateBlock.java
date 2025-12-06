// qa/luffy/pseudo/common/block/MeshCrateBlock.java
package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import qa.luffy.pseudo.common.block.entity.MeshCrateBlockEntity;

public class MeshCrateBlock extends Block implements EntityBlock {

    public MeshCrateBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MeshCrateBlockEntity(pos, state);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack,
                                           BlockState state,
                                           Level level,
                                           BlockPos pos,
                                           Player player,
                                           InteractionHand hand,
                                           BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider) {
            player.openMenu(provider);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Called when right-clicking the block with *no* item (or after item use fails)
    @Override
    public InteractionResult useWithoutItem(BlockState state,
                                            Level level,
                                            BlockPos pos,
                                            Player player,
                                            BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider) {
            player.openMenu(provider);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // When placed, read contents from the item (if any)
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeshCrateBlockEntity crate) {
                crate.loadFromStack(stack);
            }
        }
    }

    // When broken by player, drop ONE crate item that keeps all contents
    @Override
    public BlockState playerWillDestroy(Level level,
                                        BlockPos pos,
                                        BlockState state,
                                        Player player) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeshCrateBlockEntity crate) {
                // Build crate item with embedded inventory
                ItemStack crateStack = new ItemStack(this.asItem());
                crateStack = crate.createStackWithContents(crateStack);

                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                level.addFreshEntity(new ItemEntity(level, x, y, z, crateStack));
            }
        }

        // ✅ Let vanilla handle stats, particles, block removal, etc.
        return super.playerWillDestroy(level, pos, state, player);
    }
}
