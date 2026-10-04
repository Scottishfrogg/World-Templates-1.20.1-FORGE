package net.scottishfrogg.worldtemplates;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.scottishfrogg.worldtemplates.worldgen.*;
import org.slf4j.Logger;

@Mod(WorldMod.MOD_ID)
public class WorldMod {
    public static final String MOD_ID = "worldmod";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int STARTER_ISLAND_Y = 70;
    private static final int DESERT_ISLAND_Y = 76;
    private static final int BEACH_ISLAND_Y = 76;
    private static final int END_PORTAL_Y = 69;
    private static final int END_ELYTRA_Y = 150;

    @SuppressWarnings("removal")
    public WorldMod() {
        IEventBus modEventBus =
                FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        ModBiomeSource.register(modEventBus);
        ModFeatures.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        long seed = event.getServer().getWorldData().worldGenOptions().seed();
        IslandGrid.init(seed);
        CaveNoise.init(seed);
    }
    @SubscribeEvent
    public void onCreateSpawn(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level
                && CaveWorld.isCaveWorld(level)) {
            event.getSettings().setSpawn(CaveWorld.spawnPos(), 0.0F);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension().equals(Level.END)) {
            if (!SkyblockWorld.isSkyblockEnd(level)) {
                return;
            }
            createEndPortalFoundation(level);
            SkyblockEndSavedData data =
                    level.getDataStorage().computeIfAbsent(
                            SkyblockEndSavedData::load,
                            SkyblockEndSavedData::create,
                            "worldmod_skyblock_end"
                    );
            if (!data.isElytraGenerated()) {
                BlockPos elytraPos = new BlockPos(
                        0,
                        END_ELYTRA_Y,
                        -20
                );
                boolean elytraPlaced =
                        SkyblockStructure.place(
                                level,
                                elytraPos,
                                SkyblockStructure.SKYBLOCK_END_ELYTRA
                        );
                if (elytraPlaced) {
                    data.setElytraGenerated(true);
                }
            }
            return;
        }
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }
        if (CaveWorld.isCaveWorld(level)) {
            CaveWorld.generateSpawn(level);
            return;
        }
        if (!SkyblockWorld.isSkyblock(level)) {
            return;
        }
        SkyblockSavedData data =
                level.getDataStorage().computeIfAbsent(
                        SkyblockSavedData::load,
                        SkyblockSavedData::create,
                        "worldmod_skyblock"
                );
        if (data.isStarterGenerated()) {
            return;
        }
        BlockPos currentSpawn = level.getSharedSpawnPos();
        BlockPos starterIslandPos = new BlockPos(
                currentSpawn.getX(),
                STARTER_ISLAND_Y,
                currentSpawn.getZ()
        );
        level.setDefaultSpawnPos(
                starterIslandPos,
                0.0F
        );
        boolean starterPlaced =
                SkyblockStructure.place(
                        level,
                        starterIslandPos,
                        SkyblockStructure.SKYBLOCK_START
                );
        BlockPos desertIslandPos = new BlockPos(
                starterIslandPos.getX(),
                DESERT_ISLAND_Y,
                starterIslandPos.getZ() - 30
        );
        boolean desertPlaced =
                SkyblockStructure.place(
                        level,
                        desertIslandPos,
                        SkyblockStructure.SKYBLOCK_DESERT
                );
        BlockPos beachIslandPos = new BlockPos(
                starterIslandPos.getX(),
                BEACH_ISLAND_Y,
                starterIslandPos.getZ() - 80
        );
        boolean beachPlaced =
                SkyblockStructure.place(
                        level,
                        beachIslandPos,
                        SkyblockStructure.SKYBLOCK_BEACH
                );
        BlockPos endPortalPos = new BlockPos(
                beachIslandPos.getX(),
                beachIslandPos.getY(),
                beachIslandPos.getZ() - 100
        );
        boolean endPortalPlaced =
                SkyblockStructure.place(
                        level,
                        endPortalPos,
                        SkyblockStructure.SKYBLOCK_ENDPORTAL
                );
        if (starterPlaced &&
                desertPlaced &&
                beachPlaced &&
                endPortalPlaced) {
            data.setStarterGenerated(true);
        }
    }
    @SubscribeEvent
    public void onPlayerChangedDimension(
            PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (!level.dimension().equals(Level.NETHER)) {
            return;
        }
        if (!SkyblockWorld.isSkyblockNether(level)) {
            return;
        }
        SkyblockNetherSavedData data =
                level.getDataStorage().computeIfAbsent(
                        SkyblockNetherSavedData::load,
                        SkyblockNetherSavedData::create,
                        "worldmod_skyblock_nether"
                );
        if (data.isStarterGenerated()) {
            return;
        }
        BlockPos portalPos = player.blockPosition();
        BlockPos netherStarterPos = new BlockPos(
                portalPos.getX(),
                portalPos.getY(),
                portalPos.getZ() - 10
        );
        boolean starterPlaced =
                SkyblockStructure.place(
                        level,
                        netherStarterPos,
                        SkyblockStructure.SKYBLOCK_NETHER_START
                );
        BlockPos blazeIslandPos = new BlockPos(
                netherStarterPos.getX(),
                netherStarterPos.getY(),
                netherStarterPos.getZ() - 60
        );
        boolean blazePlaced =
                SkyblockStructure.place(
                        level,
                        blazeIslandPos,
                        SkyblockStructure.SKYBLOCK_NETHER_BLAZE
                );
        if (starterPlaced && blazePlaced) {
            data.setStarterGenerated(true);
        }
    }
    private void createEndPortalFoundation(ServerLevel level) {
        if (!SkyblockWorld.isSkyblockEnd(level)) {
            return;
        }
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                BlockPos pos = new BlockPos(
                        x,
                        END_PORTAL_Y,
                        z
                );
                if (level.isEmptyBlock(pos)) {
                    level.setBlock(
                            pos,
                            Blocks.END_STONE.defaultBlockState(),
                            3
                    );
                }
            }
        }
    }
    @Mod.EventBusSubscriber(
            modid = MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(
                FMLClientSetupEvent event) {
        }
    }
}