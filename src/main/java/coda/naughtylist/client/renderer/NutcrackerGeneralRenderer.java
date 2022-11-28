package coda.naughtylist.client.renderer;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.client.NLModelLayers;
import coda.naughtylist.client.model.NutcrackerGeneralModel;
import coda.naughtylist.client.renderer.layer.CandyCaneInHandLayer;
import coda.naughtylist.common.entity.NutcrackerGeneral;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NutcrackerGeneralRenderer extends MobRenderer<NutcrackerGeneral, NutcrackerGeneralModel<NutcrackerGeneral>> {
    private static final ResourceLocation TEX = new ResourceLocation(NaughtyList.MOD_ID, "textures/entity/nutcracker_general.png");

    public NutcrackerGeneralRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new NutcrackerGeneralModel<>(p_173956_.bakeLayer(NLModelLayers.NUTCRACKER_GENERAL)), 0.45F);
        this.addLayer(new CandyCaneInHandLayer<>(this, p_173956_.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(NutcrackerGeneral p_114029_) {
        return TEX;
    }
}