package qa.luffy.pseudo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.PseudoBlocks;

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

        blockWithItem(PseudoBlocks.CAPACITOR_BLOCK);
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
