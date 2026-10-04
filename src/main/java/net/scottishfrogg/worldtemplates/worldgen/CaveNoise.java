package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class CaveNoise {
    public record Noises(PerlinNoise caverns, PerlinNoise tunnelA, PerlinNoise tunnelB) {}
    private static volatile Noises noises;
    public static void init(long seed) {
        RandomSource r = new XoroshiroRandomSource(seed);
        noises = new Noises(
                PerlinNoise.create(r, -7, 1.0, 1.0, 0.5),
                PerlinNoise.create(r, -6, 1.0, 1.0),
                PerlinNoise.create(r, -6, 1.0, 1.0));
    }
    public static Noises get() { return noises; }
}