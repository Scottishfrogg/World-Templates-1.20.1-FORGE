package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class SkyblockSavedData extends SavedData {
    private boolean starterGenerated = false;
    public SkyblockSavedData() {
    }
    public static SkyblockSavedData create() {
        return new SkyblockSavedData();
    }
    public static SkyblockSavedData load(CompoundTag tag) {
        SkyblockSavedData data = new SkyblockSavedData();
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