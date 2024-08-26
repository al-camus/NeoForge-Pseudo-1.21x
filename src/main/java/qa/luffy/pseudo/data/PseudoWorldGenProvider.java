package qa.luffy.pseudo.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.world.PseudoBiomeModifiers;
import qa.luffy.pseudo.world.PseudoConfiguredFeatures;
import qa.luffy.pseudo.world.PseudoPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PseudoWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PseudoConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PseudoPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, PseudoBiomeModifiers::bootstrap);

    public PseudoWorldGenProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, BUILDER, Set.of(Pseudo.MODID));
    }

    @Override
    public String getName() {
        return "World Gen";
    }
}
