package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.scottishfrogg.worldtemplates.WorldMod;

public final class CaveWorld {
    private static final ResourceLocation SETTINGS = new ResourceLocation(WorldMod.MOD_ID, "cave_world");
    private static final BlockPos CENTER = new BlockPos(0, 64, 0);   // y = grass layer
    private CaveWorld() {}
    public static boolean isCaveWorld(ServerLevel level) {
        return level.dimension().equals(Level.OVERWORLD)
                && level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator gen
                && gen.generatorSettings().is(SETTINGS);
    }
    public static BlockPos spawnPos() { return CENTER.offset(-2, 1, 0); }
    public static void generateSpawn(ServerLevel level) {
        CaveWorldSavedData data = level.getDataStorage().computeIfAbsent(
                CaveWorldSavedData::load, CaveWorldSavedData::create, "worldmod_cave_world");
        if (data.isSpawnGenerated()) return;
        int cx = CENTER.getX(), cz = CENTER.getZ(), floorY = CENTER.getY();
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                level.getChunk((cx >> 4) + dx, (cz >> 4) + dz);
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        int radius = 9, height = 10;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist2 = dx * dx + dz * dz;
                if (dist2 > radius * radius) continue;
                int x = cx + dx, z = cz + dz;
                for (int dy = -3; dy <= 0; dy++) {
                    BlockState s = stone;
                    if (dist2 <= 16) s = dy == 0 ? grass : (dy >= -2 ? dirt : stone);
                    level.setBlock(p.set(x, floorY + dy, z), s, 2);
                }
                int domeH = (int) Math.ceil(height * Math.sqrt(1.0 - dist2 / (radius * radius)));
                for (int dy = 1; dy <= domeH; dy++)
                    level.setBlock(p.set(x, floorY + dy, z), air, 2);
            }
        }
        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int tx = cx + 2, tz = cz;
        for (int dy = 1; dy <= 5; dy++)
            level.setBlock(p.set(tx, floorY + dy, tz), log, 2);
        for (int dy = 3; dy <= 6; dy++) {
            int r = dy <= 4 ? 2 : 1;
            for (int lx = -r; lx <= r; lx++) {
                for (int lz = -r; lz <= r; lz++) {
                    if (r == 2 && Math.abs(lx) == 2 && Math.abs(lz) == 2) continue;
                    if (dy == 6 && Math.abs(lx) + Math.abs(lz) > 1) continue;
                    if (lx == 0 && lz == 0 && dy <= 5) continue;
                    p.set(tx + lx, floorY + dy, tz + lz);
                    if (level.getBlockState(p).isAir()) level.setBlock(p, leaves, 2);
                }
            }
        }
        BlockState torch = Blocks.TORCH.defaultBlockState();
        level.setBlock(p.set(cx + 6, floorY + 1, cz), torch, 2);
        level.setBlock(p.set(cx - 6, floorY + 1, cz), torch, 2);
        level.setBlock(p.set(cx, floorY + 1, cz + 6), torch, 2);
        level.setBlock(p.set(cx, floorY + 1, cz - 6), torch, 2);
        level.setDefaultSpawnPos(spawnPos(), 0.0F);
        data.setSpawnGenerated(true);
    }
}