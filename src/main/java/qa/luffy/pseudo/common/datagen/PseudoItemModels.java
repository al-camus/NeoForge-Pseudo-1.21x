package qa.luffy.pseudo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
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

import java.util.LinkedHashMap;

public class PseudoItemModels extends ItemModelProvider {
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

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
        basicItem(PseudoItems.MESH_BATTERY.get());
        basicItem(PseudoItems.DRILL_BASE.get());
        basicItem(PseudoItems.CHAINSAW_BASE.get());
        basicItem(PseudoItems.IRON_DRILL_HEAD.get());
        basicItem(PseudoItems.IRON_CHAINSAW_HEAD.get());
        basicItem(PseudoItems.ENDER_KNAPSACK.get());
        basicItem(PseudoItems.MESH_HORSE_ARMOR.get());
        basicItem(PseudoItems.TOOLBOX.get());
        basicItem(PseudoItems.SCULK_FRUIT.get());
        basicItem(PseudoItems.SCULK_TOME.get());
        basicItem(PseudoItems.POCKET_CRAFTER.get());
        //handheld items
        handheldItem(PseudoItems.MESH_CHAINSAW);
        handheldItem(PseudoItems.MESH_DRILL);
        //armor items
        trimmedArmorItem(PseudoItems.MESH_HELMET);
        trimmedArmorItem(PseudoItems.MESH_CHESTPLATE);
        trimmedArmorItem(PseudoItems.MESH_LEGGINGS);
        trimmedArmorItem(PseudoItems.MESH_BOOTS);
        //misc
        buttonItem(PseudoBlocks.MESH_BUTTON, PseudoBlocks.MESH_BLOCK);
        fenceItem(PseudoBlocks.MESH_FENCE, PseudoBlocks.MESH_BLOCK);
        wallItem(PseudoBlocks.MESH_WALL, PseudoBlocks.MESH_BLOCK);
        basicItem(PseudoBlocks.MESH_DOOR.get().asItem());

        // MESH TRAPDOOR ITEM  (uses block model as icon, but DOESN'T assert existence)
        getBuilder(PseudoBlocks.MESH_TRAPDOOR.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile(
                        modLoc("block/mesh_trapdoor_bottom")
                ));
    }

    private void trimmedArmorItem(DeferredItem<Item> itemDeferredItem) {
        final String MOD_ID = Pseudo.MODID; // Change this to your mod id

        if (itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
    }

    public void buttonItem(DeferredBlock block, DeferredBlock<Block> baseBlock){
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(Pseudo.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(Pseudo.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public ItemModelBuilder generatedItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public void handheldItem(DeferredItem<Item> item) {
        withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/handheld")).texture("layer0", ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "item/" + item.getId().getPath()));
    }
}