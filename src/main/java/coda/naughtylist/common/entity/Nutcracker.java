package coda.naughtylist.common.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;

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

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {}

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
