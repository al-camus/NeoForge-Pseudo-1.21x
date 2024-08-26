package qa.luffy.pseudo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.item.PseudoItems;

public class PseudoItemModels extends ItemModelProvider {
    public PseudoItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PseudoItems.GRAPHITE_DUST.get());
        basicItem(PseudoItems.RAW_GRAPHITE.get());
        basicItem(PseudoItems.REFINED_GRAPHITE.get());
        basicItem(PseudoItems.GRAPHENE_SHEET.get());
        basicItem(PseudoItems.GRAPHENE_MESH.get());
        basicItem(PseudoItems.IRON_GEAR.get());
        basicItem(PseudoItems.CHAINSAW.get());
    }

    public ItemModelBuilder generatedItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder handheldItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }
}