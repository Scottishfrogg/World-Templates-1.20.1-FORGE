package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.core.registries.Registries;

public class SkyblockWorld {

    public static final ResourceKey<NoiseGeneratorSettings> SKYBLOCK_OVERWORLD =
            ResourceKey.create(
                    Registries.NOISE_SETTINGS,
                    new ResourceLocation("worldmod", "skyblock_overworld")
            );
    public static final ResourceKey<NoiseGeneratorSettings> SKYBLOCK_NETHER =
            ResourceKey.create(
                    Registries.NOISE_SETTINGS,
                    new ResourceLocation("worldmod", "skyblock_nether")
            );
    public static final ResourceKey<NoiseGeneratorSettings> SKYBLOCK_END =
            ResourceKey.create(
                    Registries.NOISE_SETTINGS,
                    new ResourceLocation("worldmod", "skyblock_end")
            );
    public static boolean isSkyblock(Level level) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return false;
        }
        if (!(level.getChunkSource() instanceof ServerChunkCache serverChunkCache)) {
            return false;
        }
        ChunkGenerator generator = serverChunkCache.getGenerator();
        if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) {
            return false;
        }
        return noiseGenerator.generatorSettings()
                .unwrapKey()
                .map(key -> key.equals(SKYBLOCK_OVERWORLD))
                .orElse(false);
    }
    public static boolean isSkyblockNether(Level level) {
        if (!level.dimension().equals(Level.NETHER)) {
            return false;
        }
        if (!(level.getChunkSource() instanceof ServerChunkCache serverChunkCache)) {
            return false;
        }
        ChunkGenerator generator = serverChunkCache.getGenerator();
        if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) {
            return false;
        }
        return noiseGenerator.generatorSettings()
                .unwrapKey()
                .map(key -> key.equals(SKYBLOCK_NETHER))
                .orElse(false);
    }
    public static boolean isSkyblockEnd(Level level) {
        if (!level.dimension().equals(Level.END)) {
            return false;
        }
        if (!(level.getChunkSource() instanceof ServerChunkCache serverChunkCache)) {
            return false;
        }
        ChunkGenerator generator = serverChunkCache.getGenerator();
        if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) {
            return false;
        }
        return noiseGenerator.generatorSettings()
                .unwrapKey()
                .map(key -> key.equals(SKYBLOCK_END))
                .orElse(false);
    }
}