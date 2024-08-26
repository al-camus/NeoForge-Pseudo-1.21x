package qa.luffy.pseudo.common.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;

import java.util.function.Supplier;

public class PseudoMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Pseudo.MODID);

    public static final Supplier<MenuType<CapacitorMenu>> CAPACITOR_MENU_TYPE = MENU_TYPES.register("capacitor",
            () -> new MenuType<>(CapacitorMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
