package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class CaveWorldSavedData extends SavedData {
    private boolean spawnGenerated;
    public static CaveWorldSavedData create() { return new CaveWorldSavedData(); }
    public static CaveWorldSavedData load(CompoundTag tag) {
        CaveWorldSavedData d = new CaveWorldSavedData();
        d.spawnGenerated = tag.getBoolean("spawnGenerated");
        return d;
    }
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("spawnGenerated", spawnGenerated);
        return tag;
    }
    public boolean isSpawnGenerated() { return spawnGenerated; }
    public void setSpawnGenerated(boolean v) { spawnGenerated = v; setDirty(); }
}