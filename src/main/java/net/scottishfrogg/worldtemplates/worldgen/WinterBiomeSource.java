package net.scottishfrogg.worldtemplates.worldgen;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class WinterBiomeSource extends BiomeSource {
    public static final Codec<WinterBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset")
                            .forGetter(source -> source.preset),
                    Biome.CODEC.fieldOf("ocean")
                            .forGetter(source -> source.ocean)
            ).apply(instance, WinterBiomeSource::new));
    private static final long COLD = Climate.quantizeCoord(-0.8F);
    private final Holder<MultiNoiseBiomeSourceParameterList> preset;
    private final Holder<Biome> ocean;
    private final Supplier<MultiNoiseBiomeSource> delegate;
    private final Supplier<Map<ResourceKey<Biome>, Holder<Biome>>> byKey;
    public WinterBiomeSource(Holder<MultiNoiseBiomeSourceParameterList> preset, Holder<Biome> ocean) {
        this.preset = preset;
        this.ocean = ocean;
        this.delegate = Suppliers.memoize(() -> MultiNoiseBiomeSource.createFromPreset(preset));
        this.byKey = Suppliers.memoize(() -> {
            Map<ResourceKey<Biome>, Holder<Biome>> map = new HashMap<>();
            for (Holder<Biome> holder : delegate.get().possibleBiomes()) {
                holder.unwrapKey().ifPresent(key -> map.put(key, holder));
            }
            return map;
        });
    }
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }
    private static boolean isFrozenOcean(Holder<Biome> biome) {
        return biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN);
    }
    private static boolean snows(Holder<Biome> biome) {
        return biome.value().coldEnoughToSnow(BlockPos.ZERO);
    }
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(
                Stream.of(ocean),
                delegate.get().possibleBiomes().stream()
                        .filter(biome -> snows(biome) && !isFrozenOcean(biome))
        );
    }
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        Climate.TargetPoint point = sampler.sample(x, y, z);
        Holder<Biome> biome = delegate.get().getNoiseBiome(new Climate.TargetPoint(
                COLD, point.humidity(), point.continentalness(),
                point.erosion(), point.depth(), point.weirdness()));
        if (isFrozenOcean(biome)) {
            return ocean;
        }
        if (snows(biome)) {
            return biome;
        }
        boolean taiga = biome.unwrapKey()
                .map(key -> key.location().getPath().contains("taiga"))
                .orElse(false);
        return byKey.get().get(taiga ? Biomes.SNOWY_TAIGA : Biomes.SNOWY_PLAINS);
    }
}