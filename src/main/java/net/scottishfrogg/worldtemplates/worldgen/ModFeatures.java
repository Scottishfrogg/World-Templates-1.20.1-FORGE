package net.scottishfrogg.worldtemplates.worldgen;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.scottishfrogg.worldtemplates.WorldMod;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, WorldMod.MOD_ID);
    public static final RegistryObject<FreezingIceFeature> FREEZING_ICE =
            FEATURES.register("freezing_ice", FreezingIceFeature::new);
    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}