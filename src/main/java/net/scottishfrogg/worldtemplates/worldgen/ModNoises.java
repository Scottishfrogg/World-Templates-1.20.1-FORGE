package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.scottishfrogg.worldtemplates.WorldMod;

public class ModNoises {

    public static final ResourceKey<NormalNoise.NoiseParameters> CONTINENTALNESS_CONTINENTAL =
            ResourceKey.create(
                    Registries.NOISE,
                    new ResourceLocation(WorldMod.MOD_ID, "continentalness_continental")
            );
    private static final int FIRST_OCTAVE = -12;
    public static void bootstrap(BootstapContext<NormalNoise.NoiseParameters> context) {
        context.register(
                CONTINENTALNESS_CONTINENTAL,
                new NormalNoise.NoiseParameters(
                        FIRST_OCTAVE,
                        1.0D, 1.0D, 2.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D
                )
        );
    }
}