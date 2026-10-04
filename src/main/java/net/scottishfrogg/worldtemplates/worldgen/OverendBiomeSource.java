package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.stream.Stream;

public class OverendBiomeSource extends BiomeSource {
    public static final Codec<OverendBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset")
                            .forGetter(source -> source.preset)
            ).apply(instance, OverendBiomeSource::new));
    private final Holder<MultiNoiseBiomeSourceParameterList> preset;
    private final Supplier<MultiNoiseBiomeSource> delegate;
    private final Supplier<Holder<Biome>> replacement;
    public OverendBiomeSource(Holder<MultiNoiseBiomeSourceParameterList> preset) {
        this.preset = preset;
        this.delegate = Suppliers.memoize(() -> MultiNoiseBiomeSource.createFromPreset(preset));
        this.replacement = Suppliers.memoize(() -> delegate.get().possibleBiomes().stream()
                .filter(holder -> holder.is(Biomes.BADLANDS))
                .findFirst()
                .orElseThrow());
    }
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return delegate.get().possibleBiomes().stream()
                .filter(holder -> !holder.is(Biomes.ERODED_BADLANDS));
    }
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        Holder<Biome> biome = delegate.get().getNoiseBiome(x, y, z, sampler);
        return biome.is(Biomes.ERODED_BADLANDS) ? replacement.get() : biome;
    }
}