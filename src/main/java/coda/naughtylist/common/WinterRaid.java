package coda.naughtylist.common;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.common.entity.util.WinterRaider;
import coda.naughtylist.registry.NLEntities;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WinterRaid {
    private static final Component RAID_NAME_COMPONENT = Component.translatable("event.naughtylist.raid");
    private static final Component VICTORY = Component.translatable("event.naughtylist.raid.victory");
    private static final Component DEFEAT = Component.translatable("event.naughtylist.raid.defeat");
    private static final Component RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final Component RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
    private final Map<Integer, WinterRaider> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<WinterRaider>> groupRaiderMap = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerLevel level;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int naughtyLevel;
    private boolean active;
    private int groupsSpawned;
    private final ServerBossEvent raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);
    private int postRaidTicks;
    private int raidCooldownTicks;
    private final RandomSource random = RandomSource.create();
    private final int numGroups;
    private WinterRaid.RaidStatus status;
    private int celebrationTicks;
    private Optional<BlockPos> waveSpawnPos = Optional.empty();

    public WinterRaid(int p_37692_, ServerLevel p_37693_, BlockPos p_37694_) {
        this.id = p_37692_;
        this.level = p_37693_;
        this.active = true;
        this.raidCooldownTicks = 300;
        this.raidEvent.setProgress(0.0F);
        this.center = p_37694_;
        this.numGroups = this.getNumGroups(p_37693_.getDifficulty());
        this.status = WinterRaid.RaidStatus.ONGOING;
    }

    public WinterRaid(ServerLevel p_37696_, CompoundTag p_37697_) {
        this.level = p_37696_;
        this.id = p_37697_.getInt("Id");
        this.started = p_37697_.getBoolean("Started");
        this.active = p_37697_.getBoolean("Active");
        this.ticksActive = p_37697_.getLong("TicksActive");
        this.naughtyLevel = p_37697_.getInt("BadOmenLevel");
        this.groupsSpawned = p_37697_.getInt("GroupsSpawned");
        this.raidCooldownTicks = p_37697_.getInt("PreRaidTicks");
        this.postRaidTicks = p_37697_.getInt("PostRaidTicks");
        this.totalHealth = p_37697_.getFloat("TotalHealth");
        this.center = new BlockPos(p_37697_.getInt("CX"), p_37697_.getInt("CY"), p_37697_.getInt("CZ"));
        this.numGroups = p_37697_.getInt("NumGroups");
        this.status = WinterRaid.RaidStatus.getByName(p_37697_.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (p_37697_.contains("HeroesOfTheVillage", 9)) {
            ListTag listtag = p_37697_.getList("HeroesOfTheVillage", 11);

            for(int i = 0; i < listtag.size(); ++i) {
                this.heroesOfTheVillage.add(NbtUtils.loadUUID(listtag.get(i)));
            }
        }

    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }

    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == WinterRaid.RaidStatus.STOPPED;
    }

    public boolean isVictory() {
        return this.status == WinterRaid.RaidStatus.VICTORY;
    }

    public boolean isLoss() {
        return this.status == WinterRaid.RaidStatus.LOSS;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private Predicate<ServerPlayer> validPlayer() {
        return (p_37723_) -> {
            BlockPos blockpos = p_37723_.blockPosition();


            return p_37723_.isAlive() && NaughtyList.getRaidAt(level, blockpos) == this;
        };
    }

    private void updatePlayers() {
        Set<ServerPlayer> set = Sets.newHashSet(this.raidEvent.getPlayers());
        List<ServerPlayer> list = this.level.getPlayers(this.validPlayer());

        for(ServerPlayer serverplayer : list) {
            if (!set.contains(serverplayer)) {
                this.raidEvent.addPlayer(serverplayer);
            }
        }

        for(ServerPlayer serverplayer1 : set) {
            if (!list.contains(serverplayer1)) {
                this.raidEvent.removePlayer(serverplayer1);
            }
        }

    }

    public int getMaxBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.naughtyLevel;
    }

    public void absorbBadOmen(Player p_37729_) {
        if (p_37729_.hasEffect(MobEffects.BAD_OMEN)) {
            this.naughtyLevel += p_37729_.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.naughtyLevel = Mth.clamp(this.naughtyLevel, 0, this.getMaxBadOmenLevel());
        }

        p_37729_.removeEffect(MobEffects.BAD_OMEN);
    }

    public void stop() {
        this.active = false;
        this.raidEvent.removeAllPlayers();
        this.status = WinterRaid.RaidStatus.STOPPED;
    }

    public void tick() {
        if (!this.isStopped()) {
            if (this.status == WinterRaid.RaidStatus.ONGOING) {
                boolean flag = this.active;
                this.active = this.level.hasChunkAt(this.center);
                if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                    this.stop();
                    return;
                }

                if (flag != this.active) {
                    this.raidEvent.setVisible(this.active);
                }

                if (!this.active) {
                    return;
                }

                if (!this.level.isVillage(this.center)) {
                    this.moveRaidCenterToNearbyVillageSection();
                }

                if (!this.level.isVillage(this.center)) {
                    if (this.groupsSpawned > 0) {
                        this.status = WinterRaid.RaidStatus.LOSS;
                    } else {
                        this.stop();
                    }
                }

                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }

                int i = this.getTotalRaidersAlive();
                if (i == 0 && this.hasMoreWaves()) {
                    if (this.raidCooldownTicks <= 0) {
                        if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                            this.raidCooldownTicks = 300;
                            this.raidEvent.setName(RAID_NAME_COMPONENT);
                            return;
                        }
                    } else {
                        boolean flag1 = this.waveSpawnPos.isPresent();
                        boolean flag2 = !flag1 && this.raidCooldownTicks % 5 == 0;
                        if (flag1 && !this.level.isPositionEntityTicking(this.waveSpawnPos.get())) {
                            flag2 = true;
                        }

                        if (flag2) {
                            int j = 0;
                            if (this.raidCooldownTicks < 100) {
                                j = 1;
                            } else if (this.raidCooldownTicks < 40) {
                                j = 2;
                            }

                            this.waveSpawnPos = this.getValidSpawnPos(j);
                        }

                        if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                            this.updatePlayers();
                        }

                        --this.raidCooldownTicks;
                        this.raidEvent.setProgress(Mth.clamp((float)(300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
                    }
                }

                if (this.ticksActive % 20L == 0L) {
                    this.updatePlayers();
                    this.updateRaiders();
                    if (i > 0) {
                        if (i <= 2) {
                            this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", i)));
                        } else {
                            this.raidEvent.setName(RAID_NAME_COMPONENT);
                        }
                    } else {
                        this.raidEvent.setName(RAID_NAME_COMPONENT);
                    }
                }

                boolean flag3 = false;
                int k = 0;

                while(this.shouldSpawnGroup()) {
                    BlockPos blockpos = this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(k, 20);
                    if (blockpos != null) {
                        this.started = true;
                        this.spawnGroup(blockpos);
                        if (!flag3) {
                            this.playSound(blockpos);
                            flag3 = true;
                        }
                    } else {
                        ++k;
                    }

                    if (k > 3) {
                        this.stop();
                        break;
                    }
                }

                if (this.isStarted() && !this.hasMoreWaves() && i == 0) {
                    if (this.postRaidTicks < 40) {
                        ++this.postRaidTicks;
                    } else {
                        this.status = WinterRaid.RaidStatus.VICTORY;

                        for(UUID uuid : this.heroesOfTheVillage) {
                            Entity entity = this.level.getEntity(uuid);
                            if (entity instanceof LivingEntity && !entity.isSpectator()) {
                                LivingEntity livingentity = (LivingEntity)entity;
                                livingentity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.naughtyLevel - 1, false, false, true));
                                if (livingentity instanceof ServerPlayer) {
                                    ServerPlayer serverplayer = (ServerPlayer)livingentity;
                                    serverplayer.awardStat(Stats.RAID_WIN);
                                    CriteriaTriggers.RAID_WIN.trigger(serverplayer);
                                }
                            }
                        }
                    }
                }

                this.setDirty();
            } else if (this.isOver()) {
                ++this.celebrationTicks;
                if (this.celebrationTicks >= 600) {
                    this.stop();
                    return;
                }

                if (this.celebrationTicks % 20 == 0) {
                    this.updatePlayers();
                    this.raidEvent.setVisible(true);
                    if (this.isVictory()) {
                        this.raidEvent.setProgress(0.0F);
                        this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
                    } else {
                        this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
                    }
                }
            }

        }
    }

    private void moveRaidCenterToNearbyVillageSection() {
        Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(this.center), 2);
        stream.filter(this.level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_37766_) -> {
            return p_37766_.distSqr(this.center);
        })).ifPresent(this::setCenter);
    }

    private Optional<BlockPos> getValidSpawnPos(int p_37764_) {
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = this.findRandomSpawnPos(p_37764_, 1);
            if (blockpos != null) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    private boolean hasMoreWaves() {
        if (this.hasBonusWave()) {
            return !this.hasSpawnedBonusWave();
        } else {
            return !this.isFinalWave();
        }
    }

    private boolean isFinalWave() {
        return this.getGroupsSpawned() == this.numGroups;
    }

    private boolean hasBonusWave() {
        return this.naughtyLevel > 1;
    }

    private boolean hasSpawnedBonusWave() {
        return this.getGroupsSpawned() > this.numGroups;
    }

    private boolean shouldSpawnBonusGroup() {
        return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
    }

    private void updateRaiders() {
        Iterator<Set<WinterRaider>> iterator = this.groupRaiderMap.values().iterator();
        Set<WinterRaider> set = Sets.newHashSet();

        while(iterator.hasNext()) {
            Set<WinterRaider> set1 = iterator.next();

            for(WinterRaider raider : set1) {
                BlockPos blockpos = raider.blockPosition();
                if (!raider.isRemoved() && raider.level.dimension() == this.level.dimension() && !(this.center.distSqr(blockpos) >= 12544.0D)) {
                    if (raider.tickCount > 600) {
                        if (this.level.getEntity(raider.getUUID()) == null) {
                            set.add(raider);
                        }

                        if (!this.level.isVillage(blockpos) && raider.getNoActionTime() > 2400) {
                            raider.setTicksOutsideRaid(raider.getTicksOutsideRaid() + 1);
                        }

                        if (raider.getTicksOutsideRaid() >= 30) {
                            set.add(raider);
                        }
                    }
                } else {
                    set.add(raider);
                }
            }
        }

        for(WinterRaider raider1 : set) {
            this.removeFromRaid(raider1, true);
        }

    }

    private void playSound(BlockPos p_37744_) {
        Collection<ServerPlayer> collection = this.raidEvent.getPlayers();
        long j = this.random.nextLong();

        for(ServerPlayer serverplayer : this.level.players()) {
            Vec3 vec3 = serverplayer.position();
            Vec3 vec31 = Vec3.atCenterOf(p_37744_);
            double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z));
            double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
            double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
            if (d0 <= 64.0D || collection.contains(serverplayer)) {
                serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, 64.0F, 1.0F, j));
            }
        }

    }

    private void spawnGroup(BlockPos p_37756_) {
        boolean flag = false;
        int i = this.groupsSpawned + 1;
        this.totalHealth = 0.0F;
        boolean flag1 = this.shouldSpawnBonusGroup();

        for(WinterRaid.RaiderType raid$raidertype : WinterRaid.RaiderType.VALUES) {
            int j = this.getDefaultNumSpawns(raid$raidertype, i, flag1);
            int k = 0;

            for(int l = 0; l < j; ++l) {
                WinterRaider raider = raid$raidertype.entityType.create(this.level);
                if (!flag && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    this.setLeader(i, raider);
                    flag = true;
                }

                this.joinRaid(i, raider, p_37756_, false);
                if (raid$raidertype.entityType == NLEntities.WOODEN_HORSE.get()) {
                    WinterRaider raider1 = null;
                    if (i == this.getNumGroups(Difficulty.NORMAL)) {
                        raider1 = NLEntities.NUTCRACKER.get().create(this.level);
                    } else if (i >= this.getNumGroups(Difficulty.HARD)) {
                        if (k == 0) {
                            raider1 = NLEntities.NUTCRACKER.get().create(this.level);
                        } else {
                            raider1 = NLEntities.NUTCRACKER.get().create(this.level);
                        }
                    }

                    ++k;
                    if (raider1 != null) {
                        this.joinRaid(i, raider1, p_37756_, false);
                        raider1.moveTo(p_37756_, 0.0F, 0.0F);
                        raider1.startRiding(raider);
                    }
                }
            }
        }

        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }

    public void joinRaid(int p_37714_, WinterRaider p_37715_, @Nullable BlockPos p_37716_, boolean p_37717_) {
        boolean flag = this.addWaveMob(p_37714_, p_37715_);
        if (flag) {
            p_37715_.setCurrentRaid(this);
            p_37715_.setWave(p_37714_);
            p_37715_.setCanJoinRaid(true);
            p_37715_.setTicksOutsideRaid(0);
            if (!p_37717_ && p_37716_ != null) {
                p_37715_.setPos((double)p_37716_.getX() + 0.5D, (double)p_37716_.getY() + 1.0D, (double)p_37716_.getZ() + 0.5D);
                p_37715_.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(p_37716_), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
                p_37715_.applyRaidBuffs(p_37714_, false);
                p_37715_.setOnGround(true);
                this.level.addFreshEntityWithPassengers(p_37715_);
            }
        }

    }

    public void updateBossbar() {
        this.raidEvent.setProgress(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
    }

    public float getHealthOfLivingRaiders() {
        float f = 0.0F;

        for(Set<WinterRaider> set : this.groupRaiderMap.values()) {
            for(WinterRaider raider : set) {
                f += raider.getHealth();
            }
        }

        return f;
    }

    private boolean shouldSpawnGroup() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
    }

    public int getTotalRaidersAlive() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }

    public void removeFromRaid(WinterRaider p_37741_, boolean p_37742_) {
        Set<WinterRaider> set = this.groupRaiderMap.get(p_37741_.getWave());
        if (set != null) {
            boolean flag = set.remove(p_37741_);
            if (flag) {
                if (p_37742_) {
                    this.totalHealth -= p_37741_.getHealth();
                }

                p_37741_.setCurrentRaid(null);
                this.updateBossbar();
                this.setDirty();
            }
        }

    }

    private void setDirty() {
        this.level.getRaids().setDirty();
    }

    @Nullable
    public WinterRaider getLeader(int p_37751_) {
        return this.groupToLeaderMap.get(p_37751_);
    }

    @Nullable
    private BlockPos findRandomSpawnPos(int p_37708_, int p_37709_) {
        int i = p_37708_ == 0 ? 2 : 2 - p_37708_;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i1 = 0; i1 < p_37709_; ++i1) {
            float f = this.level.random.nextFloat() * ((float)Math.PI * 2F);
            int j = this.center.getX() + Mth.floor(Mth.cos(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int l = this.center.getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int k = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
            blockpos$mutableblockpos.set(j, k, l);
            if (!this.level.isVillage(blockpos$mutableblockpos) || p_37708_ >= 2) {
                int j1 = 10;
                if (this.level.hasChunksAt(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getZ() - 10, blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getZ() + 10) && this.level.isPositionEntityTicking(blockpos$mutableblockpos) && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, blockpos$mutableblockpos, NLEntities.NUTCRACKER.get()) || this.level.getBlockState(blockpos$mutableblockpos.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutableblockpos).isAir())) {
                    return blockpos$mutableblockpos;
                }
            }
        }

        return null;
    }

    private boolean addWaveMob(int p_37753_, WinterRaider p_37754_) {
        return this.addWaveMob(p_37753_, p_37754_, true);
    }

    public boolean addWaveMob(int p_37719_, WinterRaider p_37720_, boolean p_37721_) {
        this.groupRaiderMap.computeIfAbsent(p_37719_, (p_37746_) -> Sets.newHashSet());
        Set<WinterRaider> set = this.groupRaiderMap.get(p_37719_);
        WinterRaider raider = null;

        for(WinterRaider raider1 : set) {
            if (raider1.getUUID().equals(p_37720_.getUUID())) {
                raider = raider1;
                break;
            }
        }

        if (raider != null) {
            set.remove(raider);
            set.add(p_37720_);
        }

        set.add(p_37720_);
        if (p_37721_) {
            this.totalHealth += p_37720_.getHealth();
        }

        this.updateBossbar();
        this.setDirty();
        return true;
    }

    public void setLeader(int p_37711_, WinterRaider p_37712_) {
        this.groupToLeaderMap.put(p_37711_, p_37712_);
    }

    public void removeLeader(int p_37759_) {
        this.groupToLeaderMap.remove(p_37759_);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    private void setCenter(BlockPos p_37761_) {
        this.center = p_37761_;
    }

    public int getId() {
        return this.id;
    }

    private int getDefaultNumSpawns(WinterRaid.RaiderType p_37731_, int p_37732_, boolean p_37733_) {
        return p_37733_ ? p_37731_.spawnsPerWaveBeforeBonus[this.numGroups] : p_37731_.spawnsPerWaveBeforeBonus[p_37732_];
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag save(CompoundTag p_37748_) {
        p_37748_.putInt("Id", this.id);
        p_37748_.putBoolean("Started", this.started);
        p_37748_.putBoolean("Active", this.active);
        p_37748_.putLong("TicksActive", this.ticksActive);
        p_37748_.putInt("BadOmenLevel", this.naughtyLevel);
        p_37748_.putInt("GroupsSpawned", this.groupsSpawned);
        p_37748_.putInt("PreRaidTicks", this.raidCooldownTicks);
        p_37748_.putInt("PostRaidTicks", this.postRaidTicks);
        p_37748_.putFloat("TotalHealth", this.totalHealth);
        p_37748_.putInt("NumGroups", this.numGroups);
        p_37748_.putString("Status", this.status.getName());
        p_37748_.putInt("CX", this.center.getX());
        p_37748_.putInt("CY", this.center.getY());
        p_37748_.putInt("CZ", this.center.getZ());
        ListTag listtag = new ListTag();

        for(UUID uuid : this.heroesOfTheVillage) {
            listtag.add(NbtUtils.createUUID(uuid));
        }

        p_37748_.put("HeroesOfTheVillage", listtag);
        return p_37748_;
    }

    public int getNumGroups(Difficulty p_37725_) {
        switch (p_37725_) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

    public float getEnchantOdds() {
        int i = this.getBadOmenLevel();
        if (i == 2) {
            return 0.1F;
        } else if (i == 3) {
            return 0.25F;
        } else if (i == 4) {
            return 0.5F;
        } else {
            return i == 5 ? 0.75F : 0.0F;
        }
    }

    public void addHeroOfTheVillage(Entity p_37727_) {
        this.heroesOfTheVillage.add(p_37727_.getUUID());
    }

    enum RaidStatus {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final WinterRaid.RaidStatus[] VALUES = values();

        static WinterRaid.RaidStatus getByName(String p_37804_) {
            for(WinterRaid.RaidStatus raid$raidstatus : VALUES) {
                if (p_37804_.equalsIgnoreCase(raid$raidstatus.name())) {
                    return raid$raidstatus;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum RaiderType implements IExtensibleEnum {
        GREEN_NUTCRACKER(NLEntities.NUTCRACKER_GENERAL.get(), new int[]{1, 1, 2, 2, 2, 3, 2, 4}),
        RED_NUTCRACKER(NLEntities.NUTCRACKER.get(), new int[]{1, 4, 3, 3, 4, 4, 4, 2}),
        WOODEN_HORSE(NLEntities.WOODEN_HORSE.get(), new int[]{0, 1, 1, 2, 1, 2, 1, 3});

        static WinterRaid.RaiderType[] VALUES = values();
        final EntityType<? extends WinterRaider> entityType;
        final int[] spawnsPerWaveBeforeBonus;

        RaiderType(EntityType<? extends WinterRaider> p_37821_, int[] p_37822_) {
            this.entityType = p_37821_;
            this.spawnsPerWaveBeforeBonus = p_37822_;
        }

        public static WinterRaid.RaiderType create(String name, EntityType<? extends Raider> typeIn, int[] waveCountsIn) {
            throw new IllegalStateException("Enum not extended");
        }

        @Override
        @Deprecated
        public void init() {
            VALUES = values();
        }
    }
}
