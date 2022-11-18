package coda.naughtylist.registry;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.common.entity.Nutcracker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NLEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NaughtyList.MOD_ID);

    public static final RegistryObject<EntityType<Nutcracker>> NUTCRACKER = ENTITIES.register("nutcracker", () -> EntityType.Builder.of(Nutcracker::new, MobCategory.MONSTER).sized(1.0F, 2.0F).build("nutcracker"));
}
