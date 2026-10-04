package net.scottishfrogg.worldtemplates.worldgen;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseRouterData extends NoiseRouterData {
    private static final Map<ResourceKey<DensityFunction>, ResourceKey<DensityFunction>> REDIRECTS = Map.of(
            NoiseRouterData.CONTINENTS_LARGE, ModDensityFunctions.CONTINENTS,
            vanillaKey("overworld_large_biomes/factor"), ModDensityFunctions.FACTOR,
            vanillaKey("overworld_large_biomes/depth"), ModDensityFunctions.DEPTH,
            vanillaKey("overworld_large_biomes/sloped_cheese"), ModDensityFunctions.SLOPED_CHEESE
    );
    private static ResourceKey<DensityFunction> vanillaKey(String path) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(path));
    }
    public static NoiseRouter createOverworldRouter(
            HolderGetter<DensityFunction> densityFunctions,
            HolderGetter<NormalNoise.NoiseParameters> noises
    ) {
        return NoiseRouterData.overworld(
                new RedirectingGetter(densityFunctions),
                noises,
                true,
                false
        );
    }
    public static NoiseRouter createIslandsRouter(
            HolderGetter<DensityFunction> densityFunctions,
            HolderGetter<NormalNoise.NoiseParameters> noises
    ) {
        NoiseRouter base = NoiseRouterData.overworld(densityFunctions, noises, false, false);
        DensityFunction shape = new IslandShape();
        return new NoiseRouter(
                base.barrierNoise(), base.fluidLevelFloodednessNoise(), base.fluidLevelSpreadNoise(), base.lavaNoise(),
                base.temperature(), base.vegetation(), base.continents(), base.erosion(), base.depth(), base.ridges(),
                shape,
                DensityFunctions.interpolated(shape),
                base.veinToggle(), base.veinRidged(), base.veinGap()
        );
    }
    private record RedirectingGetter(HolderGetter<DensityFunction> delegate) implements HolderGetter<DensityFunction> {
        @Override
        public Optional<Holder.Reference<DensityFunction>> get(ResourceKey<DensityFunction> key) {
            return delegate.get(REDIRECTS.getOrDefault(key, key));
        }
        @Override
        public Optional<HolderSet.Named<DensityFunction>> get(TagKey<DensityFunction> tag) {
            return delegate.get(tag);
        }
    }
}