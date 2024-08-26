package qa.luffy.pseudo.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.Tags;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;

import java.util.List;

public class PseudoConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GRAPHITE_ORE_KEY = registerKey("overworld_graphite_ore_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_GRAPHITE_ORE_KEY = registerKey("nether_graphite_ore_key");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchTest(Tags.Blocks.NETHERRACKS);

        List<OreConfiguration.TargetBlockState> overworldGraphiteOres = List.of(
                OreConfiguration.target(deepslateReplaceables, PseudoBlocks.DEEPSLATE_GRAPHITE_ORE.get().defaultBlockState())
        );

        register(context, OVERWORLD_GRAPHITE_ORE_KEY, Feature.ORE,
                new OreConfiguration(overworldGraphiteOres, 8));
        register(context, NETHER_GRAPHITE_ORE_KEY, Feature.ORE,
                new OreConfiguration(netherReplaceables, PseudoBlocks.NETHER_GRAPHITE_ORE.get().defaultBlockState(), 12));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Pseudo.resource(key));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
