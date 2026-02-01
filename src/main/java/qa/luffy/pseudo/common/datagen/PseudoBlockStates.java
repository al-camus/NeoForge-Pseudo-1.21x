package qa.luffy.pseudo.common.datagen;

import net.minecraft.core.Direction;
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
import qa.luffy.pseudo.common.block.LedBlock;
import qa.luffy.pseudo.common.block.MeshLampBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;

public class PseudoBlockStates extends BlockStateProvider {
    public PseudoBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Pseudo.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Simple cube-all blocks with matching item models
        blockWithItem(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE);
        blockWithItem(PseudoBlocks.NETHER_GRAPHITE_ORE);
        blockWithItem(PseudoBlocks.RAW_GRAPHITE_BLOCK);
        blockWithItem(PseudoBlocks.GRAPHITE_DUST_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BRICK);
        blockWithItem(PseudoBlocks.GRAPHENE_SHEET_BLOCK);
        blockWithItem(PseudoBlocks.MESH_BLOCK);
        blockWithItem(PseudoBlocks.CAPACITOR_BLOCK);
        blockWithItem(PseudoBlocks.MESH_CRATE);

        // Stairs / slabs / fences / etc.
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

        //blockItems
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

        //crops
        cropBlock(
                (CropBlock) PseudoBlocks.GOLDEN_CARROT_CROP.get(),
                "golden_carrot_crop_stage",
                "golden_carrot_crop_stage"
        );

        //doors/trapdoors
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

        // light blocks
        customLamp();
        customLed();
    }

    // === Crops ===
    public void cropBlock(CropBlock block, String modelName, String textureName) {
        getVariantBuilder(block).forAllStates(state -> states(state, modelName, textureName));
    }

    // === Mesh Lamps (full cube) ===
    private void customLamp() {
        ResourceLocation lampOffTex = modLoc("block/mesh_lamp_off");
        ResourceLocation lampOnTex = modLoc("block/mesh_lamp_on");

        ModelFile lampOff = models().cubeAll("mesh_lamp_off", lampOffTex);
        ModelFile lampOn = models().cubeAll("mesh_lamp_on", lampOnTex);

        // Normal Mesh Lamp
        getVariantBuilder(PseudoBlocks.MESH_LAMP.get()).forAllStates(state -> {
            boolean active = state.getValue(MeshLampBlock.ACTIVATED);
            ModelFile model = active ? lampOn : lampOff;
            return new ConfiguredModel[]{new ConfiguredModel(model)};
        });
        simpleBlockItem(PseudoBlocks.MESH_LAMP.get(), lampOff);

        // Inverted Mesh Lamp
        getVariantBuilder(PseudoBlocks.MESH_LAMP_INVERTED.get()).forAllStates(state -> {
            boolean active = state.getValue(MeshLampBlock.ACTIVATED);
            ModelFile model = active ? lampOn : lampOff;
            return new ConfiguredModel[]{new ConfiguredModel(model)};
        });
        simpleBlockItem(PseudoBlocks.MESH_LAMP_INVERTED.get(), lampOn);
    }

    private void customLed() {
        ResourceLocation ledTex = modLoc("block/mesh_lamp_on");

        ModelFile ledUp    = ledPlateModel("led_up", ledTex, Direction.UP);
        ModelFile ledDown  = ledPlateModel("led_down", ledTex, Direction.DOWN);
        ModelFile ledNorth = ledPlateModel("led_north", ledTex, Direction.NORTH);
        ModelFile ledSouth = ledPlateModel("led_south", ledTex, Direction.SOUTH);
        ModelFile ledWest  = ledPlateModel("led_west", ledTex, Direction.WEST);
        ModelFile ledEast  = ledPlateModel("led_east", ledTex, Direction.EAST);

        getVariantBuilder(PseudoBlocks.LED.get()).forAllStates(state -> {
            Direction facing = state.getValue(LedBlock.FACING);
            ModelFile model = switch (facing) {
                case DOWN  -> ledDown;
                case UP    -> ledUp;
                case NORTH -> ledNorth;
                case SOUTH -> ledSouth;
                case WEST  -> ledWest;
                case EAST  -> ledEast;
            };
            return new ConfiguredModel[]{ new ConfiguredModel(model) };
        });

        simpleBlockItem(PseudoBlocks.LED.get(), ledUp);
    }

    private ModelFile ledPlateModel(String name, ResourceLocation texture, Direction facing) {
        var builder = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("all", texture)
                .texture("particle", texture);

        switch (facing) {
            case DOWN ->
                    builder.element().from(7.0F, 15.0F, 7.5F).to(9.0F, 16.0F, 8.5F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();

            case UP ->
                    builder.element().from(7.0F, 0.0F, 7.5F).to(9.0F, 1.0F, 8.5F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();

            case NORTH ->
                    builder.element().from(7.0F, 7.5F, 15.0F).to(9.0F, 8.5F, 16.0F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();

            case SOUTH ->
                    builder.element().from(7.0F, 7.5F, 0.0F).to(9.0F, 8.5F, 1.0F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();

            case WEST ->
                    builder.element().from(15.0F, 7.5F, 7.0F).to(16.0F, 8.5F, 9.0F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();

            case EAST ->
                    builder.element().from(0.0F, 7.5F, 7.0F).to(1.0F, 8.5F, 9.0F)
                            .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").end()
                            .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").end()
                            .end();
        }

        return builder;
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

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(
                deferredBlock.get(),
                new ModelFile.UncheckedModelFile("pseudo:block/" + deferredBlock.getId().getPath())
        );
    }
}
