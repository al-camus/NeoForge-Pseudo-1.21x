package qa.luffy.pseudo.common.datagen;

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
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PseudoRecipes extends RecipeProvider {
    public PseudoRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.GRAPHENE_MESH.get())
                .define('I', PseudoItems.GRAPHENE_SHEET.get())
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_SHEET.get()), has(PseudoItems.GRAPHENE_SHEET.get()))
                .save(recipeOutput, Pseudo.resource("graphene_mesh"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.GRAPHENE_SHEET.get())
                .define('I', PseudoItems.REFINED_GRAPHITE.get())
                .pattern("III")
                .unlockedBy(getHasName(PseudoItems.RAW_GRAPHITE.get()), has(PseudoItems.RAW_GRAPHITE.get()))
                .save(recipeOutput, Pseudo.resource("graphene_sheet"));

        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE, "raw_graphite_from_block", RecipeCategory.MISC, PseudoBlocks.RAW_GRAPHITE_BLOCK, "raw_graphite_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHITE_DUST, "graphite_dust_from_block", RecipeCategory.MISC, PseudoBlocks.GRAPHITE_DUST_BLOCK, "graphite_dust_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE, "refined_graphite_from_block", RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK, "refined_graphite_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHENE_SHEET, "graphene_sheet_from_block", RecipeCategory.MISC, PseudoBlocks.GRAPHENE_SHEET_BLOCK, "graphene_sheet_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHENE_MESH, "graphene_mesh_from_block", RecipeCategory.MISC, PseudoBlocks.MESH_BLOCK, "graphene_mesh_block");

        twoByTwoPacker(recipeOutput, RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BRICK, PseudoBlocks.REFINED_GRAPHITE_BLOCK);

        blasting(recipeOutput, List.of(Items.COAL), RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE.get(), 0.8f, 1600, PseudoItems.RAW_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(Items.CHARCOAL), RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE.get(), 0.8f, 1600, PseudoItems.RAW_GRAPHITE.getRegisteredName());
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
