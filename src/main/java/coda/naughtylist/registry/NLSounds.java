package coda.naughtylist.registry;

import coda.naughtylist.NaughtyList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NLSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NaughtyList.MOD_ID);

    public static final RegistryObject<SoundEvent> CLACKING = create("nutcracker.clacking");
    public static final RegistryObject<SoundEvent> NUTCRACKER_AMBIENT = create("nutcracker.ambient");
    public static final RegistryObject<SoundEvent> NUTCRACKER_HURT = create("nutcracker.hurt");
    public static final RegistryObject<SoundEvent> NUTCRACKER_DEATH = create("nutcracker.death");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(NaughtyList.MOD_ID, name)));
    }
}
