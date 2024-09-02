package qa.luffy.pseudo.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import qa.luffy.pseudo.common.init.PseudoTags;

public class DiggingMittsItem extends DiggerItem {

    public DiggingMittsItem(Tier pTier, TagKey<Block> blocks, Properties pProperties) {
        super(pTier, blocks, pProperties);
    }
}