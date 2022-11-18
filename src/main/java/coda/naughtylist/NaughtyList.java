package coda.naughtylist;

import coda.naughtylist.common.WinterRaidSavedData;
import coda.naughtylist.common.entity.Nutcracker;
import coda.naughtylist.registry.NLEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NaughtyList.MOD_ID)
public class NaughtyList {
    public static final String MOD_ID = "naughtylist";

    public NaughtyList() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::createAttributes);
    }

    private void createAttributes(EntityAttributeCreationEvent e) {
        e.put(NLEntities.NUTCRACKER.get(), Nutcracker.createAttributes().build());
    }

    private void checkRaid(TickEvent.LevelTickEvent e) {
        Level level = e.level;


    }


    // todo - caps?
/*    @Nullable
    public Raid getRaidAt(BlockPos p_8833_) {
        return this.raids.getNearbyRaid(p_8833_, 9216);
    }*/
}
