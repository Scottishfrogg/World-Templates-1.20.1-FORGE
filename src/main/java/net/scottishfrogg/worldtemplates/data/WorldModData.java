package net.scottishfrogg.worldtemplates.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.scottishfrogg.worldtemplates.WorldMod;
import net.scottishfrogg.worldtemplates.worldgen.ModWorldgen;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(
        modid = WorldMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class WorldModData {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> registries =
                event.getLookupProvider();
        generator.addProvider(
                event.includeServer(),
                new DatapackBuiltinEntriesProvider(
                        generator.getPackOutput(),
                        registries,
                        ModWorldgen.BUILDER,
                        Set.of(WorldMod.MOD_ID)
                )
        );
    }
}