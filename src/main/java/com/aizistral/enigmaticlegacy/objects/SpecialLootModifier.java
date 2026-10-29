package com.aizistral.enigmaticlegacy.objects;

import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;

/**
 * Loot modifier stand-in; 1.7.10 has no global loot modifier system, so this
 * only retains the configuration entries used elsewhere.
 */
public class SpecialLootModifier {

    public static final List<ResourceLocation> SUSPICIOUS_TABLES = ImmutableList.of(
        new ResourceLocation("minecraft", "archaeology/desert_pyramid"),
        new ResourceLocation("minecraft", "archaeology/desert_well"),
        new ResourceLocation("minecraft", "archaeology/ocean_ruin_cold"),
        new ResourceLocation("minecraft", "archaeology/ocean_ruin_warm"),
        new ResourceLocation("minecraft", "archaeology/trail_ruins_common"),
        new ResourceLocation("minecraft", "archaeology/trail_ruins_rare"));

    public static Omniconfig.DoubleParameter earthHeartChance;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("CustomLoot");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            earthHeartChance = builder
                .comment("The chance for Heart of the Earth to be obtained from suspicious blocks (default is 3%).")
                .getDouble("EarthHeartChance", 0.03);
        }

        builder.popPrefix();
    }

}
