package coda.naughtylist.common.entity;

import coda.naughtylist.common.entity.util.WinterRaider;
import coda.naughtylist.registry.NLItems;
import coda.naughtylist.registry.NLSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class Nutcracker extends WinterRaider {

    public Nutcracker(EntityType<? extends WinterRaider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MountHorseGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(NLItems.NUTCRACKER_SPAWN_EGG.get());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return NLSounds.NUTCRACKER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isAggressive() ? null : NLSounds.NUTCRACKER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NLSounds.NUTCRACKER_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (isAggressive() && tickCount % 15 == 0) {
            playSound(NLSounds.CLACKING.get(), 1.0F, 1.0F);
        }
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {}

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
