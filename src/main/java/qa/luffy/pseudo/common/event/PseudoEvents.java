package qa.luffy.pseudo.common.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.GoldenCarrotCropBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;
import qa.luffy.pseudo.common.villager.PseudoVillagers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PseudoEvents {

    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Post event) {
        Level level = (Level) event.getLevel();
        BlockPos cropPos = event.getPos();
        BlockState newState = event.getState();
        if (!newState.is(Blocks.CARROTS)) {
            return;
        }
        int carrotAge = newState.getValue(CropBlock.AGE);
        int maxAgeCarrot = ((CarrotBlock) newState.getBlock()).getMaxAge(); // should be 7
        BlockPos farmlandPos = cropPos.below();
        BlockPos goldPos = farmlandPos.below();
        BlockState goldState = level.getBlockState(goldPos);
        final Set<Block> VALID_GOLD_BLOCKS = Set.of(
                Blocks.GOLD_ORE,
                Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.NETHER_GOLD_ORE,
                Blocks.RAW_GOLD_BLOCK,
                Blocks.GOLD_BLOCK
        );
        if (!VALID_GOLD_BLOCKS.contains(goldState.getBlock())) {
            return;
        }
        float chance = 0.1f;
        if (level.getRandom().nextFloat() >= chance) {
            return;
        }
        BlockState goldenCarrotState = PseudoBlocks.GOLDEN_CARROT_CROP
                .get()
                .defaultBlockState()
                .setValue(GoldenCarrotCropBlock.AGE, carrotAge);

        level.setBlock(cropPos, goldenCarrotState, Block.UPDATE_ALL);
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        if (event.getType() == VillagerProfession.TOOLSMITH) {
            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    Optional.of(new ItemCost(Items.COAL, 24)),
                    new ItemStack(PseudoItems.RAW_GRAPHITE.get(), 24),
                    8, 10, 0.05f
            ));
            return;
        }

        if (event.getType() == VillagerProfession.LEATHERWORKER) {
            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16),
                    Optional.of(new ItemCost(Items.LEATHER, 4)),
                    new ItemStack(PseudoItems.MOLE_MITTS.get(), 1),
                    3, 10, 0.05f
            ));
            return;
        }

        if (event.getType() == VillagerProfession.LIBRARIAN) {
            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    Optional.of(new ItemCost(Items.WRITABLE_BOOK, 1)),
                    new ItemStack(PseudoItems.CLIPBOARD.get(), 1),
                    3, 10, 0.05f
            ));

            trades.get(5).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 48),
                    new ItemStack(PseudoItems.SCULK_TOME.get(), 1),
                    1, 30, 0.05f
            ));
            return;
        }

        if (event.getType() == VillagerProfession.CLERIC) {
            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(PseudoItems.WARMING_STONE.get(), 1),
                    4, 10, 0.05f
            ));
            return;
        }

        if (event.getType() == VillagerProfession.FLETCHER) {
            trades.get(5).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 43),
                    new ItemStack(PseudoItems.SLINGSHOT.get(), 1),
                    1, 30, 0.05f
            ));
            return;
        }

        if (event.getType() == VillagerProfession.WEAPONSMITH) {
            trades.get(5).add((trader, random) -> {
                ItemStack sword = new ItemStack(PseudoItems.CURSED_SWORD.get(), 1);
                sword.set(DataComponents.CUSTOM_NAME, Component.literal("Kitetsu III"));
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 43),
                        sword,
                        1, 30, 0.05f
                );
            });
            return;
        }

        if (event.getType() == VillagerProfession.FISHERMAN) {
            trades.get(5).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.HEART_OF_THE_SEA, 1),
                    new ItemStack(PseudoItems.MOTHER_SEA_MUSIC_DISC.get(), 1),
                    1, 30, 0.05f
            ));
        }

        if (event.getType() == PseudoVillagers.SCIENTIST.value()) {

            trades.get(1).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(PseudoItems.RAW_GRAPHITE.get(), 4),
                    12, 2, 0.05f
            ));

            trades.get(1).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 64)),
                    new ItemStack(PseudoItems.GRAPHENE_MESH.get(), 1),
                    4, 2, 0.05f
            ));

            trades.get(1).add((trader, random) -> {
                if (random.nextBoolean()) {
                    return new MerchantOffer(
                            new ItemCost(Items.EMERALD, 24),
                            Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 12)),
                            new ItemStack(PseudoItems.MESH_GEAR.get(), 1),
                            2, 2, 0.05f
                    );
                }
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 32),
                        Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 24)),
                        new ItemStack(PseudoItems.MESH_BATTERY.get(), 1),
                        2, 2, 0.05f
                );
            });

            trades.get(2).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    Optional.of(new ItemCost(Items.IRON_INGOT, 12)),
                    new ItemStack(PseudoItems.MESH_MITTS.get(), 1),
                    1, 5, 0.05f
            ));

            trades.get(2).add((trader, random) -> {
                if (random.nextBoolean()) {
                    return new MerchantOffer(
                            new ItemCost(Items.EMERALD, 12),
                            Optional.of(new ItemCost(Items.IRON_INGOT, 4)),
                            new ItemStack(PseudoItems.IRON_DRILL_HEAD.get(), 1),
                            1, 5, 0.05f
                    );
                }
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 12),
                        Optional.of(new ItemCost(Items.IRON_INGOT, 4)),
                        new ItemStack(PseudoItems.IRON_CHAINSAW_HEAD.get(), 1),
                        1, 5, 0.05f
                );
            });

            trades.get(2).add((trader, random) -> {
                if (random.nextBoolean()) {
                    return new MerchantOffer(
                            new ItemCost(Items.EMERALD, 24),
                            Optional.of(new ItemCost(Items.IRON_INGOT, 24)),
                            new ItemStack(PseudoItems.DRILL_BASE.get(), 1),
                            1, 5, 0.05f
                    );
                }
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 24),
                        Optional.of(new ItemCost(Items.IRON_INGOT, 24)),
                        new ItemStack(PseudoItems.CHAINSAW_BASE.get(), 1),
                        1, 5, 0.05f
                );
            });

            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(PseudoBlocks.LED.get(), 1),
                    8, 10, 0.05f
            ));

            trades.get(3).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 48),
                    Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 32)),
                    new ItemStack(PseudoItems.TOOLBOX.get(), 1),
                    1, 10, 0.05f
            ));

            trades.get(3).add((trader, random) -> {
                if (random.nextBoolean()) {
                    return new MerchantOffer(
                            new ItemCost(Items.EMERALD, 64),
                            Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 64)),
                            new ItemStack(PseudoItems.MESH_CHAINSAW.get(), 1),
                            1, 10, 0.05f
                    );
                }
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 64),
                        Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 64)),
                        new ItemStack(PseudoItems.MESH_DRILL.get(), 1),
                        1, 10, 0.05f
                );
            });

            trades.get(4).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 32),
                    Optional.of(new ItemCost(Items.CRAFTER, 1)),
                    new ItemStack(PseudoItems.POCKET_CRAFTER.get(), 1),
                    1, 15, 0.05f
            ));

            trades.get(4).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 43),
                    Optional.of(new ItemCost(PseudoItems.MESH_BATTERY.get(), 1)),
                    new ItemStack(PseudoBlocks.CAPACITOR_BLOCK.get(), 1),
                    1, 15, 0.05f
            ));

            trades.get(4).add((trader, random) -> {
                int roll = random.nextInt(4);
                ItemStack out = switch (roll) {
                    case 0 -> new ItemStack(PseudoItems.MESH_HELMET.get(), 1);
                    case 1 -> new ItemStack(PseudoItems.MESH_CHESTPLATE.get(), 1);
                    case 2 -> new ItemStack(PseudoItems.MESH_LEGGINGS.get(), 1);
                    default -> new ItemStack(PseudoItems.MESH_BOOTS.get(), 1);
                };
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 64),
                        Optional.of(new ItemCost(PseudoItems.REFINED_GRAPHITE.get(), 64)),
                        out,
                        1, 15, 0.05f
                );
            });

            trades.get(5).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    Optional.of(new ItemCost(Items.ENDER_CHEST, 1)),
                    new ItemStack(PseudoItems.ENDER_KNAPSACK.get(), 1),
                    1, 30, 0.05f
            ));

            trades.get(5).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 43),
                    Optional.of(new ItemCost(Items.SHULKER_BOX, 1)),
                    new ItemStack(PseudoBlocks.MESH_CRATE.get(), 1),
                    1, 30, 0.05f
            ));
        }
    }

    @SubscribeEvent
    public static void addWanderingTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();

        genericTrades.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 7),
                new ItemStack(PseudoItems.SCULK_FRUIT.get(), 1),
                3, 0, 0.2f
        ));

        genericTrades.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                new ItemStack(PseudoItems.WIND_KNOTS.get(), 1),
                1, 0, 0.2f
        ));
    }
}