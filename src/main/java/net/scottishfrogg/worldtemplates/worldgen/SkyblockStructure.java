package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Optional;

public class SkyblockStructure {

    public static final ResourceLocation SKYBLOCK_START =
            new ResourceLocation("worldmod", "skyblock_start");
    public static final ResourceLocation SKYBLOCK_DESERT =
            new ResourceLocation("worldmod", "skyblock_desert");
    public static final ResourceLocation SKYBLOCK_BEACH =
            new ResourceLocation("worldmod", "skyblock_beach");
    public static final ResourceLocation SKYBLOCK_NETHER_START =
            new ResourceLocation("worldmod", "skyblock_nether_start");
    public static final ResourceLocation SKYBLOCK_NETHER_BLAZE =
            new ResourceLocation("worldmod", "skyblock_nether_blaze");
    public static final ResourceLocation SKYBLOCK_ENDPORTAL =
            new ResourceLocation("worldmod", "skyblock_endportal");
    public static final ResourceLocation SKYBLOCK_END_ELYTRA =
            new ResourceLocation("worldmod", "skyblock_end_elytra");

    public static boolean place(
            ServerLevel level,
            BlockPos pos,
            ResourceLocation structure
    ) {
        Optional<StructureTemplate> optionalTemplate =
                level.getStructureManager().get(structure);
        if (optionalTemplate.isEmpty()) {
            return false;
        }
        StructureTemplate template = optionalTemplate.get();
        StructurePlaceSettings settings =
                new StructurePlaceSettings();
        return template.placeInWorld(
                level,
                pos,
                pos,
                settings,
                level.getRandom(),
                2
        );
    }
}
