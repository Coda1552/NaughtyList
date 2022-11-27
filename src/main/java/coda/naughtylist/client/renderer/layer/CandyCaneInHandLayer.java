package coda.naughtylist.client.renderer.layer;

import coda.naughtylist.client.model.NutcrackerGeneralModel;
import coda.naughtylist.common.entity.NutcrackerGeneral;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CandyCaneInHandLayer<T extends NutcrackerGeneral, M extends NutcrackerGeneralModel<T>> extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public CandyCaneInHandLayer(RenderLayerParent<T, M> p_234846_, ItemInHandRenderer p_234847_) {
        super(p_234846_);
        this.itemInHandRenderer = p_234847_;
    }

    public void render(PoseStack p_117204_, MultiBufferSource p_117205_, int p_117206_, T enttiy, float p_117208_, float p_117209_, float p_117210_, float p_117211_, float p_117212_, float p_117213_) {
        if (enttiy.isAggressive()) {

            boolean flag = enttiy.getMainArm() == HumanoidArm.RIGHT;
            ItemStack itemstack = flag ? enttiy.getOffhandItem() : enttiy.getMainHandItem();
            ItemStack itemstack1 = flag ? enttiy.getMainHandItem() : enttiy.getOffhandItem();

            if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
                p_117204_.pushPose();

                this.renderArmWithItem(enttiy, itemstack1, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, p_117204_, p_117205_, p_117206_);
                this.renderArmWithItem(enttiy, itemstack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, p_117204_, p_117205_, p_117206_);
                p_117204_.popPose();
            }
        }
    }

    protected void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemTransforms.TransformType p_117187_, HumanoidArm arm, PoseStack stack, MultiBufferSource p_117190_, int p_117191_) {
        if (!itemStack.isEmpty()) {
            stack.pushPose();

            boolean flag = arm == HumanoidArm.LEFT;
            this.getParentModel().translateToHand(arm, stack);
            float f = !flag ? 0.225F : -0.225F;

            //stack.translate(0.525D, 1.15D, 0.4D);
            stack.translate(f, 0.85D, 0.0D);
            stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));


            this.itemInHandRenderer.renderItem(entity, itemStack, p_117187_, flag, stack, p_117190_, p_117191_);
            stack.popPose();
        }
    }
}