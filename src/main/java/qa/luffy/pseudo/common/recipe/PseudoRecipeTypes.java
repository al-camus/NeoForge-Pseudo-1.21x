package qa.luffy.pseudo.common.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;

import java.util.function.Supplier;

public class PseudoRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Pseudo.MODID);

    public static final Supplier<RecipeType<CapacitorRecipe>> CAPACITOR = RECIPE_TYPES.register("capacitor",
                    () -> RecipeType.<CapacitorRecipe>simple(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "capacitor")));

    public static void register(IEventBus eventBus){
        RECIPE_TYPES.register(eventBus);
    }
}
