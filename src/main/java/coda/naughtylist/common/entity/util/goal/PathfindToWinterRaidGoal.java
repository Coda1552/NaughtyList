package coda.naughtylist.common.entity.util.goal;

import coda.naughtylist.common.WinterRaid;
import coda.naughtylist.common.WinterRaidSavedData;
import coda.naughtylist.common.entity.WinterRaider;
import com.google.common.collect.Sets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PathfindToWinterRaidGoal<T extends WinterRaider> extends Goal {
   private final T mob;
   private int recruitmentTick;

   public PathfindToWinterRaidGoal(T p_25706_) {
      this.mob = p_25706_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
   }

   public boolean canContinueToUse() {
      return this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.level instanceof ServerLevel && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
   }

   public void tick() {
      if (this.mob.hasActiveRaid()) {
         WinterRaid raid = this.mob.getCurrentRaid();
         if (this.mob.tickCount > this.recruitmentTick) {
            this.recruitmentTick = this.mob.tickCount + 20;
            this.recruitNearby(raid);
         }

         if (!this.mob.isPathFinding()) {
            Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(raid.getCenter()), ((float)Math.PI / 2F));
            if (vec3 != null) {
               this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
            }
         }
      }

   }

   private void recruitNearby(WinterRaid p_25709_) {
      if (p_25709_.isActive()) {
         Set<WinterRaider> set = Sets.newHashSet();
         List<WinterRaider> list = this.mob.level.getEntitiesOfClass(WinterRaider.class, this.mob.getBoundingBox().inflate(16.0D), (p_25712_) -> !p_25712_.hasActiveRaid() && WinterRaidSavedData.canJoinRaid(p_25712_, p_25709_));
         set.addAll(list);

         for(WinterRaider raider : set) {
            p_25709_.joinRaid(p_25709_.getGroupsSpawned(), raider, null, true);
         }
      }

   }
}