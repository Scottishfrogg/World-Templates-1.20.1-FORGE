package net.scottishfrogg.worldtemplates.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.worldtemplates.WorldMod;

@Mod.EventBusSubscriber(
        modid = WorldMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class SkyblockWorldCreationHandler {
    @SubscribeEvent
    public static void onCreateWorldScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) {
            return;
        }
        WorldCreationUiState uiState = screen.getUiState();
        updateStructures(uiState);
        uiState.addListener(SkyblockWorldCreationHandler::updateStructures);
    }
    private static void updateStructures(WorldCreationUiState uiState) {
        if (isSkyblock(uiState)) {
            if (uiState.isGenerateStructures()) {
                uiState.setGenerateStructures(false);
            }
        }
    }
    private static boolean isSkyblock(WorldCreationUiState uiState) {
        WorldCreationUiState.WorldTypeEntry worldType = uiState.getWorldType();
        if (worldType == null) {
            return false;
        }
        return worldType.preset()
                .unwrapKey()
                .map(key -> key.location().getNamespace().equals(WorldMod.MOD_ID)
                        && key.location().getPath().equals("skyblock"))
                .orElse(false);
    }
}