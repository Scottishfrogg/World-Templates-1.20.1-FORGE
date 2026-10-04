package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record IslandShape() implements DensityFunction.SimpleFunction {
    public static final KeyDispatchDataCodec<IslandShape> CODEC =
            KeyDispatchDataCodec.of(MapCodec.unit(new IslandShape()));
    @Override
    public double compute(FunctionContext ctx) {
        int x = ctx.blockX();
        int z = ctx.blockZ();
        IslandGrid.Island isl = IslandGrid.nearest(x, z);
        double t = IslandGrid.normalizedDist(isl, x, z);
        double height = IslandGrid.terrainHeight(isl, t);
        return Math.max(-1.0D, Math.min(1.0D, (height - ctx.blockY()) / 8.0D));
    }
    @Override
    public double minValue() {
        return -1.0D;
    }
    @Override
    public double maxValue() {
        return 1.0D;
    }
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}