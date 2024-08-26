package qa.luffy.pseudo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.PseudoBlocks;

import java.util.concurrent.CompletableFuture;

public class PseudoBlockTags extends BlockTagsProvider {
    public PseudoBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE.get())
                .add(PseudoBlocks.NETHER_GRAPHITE_ORE.get())
                .add(PseudoBlocks.RAW_GRAPHITE_BLOCK.get())
                .add(PseudoBlocks.GRAPHITE_DUST_BLOCK.get())
                .add(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get())
                .add(PseudoBlocks.REFINED_GRAPHITE_BRICK.get())
                .add(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get())
                .add(PseudoBlocks.CAPACITOR_BLOCK.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE.get())
                .add(PseudoBlocks.NETHER_GRAPHITE_ORE.get());
    }
}
