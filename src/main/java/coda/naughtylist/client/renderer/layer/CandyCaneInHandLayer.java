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

    public void render(PoseStack p_117204_, MultiBufferSource p_117205_, int p_117206_, T p_117207_, float p_117208_, float p_117209_, float p_117210_, float p_117211_, float p_117212_, float p_117213_) {
        boolean flag = p_117207_.getMainArm() == HumanoidArm.RIGHT;
        ItemStack itemstack = flag ? p_117207_.getOffhandItem() : p_117207_.getMainHandItem();
        ItemStack itemstack1 = flag ? p_117207_.getMainHandItem() : p_117207_.getOffhandItem();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            p_117204_.pushPose();

            this.renderArmWithItem(p_117207_, itemstack1, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, p_117204_, p_117205_, p_117206_);
            this.renderArmWithItem(p_117207_, itemstack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, p_117204_, p_117205_, p_117206_);
            p_117204_.popPose();
        }
    }

    protected void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemTransforms.TransformType p_117187_, HumanoidArm arm, PoseStack stack, MultiBufferSource p_117190_, int p_117191_) {
        if (!itemStack.isEmpty()) {
            stack.pushPose();

            this.getParentModel().translateToHand(arm, stack);

            stack.mulPose(Vector3f.XP.rotationDegrees(-125.0F));
            stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = arm == HumanoidArm.LEFT;

            stack.translate(0.0D, 0.0D, 0.4D);

           this.itemInHandRenderer.renderItem(entity, itemStack, p_117187_, flag, stack, p_117190_, p_117191_);
            stack.popPose();
        }
    }
}