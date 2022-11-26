package coda.naughtylist.registry;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.common.item.CandyCaneItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NLItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaughtyList.MOD_ID);
    public static final CreativeModeTab GROUP = new CreativeModeTab(NaughtyList.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(NLItems.CANDY_CANE.get());
        }
    };

    public static final RegistryObject<Item> CANDY_CANE = ITEMS.register("candy_cane", () -> new CandyCaneItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(251).tab(GROUP)));
    public static final RegistryObject<Item> NUTCRACKER_SPAWN_EGG = ITEMS.register("nutcracker_spawn_egg", () -> new ForgeSpawnEggItem(NLEntities.NUTCRACKER, 0x9a251b, 0xd7c185, new Item.Properties().tab(GROUP)));
    public static final RegistryObject<Item> WOODEN_HORSE_SPAWN_EGG = ITEMS.register("wooden_horse_spawn_egg", () -> new ForgeSpawnEggItem(NLEntities.WOODEN_HORSE, 0x82613a, 0x82613a, new Item.Properties().tab(GROUP)));
    public static final RegistryObject<Item> NUTCRACKER_GENERAL_SPAWN_EGG = ITEMS.register("nutcracker_general_spawn_egg", () -> new ForgeSpawnEggItem(NLEntities.NUTCRACKER_GENERAL, 0x607f1a, 0xd7c185, new Item.Properties().tab(GROUP)));
}
