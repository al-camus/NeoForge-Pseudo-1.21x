package qa.luffy.pseudo.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import qa.luffy.pseudo.Pseudo;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PseudoDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        PseudoBlockTagProvider blockTagProvider = new PseudoBlockTagProvider(packOutput, registries, existingFileHelper);

        generator.addProvider(event.includeServer(), new PseudoRecipeProvider(packOutput, registries));
        generator.addProvider(event.includeServer(), new PseudoLootTableProvider(packOutput, registries));
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new PseudoItemTagProvider(packOutput, registries, blockTagProvider.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeClient(), new PseudoItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new PseudoBlockStateProvider(packOutput, existingFileHelper));
    }
}
