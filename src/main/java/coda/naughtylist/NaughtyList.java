package coda.naughtylist;

import coda.naughtylist.common.WinterRaid;
import coda.naughtylist.common.WinterRaidSavedData;
import coda.naughtylist.common.entity.Nutcracker;
import coda.naughtylist.registry.NLEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(NaughtyList.MOD_ID)
public class NaughtyList {
    public static final String MOD_ID = "naughtylist";

    public NaughtyList() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        forgeBus.addListener(this::blockBroken);
        forgeBus.addListener(this::levelTick);
        bus.addListener(this::createAttributes);

        NLEntities.ENTITIES.register(bus);
    }

    private void createAttributes(EntityAttributeCreationEvent e) {
        e.put(NLEntities.NUTCRACKER.get(), Nutcracker.createAttributes().build());
    }

    private void levelTick(TickEvent.LevelTickEvent e) {
        Level eLevel = e.level;

        if (eLevel instanceof ServerLevel level) {
            WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));;

            raids.tick();
        }
    }

    private void blockBroken(BlockEvent.BreakEvent e) {
        if (e.getState().is(Blocks.CRYING_OBSIDIAN) && e.getLevel() instanceof ServerLevel level && e.getPlayer() instanceof ServerPlayer player) {
            WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));;

            raids.createOrExtendRaid(player);
        }
    }

    @Nullable
    public static WinterRaid getRaidAt(ServerLevel level, BlockPos pos) {
        WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));

        return raids.getNearbyRaid(pos, 9216);
    }
}
