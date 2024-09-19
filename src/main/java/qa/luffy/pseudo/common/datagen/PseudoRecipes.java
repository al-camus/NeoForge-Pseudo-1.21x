package qa.luffy.pseudo.common.datagen;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;
import qa.luffy.pseudo.common.recipe.capacitor.CapacitorRecipeBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PseudoRecipes extends RecipeProvider {
    public PseudoRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        twoByTwo(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHENE_MESH.get(), PseudoItems.GRAPHENE_SHEET.get(), 1, "graphene_mesh_from_sheets");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.GRAPHENE_SHEET.get())
                .define('G', PseudoItems.REFINED_GRAPHITE.get())
                .pattern("GGG")
                .unlockedBy(getHasName(PseudoItems.RAW_GRAPHITE.get()), has(PseudoItems.RAW_GRAPHITE.get()))
                .save(recipeOutput, Pseudo.resource("graphene_sheet_from_refined_graphite"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_GEAR.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('I', Items.IRON_INGOT)
                .pattern("MIM")
                .pattern("I I")
                .pattern("MIM")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_gear"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_BATTERY.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .pattern(" C ")
                .pattern("MRM")
                .pattern("MRM")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.DRILL_BASE.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('B', PseudoItems.MESH_BATTERY.get())
                .define('G', PseudoItems.MESH_GEAR.get())
                .pattern(" G ")
                .pattern("MBM")
                .pattern("MM ")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("drill_base_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.DRILL_BASE.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('B', PseudoItems.MESH_BATTERY.get())
                .define('G', PseudoItems.MESH_GEAR.get())
                .pattern(" G ")
                .pattern("MBM")
                .pattern(" MM")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("drill_base_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.CHAINSAW_BASE.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('B', PseudoItems.MESH_BATTERY.get())
                .define('G', PseudoItems.MESH_GEAR.get())
                .pattern(" MG")
                .pattern("MBM")
                .pattern(" M ")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("chainsaw_base_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.CHAINSAW_BASE.get())
                .define('M', PseudoItems.GRAPHENE_MESH.get())
                .define('B', PseudoItems.MESH_BATTERY.get())
                .define('G', PseudoItems.MESH_GEAR.get())
                .pattern("GM ")
                .pattern("MBM")
                .pattern(" M ")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("chainsaw_base_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.IRON_DRILL_HEAD.get())
                .define('I', Items.IRON_INGOT)
                .pattern("III")
                .pattern("II ")
                .pattern("I  ")
                .unlockedBy(getHasName(PseudoItems.DRILL_BASE.get()), has(PseudoItems.DRILL_BASE.get()))
                .save(recipeOutput, Pseudo.resource("iron_drill_head_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.IRON_DRILL_HEAD.get())
                .define('I', Items.IRON_INGOT)
                .pattern("III")
                .pattern(" II")
                .pattern("  I")
                .unlockedBy(getHasName(PseudoItems.DRILL_BASE.get()), has(PseudoItems.DRILL_BASE.get()))
                .save(recipeOutput, Pseudo.resource("iron_drill_head_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.IRON_CHAINSAW_HEAD.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Items.CHAIN)
                .pattern("IC ")
                .pattern("CIC")
                .pattern(" CI")
                .unlockedBy(getHasName(PseudoItems.CHAINSAW_BASE.get()), has(PseudoItems.CHAINSAW_BASE.get()))
                .save(recipeOutput, Pseudo.resource("iron_chainsaw_head_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.IRON_CHAINSAW_HEAD.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Items.CHAIN)
                .pattern(" CI")
                .pattern("CIC")
                .pattern("IC ")
                .unlockedBy(getHasName(PseudoItems.CHAINSAW_BASE.get()), has(PseudoItems.CHAINSAW_BASE.get()))
                .save(recipeOutput, Pseudo.resource("iron_chainsaw_head_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_DRILL.get())
                .define('H', PseudoItems.IRON_DRILL_HEAD)
                .define('B', PseudoItems.DRILL_BASE)
                .pattern("HB")
                .unlockedBy(getHasName(PseudoItems.IRON_DRILL_HEAD.get()), has(PseudoItems.IRON_DRILL_HEAD.get()))
                .save(recipeOutput, Pseudo.resource("mesh_drill"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_CHAINSAW.get())
                .define('H', PseudoItems.IRON_CHAINSAW_HEAD)
                .define('B', PseudoItems.CHAINSAW_BASE)
                .pattern("HB")
                .unlockedBy(getHasName(PseudoItems.IRON_CHAINSAW_HEAD.get()), has(PseudoItems.IRON_CHAINSAW_HEAD.get()))
                .save(recipeOutput, Pseudo.resource("mesh_chainsaw"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MOLE_MITTS.get())
                .define('F', Items.FLINT)
                .define('L', Items.LEATHER)
                .pattern("FFF")
                .pattern("FLL")
                .pattern(" LF")
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .save(recipeOutput, Pseudo.resource("mole_mitts_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MOLE_MITTS.get())
                .define('F', Items.FLINT)
                .define('L', Items.LEATHER)
                .pattern("FFF")
                .pattern("LLF")
                .pattern("FL ")
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .save(recipeOutput, Pseudo.resource("mole_mitts_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_MITTS.get())
                .define('I', Items.IRON_INGOT)
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("III")
                .pattern("IMM")
                .pattern(" MI")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_mitts_left"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_MITTS.get())
                .define('I', Items.IRON_INGOT)
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("III")
                .pattern("MMI")
                .pattern("IM ")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_mitts_right"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_HELMET.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("MMM")
                .pattern("M M")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_helmet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_CHESTPLATE.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("M M")
                .pattern("MMM")
                .pattern("MMM")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_chestplate"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_LEGGINGS.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("MMM")
                .pattern("M M")
                .pattern("M M")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_leggings"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.MESH_BOOTS.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .pattern("M M")
                .pattern("M M")
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("mesh_boots"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.ENDER_KNAPSACK.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .define('E', Items.ENDER_EYE)
                .pattern("MMM")
                .pattern("MEM")
                .pattern("MMM")
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .unlockedBy(getHasName(PseudoItems.GRAPHENE_MESH.get()), has(PseudoItems.GRAPHENE_MESH.get()))
                .save(recipeOutput, Pseudo.resource("ender_knapsack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.SCULK_FRUIT.get())
                .define('S', Items.SCULK_VEIN)
                .define('A', Items.APPLE)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .unlockedBy(getHasName(Items.SCULK_VEIN), has(Items.SCULK_VEIN))
                .save(recipeOutput, Pseudo.resource("sculk_apple"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoItems.SCULK_TOME.get())
                .define('S', Items.SCULK_VEIN)
                .define('E', Items.ENCHANTED_BOOK)
                .pattern("SSS")
                .pattern("SES")
                .pattern("SSS")
                .unlockedBy(getHasName(Items.SCULK_VEIN), has(Items.SCULK_VEIN))
                .save(recipeOutput, Pseudo.resource("sculk_tome"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PseudoBlocks.CAPACITOR_BLOCK.get())
                .define('M', PseudoItems.GRAPHENE_MESH)
                .define('B', PseudoItems.MESH_BATTERY)
                .pattern("MMM")
                .pattern("BBB")
                .pattern("MMM")
                .unlockedBy(getHasName(PseudoItems.MESH_BATTERY), has(PseudoItems.MESH_BATTERY))
                .save(recipeOutput, Pseudo.resource("capacitor_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 9)
                .requires(PseudoBlocks.REFINED_GRAPHITE_BRICK)
                .unlockedBy(getHasName(PseudoItems.REFINED_GRAPHITE), has(PseudoItems.REFINED_GRAPHITE.get()))
                .save(recipeOutput, Pseudo.resource("refined_graphite_from_brick"));

        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE, "raw_graphite_from_block", RecipeCategory.MISC, PseudoBlocks.RAW_GRAPHITE_BLOCK, "raw_graphite_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHITE_DUST, "graphite_dust_from_block", RecipeCategory.MISC, PseudoBlocks.GRAPHITE_DUST_BLOCK, "graphite_dust_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE, "refined_graphite_from_block", RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK, "refined_graphite_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHENE_SHEET, "graphene_sheet_from_block", RecipeCategory.MISC, PseudoBlocks.GRAPHENE_SHEET_BLOCK, "graphene_sheet_block");
        nineBlockStorageRecipe(recipeOutput, RecipeCategory.MISC, PseudoItems.GRAPHENE_MESH, "graphene_mesh_from_block", RecipeCategory.MISC, PseudoBlocks.MESH_BLOCK, "mesh_block");

        twoByTwo(recipeOutput, RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BRICK, PseudoBlocks.REFINED_GRAPHITE_BLOCK, 4, "refined_graphite_brick_from_block");

        pressurePlate(recipeOutput, PseudoBlocks.MESH_PRESSURE_PLATE.get(), PseudoBlocks.MESH_BLOCK.get());
        buttonBuilder(PseudoBlocks.MESH_BUTTON.get(), Ingredient.of(PseudoBlocks.MESH_BLOCK.get())).group("graphene_mesh_block").unlockedBy("has_mesh_block", has(PseudoBlocks.MESH_BLOCK.get())).save(recipeOutput);

        smelting(recipeOutput, List.of(PseudoItems.GRAPHITE_DUST.get()), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 200, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        smelting(recipeOutput, List.of(PseudoItems.RAW_GRAPHITE.get()), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 200, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        smelting(recipeOutput, List.of(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 200, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        smelting(recipeOutput, List.of(PseudoBlocks.NETHER_GRAPHITE_ORE), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 200, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        smelting(recipeOutput, List.of(PseudoBlocks.GRAPHITE_DUST_BLOCK.get()), RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK.get(), 0.8f, 400, PseudoBlocks.REFINED_GRAPHITE_BLOCK.getRegisteredName());
        smelting(recipeOutput, List.of(PseudoBlocks.RAW_GRAPHITE_BLOCK.get()), RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK.get(), 0.8f, 400, PseudoBlocks.REFINED_GRAPHITE_BLOCK.getRegisteredName());

        blasting(recipeOutput, List.of(Items.COAL), RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE.get(), 0.8f, 200, PseudoItems.RAW_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(Items.CHARCOAL), RecipeCategory.MISC, PseudoItems.RAW_GRAPHITE.get(), 0.8f, 200, PseudoItems.RAW_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(Items.COAL_BLOCK), RecipeCategory.MISC, PseudoBlocks.RAW_GRAPHITE_BLOCK.get(), 0.8f, 400, PseudoBlocks.RAW_GRAPHITE_BLOCK.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoItems.GRAPHITE_DUST.get()), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 100, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoItems.RAW_GRAPHITE.get()), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 100, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 100, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoBlocks.NETHER_GRAPHITE_ORE), RecipeCategory.MISC, PseudoItems.REFINED_GRAPHITE.get(), 0.8f, 100, PseudoItems.REFINED_GRAPHITE.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoBlocks.GRAPHITE_DUST_BLOCK.get()), RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK.get(), 0.8f, 200, PseudoBlocks.REFINED_GRAPHITE_BLOCK.getRegisteredName());
        blasting(recipeOutput, List.of(PseudoBlocks.RAW_GRAPHITE_BLOCK.get()), RecipeCategory.MISC, PseudoBlocks.REFINED_GRAPHITE_BLOCK.get(), 0.8f, 200, PseudoBlocks.REFINED_GRAPHITE_BLOCK.getRegisteredName());

        energizing(recipeOutput, Ingredient.of(Items.OAK_LOG, Items.DARK_OAK_LOG), 100, new ItemStack(Items.CHARCOAL, 4), null); //TEST RECIPE
        energizing(recipeOutput, Ingredient.of(Items.ACACIA_LOG, Items.BIRCH_LOG), 50, new ItemStack(Items.CHARCOAL, 2), "lower_output"); //TEST RECIPE
        energizing(recipeOutput, Ingredient.of(Items.REDSTONE_ORE), 200, new ItemStack(Items.STONE, 1), "lower_output"); //TEST RECIPE
        energizing(recipeOutput, Ingredient.of(Items.REDSTONE), 200, new ItemStack(Items.RED_DYE, 1), "lower_output"); //TEST RECIPE

    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    /**
     * Equivalent to twoByTwoPacker, with the Pseudo namespace
     * @param recipeOutput
     * @param category
     * @param unpacked
     */
    protected static void twoByTwo(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, int count, String name) {
        ShapedRecipeBuilder.shaped(category, packed, count)
                .define('#', unpacked)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput, Pseudo.resource(name));
    }

    /**
     * Reverse of twoByTwoPacker, with the Pseudo namespace
     * @param recipeOutput
     * @param category
     * @param packed
     * @param unpacked
     */
    protected static void twoByTwoUnpack(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, String name) {
        ShapelessRecipeBuilder.shapeless(category, unpacked, 4)
                .requires(packed)
                .unlockedBy(getHasName(packed), has(packed))
                .save(recipeOutput, Pseudo.resource(name));
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

    /**
     * Recipes for the Capacitor
     * @param recipeOutput
     * @param input
     * @param energy in FE
     * @param result
     * @param suffix
     */
    protected static void energizing(RecipeOutput recipeOutput, Ingredient input, int energy, ItemStack result, @Nullable String suffix) {
        if (suffix == null) suffix = "";
        else suffix = "_" + suffix;
        new CapacitorRecipeBuilder(result, energy, input)
                .save(recipeOutput, Pseudo.resource(getItemName(result.getItem()) + suffix + "_energizing"));
    }
}
