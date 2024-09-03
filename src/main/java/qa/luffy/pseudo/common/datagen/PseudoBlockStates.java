package qa.luffy.pseudo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import qa.luffy.pseudo.common.Pseudo;
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
        blockWithItem(PseudoBlocks.GRAPHITE_DUST_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BLOCK);
        blockWithItem(PseudoBlocks.REFINED_GRAPHITE_BRICK);
        blockWithItem(PseudoBlocks.GRAPHENE_SHEET_BLOCK);
        blockWithItem(PseudoBlocks.MESH_BLOCK);

        pressurePlateBlock(((PressurePlateBlock) PseudoBlocks.MESH_PRESSURE_PLATE.get()), blockTexture(PseudoBlocks.MESH_BLOCK.get()));
        buttonBlock(((ButtonBlock) PseudoBlocks.MESH_BUTTON.get()), blockTexture(PseudoBlocks.MESH_BLOCK.get()));

        blockWithItem(PseudoBlocks.CAPACITOR_BLOCK);
        blockItem(PseudoBlocks.MESH_PRESSURE_PLATE);
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock){
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("pseudo:block/" + deferredBlock.getId().getPath()));
    }
}
