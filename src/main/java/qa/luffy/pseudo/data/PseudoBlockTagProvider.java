package qa.luffy.pseudo.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class PseudoBlockTagProvider extends BlockTagsProvider {
    public PseudoBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RAW_GRAPHITE_BLOCK.get())
                .add(ModBlocks.REFINED_GRAPHITE_BLOCK.get())
                .add(ModBlocks.MESH_BLOCK.get())
                .add(ModBlocks.NETHER_GRAPHITE_ORE.get());
    }
}
