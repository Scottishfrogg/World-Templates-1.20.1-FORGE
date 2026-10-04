package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class SkyblockEndSavedData extends SavedData {
    private boolean elytraGenerated = false;
    public SkyblockEndSavedData() {
    }
    public static SkyblockEndSavedData create() {
        return new SkyblockEndSavedData();
    }
    public static SkyblockEndSavedData load(CompoundTag tag) {
        SkyblockEndSavedData data = new SkyblockEndSavedData();
        data.elytraGenerated = tag.getBoolean("ElytraGenerated");
        return data;
    }
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("ElytraGenerated", elytraGenerated);
        return tag;
    }
    public boolean isElytraGenerated() {
        return elytraGenerated;
    }
    public void setElytraGenerated(boolean generated) {
        this.elytraGenerated = generated;
        setDirty();
    }
}