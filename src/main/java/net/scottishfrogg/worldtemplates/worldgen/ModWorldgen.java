package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ModWorldgen {

    public static final RegistrySetBuilder BUILDER =
            new RegistrySetBuilder()
                    .add(
                            Registries.NOISE,
                            ModNoises::bootstrap
                    )
                    .add(
                            Registries.DENSITY_FUNCTION,
                            ModDensityFunctions::bootstrap
                    )
                    .add(
                            Registries.NOISE_SETTINGS,
                            ModNoiseSettings::bootstrap
                    );
}