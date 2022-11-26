package coda.naughtylist.client;

import coda.naughtylist.NaughtyList;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class NLModelLayers {
    public static final ModelLayerLocation WOODEN_HORSE = create("wooden_horse");
    public static final ModelLayerLocation NUTCRACKER = create("nutcracker");
    public static final ModelLayerLocation CANDY_CANE = create("candy_cane");
    public static final ModelLayerLocation NUTCRACKER_GENERAL = create("nutcracker_general");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(new ResourceLocation(NaughtyList.MOD_ID, name), "main");
    }
}
