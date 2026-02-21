package qa.luffy.pseudo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.GoldenCarrotCropBlock;
import qa.luffy.pseudo.common.block.MeshLampBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;

public class PseudoBlockStates extends BlockStateProvider {
    public PseudoBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Pseudo.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE);
        blockWithItem(PseudoBlocks.NETHER_GRAPHITE_ORE);
        blockWithItem(PseudoBlocks.RAW_GRAPHITE_BLOCK);
        blockWithItem(PseudoBlocks.COAL_DUST_BLOCK);
        blockWithItem(PseudoBlocks.GRAPHITE_DUST_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BRICK);
        blockWithItem(PseudoBlocks.GRAPHENE_SHEET_BLOCK);
        blockWithItem(PseudoBlocks.MESH_BLOCK);
        blockWithItem(PseudoBlocks.CAPACITOR_BLOCK);

        stairsBlock((StairBlock) PseudoBlocks.REFINED_GRAPHITE_STAIRS.get(),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get()));
        slabBlock((SlabBlock) PseudoBlocks.REFINED_GRAPHITE_SLAB.get(),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get()),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get()));
        stairsBlock((StairBlock) PseudoBlocks.GRAPHITE_BRICK_STAIRS.get(),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BRICK.get()));
        slabBlock((SlabBlock) PseudoBlocks.GRAPHITE_BRICK_SLAB.get(),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BRICK.get()),
                blockTexture(PseudoBlocks.REFINED_GRAPHITE_BRICK.get()));
        stairsBlock((StairBlock) PseudoBlocks.GRAPHENE_SHEET_STAIRS.get(),
                blockTexture(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get()));
        slabBlock((SlabBlock) PseudoBlocks.GRAPHENE_SHEET_SLAB.get(),
                blockTexture(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get()),
                blockTexture(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get()));
        stairsBlock((StairBlock) PseudoBlocks.MESH_STAIRS.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        slabBlock((SlabBlock) PseudoBlocks.MESH_SLAB.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        fenceBlock((FenceBlock) PseudoBlocks.MESH_FENCE.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        fenceGateBlock((FenceGateBlock) PseudoBlocks.MESH_FENCE_GATE.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        wallBlock((WallBlock) PseudoBlocks.MESH_WALL.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        pressurePlateBlock((PressurePlateBlock) PseudoBlocks.MESH_PRESSURE_PLATE.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        buttonBlock((ButtonBlock) PseudoBlocks.MESH_BUTTON.get(),
                blockTexture(PseudoBlocks.MESH_BLOCK.get()));

        blockItem(PseudoBlocks.REFINED_GRAPHITE_STAIRS);
        blockItem(PseudoBlocks.REFINED_GRAPHITE_SLAB);
        blockItem(PseudoBlocks.GRAPHITE_BRICK_STAIRS);
        blockItem(PseudoBlocks.GRAPHITE_BRICK_SLAB);
        blockItem(PseudoBlocks.GRAPHENE_SHEET_STAIRS);
        blockItem(PseudoBlocks.GRAPHENE_SHEET_SLAB);
        blockItem(PseudoBlocks.MESH_STAIRS);
        blockItem(PseudoBlocks.MESH_SLAB);
        blockItem(PseudoBlocks.MESH_PRESSURE_PLATE);
        blockItem(PseudoBlocks.MESH_FENCE);
        blockItem(PseudoBlocks.MESH_FENCE_GATE);
        blockItem(PseudoBlocks.MESH_WALL);

        simpleBlock(PseudoBlocks.THISTLE.get(), models().cross(blockTexture(PseudoBlocks.THISTLE.get()).getPath(), blockTexture(PseudoBlocks.THISTLE.get())).renderType("cutout"));
        simpleBlock(PseudoBlocks.POTTED_THISTLE.get(), models().singleTexture("potted_thistle", ResourceLocation.parse("flower_pot_cross"), "plant", blockTexture(PseudoBlocks.THISTLE.get())).renderType("cutout"));
        cropBlock(
                (CropBlock) PseudoBlocks.GOLDEN_CARROT_CROP.get(),
                "golden_carrot_crop_stage",
                "golden_carrot_crop_stage"
        );

        doorBlockWithRenderType(
                (DoorBlock) PseudoBlocks.MESH_DOOR.get(),
                modLoc("block/mesh_door_bottom"),
                modLoc("block/mesh_door_top"),
                "cutout"
        );
        trapdoorBlockWithRenderType(
                (TrapDoorBlock) PseudoBlocks.MESH_TRAPDOOR.get(),
                modLoc("block/mesh_trapdoor"),
                true,
                "cutout"
        );

        customLamp();
        meshCrateBlock();
        toolboxBlock();
        clipboardBlock();
    }

    public void cropBlock(CropBlock block, String modelName, String textureName) {
        getVariantBuilder(block).forAllStates(state -> states(state, modelName, textureName));
    }

    private void customLamp() {
        ResourceLocation lampOffTex = modLoc("block/mesh_lamp_off");
        ResourceLocation lampOnTex = modLoc("block/mesh_lamp_on");

        ModelFile lampOff = models().cubeAll("mesh_lamp_off", lampOffTex);
        ModelFile lampOn = models().cubeAll("mesh_lamp_on", lampOnTex);

        getVariantBuilder(PseudoBlocks.MESH_LAMP.get()).forAllStates(state -> {
            boolean active = state.getValue(MeshLampBlock.ACTIVATED);
            ModelFile model = active ? lampOn : lampOff;
            return new ConfiguredModel[]{new ConfiguredModel(model)};
        });
        simpleBlockItem(PseudoBlocks.MESH_LAMP.get(), lampOff);

        getVariantBuilder(PseudoBlocks.MESH_LAMP_INVERTED.get()).forAllStates(state -> {
            boolean active = state.getValue(MeshLampBlock.ACTIVATED);
            ModelFile model = active ? lampOn : lampOff;
            return new ConfiguredModel[]{new ConfiguredModel(model)};
        });
        simpleBlockItem(PseudoBlocks.MESH_LAMP_INVERTED.get(), lampOn);
    }

    private void meshCrateBlock() {
        ModelFile model = models().getExistingFile(modLoc("block/mesh_crate"));
        horizontalBlock(PseudoBlocks.MESH_CRATE.get(), state -> model);
    }

    private void toolboxBlock() {
        ModelFile model = models().getExistingFile(modLoc("block/toolbox"));
        horizontalBlock(PseudoBlocks.TOOLBOX_BLOCK.get(), state -> model);
    }

    private void clipboardBlock() {
        ModelFile model = new ModelFile.UncheckedModelFile(modLoc("block/clipboard"));
        horizontalBlock(PseudoBlocks.CLIPBOARD_BLOCK.get(), state -> model);
    }

    private ConfiguredModel[] states(BlockState state, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        int age = state.getValue(GoldenCarrotCropBlock.AGE);
        int stage = age / 2;
        models[0] = new ConfiguredModel(
                models().crop(
                        modelName + stage,
                        ResourceLocation.fromNamespaceAndPath(
                                Pseudo.MODID,
                                "block/" + textureName + stage
                        )
                ).renderType("cutout")
        );
        return models;
    }

    private <T extends Block> void blockWithItem(DeferredBlock<T> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private <T extends Block> void blockItem(DeferredBlock<T> deferredBlock) {
        simpleBlockItem(
                deferredBlock.get(),
                new ModelFile.UncheckedModelFile("pseudo:block/" + deferredBlock.getId().getPath())
        );
    }

}
