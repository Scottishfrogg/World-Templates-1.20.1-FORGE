package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.scottishfrogg.worldtemplates.WorldMod;


public class ModDensityFunctions {
    public static final ResourceKey<DensityFunction> CONTINENTS = ownKey("continental/continents");
    public static final ResourceKey<DensityFunction> OFFSET = ownKey("continental/offset");
    public static final ResourceKey<DensityFunction> FACTOR = ownKey("continental/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = ownKey("continental/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = ownKey("continental/depth");
    public static final ResourceKey<DensityFunction> SLOPED_CHEESE = ownKey("continental/sloped_cheese");
    private static final ResourceKey<DensityFunction> VANILLA_SHIFT_X = vanillaKey("shift_x");
    private static final ResourceKey<DensityFunction> VANILLA_SHIFT_Z = vanillaKey("shift_z");
    private static final ResourceKey<DensityFunction> VANILLA_BASE_3D_NOISE = vanillaKey("overworld/base_3d_noise");
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0D);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static ResourceKey<DensityFunction> ownKey(String path) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(WorldMod.MOD_ID, path));
    }
    private static ResourceKey<DensityFunction> vanillaKey(String path) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(path));
    }
    public static void bootstrap(BootstapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        DensityFunction shiftX = getFunction(functions, VANILLA_SHIFT_X);
        DensityFunction shiftZ = getFunction(functions, VANILLA_SHIFT_Z);
        Holder<DensityFunction> continents = context.register(
                CONTINENTS,
                DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(
                                shiftX,
                                shiftZ,
                                0.25D,
                                noises.getOrThrow(ModNoises.CONTINENTALNESS_CONTINENTAL)
                        )
                )
        );
        Holder<DensityFunction> erosion = functions.getOrThrow(NoiseRouterData.EROSION_LARGE);
        DensityFunction jaggedNoise = DensityFunctions.noise(noises.getOrThrow(Noises.JAGGED), 1500.0D, 0.0D);
        registerTerrainNoises(context, functions, jaggedNoise, continents, erosion);
    }
    private static void registerTerrainNoises(
            BootstapContext<DensityFunction> context,
            HolderGetter<DensityFunction> functions,
            DensityFunction jaggedNoise,
            Holder<DensityFunction> continents,
            Holder<DensityFunction> erosion
    ) {
        DensityFunctions.Spline.Coordinate continentsCoord = new DensityFunctions.Spline.Coordinate(continents);
        DensityFunctions.Spline.Coordinate erosionCoord = new DensityFunctions.Spline.Coordinate(erosion);
        DensityFunctions.Spline.Coordinate ridgesCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(NoiseRouterData.RIDGES));
        DensityFunctions.Spline.Coordinate ridgesFoldedCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(NoiseRouterData.RIDGES_FOLDED));
        DensityFunction offset = registerAndWrap(context, OFFSET, splineWithBlending(
                DensityFunctions.add(
                        DensityFunctions.constant((double) NoiseRouterData.GLOBAL_OFFSET),
                        DensityFunctions.spline(TerrainProvider.overworldOffset(continentsCoord, erosionCoord, ridgesFoldedCoord, false))
                ),
                DensityFunctions.blendOffset()
        ));
        DensityFunction factor = registerAndWrap(context, FACTOR, splineWithBlending(
                DensityFunctions.spline(TerrainProvider.overworldFactor(continentsCoord, erosionCoord, ridgesCoord, ridgesFoldedCoord, false)),
                BLENDING_FACTOR
        ));
        DensityFunction depth = registerAndWrap(context, DEPTH, DensityFunctions.add(
                DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D),
                offset
        ));
        DensityFunction jaggedness = registerAndWrap(context, JAGGEDNESS, splineWithBlending(
                DensityFunctions.spline(TerrainProvider.overworldJaggedness(continentsCoord, erosionCoord, ridgesCoord, ridgesFoldedCoord, false)),
                BLENDING_JAGGEDNESS
        ));
        DensityFunction jaggedProduct = DensityFunctions.mul(jaggedness, jaggedNoise.halfNegative());
        DensityFunction base = noiseGradientDensity(factor, DensityFunctions.add(depth, jaggedProduct));
        context.register(
                SLOPED_CHEESE,
                DensityFunctions.add(base, getFunction(functions, VANILLA_BASE_3D_NOISE))
        );
    }
    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction function) {
        return new DensityFunctions.HolderHolder(context.register(key, function));
    }
    private static DensityFunction getFunction(HolderGetter<DensityFunction> functions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(functions.getOrThrow(key));
    }
    private static DensityFunction splineWithBlending(DensityFunction spline, DensityFunction blending) {
        DensityFunction lerped = DensityFunctions.lerp(DensityFunctions.blendAlpha(), blending, spline);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(lerped));
    }
    private static DensityFunction noiseGradientDensity(DensityFunction factor, DensityFunction depth) {
        DensityFunction product = DensityFunctions.mul(depth, factor);
        return DensityFunctions.mul(DensityFunctions.constant(4.0D), product.quarterNegative());
    }
}