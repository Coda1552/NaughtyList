package coda.naughtylist.client.model;

import coda.naughtylist.common.entity.NutcrackerGeneral;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class NutcrackerGeneralModel<T extends NutcrackerGeneral> extends EntityModel<T> implements ArmedModel {
	private final ModelPart root;
	private final ModelPart lArm;
	private final ModelPart rArm;
	private final ModelPart lLeg;
	private final ModelPart rLeg;
	private final ModelPart head;
	private final ModelPart torso;
	private final ModelPart jaw;
	private final ModelPart armsTucked;

	public NutcrackerGeneralModel(ModelPart r) {
		this.root = r.getChild("root");
		this.lArm = root.getChild("lArm");
		this.rArm = root.getChild("rArm");
		this.lLeg = root.getChild("lLeg");
		this.rLeg = root.getChild("rLeg");
		this.head = root.getChild("head");
		this.jaw = head.getChild("Jaw");
		this.torso = root.getChild("torso");
		this.armsTucked = root.getChild("armTucked");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -1.0F));

		PartDefinition lArm = root.addOrReplaceChild("lArm", CubeListBuilder.create().texOffs(28, 31).mirror().addBox(-2.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, 3.0F, 1.0F));

		PartDefinition armTucked = root.addOrReplaceChild("armTucked", CubeListBuilder.create().texOffs(40, 6).addBox(4.5F, 0.5F, -3.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(31, 0).addBox(-4.5F, 5.5F, -3.0F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(40, 6).mirror().addBox(-7.5F, 0.5F, -3.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition rArm = root.addOrReplaceChild("rArm", CubeListBuilder.create().texOffs(28, 31).addBox(-0.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 3.0F, 1.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -22.0F, -4.5F, 11.0F, 22.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(40, 22).addBox(-1.5F, -24.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Jaw = head.addOrReplaceChild("Jaw", CubeListBuilder.create().texOffs(37, 28).addBox(-3.5F, 1.0F, -2.5F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.5F, -3.0F, -2.5F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.5F, -3.0F, -2.5F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

		PartDefinition lLeg = root.addOrReplaceChild("lLeg", CubeListBuilder.create().texOffs(40, 17).mirror().addBox(-2.0F, 1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 15.5F, 1.0F));

		PartDefinition rLeg = root.addOrReplaceChild("rLeg", CubeListBuilder.create().texOffs(40, 17).addBox(-2.0F, 1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 15.5F, 1.0F));

		PartDefinition torso = root.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 31).addBox(-4.5F, -19.0F, -2.5F, 9.0F, 17.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 1.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//AnimationUtils.animateZombieArms(this.lArm, this.rArm, true, this.attackTime, ageInTicks);

		this.rArm.visible = true;
		this.lArm.visible = true;

		this.rLeg.xRot = Mth.cos(limbSwing * 0.6F) * 1.4F * limbSwingAmount * 0.5F;
		this.lLeg.xRot = Mth.cos(limbSwing * 0.6F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;

		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
		float f = p_102108_ == HumanoidArm.RIGHT ? 2.5F : -2.5F;
		ModelPart modelpart = this.getArm(p_102108_);

		p_102109_.translate(0.0D, 0.15D, -1.15D);
		p_102109_.mulPose(Vector3f.XP.rotationDegrees(32.0F));

		modelpart.x += f;
		modelpart.translateAndRotate(p_102109_);
		modelpart.x -= f;
	}

	protected ModelPart getArm(HumanoidArm p_102852_) {
		return p_102852_ == HumanoidArm.LEFT ? this.lArm : this.rArm;
	}
}