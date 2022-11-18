package coda.naughtylist.client.renderer;

import coda.naughtylist.common.entity.Nutcracker;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NutcrackerRenderer extends MobRenderer<Nutcracker, CowModel<Nutcracker>> {
    private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

    public NutcrackerRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new CowModel<>(p_173956_.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    public ResourceLocation getTextureLocation(Nutcracker p_114029_) {
        return COW_LOCATION;
    }
}