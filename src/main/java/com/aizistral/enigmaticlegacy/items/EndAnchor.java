package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.GenericBlockItem;
import com.aizistral.enigmaticlegacy.objects.AnchorSearchResult;
import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class EndAnchor extends GenericBlockItem {

    public static Omniconfig.BooleanParameter endExclusiveMode;
    public static Omniconfig.PerhapsParameter chargeSaveChance;
    public static final List<ResourceLocation> DIMENSION_BLACKLIST = new ArrayList<>();

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("EndAnchor");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            endExclusiveMode = builder.comment("If true, Dimensional Anchor will only work in The End.")
                .sync()
                .getBoolean("EndExclusiveMode", false);

            chargeSaveChance = builder
                .comment("Chance that Dimensional Anchor will not spend a charge upon player respawn")
                .getPerhaps("ChargeSaveChance", 35);
        }

        builder.popPrefix();

        DIMENSION_BLACKLIST.clear();
        String[] blacklist = builder.config.getStringList(
            "EndAnchorDimensionBlacklist",
            builder.getCurrentCategory(),
            new String[0],
            "List of specific dimensions in which Dimensional Anchor will not work. If you want it to only work in The End - use EndExclusiveMode instead."
                + " Examples: minecraft:overworld, minecraft:the_nether");

        Arrays.stream(blacklist)
            .forEach(entry -> DIMENSION_BLACKLIST.add(new ResourceLocation(entry)));
    }

    public EndAnchor() {
        super(
            EnigmaticBlocks.END_ANCHOR,
            getDefaultProperties().stacksTo(1)
                .fireResistant()
                .rarity(EnumRarity.epic));
    }

    public void appendHoverText(ItemStack stack, World level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (endExclusiveMode.getValue()) {
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.endAnchor1_alt");
            } else if (!DIMENSION_BLACKLIST.isEmpty()) {
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.endAnchor1_alt2");
            } else {
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.endAnchor1");
            }
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.endAnchor2");
            ItemLoreHelper.addLocalizedString(
                tooltip,
                "tooltip.enigmaticlegacy.endAnchor3",
                EnumChatFormatting.GOLD,
                chargeSaveChance + "%");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.endAnchor4");
        } else {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    public static AnchorSearchResult findAndUseEndAnchor(WorldServer level, BlockPos pos, float angle, boolean forced,
        boolean keep) {
        AnchorSearchResult result = findEndAnchor(level, pos, angle, forced, keep);
        if (result.found()) {
            boolean usedCharge = false;

            if (!keep && result.location()
                .isPresent()) {
                usedCharge = useEndAnchor(level, pos);
            }

            return new AnchorSearchResult(result.location(), result.found(), usedCharge);
        } else return result;
    }

    public static AnchorSearchResult findEndAnchor(WorldServer level, BlockPos pos, float angle, boolean forced,
        boolean keep) {
        Block block = level.getBlock(pos.getX(), pos.getY(), pos.getZ());
        int meta = level.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());

        if (block instanceof BlockEndAnchor && meta > 0 && BlockEndAnchor.canSetSpawn(level)) {
            Optional<Vec3> optional = Optional
                .of(Vec3.createVectorHelper(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D));
            return new AnchorSearchResult(optional, true, false);
        }

        return new AnchorSearchResult(Optional.empty(), false, false);
    }

    public static boolean useEndAnchor(WorldServer level, BlockPos pos) {
        if (!EndAnchor.chargeSaveChance.getValue()
            .roll()) {
            int meta = level.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            level.setBlockMetadataWithNotify(pos.getX(), pos.getY(), pos.getZ(), Math.max(meta - 1, 0), 3);
            return true;
        } else return false;
    }

}
