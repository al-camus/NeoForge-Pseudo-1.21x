package qa.luffy.pseudo.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.GoldenCarrotCropBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;

import java.util.Set;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PseudoEvents {
    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Post event) {
        Level level = (Level) event.getLevel();
        BlockPos cropPos = event.getPos();
        BlockState newState = event.getState();
        if (!newState.is(Blocks.CARROTS)) {
            return;
        }
        int carrotAge = newState.getValue(CropBlock.AGE);
        int maxAgeCarrot = ((CarrotBlock) newState.getBlock()).getMaxAge(); // should be 7
        BlockPos farmlandPos = cropPos.below();
        BlockPos goldPos = farmlandPos.below();
        BlockState goldState = level.getBlockState(goldPos);
        final Set<Block> VALID_GOLD_BLOCKS = Set.of(
                Blocks.GOLD_ORE,
                Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.NETHER_GOLD_ORE,
                Blocks.RAW_GOLD_BLOCK,
                Blocks.GOLD_BLOCK
        );
        if (!VALID_GOLD_BLOCKS.contains(goldState.getBlock())) {
            return;
        }
        float chance = 0.1f;
        if (level.getRandom().nextFloat() >= chance) {
            return;
        }
        // 5. Convert to golden carrot, preserving age
        BlockState goldenCarrotState = PseudoBlocks.GOLDEN_CARROT_CROP
                .get()
                .defaultBlockState()
                .setValue(GoldenCarrotCropBlock.AGE, carrotAge);

        level.setBlock(cropPos, goldenCarrotState, Block.UPDATE_ALL);
    }
}
