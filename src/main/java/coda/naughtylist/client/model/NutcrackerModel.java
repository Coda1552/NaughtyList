package coda.naughtylist.client.model;

import coda.naughtylist.common.entity.Nutcracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class NutcrackerModel<T extends Nutcracker> extends EntityModel<T> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart topHead;
	private final ModelPart jaw;
	private final ModelPart torso;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart arms;

	public NutcrackerModel(ModelPart r) {
		this.root = r.getChild("root");
		this.head = root.getChild("head");
		this.topHead = head.getChild("Top_head");
		this.jaw = head.getChild("Jaw");
		this.torso = root.getChild("torso");
		this.rightLeg = root.getChild("rightLeg");
		this.leftLeg = root.getChild("leftLeg");
		this.arms = root.getChild("arms");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(36, 29).addBox(-2.0F, 1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(2.5F, -3.5F, 0.0F));

		PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(36, 29).mirror().addBox(-2.0F, 1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.025F)).mirror(false), PartPose.offset(-2.5F, -3.5F, 0.0F));

		PartDefinition torso = root.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 39).addBox(-4.5F, -3.0F, -2.5F, 9.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition Top_head = head.addOrReplaceChild("Top_head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -36.0F, -4.5F, 11.0F, 18.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(-6.5F, -18.0F, -5.5F, 13.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition cube_r1 = Top_head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 55).addBox(0.05F, 0.0F, -4.5F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.45F, -23.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition cube_r2 = Top_head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 55).addBox(-0.05F, 0.0F, -4.5F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.45F, -23.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition Jaw = head.addOrReplaceChild("Jaw", CubeListBuilder.create().texOffs(36, 17).addBox(-6.5F, -15.0F, -5.5F, 13.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(4.5F, -20.0F, -0.5F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(-4.5F, -20.0F, -0.5F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition arms = root.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(28, 39).addBox(4.5F, 0.0F, -2.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(28, 39).mirror().addBox(-7.5F, 0.0F, -2.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(31, 0).addBox(-4.5F, 5.0F, -2.5F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 2.0F, -0.5236F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6F) * 1.4F * limbSwingAmount * 0.5F;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;

		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

		if (entity.isAggressive()) {
			this.topHead.y = Mth.cos(ageInTicks * 0.6F) * 1.2F + 13.5F;
		}
		else {
			this.topHead.y = 13F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}