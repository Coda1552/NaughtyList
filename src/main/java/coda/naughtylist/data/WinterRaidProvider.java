package coda.naughtylist.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WinterRaidProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<WinterRaidData> EMBRYO = CapabilityManager.get(new CapabilityToken<>(){});

    private WinterRaidData embryo = null;
    private final LazyOptional<WinterRaidData> opt = LazyOptional.of(this::createEmbryo);

    @Nonnull
    private WinterRaidData createEmbryo() {
        if (embryo == null) {
            embryo = new WinterRaidData();
        }
        return embryo;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == EMBRYO) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createEmbryo().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createEmbryo().loadNBTData(tag);
    }
}
