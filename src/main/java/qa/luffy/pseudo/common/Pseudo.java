package qa.luffy.pseudo.common;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.block.entity.CapacitorBlockEntity;
import qa.luffy.pseudo.common.block.entity.PseudoBlockEntities;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.init.PseudoArmorMaterials;
import qa.luffy.pseudo.common.init.PseudoCreativeModeTabs;
import qa.luffy.pseudo.common.item.PseudoItems;
import qa.luffy.pseudo.common.menu.PseudoMenus;
import qa.luffy.pseudo.common.recipe.PseudoCustomRecipes;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

@Mod(Pseudo.MODID)
public class Pseudo {
    public static final String MODID = "pseudo";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Pseudo(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onRegisterCapabilities);

        PseudoCreativeModeTabs.register(modEventBus);
        PseudoMenus.register(modEventBus);
        PseudoCustomRecipes.register(modEventBus);

        PseudoDataComponents.register(modEventBus);
        PseudoItems.register(modEventBus);
        PseudoBlocks.register(modEventBus);
        PseudoBlockEntities.register(modEventBus);

        PseudoArmorMaterials.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, PseudoConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
            pot.addPlant(PseudoBlocks.THISTLE.getId(), PseudoBlocks.POTTED_THISTLE);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PseudoBlockEntities.CAPACITOR_TYPE.get(),
                CapacitorBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                PseudoBlockEntities.CAPACITOR_TYPE.get(),
                CapacitorBlockEntity::getEnergyStorage
        );

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (itemStack, context) -> itemStack.getItem() instanceof EnergyStorageItem esi ? esi.getEnergy(itemStack) : null,
                PseudoItems.MESH_CHAINSAW.get(),
                PseudoItems.MESH_BATTERY.get(),
                PseudoItems.MESH_DRILL.get()
        );
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
