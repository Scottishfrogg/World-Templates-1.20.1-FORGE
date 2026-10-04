package net.scottishfrogg.worldtemplates.worldgen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public final class IslandGrid {
    public static final int CELL = 850;
    public static final int JITTER = 75;
    public static final int SEA = 63;
    public static final int OCEAN_FLOOR = 30;
    private static volatile long seed;
    private static final Map<Long, Island> CACHE = new ConcurrentHashMap<>();
    public record Island(int cx, int cz, int radius, int height, double flatness, int biomeRoll, double phase) {
        public boolean isSpawn() {
            return biomeRoll < 0;
        }
    }
    private IslandGrid() {}
    public static void init(long worldSeed) {
        seed = worldSeed;
        CACHE.clear();
    }
    private static Island get(int gx, int gz) {
        long key = ((long) gx << 32) ^ (gz & 0xFFFFFFFFL);
        return CACHE.computeIfAbsent(key, k -> {
            if (gx == 0 && gz == 0) {
                return new Island(0, 0, 110, 28, 2.0D, -1, 0.0D);
            }
            RandomSource r = new XoroshiroRandomSource(seed ^ (gx * 341873128712L) ^ (gz * 132897987541L));
            int cx = gx * CELL + r.nextIntBetweenInclusive(-JITTER, JITTER);
            int cz = gz * CELL + r.nextIntBetweenInclusive(-JITTER, JITTER);
            double sizeRoll = r.nextDouble();
            int radius;
            if (sizeRoll < 0.30D) {
                radius = r.nextIntBetweenInclusive(25, 50);
                radius = r.nextIntBetweenInclusive(50, 90);
            } else {
                radius = r.nextIntBetweenInclusive(90, 140);
            }
            double heightRoll = r.nextDouble();
            int height;
            double flatness;
            if (heightRoll < 0.30D) {
                height = r.nextIntBetweenInclusive(3, 6);
                flatness = 3.0D + r.nextDouble() * 3.0D;
            } else if (heightRoll < 0.70D) {
                height = r.nextIntBetweenInclusive(8, 16);
                flatness = 2.0D + r.nextDouble() * 2.0D;
            } else {
                height = r.nextIntBetweenInclusive(18, 34);
                flatness = 2.0D;
            }
            height = Math.min(height, Math.max(3, radius / 3));
            return new Island(cx, cz, radius, height, flatness, r.nextInt(Integer.MAX_VALUE), r.nextDouble() * Math.PI * 2);
        });
    }
    public static Island nearest(int x, int z) {
        int bx = Math.round((float) x / CELL);
        int bz = Math.round((float) z / CELL);
        Island best = null;
        double bestD = Double.MAX_VALUE;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Island i = get(bx + dx, bz + dz);
                double ox = x - i.cx();
                double oz = z - i.cz();
                double d = ox * ox + oz * oz;
                if (d < bestD) {
                    bestD = d;
                    best = i;
                }
            }
        }
        return best;
    }
    public static double normalizedDist(Island i, int x, int z) {
        double dx = x - i.cx();
        double dz = z - i.cz();
        double a = Math.atan2(dz, dx);
        double wobble = 1 + 0.18 * Math.sin(a * 3 + i.phase()) + 0.10 * Math.sin(a * 5 + i.phase() * 2);
        return Math.sqrt(dx * dx + dz * dz) / (i.radius() * wobble);
    }
    public static double terrainHeight(Island i, double t) {
        if (t <= 1.0D) {
            return SEA + 1 + (1 - Math.pow(t, i.flatness())) * i.height();
        }
        double u = Math.min(1.0D, (t - 1.0D) / 0.5D);
        double s = u * u * (3 - 2 * u);
        return SEA + 1 + (OCEAN_FLOOR - SEA - 1) * s;
    }
}