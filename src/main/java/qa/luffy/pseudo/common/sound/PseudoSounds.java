package qa.luffy.pseudo.common.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;

import java.util.function.Supplier;

public class PseudoSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Pseudo.MODID);

    public static final Supplier<SoundEvent> MOTHER_SEA = registerSoundEvent("mother_sea");
    public static final ResourceKey<JukeboxSong> MOTHER_SEA_KEY = createSong("mother_sea");

    public static final Supplier<SoundEvent> CHAINSAW_MINE = registerSoundEvent("chainsaw_mine");
    public static final Supplier<SoundEvent> CHAINSAW_FAIL = registerSoundEvent("chainsaw_fail");

    public static final Supplier<SoundEvent> MESH_STEP = registerSoundEvent("mesh_step");

    public static final Supplier<SoundEvent> GRAPHENE_MESH_BREAK = registerSoundEvent("graphene_mesh_break");
    public static final Supplier<SoundEvent> GRAPHENE_MESH_PLACE = registerSoundEvent("graphene_mesh_place");
    public static final Supplier<SoundEvent> GRAPHENE_MESH_HIT = registerSoundEvent("graphene_mesh_hit");
    public static final Supplier<SoundEvent> GRAPHENE_MESH_FALL = registerSoundEvent("graphene_mesh_fall");

    public static final DeferredSoundType GRAPHENE_MESH = new DeferredSoundType(
            1.0f, 1.0f,
            PseudoSounds.GRAPHENE_MESH_BREAK,
            PseudoSounds.MESH_STEP,           // custom step
            PseudoSounds.GRAPHENE_MESH_PLACE,
            PseudoSounds.GRAPHENE_MESH_HIT,
            PseudoSounds.GRAPHENE_MESH_FALL
    );

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, name));
    }

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
