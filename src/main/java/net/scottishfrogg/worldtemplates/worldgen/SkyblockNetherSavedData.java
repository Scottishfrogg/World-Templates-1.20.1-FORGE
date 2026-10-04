package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class SkyblockNetherSavedData extends SavedData {
    private boolean starterGenerated = false;
    public SkyblockNetherSavedData() {
    }
    public static SkyblockNetherSavedData create() {
        return new SkyblockNetherSavedData();
    }
    public static SkyblockNetherSavedData load(CompoundTag tag) {
        SkyblockNetherSavedData data = new SkyblockNetherSavedData();
        data.starterGenerated = tag.getBoolean("StarterGenerated");
        return data;
    }
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("StarterGenerated", starterGenerated);
        return tag;
    }
    public boolean isStarterGenerated() {
        return starterGenerated;
    }
    public void setStarterGenerated(boolean generated) {
        this.starterGenerated = generated;
        setDirty();
    }
}
