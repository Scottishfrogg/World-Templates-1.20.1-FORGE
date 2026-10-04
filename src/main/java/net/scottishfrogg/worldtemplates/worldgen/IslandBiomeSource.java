package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

public class IslandBiomeSource extends BiomeSource {
    public static final Codec<IslandBiomeSource> CODEC = RecordCodecBuilder.create(i -> i.group(
            Biome.CODEC.fieldOf("spawn_biome").forGetter(s -> s.spawn),
            Biome.CODEC.fieldOf("ocean_biome").forGetter(s -> s.ocean),
            Biome.LIST_CODEC.fieldOf("island_biomes").forGetter(s -> s.islands)
    ).apply(i, IslandBiomeSource::new));
    private final Holder<Biome> spawn;
    private final Holder<Biome> ocean;
    private final HolderSet<Biome> islands;
    public IslandBiomeSource(Holder<Biome> spawn, Holder<Biome> ocean, HolderSet<Biome> islands) {
        this.spawn = spawn;
        this.ocean = ocean;
        this.islands = islands;
    }
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(Stream.of(spawn, ocean), islands.stream());
    }
    @Override
    public Holder<Biome> getNoiseBiome(int qx, int qy, int qz, Climate.Sampler sampler) {
        int x = QuartPos.toBlock(qx);
        int z = QuartPos.toBlock(qz);
        IslandGrid.Island isl = IslandGrid.nearest(x, z);
        if (IslandGrid.normalizedDist(isl, x, z) > 1.05D) {
            return ocean;
        }
        return isl.isSpawn() ? spawn : islands.get(isl.biomeRoll() % islands.size());
    }
}