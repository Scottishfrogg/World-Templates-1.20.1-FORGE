package net.scottishfrogg.worldtemplates.worldgen;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.scottishfrogg.worldtemplates.WorldMod;

public class ModNoiseSettings {

    public static final ResourceKey<NoiseGeneratorSettings> CONTINENTAL =
            ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation("worldmod", "continental"));
    public static final ResourceKey<NoiseGeneratorSettings> ISLANDS =
            ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(WorldMod.MOD_ID, "islands"));
    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
        context.register(
                CONTINENTAL,
                new NoiseGeneratorSettings(
                        new NoiseSettings(-64, 384, 1, 2),
                        Blocks.STONE.defaultBlockState(),
                        Blocks.WATER.defaultBlockState(),
                        ModNoiseRouterData.createOverworldRouter(
                                context.lookup(Registries.DENSITY_FUNCTION),
                                context.lookup(Registries.NOISE)
                        ),
                        SurfaceRuleData.overworld(),
                        new OverworldBiomeBuilder().spawnTarget(),
                        63,
                        false,
                        true,
                        true,
                        false
                )
        );
        context.register(
                ISLANDS,
                new NoiseGeneratorSettings(
                        new NoiseSettings(-64, 384, 1, 2),
                        Blocks.STONE.defaultBlockState(),
                        Blocks.WATER.defaultBlockState(),
                        ModNoiseRouterData.createIslandsRouter(
                                context.lookup(Registries.DENSITY_FUNCTION),
                                context.lookup(Registries.NOISE)),
                        SurfaceRuleData.overworld(),
                        List.of(),
                        63,
                        false,
                        false,
                        false,
                        false
                )
        );
    }
}