package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldgenProvider extends DatapackBuiltinEntriesProvider {

    public ModWorldgenProvider(
            DataGenerator generator,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(
                generator.getPackOutput(),
                registries,
                ModWorldgen.BUILDER,
                Set.of("worldmod")
        );
    }
}