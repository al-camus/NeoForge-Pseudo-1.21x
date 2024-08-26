package qa.luffy.pseudo.world;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import qa.luffy.pseudo.Pseudo;

import java.util.List;

public class PseudoPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_GRAPHITE_ORE_PLACED_KEY = registerKey("overworld_graphite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_GRAPHITE_ORE_PLACED_KEY = registerKey("nether_graphite_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, OVERWORLD_GRAPHITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(PseudoConfiguredFeatures.OVERWORLD_GRAPHITE_ORE_KEY),
                PseudoOrePlacements.commonOrePlacement(20,
                        HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(0))));

        register(context, NETHER_GRAPHITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(PseudoConfiguredFeatures.NETHER_GRAPHITE_ORE_KEY),
                PseudoOrePlacements.commonOrePlacement(10, PlacementUtils.RANGE_10_10));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Pseudo.resource(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}