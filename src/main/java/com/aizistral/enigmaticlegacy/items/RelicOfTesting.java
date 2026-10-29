package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IHidden;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RelicOfTesting extends ItemBase implements IHidden {

    public Random lootRandomizer = new Random();

    public RelicOfTesting() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1));
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.relicOfTesting1");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem();

        SuperpositionHandler.setSpellstoneCooldown(playerIn, 0);

        SuperpositionHandler.setPersistentInteger(playerIn, EnigmaticItems.TATTERED_TOME.persistantPointsTag, 0);

        if (!worldIn.isRemote) {
            // Quote.NO_PERIL.play((ServerPlayer) playerIn);

            TransientPlayerData.get(playerIn)
                .setUnlockedNarrator(false);
            // playerIn.sendMessage(Component.literal("INTEGER: " + UltimaTestConfig.integerTest.getValue()),
            // playerIn.getUniqueID());
            // playerIn.sendMessage(Component.literal("FLOAT: " + UltimaTestConfig.floatTest.getValue()),
            // playerIn.getUniqueID());
            // playerIn.sendMessage(Component.literal("BOOLEAN: " + UltimaTestConfig.booleanTest.getValue()),
            // playerIn.getUniqueID());

            worldIn.setEntityState(playerIn, (byte) 60);
        } else {
            playerIn.deathTime = 20;
        }

        /*
         * if(!worldIn.isRemote) {
         * Component name = null;
         * if(itemstack.hasDisplayName()) {
         * name = itemstack.getDisplayName();
         * } else {
         * name = Component.translatable(this.getTranslationKey());
         * }
         * playerIn.openContainer(new PortableCrafterContainerProvider(name));
         * }
         */

        playerIn.swingItem();

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);

    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        List<net.minecraft.entity.monster.EntityCreeper> list = world.getEntitiesWithinAABB(
            net.minecraft.entity.monster.EntityCreeper.class,
            SuperpositionHandler.getBoundingBoxAroundEntity(entity, 24D));

        for (net.minecraft.entity.monster.EntityCreeper creeper : list) {
            if (creeper.getAttackTarget() == entity) {
                creeper.setAttackTarget(null);
            }
        }

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (entity.ticksExisted % 20 == 0) {
                // System.out.println("Time since rest: " +
                // player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)));
            }
        }

        /*
         * if (world.isRemote) {
         * if (UltimaTestConfig.fovOverride.getValue() != Minecraft.getMinecraft().gameSettings.fov) {
         * Minecraft.getMinecraft().gameSettings.fov = UltimaTestConfig.fovOverride.getValue();
         * }
         * }
         */
    }

    /*
     * public InteractionResult onItemUse(UseOnContext context) { Player
     * player = context.getPlayer(); Level world = context.getWorld(); //ItemStack
     * stack = context.getItem();
     * if (world.getBlockState(context.getPos()).hasTileEntity()) { if
     * (world.getTileEntity(context.getPos()) instanceof ChestTileEntity) {
     * ChestTileEntity chest = (ChestTileEntity)
     * world.getTileEntity(context.getPos());
     * if (context.getFace() != Direction.UP) { chest.clear(); } else {
     * chest.setLootTable(BuiltInLootTables.CHESTS_SIMPLE_DUNGEON,
     * lootRandomizer.nextLong()); chest.fillWithLoot(player); }
     * return InteractionResult.SUCCESS; } }
     * return InteractionResult.PASS; }
     */

}
