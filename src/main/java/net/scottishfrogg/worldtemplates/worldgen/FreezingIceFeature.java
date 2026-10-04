package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.scottishfrogg.worldtemplates.WorldMod;

public class FreezingIceFeature extends Feature<NoneFeatureConfiguration> {
    public static final ResourceKey<Biome> FREEZING_OCEAN =
            ResourceKey.create(Registries.BIOME, new ResourceLocation(WorldMod.MOD_ID, "freezing_ocean"));
    private static final int THICKNESS = 3;
    public FreezingIceFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        int top = context.chunkGenerator().getSeaLevel() - 1;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean placed = false;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = origin.getX() + x;
                int wz = origin.getZ() + z;
                pos.set(wx, top, wz);
                if (!level.getBiome(pos).is(FREEZING_OCEAN)) {
                    continue;
                }
                for (int dy = 0; dy < THICKNESS; dy++) {
                    pos.set(wx, top - dy, wz);
                    if (level.getBlockState(pos).is(Blocks.WATER)) {
                        level.setBlock(pos, Blocks.ICE.defaultBlockState(), 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }
}