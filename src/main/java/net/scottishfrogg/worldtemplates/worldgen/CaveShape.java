package net.scottishfrogg.worldtemplates.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public class CaveShape implements DensityFunction.SimpleFunction {
    public static final KeyDispatchDataCodec<CaveShape> CODEC =
            KeyDispatchDataCodec.of(MapCodec.unit(CaveShape::new));
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 320;
    private static final int SHELL = 10;
    private static final double CAVERN_THRESHOLD = 0.22;
    private static final double TUNNEL_WIDTH = 0.07;
    private static final double Y_SQUASH = 1.6;
    @Override
    public double compute(FunctionContext ctx) {
        CaveNoise.Noises n = CaveNoise.get();
        if (n == null) return 1.0;
        int x = ctx.blockX(), y = ctx.blockY(), z = ctx.blockZ();
        double shell = Mth.clamp(Math.min(y - MIN_Y, MAX_Y - y) / (double) SHELL, 0.0, 1.0);
        if (shell <= 0.0) return 1.0;
        double yy = y * Y_SQUASH;
        double cavernAir = n.caverns().getValue(x, yy, z) - CAVERN_THRESHOLD;
        double tunnelAir = TUNNEL_WIDTH - Math.max(
                Math.abs(n.tunnelA().getValue(x, yy, z)),
                Math.abs(n.tunnelB().getValue(x, yy, z)));
        double air = Math.max(cavernAir, tunnelAir);
        double density = Mth.clamp(-air * 10.0, -1.0, 1.0);
        return Mth.lerp(shell, 1.0, density);
    }
    @Override public double minValue() { return -1.0; }
    @Override public double maxValue() { return 1.0; }
    @Override public KeyDispatchDataCodec<? extends DensityFunction> codec() { return CODEC; }
}