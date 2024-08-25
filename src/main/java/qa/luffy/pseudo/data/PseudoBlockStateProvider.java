package qa.luffy.pseudo.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import qa.luffy.pseudo.Pseudo;

public class PseudoBlockStateProvider extends BlockStateProvider {
    public PseudoBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Pseudo.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }
}
