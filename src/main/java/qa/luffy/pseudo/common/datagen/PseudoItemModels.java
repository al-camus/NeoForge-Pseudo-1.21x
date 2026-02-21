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
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();
    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public PseudoItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Pseudo.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PseudoItems.COAL_DUST.get());
        basicItem(PseudoItems.GRAPHITE_DUST.get());
        basicItem(PseudoItems.RAW_GRAPHITE.get());
        basicItem(PseudoItems.REFINED_GRAPHITE.get());
        basicItem(PseudoItems.CARBON_FILAMENT.get());
        basicItem(PseudoItems.CARBON_FIBER.get());
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
        basicItem(PseudoItems.WARMING_STONE.get());
        basicItem(PseudoItems.MOTHER_SEA_MUSIC_DISC.get());
        basicItem(PseudoItems.SCULK_FRUIT.get());
        basicItem(PseudoItems.SCULK_TOME.get());

        // NEW: weapon-style (handheld parent)
        handheldItem(PseudoItems.CRIMSON_DAGGER);

        trimmedArmorItem(PseudoItems.MESH_HELMET);
        trimmedArmorItem(PseudoItems.MESH_CHESTPLATE);
        trimmedArmorItem(PseudoItems.MESH_LEGGINGS);
        trimmedArmorItem(PseudoItems.MESH_BOOTS);

        buttonItem(PseudoBlocks.MESH_BUTTON, PseudoBlocks.MESH_BLOCK);
        fenceItem(PseudoBlocks.MESH_FENCE, PseudoBlocks.MESH_BLOCK);
        wallItem(PseudoBlocks.MESH_WALL, PseudoBlocks.MESH_BLOCK);

        basicItem(PseudoBlocks.MESH_DOOR.get().asItem());
        getBuilder(PseudoBlocks.MESH_TRAPDOOR.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/mesh_trapdoor_bottom")));

        flowerItem(PseudoBlocks.THISTLE);

        withExistingParent("clipboard", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/clipboard"));
    }

    private void handheldItem(DeferredItem<Item> itemDeferredItem) {
        String name = itemDeferredItem.getId().getPath();
        withExistingParent(name, mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + name));
    }

    private void trimmedArmorItem(DeferredItem<Item> itemDeferredItem) {
        if (!(itemDeferredItem.get() instanceof ArmorItem armorItem)) return;

        String baseName = itemDeferredItem.getId().getPath();
        ResourceLocation baseTex = modLoc("item/" + baseName);

        ItemModelBuilder base = withExistingParent(baseName, mcLoc("item/generated"))
                .texture("layer0", baseTex);

        String armorType = switch (armorItem.getEquipmentSlot()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "helmet";
        };

        TRIM_MATERIALS.forEach((trimKey, trimValue) -> {
            ResourceLocation trimLoc = trimKey.location();
            ResourceLocation trimTex = ResourceLocation.withDefaultNamespace(
                    "trims/items/" + armorType + "_trim_" + trimLoc.getPath()
            );

            existingFileHelper.trackGenerated(trimTex, PackType.CLIENT_RESOURCES, ".png", "textures");

            String trimModelName = baseName + "_" + trimLoc.getPath() + "_trim";

            getBuilder(trimModelName)
                    .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                    .texture("layer0", baseTex)
                    .texture("layer1", trimTex);

            base.override()
                    .predicate(mcLoc("trim_type"), trimValue)
                    .model(new ModelFile.UncheckedModelFile(Pseudo.MODID + ":item/" + trimModelName))
                    .end();
        });
    }

    public void buttonItem(DeferredBlock block, DeferredBlock<Block> baseBlock) {
        withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture", modLoc("block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture", modLoc("block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", modLoc("block/" + baseBlock.getId().getPath()));
    }

    public void flowerItem(DeferredBlock<Block> block) {
        withExistingParent(block.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + block.getId().getPath()));
    }
}
