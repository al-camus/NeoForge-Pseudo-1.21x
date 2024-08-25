package qa.luffy.pseudo.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.ModBlocks;
import qa.luffy.pseudo.item.ModItems;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PseudoRecipeProvider extends RecipeProvider {
    public PseudoRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRAPHENE_MESH.get())
                .define('I', ModItems.GRAPHENE_SHEET.get())
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(ModItems.GRAPHENE_SHEET.get()), has(ModItems.GRAPHENE_SHEET.get()))
                .save(recipeOutput, Pseudo.resource("graphene_mesh"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRAPHENE_SHEET.get())
                .define('I', ModItems.RAW_GRAPHITE.get())
                .pattern("III")
                .unlockedBy(getHasName(ModItems.RAW_GRAPHITE.get()), has(ModItems.RAW_GRAPHITE.get()))
                .save(recipeOutput, Pseudo.resource("graphene_sheet"));

        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, ModItems.RAW_GRAPHITE, "raw_graphite_from_block", RecipeCategory.MISC, ModBlocks.RAW_GRAPHITE_BLOCK, "raw_graphite_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, ModItems.GRAPHENE_MESH, "graphene_mesh_from_block", RecipeCategory.MISC, ModBlocks.MESH_BLOCK, "graphene_mesh_block");

        blasting(recipeOutput, List.of(Items.COAL), RecipeCategory.MISC, ModItems.RAW_GRAPHITE.get(), 0.8f, 1600, ModItems.RAW_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(Items.CHARCOAL), RecipeCategory.MISC, ModItems.RAW_GRAPHITE.get(), 0.8f, 1600, ModItems.RAW_GRAPHITE.getRegisteredName());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    /**
     * Equivalent to nineBlockStorageRecipes, with the Psuedo namespace
     * @param recipeOutput
     * @param unpackedCategory
     * @param unpacked
     * @param packedCategory
     * @param packed
     */
    protected static void nineBlockStorageRecipe(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, DeferredItem<?> unpacked, String unpackedName, RecipeCategory packedCategory, DeferredBlock<?> packed, String packedName) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
                .requires(packed)
                .group(unpacked.getRegisteredName())
                .unlockedBy(getHasName(packed), has(packed))
                .save(recipeOutput, Pseudo.resource(unpackedName));

        ShapedRecipeBuilder.shaped(packedCategory, packed)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packed.getRegisteredName())
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput, Pseudo.resource(packedName));
    }

    /**
     * Equivalent to oreSmelting, with the Psuedo namespace
     * @param recipeOutput
     * @param ingredients
     * @param category
     * @param result
     * @param experience
     * @param cookingTime
     * @param group
     */
    protected static void smelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        cooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_smelting");
    }

    /**
     * Equivalent to oreBlasting, with the Psuedo namespace
     * @param recipeOutput
     * @param ingredients
     * @param category
     * @param result
     * @param experience
     * @param cookingTime
     * @param group
     */
    protected static void blasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        cooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
    }

    /**
     * Equivalent to oreCooking, with the Psuedo namespace
     * @param recipeOutput
     * @param serializer
     * @param recipeFactory
     * @param ingredients
     * @param category
     * @param result
     * @param experience
     * @param cookingTime
     * @param group
     * @param suffix
     * @param <T>
     */
    protected static <T extends AbstractCookingRecipe> void cooking(RecipeOutput recipeOutput, RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String suffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Pseudo.resource(getItemName(result) + suffix + "_" + getItemName(itemlike)));
        }

    }
}
