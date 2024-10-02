package qa.luffy.pseudo.common.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipe;

import java.util.function.Supplier;

public class PseudoCustomRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Pseudo.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Pseudo.MODID);


    public static final Supplier<RecipeSerializer<CapacitorRecipe>> CAPACITOR_SERIALIZER = SERIALIZERS.register(
            "energizing", CapacitorRecipe.Serializer::new);
    public static final Supplier<RecipeType<CapacitorRecipe>> CAPACITOR_TYPE = TYPES.register("energizing",
            () -> RecipeType.simple(Pseudo.resource("energizing")));


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
