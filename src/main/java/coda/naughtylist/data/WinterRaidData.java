package coda.naughtylist.data;

import net.minecraft.nbt.CompoundTag;

public class WinterRaidData {
    private int timer;

    public int getRaid() {
        return timer;
    }

    public void setRaid(int timer) {
        this.timer = timer;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("embryoTimer", timer);
    }

    public void loadNBTData(CompoundTag tag) {
        timer = tag.getInt("embryoTimer");
    }
}

