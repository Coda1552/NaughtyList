package coda.naughtylist.client;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.client.model.CandyCaneModel;
import coda.naughtylist.client.model.NutcrackerModel;
import coda.naughtylist.client.model.WoodenHorseModel;
import coda.naughtylist.client.renderer.NutcrackerRenderer;
import coda.naughtylist.client.renderer.ThrownCandyCaneRenderer;
import coda.naughtylist.client.renderer.WoodenHorseRenderer;
import coda.naughtylist.registry.NLEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NaughtyList.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent e) {
        EntityRenderers.register(NLEntities.NUTCRACKER.get(), NutcrackerRenderer::new);
        EntityRenderers.register(NLEntities.WOODEN_HORSE.get(), WoodenHorseRenderer::new);
        EntityRenderers.register(NLEntities.CANDY_CANE.get(), ThrownCandyCaneRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(NLModelLayers.WOODEN_HORSE, WoodenHorseModel::createBodyLayer);
        e.registerLayerDefinition(NLModelLayers.NUTCRACKER, NutcrackerModel::createBodyLayer);
        e.registerLayerDefinition(NLModelLayers.CANDY_CANE, CandyCaneModel::createBodyLayer);
    }
}
