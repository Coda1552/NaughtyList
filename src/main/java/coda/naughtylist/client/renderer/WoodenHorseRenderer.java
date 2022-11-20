package coda.naughtylist.client.renderer;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.client.NLModelLayers;
import coda.naughtylist.client.model.WoodenHorseModel;
import coda.naughtylist.common.entity.WoodenHorse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WoodenHorseRenderer extends MobRenderer<WoodenHorse, WoodenHorseModel<WoodenHorse>> {
    private static final ResourceLocation TEX = new ResourceLocation(NaughtyList.MOD_ID, "textures/entity/wooden_horse.png");

    public WoodenHorseRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new WoodenHorseModel<>(p_173956_.bakeLayer(NLModelLayers.WOODEN_HORSE)), 0.7F);
    }

    public ResourceLocation getTextureLocation(WoodenHorse p_114029_) {
        return TEX;
    }

    @Override
    protected void setupRotations(WoodenHorse pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
    }
}