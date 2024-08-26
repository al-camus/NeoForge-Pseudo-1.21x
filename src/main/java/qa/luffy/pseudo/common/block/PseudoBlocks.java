package qa.luffy.pseudo.common.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.function.Supplier;

public class PseudoBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Pseudo.MODID);
    //blocks
    public static final DeferredBlock<Block> DEEPSLATE_GRAPHITE_ORE = registerBlock("deepslate_graphite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> NETHER_GRAPHITE_ORE = registerBlock("nether_graphite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RAW_GRAPHITE_BLOCK = registerBlock("raw_graphite_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRAPHITE_DUST_BLOCK = registerBlock("graphite_dust_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REFINED_GRAPHITE_BLOCK = registerBlock("refined_graphite_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REFINED_GRAPHITE_BRICK = registerBlock("refined_graphite_brick", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRAPHENE_SHEET_BLOCK = registerBlock("graphene_sheet_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f, 4f)));
    public static final DeferredBlock<Block> MESH_BLOCK = registerBlock("mesh_block", () -> new Block(BlockBehaviour.Properties.of().strength(5f, 10000f)));
    //block entities
    public static final DeferredBlock<Block> CAPACITOR_BLOCK = registerBlock("capacitor",
            () -> new CapacitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

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
