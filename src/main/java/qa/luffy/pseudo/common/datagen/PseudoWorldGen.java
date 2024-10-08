package qa.luffy.pseudo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.world.PseudoBiomeModifiers;
import qa.luffy.pseudo.common.world.PseudoConfiguredFeatures;
import qa.luffy.pseudo.common.world.PseudoPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PseudoWorldGen extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PseudoConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PseudoPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, PseudoBiomeModifiers::bootstrap);

    public PseudoWorldGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, BUILDER, Set.of(Pseudo.MODID));
    }

    @Override
    public String getName() {
        return "World Gen";
    }
}
