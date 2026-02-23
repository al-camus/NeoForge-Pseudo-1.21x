package qa.luffy.pseudo.common.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;

public class PseudoVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Pseudo.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, Pseudo.MODID);

    public static final Holder<PoiType> CLIPBOARD_POI = POI_TYPES.register("clipboard_poi",
            () -> new PoiType(ImmutableSet.copyOf(PseudoBlocks.CLIPBOARD_BLOCK.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final Holder<VillagerProfession> SCIENTIST = VILLAGER_PROFESSIONS.register("scientist",
            () -> new VillagerProfession("scientist", holder -> holder.value() == CLIPBOARD_POI.value(),
                    holder -> holder.value() == CLIPBOARD_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP));



    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}