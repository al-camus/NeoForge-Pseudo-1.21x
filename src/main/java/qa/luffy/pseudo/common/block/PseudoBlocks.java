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
import net.minecraft.world.level.block.state.properties.WoodType;
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
    public static final DeferredBlock<Block> REFINED_GRAPHITE_STAIRS = registerBlock("refined_graphite_stairs", () -> new StairBlock(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REFINED_GRAPHITE_SLAB = registerBlock("refined_graphite_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REFINED_GRAPHITE_BRICK = registerSimpleBlock("refined_graphite_brick", BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GRAPHITE_BRICK_STAIRS = registerBlock("graphite_brick_stairs", () -> new StairBlock(PseudoBlocks.REFINED_GRAPHITE_BRICK.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRAPHITE_BRICK_SLAB = registerBlock("graphite_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRAPHENE_SHEET_BLOCK = registerBlock("graphene_sheet_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f, 4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRAPHENE_SHEET_STAIRS = registerBlock("graphene_sheet_stairs", () -> new StairBlock(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(4f, 4f)));
    public static final DeferredBlock<Block> GRAPHENE_SHEET_SLAB = registerBlock("graphene_sheet_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f, 4f)));
    public static final DeferredBlock<Block> MESH_BLOCK = registerBlock("mesh_block", () -> new Block(BlockBehaviour.Properties.of().strength(5f, 10000f)));
    public static final DeferredBlock<Block> MESH_STAIRS = registerBlock("mesh_stairs", () -> new StairBlock(PseudoBlocks.MESH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(5f, 10000f)));
    public static final DeferredBlock<Block> MESH_SLAB = registerBlock("mesh_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(5f, 10000f)));
    // Normal mesh lamp: OFF when placed, ON after first right-click
    public static final DeferredBlock<Block> MESH_LAMP = BLOCKS.register(
            "mesh_lamp",
            () -> new MeshLampBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.3F)
                            .lightLevel(state -> state.getValue(MeshLampBlock.ACTIVATED) ? 15 : 0),
                    false // defaultOn = false → OFF when crafted
            )
    );

    // Inverted mesh lamp: ON when placed, OFF after first right-click
    public static final DeferredBlock<Block> MESH_LAMP_INVERTED = BLOCKS.register(
            "mesh_lamp_inverted",
            () -> new MeshLampBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.3F)
                            .lightLevel(state -> state.getValue(MeshLampBlock.ACTIVATED) ? 15 : 0),
                    true // defaultOn = true → ON when crafted
            )
    );    public static final DeferredBlock<Block> MESH_PRESSURE_PLATE = registerBlock("mesh_pressure_plate", () -> new MeshPressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(4f, 10000f).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> MESH_BUTTON = registerBlock("mesh_button", () -> new MeshButtonBlock(BlockSetType.IRON, 5, BlockBehaviour.Properties.of().strength(4f, 10000f).noCollission()));
    public static final DeferredBlock<Block> MESH_FENCE = registerBlock("mesh_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(4f, 10000f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MESH_FENCE_GATE = registerBlock("mesh_fence_gate", () -> new FenceGateBlock(WoodType.CRIMSON, BlockBehaviour.Properties.of().strength(4f, 10000f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MESH_WALL = registerBlock("mesh_wall", () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f, 10000f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MESH_DOOR = registerBlock("mesh_door", () -> new DoorBlock(BlockSetType.CRIMSON, BlockBehaviour.Properties.of().strength(4f, 10000f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<Block> MESH_TRAPDOOR = registerBlock("mesh_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, BlockBehaviour.Properties.of().strength(4f, 10000f).requiresCorrectToolForDrops().noOcclusion()));

    public static final DeferredBlock<Block> GOLDEN_CARROT_CROP = BLOCKS.register("golden_carrot_crop", () -> new GoldenCarrotCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARROTS)));

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

    public static final DeferredItem<BlockItem> MESH_CRATE_ITEM = PseudoItems.ITEMS.register("mesh_crate", () -> {
        return new BlockItem(PseudoBlocks.MESH_CRATE.get(),
                new Item.Properties()
                        .stacksTo(1)
        );
    });

    //block entities
    public static final DeferredBlock<Block> CAPACITOR_BLOCK = registerBlock("capacitor",
            () -> new CapacitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<Block> MESH_CRATE = BLOCKS.register("mesh_crate",
            () -> new MeshCrateBlock(BlockBehaviour.Properties.of().strength(3.0F, 6.0F)));

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
