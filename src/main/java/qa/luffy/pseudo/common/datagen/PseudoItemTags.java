package qa.luffy.pseudo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.concurrent.CompletableFuture;

public class PseudoItemTags extends ItemTagsProvider {
    public PseudoItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(PseudoItems.MESH_HELMET.get())
                .add(PseudoItems.MESH_CHESTPLATE.get())
                .add(PseudoItems.MESH_LEGGINGS.get())
                .add(PseudoItems.MESH_BOOTS.get());
    }
}
