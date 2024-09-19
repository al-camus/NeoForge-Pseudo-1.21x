package qa.luffy.pseudo.common.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PseudoItems;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PseudoBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Pseudo.MODID);
    //blocks
    public static final DeferredBlock<Block> DEEPSLATE_GRAPHITE_ORE = registerBlock("deepslate_graphite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> NETHER_GRAPHITE_ORE = registerBlock("nether_graphite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RAW_GRAPHITE_BLOCK = registerSimpleBlock("raw_graphite_block", BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GRAPHITE_DUST_BLOCK = registerSimpleBlock("graphite_dust_block", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> REFINED_GRAPHITE_BLOCK = registerSimpleBlock("refined_graphite_block", BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> REFINED_GRAPHITE_BRICK = registerSimpleBlock("refined_graphite_brick", BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GRAPHENE_SHEET_BLOCK = registerBlock("graphene_sheet_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f, 4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MESH_BLOCK = registerBlock("mesh_block", () -> new Block(BlockBehaviour.Properties.of().strength(5f, 10000f)));
    public static final DeferredBlock<Block> MESH_PRESSURE_PLATE = registerBlock("mesh_pressure_plate", () -> new MeshPressurePlateBlock(BlockSetType.IRON,
            BlockBehaviour.Properties.of()
                    .strength(4f, 10000f).forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> MESH_BUTTON = registerBlock("mesh_button", () -> new ButtonBlock(BlockSetType.IRON, 5, BlockBehaviour.Properties.of().strength(4f, 10000f).noCollission()));
    //block-items
    public static final DeferredItem<BlockItem> RAW_GRAPHITE_BLOCK_ITEM = PseudoItems.ITEMS.register("raw_graphite_block", () -> {
        return new BlockItem(PseudoBlocks.RAW_GRAPHITE_BLOCK.get(), new Item.Properties()) {
            @Override
            public int getBurnTime(@NotNull ItemStack itemBlock, @Nullable RecipeType<?> recipeType) {
                return 16000;
            }
        };
    });
    public static final DeferredItem<BlockItem> GRAPHITE_DUST_BLOCK_ITEM = PseudoItems.ITEMS.register("graphite_dust_block", () -> {
        return new BlockItem(PseudoBlocks.GRAPHITE_DUST_BLOCK.get(), new Item.Properties()) {
            @Override
            public int getBurnTime(@NotNull ItemStack itemBlock, @Nullable RecipeType<?> recipeType) {
                return 8000;
            }
        };
    });
    public static final DeferredItem<BlockItem> REFINED_GRAPHITE_BLOCK_ITEM = PseudoItems.ITEMS.register("refined_graphite_block", () -> {
        return new BlockItem(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get(), new Item.Properties()) {
            @Override
            public int getBurnTime(@NotNull ItemStack itemBlock, @Nullable RecipeType<?> recipeType) {
                return 24000;
            }
        };
    });
    public static final DeferredItem<BlockItem> REFINED_GRAPHITE_BRICK_ITEM = PseudoItems.ITEMS.register("refined_graphite_brick", () -> {
        return new BlockItem(PseudoBlocks.REFINED_GRAPHITE_BRICK.get(), new Item.Properties()) {
            @Override
            public int getBurnTime(@NotNull ItemStack itemBlock, @Nullable RecipeType<?> recipeType) {
                return 24000;
            }
        };
    });
    //block entities
    public static final DeferredBlock<Block> CAPACITOR_BLOCK = registerBlock("capacitor",
            () -> new CapacitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    private static DeferredBlock<Block> registerSimpleBlock(String name, BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, Block::new, props);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PseudoItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
