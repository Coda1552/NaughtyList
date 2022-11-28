package coda.naughtylist.registry;

import coda.naughtylist.NaughtyList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NLItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaughtyList.MOD_ID);

    public static final RegistryObject<Item> WOODEN_HORSE_SPAWN_EGG = ITEMS.register("wooden_horse_spawn_egg", () -> new ForgeSpawnEggItem(NLEntities.WOODEN_HORSE, 0x000000, 0x00000, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
