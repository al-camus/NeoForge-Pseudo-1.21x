package qa.luffy.pseudo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;

public class PseudoItemModels extends ItemModelProvider {
    public PseudoItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //basic items
        basicItem(PseudoItems.GRAPHITE_DUST.get());
        basicItem(PseudoItems.RAW_GRAPHITE.get());
        basicItem(PseudoItems.REFINED_GRAPHITE.get());
        basicItem(PseudoItems.GRAPHENE_SHEET.get());
        basicItem(PseudoItems.GRAPHENE_MESH.get());
        basicItem(PseudoItems.MESH_GEAR.get());
        basicItem(PseudoItems.TOOLBOX.get());
        basicItem(PseudoItems.MESH_GEAR.get());
        basicItem(PseudoItems.MESH_HELMET.get());
        basicItem(PseudoItems.MESH_CHESTPLATE.get());
        basicItem(PseudoItems.MESH_LEGGINGS.get());
        basicItem(PseudoItems.MESH_BOOTS.get());
        basicItem(PseudoItems.SKULK_FRUIT.get());
        basicItem(PseudoItems.SKULK_TOME.get());
        //handheld items
        handheldItem(PseudoItems.MOLE_MITTS);
        handheldItem(PseudoItems.MESH_MITTS);
        handheldItem(PseudoItems.CHAINSAW);
        handheldItem(PseudoItems.DRILL);
        //misc
        buttonItem(PseudoBlocks.MESH_BUTTON, PseudoBlocks.MESH_BLOCK);
    }

    public void buttonItem(DeferredBlock block, DeferredBlock<Block> baseBlock){
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "block/" + baseBlock.getId().getPath()));
    }

    public ItemModelBuilder generatedItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder handheldItem(DeferredItem<Item> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/handheld")).texture("layer0", ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "item/" + item.getId().getPath()));
    }

}