package qa.luffy.pseudo.common.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.GoldenCarrotCropBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.Set;

public class PseudoBlockLootTables extends BlockLootSubProvider {
    public PseudoBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(PseudoBlocks.RAW_GRAPHITE_BLOCK.get());
        dropSelf(PseudoBlocks.COAL_DUST_BLOCK.get());
        dropSelf(PseudoBlocks.GRAPHITE_DUST_BLOCK.get());
        dropSelf(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get());
        dropSelf(PseudoBlocks.REFINED_GRAPHITE_STAIRS.get());
        this.add(PseudoBlocks.REFINED_GRAPHITE_SLAB.get(), this::createSlabItemTable);
        dropSelf(PseudoBlocks.REFINED_GRAPHITE_BRICK.get());
        dropSelf(PseudoBlocks.GRAPHITE_BRICK_STAIRS.get());
        this.add(PseudoBlocks.GRAPHITE_BRICK_SLAB.get(), this::createSlabItemTable);
        dropSelf(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get());
        dropSelf(PseudoBlocks.GRAPHENE_SHEET_STAIRS.get());
        this.add(PseudoBlocks.GRAPHENE_SHEET_SLAB.get(), this::createSlabItemTable);
        dropSelf(PseudoBlocks.MESH_BLOCK.get());
        dropSelf(PseudoBlocks.MESH_STAIRS.get());
        this.add(PseudoBlocks.MESH_SLAB.get(), this::createSlabItemTable);
        dropSelf(PseudoBlocks.MESH_LAMP.get());
        dropSelf(PseudoBlocks.MESH_LAMP_INVERTED.get());
        dropSelf(PseudoBlocks.MESH_FENCE.get());
        dropSelf(PseudoBlocks.MESH_FENCE_GATE.get());
        this.add(PseudoBlocks.MESH_DOOR.get(), this::createDoorTable);
        dropSelf(PseudoBlocks.MESH_TRAPDOOR.get());
        dropSelf(PseudoBlocks.MESH_WALL.get());
        dropSelf(PseudoBlocks.LED.get());

        dropSelf(PseudoBlocks.MESH_BUTTON.get());
        dropSelf(PseudoBlocks.MESH_PRESSURE_PLATE.get());
        dropSelf(PseudoBlocks.CAPACITOR_BLOCK.get());
        this.add(PseudoBlocks.MESH_CRATE.get(), block -> LootTable.lootTable());
        dropSelf(PseudoBlocks.TOOLBOX_BLOCK.get());

        this.add(PseudoBlocks.CLIPBOARD_BLOCK.get(), this::clipboardTable);

        add(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE.get(), ore -> createOreDrop(ore, PseudoItems.RAW_GRAPHITE.get()));
        add(PseudoBlocks.NETHER_GRAPHITE_ORE.get(), ore -> createOreDrop(ore, PseudoItems.RAW_GRAPHITE.get()));

        this.dropSelf(PseudoBlocks.THISTLE.get());
        this.add(PseudoBlocks.POTTED_THISTLE.get(), createPotFlowerItemTable(PseudoBlocks.THISTLE));

        LootItemCondition.Builder ripe =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(PseudoBlocks.GOLDEN_CARROT_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GoldenCarrotCropBlock.AGE, 7));

        this.add(PseudoBlocks.GOLDEN_CARROT_CROP.get(),
                this.createCropDrops(PseudoBlocks.GOLDEN_CARROT_CROP.get(), Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, ripe));
    }

    private LootTable.Builder clipboardTable(Block block) {
        return LootTable.lootTable().withPool(
                applyExplosionCondition(block,
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                .include(PseudoDataComponents.CLIPBOARD_CONTENT.get())
                                        )
                                )
                )
        );
    }

    protected LootTable.Builder createCustomCountOreDrop(Block block, NumberProvider count) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,
                        LootItem.lootTableItem(block)
                                .apply(SetItemCountFunction.setCount(count))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return PseudoBlocks.BLOCKS.getEntries().stream().map(Holder::value).toList();
    }
}
