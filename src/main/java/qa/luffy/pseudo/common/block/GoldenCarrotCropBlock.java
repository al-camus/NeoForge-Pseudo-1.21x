package qa.luffy.pseudo.common.block;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class GoldenCarrotCropBlock extends CropBlock {
    public static int maxAge = 7;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);

    public GoldenCarrotCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(AGE, 0)
        );
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override protected ItemLike getBaseSeedId() {
        return Items.GOLDEN_CARROT;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

}

