package coda.naughtylist.common.entity;

import coda.naughtylist.registry.NLEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class WoodenHorse extends WinterRaider {

    public WoodenHorse(EntityType<? extends WinterRaider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public float getStepHeight() {
        return 1.1F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.2D;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        if (random.nextBoolean()) {
            Nutcracker nutcracker = NLEntities.NUTCRACKER.get().create(level);

            if (nutcracker != null) {

                nutcracker.startRiding(this, true);
                nutcracker.moveTo(position());

                level.addFreshEntity(nutcracker);
            }
        }

        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
