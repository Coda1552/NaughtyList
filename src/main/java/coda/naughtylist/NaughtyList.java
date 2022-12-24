package coda.naughtylist;

import coda.naughtylist.common.WinterRaid;
import coda.naughtylist.common.WinterRaidSavedData;
import coda.naughtylist.common.entity.Nutcracker;
import coda.naughtylist.common.entity.NutcrackerGeneral;
import coda.naughtylist.common.entity.WinterRaider;
import coda.naughtylist.common.entity.WoodenHorse;
import coda.naughtylist.registry.NLBlocks;
import coda.naughtylist.registry.NLEntities;
import coda.naughtylist.registry.NLItems;
import coda.naughtylist.registry.NLSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
        forgeBus.addListener(this::addGoals);
        bus.addListener(this::createAttributes);

        NLBlocks.BLOCKS.register(bus);
        NLEntities.ENTITIES.register(bus);
        NLItems.ITEMS.register(bus);
        NLSounds.SOUNDS.register(bus);
    }

    private void createAttributes(EntityAttributeCreationEvent e) {
        e.put(NLEntities.NUTCRACKER.get(), Nutcracker.createAttributes().build());
        e.put(NLEntities.WOODEN_HORSE.get(), WoodenHorse.createAttributes().build());
        e.put(NLEntities.NUTCRACKER_GENERAL.get(), NutcrackerGeneral.createAttributes().build());
    }

    private void levelTick(TickEvent.LevelTickEvent e) {
        Level eLevel = e.level;

        if (eLevel instanceof ServerLevel level) {
            WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));;

            raids.tick();
        }
    }

    private void blockBroken(BlockEvent.BreakEvent e) {
        if (e.getState().is(NLBlocks.SNOW_GLOBE.get()) && e.getLevel() instanceof ServerLevel level && e.getPlayer() instanceof ServerPlayer player && level.isVillage(e.getPos())) {
            WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));;

            raids.createOrExtendRaid(player);
        }
    }

    @Nullable
    public static WinterRaid getRaidAt(ServerLevel level, BlockPos pos) {
        WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));

        return raids.getNearbyRaid(pos, 9216);
    }

    private void addGoals(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof AbstractVillager villager) {
            villager.goalSelector.addGoal(0, new AvoidEntityGoal<>(villager, WinterRaider.class, 1.0F, 1.05D, 1.15D));
        }
        if (e.getEntity() instanceof IronGolem golem) {
            golem.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(golem, WinterRaider.class, false));
        }
    }
}
