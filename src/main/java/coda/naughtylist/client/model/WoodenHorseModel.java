package coda.naughtylist.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class WoodenHorseModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart tail;
	private final ModelPart Body;

	public WoodenHorseModel(ModelPart root) {
		this.head = root.getChild("head");
		this.tail = root.getChild("tail");
		this.Body = root.getChild("Body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 39).addBox(-3.0F, -12.0F, -9.0F, 6.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(36, 45).addBox(-1.0F, -14.0F, 1.0F, 2.0F, 14.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).mirror().addBox(-3.0F, -15.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 0).addBox(1.0F, -15.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 0).mirror().addBox(-7.0F, -9.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(18, 0).addBox(3.0F, -9.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, -9.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -5.0F, -1.0F, 8.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 10.0F));

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(5.0F, -9.0F, -15.0F, 0.0F, 9.0F, 30.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 0).addBox(-5.0F, -9.0F, -15.0F, 0.0F, 9.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -2.0F, -15.0F, 10.0F, 0.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(37, 16).addBox(-6.0F, -15.0F, -12.0F, 12.0F, 6.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}