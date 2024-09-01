package qa.luffy.pseudo.common.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import qa.luffy.pseudo.common.util.PseudoTags;

public class DiggingMittsItem extends DiggerItem {

    public DiggingMittsItem(Tier pTier, Properties pProperties) {
        super(pTier, PseudoTags.Blocks.MITTS_MINEABLE, pProperties);
    }
}