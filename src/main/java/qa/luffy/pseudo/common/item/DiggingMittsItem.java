package qa.luffy.pseudo.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

public class DiggingMittsItem extends DiggerItem {

    public DiggingMittsItem(Tier pTier, TagKey<Block> blocks, Properties pProperties) {
        super(pTier, blocks, pProperties);
    }
}