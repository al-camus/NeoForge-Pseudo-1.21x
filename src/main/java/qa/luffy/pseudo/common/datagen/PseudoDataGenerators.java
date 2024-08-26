package qa.luffy.pseudo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.datagen.loot.PseudoLootTables;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PseudoDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        PseudoBlockTags blockTagProvider = new PseudoBlockTags(packOutput, registries, existingFileHelper);

        generator.addProvider(event.includeServer(), new PseudoRecipes(packOutput, registries));
        generator.addProvider(event.includeServer(), new PseudoLootTables(packOutput, registries));
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new PseudoItemTags(packOutput, registries, blockTagProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new PseudoWorldGenProvider(packOutput, registries));

        generator.addProvider(event.includeClient(), new PseudoItemModels(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new PseudoBlockStates(packOutput, existingFileHelper));
    }
}
