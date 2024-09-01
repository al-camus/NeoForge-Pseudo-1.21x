package qa.luffy.pseudo.common;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.slf4j.Logger;
import qa.luffy.pseudo.client.screen.CapacitorScreen;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.entity.PseudoBlockEntities;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.init.PseudoCreativeTabs;
import qa.luffy.pseudo.common.item.PseudoItems;
import qa.luffy.pseudo.common.menu.PseudoMenus;
import qa.luffy.pseudo.common.recipe.PseudoRecipeSerializers;
import qa.luffy.pseudo.common.recipe.PseudoRecipeTypes;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Pseudo.MODID)
public class Pseudo  {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "pseudo";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Pseudo(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        PseudoCreativeTabs.register(modEventBus);
        PseudoMenus.register(modEventBus);

        PseudoRecipeTypes.register(modEventBus);
        PseudoRecipeSerializers.register(modEventBus);

        PseudoDataComponents.register(modEventBus);
        PseudoItems.register(modEventBus);
        PseudoBlocks.register(modEventBus);
        PseudoBlockEntities.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        //Register capabilities
        modEventBus.addListener(this::onRegisterCapabilities);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        //Item Block Entities
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PseudoBlockEntities.CAPACITOR_TYPE.get(),
                (entity, direction) -> new SidedInvWrapper((WorldlyContainer) entity, direction));
        //Energy Block Entities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                PseudoBlockEntities.CAPACITOR_TYPE.get(),
                (entity, direction) -> ((EnergyStorageBlock) entity).getEnergyStorage(direction));
        //Energy Items
        event.registerItem(Capabilities.EnergyStorage.ITEM, (itemStack, context) -> {
            if(itemStack.getItem() instanceof EnergyStorageItem energyStorageItem){
                return energyStorageItem.getEnergy(itemStack);
            }
            return null;
        }, PseudoItems.CHAINSAW.get());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        private static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(PseudoMenus.CAPACITOR_MENU_TYPE.get(), CapacitorScreen::new);
        }
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
