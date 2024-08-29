package qa.luffy.pseudo.common.recipe.capacitor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CapacitorRecipeSerializer implements RecipeSerializer<CapacitorRecipe> {
    public static final MapCodec<CapacitorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("energy").forGetter(CapacitorRecipe::getInputEnergy),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CapacitorRecipe::getInputItem),
            ItemStack.CODEC.fieldOf("result").forGetter(CapacitorRecipe::getResult)
    ).apply(inst, CapacitorRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CapacitorRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CapacitorRecipe::getInputEnergy,
                    Ingredient.CONTENTS_STREAM_CODEC, CapacitorRecipe::getInputItem,
                    ItemStack.STREAM_CODEC, CapacitorRecipe::getResult,
                    CapacitorRecipe::new
            );

    // Return our map codec.
    @Override
    public MapCodec<CapacitorRecipe> codec() {
        return CODEC;
    }

    // Return our stream codec.
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CapacitorRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
