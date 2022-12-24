package coda.naughtylist.registry;

import coda.naughtylist.NaughtyList;
import coda.naughtylist.common.block.SnowGlobeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NLBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NaughtyList.MOD_ID);

    public static final RegistryObject<Block> SNOW_GLOBE = BLOCKS.register("snow_globe", () -> new SnowGlobeBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(1.2F).noOcclusion().sound(SoundType.GLASS)));

}
