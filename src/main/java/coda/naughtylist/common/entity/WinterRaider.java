package coda.naughtylist.common.entity;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.common.WinterRaid;
import coda.naughtylist.common.WinterRaidSavedData;
import coda.naughtylist.common.entity.util.goal.PathfindToWinterRaidGoal;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class WinterRaider extends PatrollingMonster {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING = SynchedEntityData.defineId(WinterRaider.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    protected WinterRaid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;

    protected WinterRaider(EntityType<? extends PatrollingMonster> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PathfindToWinterRaidGoal<>(this));
        this.goalSelector.addGoal(3, new WinterRaider.RaiderMoveThroughVillageGoal(this, 1.05F, 1));
        this.goalSelector.addGoal(4, new WinterRaider.RaiderCelebration(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WinterRaider.class).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CELEBRATING, false);
    }

    public abstract void applyRaidBuffs(int p_37844_, boolean p_37845_);

    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean p_37898_) {
        this.canJoinRaid = p_37898_;
    }

    public void aiStep() {
        if (this.level instanceof ServerLevel level && this.isAlive()) {
            WinterRaid raid = this.getCurrentRaid();
            if (this.canJoinRaid()) {
                if (raid == null) {
                    if (this.level.getGameTime() % 20L == 0L) {
                        WinterRaid raid1 = NaughtyList.getRaidAt(level, blockPosition());
                        if (raid1 != null && WinterRaidSavedData.canJoinRaid(this, raid1)) {
                            raid1.joinRaid(raid1.getGroupsSpawned(), this, null, true);
                        }
                    }
                } else {
                    LivingEntity livingentity = this.getTarget();
                    if (livingentity != null && (livingentity.getType() == EntityType.PLAYER || livingentity.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }

        super.aiStep();
    }

    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }

    public void die(DamageSource p_37847_) {
        if (this.level instanceof ServerLevel) {
            Entity entity = p_37847_.getEntity();
            WinterRaid raid = this.getCurrentRaid();
            if (raid != null) {
                if (this.isPatrolLeader()) {
                    raid.removeLeader(this.getWave());
                }

                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    raid.addHeroOfTheVillage(entity);
                }

                raid.removeFromRaid(this, false);
            }
        }
        super.die(p_37847_);
    }

    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }

    public void setCurrentRaid(@Nullable WinterRaid p_37852_) {
        this.raid = p_37852_;
    }

    @Nullable
    public WinterRaid getCurrentRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }

    public void setWave(int p_37843_) {
        this.wave = p_37843_;
    }

    public int getWave() {
        return this.wave;
    }

    public boolean isCelebrating() {
        return this.entityData.get(IS_CELEBRATING);
    }

    public void setCelebrating(boolean p_37900_) {
        this.entityData.set(IS_CELEBRATING, p_37900_);
    }

    public void addAdditionalSaveData(CompoundTag p_37870_) {
        super.addAdditionalSaveData(p_37870_);
        p_37870_.putInt("Wave", this.wave);
        p_37870_.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            p_37870_.putInt("RaidId", this.raid.getId());
        }

    }

    public void readAdditionalSaveData(CompoundTag p_37862_) {
        super.readAdditionalSaveData(p_37862_);
        this.wave = p_37862_.getInt("Wave");
        this.canJoinRaid = p_37862_.getBoolean("CanJoinRaid");
        if (p_37862_.contains("RaidId", 3)) {
            if (this.level instanceof ServerLevel level) {
                WinterRaidSavedData raids = level.getDataStorage().computeIfAbsent(tag -> WinterRaidSavedData.load(level, tag), () -> new WinterRaidSavedData(level), WinterRaidSavedData.getFileId(level.dimensionTypeRegistration()));

                this.raid = raids.get(p_37862_.getInt("RaidId"));
            }

            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }

    }

    public boolean removeWhenFarAway(double p_37894_) {
        return this.getCurrentRaid() == null && super.removeWhenFarAway(p_37894_);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
    }

    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }

    public void setTicksOutsideRaid(int p_37864_) {
        this.ticksOutsideRaid = p_37864_;
    }

    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }

        return super.hurt(p_37849_, p_37850_);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.setCanJoinRaid(p_37858_ != MobSpawnType.NATURAL);

        if (this.isPatrolLeader()) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
            this.setDropChance(EquipmentSlot.HEAD, 0.0F);
        }

        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    public abstract SoundEvent getCelebrateSound();

    public class RaiderCelebration extends Goal {
        private final WinterRaider mob;

        RaiderCelebration(WinterRaider p_37924_) {
            this.mob = p_37924_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            WinterRaid raid = this.mob.getCurrentRaid();
            return this.mob.isAlive() && this.mob.getTarget() == null && raid != null && raid.isLoss();
        }

        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                WinterRaider.this.playSound(WinterRaider.this.getCelebrateSound(), WinterRaider.this.getSoundVolume(), WinterRaider.this.getVoicePitch());
            }

            if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
                this.mob.getJumpControl().jump();
            }

            super.tick();
        }
    }

    static class RaiderMoveThroughVillageGoal extends Goal {
        private final WinterRaider raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public RaiderMoveThroughVillageGoal(WinterRaider p_37936_, double p_37937_, int p_37938_) {
            this.raider = p_37936_;
            this.speedModifier = p_37937_;
            this.distanceToPoi = p_37938_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }

        private boolean hasSuitablePoi() {
            ServerLevel serverlevel = (ServerLevel)this.raider.level;
            BlockPos blockpos = this.raider.blockPosition();
            Optional<BlockPos> optional = serverlevel.getPoiManager().getRandom((p_219843_) -> {
                return p_219843_.is(PoiTypes.HOME);
            }, this::hasNotVisited, PoiManager.Occupancy.ANY, blockpos, 48, this.raider.random);
            if (!optional.isPresent()) {
                return false;
            } else {
                this.poiPos = optional.get().immutable();
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.raider.getNavigation().isDone()) {
                return false;
            } else {
                return this.raider.getTarget() == null && !this.poiPos.closerToCenterThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
            }
        }

        public void stop() {
            if (this.poiPos.closerToCenterThan(this.raider.position(), (double)this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }

        }

        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3 vec3 = Vec3.atBottomCenterOf(this.poiPos);
                Vec3 vec31 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, vec3, ((float)Math.PI / 10F));
                if (vec31 == null) {
                    vec31 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, vec3, ((float)Math.PI / 2F));
                }

                if (vec31 == null) {
                    this.stuck = true;
                    return;
                }

                this.raider.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
            }

        }

        private boolean hasNotVisited(BlockPos p_37943_) {
            for(BlockPos blockpos : this.visited) {
                if (Objects.equals(p_37943_, blockpos)) {
                    return false;
                }
            }

            return true;
        }

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }

        }
    }

    public static class MountHorseGoal extends Goal {
        private final WinterRaider raider;

        public MountHorseGoal(WinterRaider raider) {
            this.raider = raider;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public void tick() {
            super.tick();
            WoodenHorse nearestHorse = raider.level.getNearestEntity(WoodenHorse.class, TargetingConditions.DEFAULT, this.raider, raider.getX(), raider.getY(), raider.getZ(), raider.getBoundingBox().inflate(20.0D));
            moveToHorse();
            if (nearestHorse != null && nearestHorse.distanceToSqr(raider) <= 9) {
                raider.startRiding(nearestHorse);
            }
        }

        private boolean isHorseNearby() {
            WoodenHorse nearestHorse = raider.level.getNearestEntity(WoodenHorse.class, TargetingConditions.DEFAULT, this.raider, raider.getX(), raider.getY(), raider.getZ(), raider.getBoundingBox().inflate(20.0D));

            return nearestHorse != null && nearestHorse.getPassengers().isEmpty();
        }

        private void moveToHorse() {
            WoodenHorse nearestHorse = raider.level.getNearestEntity(WoodenHorse.class, TargetingConditions.DEFAULT, this.raider, raider.getX(), raider.getY(), raider.getZ(), raider.getBoundingBox().inflate(20.0D));

            if (nearestHorse != null) {
                raider.navigation.moveTo(nearestHorse, 1.0D);
            }
        }

        public boolean canUse() {
            WoodenHorse nearestHorse = raider.level.getNearestEntity(WoodenHorse.class, TargetingConditions.DEFAULT, this.raider, raider.getX(), raider.getY(), raider.getZ(), raider.getBoundingBox().inflate(20.0D));

            return !raider.isPassenger() && !(raider instanceof WoodenHorse) && nearestHorse != null && isHorseNearby();
        }
    }
}