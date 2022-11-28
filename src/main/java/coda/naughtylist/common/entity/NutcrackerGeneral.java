package coda.naughtylist.common.entity;

import coda.naughtylist.common.entity.util.WinterRaider;
import coda.naughtylist.registry.NLItems;
import coda.naughtylist.registry.NLSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class NutcrackerGeneral extends WinterRaider implements InventoryCarrier, RangedAttackMob {
    private final SimpleContainer inventory = new SimpleContainer(1);

    public NutcrackerGeneral(EntityType<? extends WinterRaider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MountHorseGoal(this));
        this.goalSelector.addGoal(1, new CandyCaneAttackGoal(this, 1.0D, 40, 10.0F));
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(NLItems.NUTCRACKER_GENERAL_SPAWN_EGG.get());
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.28D);
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
    public void performRangedAttack(LivingEntity p_32356_, float p_32357_) {
        ThrownCandyCane thrownCandyCane = new ThrownCandyCane(this.level, this, new ItemStack(NLItems.CANDY_CANE.get()));
        double d0 = p_32356_.getX() - this.getX();
        double d1 = p_32356_.getY(0.3333333333333333D) - thrownCandyCane.getY();
        double d2 = p_32356_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        thrownCandyCane.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(thrownCandyCane);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag listtag = new ListTag();

        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                listtag.add(itemstack.save(new CompoundTag()));
            }
        }

        tag.put("Inventory", listtag);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag listtag = tag.getList("Inventory", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            ItemStack itemstack = ItemStack.of(listtag.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33282_, DifficultyInstance p_33283_, MobSpawnType p_33284_, @Nullable SpawnGroupData p_33285_, @Nullable CompoundTag p_33286_) {
        RandomSource randomsource = p_33282_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_33283_);
        //this.populateDefaultEquipmentEnchantments(randomsource, p_33283_);
        return super.finalizeSpawn(p_33282_, p_33283_, p_33284_, p_33285_, p_33286_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219059_, DifficultyInstance p_219060_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(NLItems.CANDY_CANE.get()));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    static class CandyCaneAttackGoal extends RangedAttackGoal {
        private final NutcrackerGeneral nutcracker;

        public CandyCaneAttackGoal(RangedAttackMob p_32450_, double p_32451_, int p_32452_, float p_32453_) {
            super(p_32450_, p_32451_, p_32452_, p_32453_);
            this.nutcracker = (NutcrackerGeneral)p_32450_;
        }

        public boolean canUse() {
            return super.canUse() && this.nutcracker.getMainHandItem().is(NLItems.CANDY_CANE.get());
        }

        public void start() {
            super.start();
            this.nutcracker.setAggressive(true);
            this.nutcracker.startUsingItem(InteractionHand.MAIN_HAND);
        }

        public void stop() {
            super.stop();
            this.nutcracker.stopUsingItem();
            this.nutcracker.setAggressive(false);
        }
    }
}
