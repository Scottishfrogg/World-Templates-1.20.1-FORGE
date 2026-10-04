package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.scottishfrogg.worldtemplates.WorldMod;

public class ModBiomeSource {
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, WorldMod.MOD_ID);
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_TYPES =
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, WorldMod.MOD_ID);
    static {
        BIOME_SOURCES.register("islands", () -> IslandBiomeSource.CODEC);
        BIOME_SOURCES.register("winter", () -> WinterBiomeSource.CODEC);
        BIOME_SOURCES.register("overend", () -> OverendBiomeSource.CODEC);
        DENSITY_TYPES.register("island_shape", () -> IslandShape.CODEC.codec());
        DENSITY_TYPES.register("cave_shape", () -> CaveShape.CODEC.codec());
    }
    public static void register(IEventBus bus) {
        BIOME_SOURCES.register(bus);
        DENSITY_TYPES.register(bus);
    }
}