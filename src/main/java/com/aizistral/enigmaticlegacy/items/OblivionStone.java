package com.aizistral.enigmaticlegacy.items;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OblivionStone extends ItemBase implements Vanishable {

    public static Omniconfig.IntParameter itemSoftcap;
    public static Omniconfig.IntParameter itemHardcap;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("OblivionStone");

        itemSoftcap = builder.comment(
            "Soft cap for Keystone of The Oblivion. When it's reached, the list view seen in it's Ctrl tooltip will be fixed at this amount of items, and become chaotic and unreadable. Required since monitors are not infinitely large these days.")
            .min(1)
            .getInt("Softcap", 25);

        itemHardcap = builder.comment(
            "Hard cap for Keystone of The Oblivion. When it's reached, you will no longer be able to add new items to it's list via crafting. Required to prevent potential perfomance issues with ridiculously large lists.")
            .min(1)
            .getInt("Hardcap", 100);

        builder.popPrefix();
    }

    public OblivionStone() {
        super(
            ItemBase.getDefaultProperties()
                .stacksTo(1)
                .rarity(EnumRarity.rare)
                .fireResistant());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone11");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone12");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone13");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone14");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone15");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStone16");
        } else if (Screen.hasControlDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStoneCtrlList");

            if (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("SupersolidID", 9)) {
                NBTTagList arr = stack.getTagCompound()
                    .getTagList("SupersolidID", 8);

                if (arr.tagCount() <= itemSoftcap.getValue()) {
                    for (int i = 0; i < arr.tagCount(); i++) {
                        Item something = (Item) Item.itemRegistry.getObject(arr.getStringTagAt(i));

                        if (something != null) {
                            ItemStack displayStack = new ItemStack(something, 1);
                            list.add(
                                Component.literal(" - " + displayStack.getDisplayName())
                                    .withStyle(EnumChatFormatting.GOLD));
                        }
                    }
                } else {
                    for (int s = 0; s < itemSoftcap.getValue(); s++) {
                        int randomID = random.nextInt(arr.tagCount());
                        Item something = (Item) Item.itemRegistry.getObject(arr.getStringTagAt(randomID));

                        if (something != null) {
                            ItemStack displayStack = new ItemStack(something, 1);
                            list.add(
                                Component.literal(" - " + displayStack.getDisplayName())
                                    .withStyle(EnumChatFormatting.GOLD));
                        }
                    }
                }
            }

        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStoneHoldCtrl");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        MutableComponent mode;

        if (ItemNBTHelper.getBoolean(stack, "IsActive", true)) {
            mode = Component.translatable(
                "tooltip.enigmaticlegacy.voidStoneMode" + ItemNBTHelper.getInt(stack, "ConsumptionMode", 0));
        } else {
            mode = Component.translatable("tooltip.enigmaticlegacy.voidStoneModeInactive");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidStoneModeDesc", null, mode.getString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();
        int mode = ItemNBTHelper.getInt(stack, "ConsumptionMode", 0);

        if (player.isSneaking()) {
            world.playSoundAtEntity(
                player,
                (ItemNBTHelper.getBoolean(stack, "IsActive", true) ? EnigmaticSounds.CHARGED_OFF
                    : EnigmaticSounds.CHARGED_ON).getName(),
                (float) (0.8F + (Math.random() * 0.2F)),
                (float) (0.8F + (Math.random() * 0.2F)));
            ItemNBTHelper.setBoolean(stack, "IsActive", !ItemNBTHelper.getBoolean(stack, "IsActive", true));
        } else {
            if (mode >= 0 && mode < 2) {
                ItemNBTHelper.setInt(stack, "ConsumptionMode", mode + 1);
            } else {
                ItemNBTHelper.setInt(stack, "ConsumptionMode", 0);
            }

            world.playSoundAtEntity(
                player,
                (SoundEvents.EXPERIENCE_ORB_PICKUP).getName(),
                1.0F,
                (float) (0.8F + (Math.random() * 0.2F)));
        }

        player.swingItem();
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer) || entity.ticksExisted % 4 != 0) return;

        EntityPlayer player = (EntityPlayer) entity;

        if (!ItemNBTHelper.getBoolean(stack, "IsActive", true) || !stack.hasTagCompound()
            || stack.getTagCompound()
                .getTagList("SupersolidID", 8)
                .tagCount() < 1)
            return;

        NBTTagList arr = stack.getTagCompound()
            .getTagList("SupersolidID", 8);
        OblivionStone.consumeStuff(player, arr, ItemNBTHelper.getInt(stack, "ConsumptionMode", 0));
    }

    public static void consumeStuff(EntityPlayer player, NBTTagList list, int mode) {
        HashMap<Integer, ItemStack> stackMap = new HashMap<Integer, ItemStack>();
        int filledStacks = 0;

        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            ItemStack slotStack = player.inventory.mainInventory[slot];

            if (slotStack != null && slotStack.stackSize > 0) {
                filledStacks += 1;

                if (slotStack.getItem() != EnigmaticItems.VOID_STONE) {
                    stackMap.put(slot, slotStack);
                }
            }
        }

        if (stackMap.isEmpty()) return;

        if (mode == 0) {
            for (int i = 0; i < list.tagCount(); i++) {
                Item target = (Item) Item.itemRegistry.getObject(list.getStringTagAt(i));

                if (target == null) continue;

                for (int slot : stackMap.keySet()) {
                    if (stackMap.get(slot)
                        .getItem() == target) {
                        player.inventory.setInventorySlotContents(slot, null);
                    }
                }
            }
        } else if (mode == 1) {
            for (int i = 0; i < list.tagCount(); i++) {
                Item target = (Item) Item.itemRegistry.getObject(list.getStringTagAt(i));

                if (target == null) continue;

                HashMap<Integer, ItemStack> localStackMap = new HashMap<Integer, ItemStack>(stackMap);
                Multimap<Integer, Integer> stackSizeMultimap = ArrayListMultimap.create();

                for (int slot : stackMap.keySet()) {
                    if (stackMap.get(slot)
                        .getItem() != target) {
                        localStackMap.remove(slot);
                    }
                }

                for (int slot : localStackMap.keySet()) {
                    stackSizeMultimap.put(localStackMap.get(slot).stackSize, slot);
                }

                while (localStackMap.size() > 1) {
                    int smallestStackSize = Collections.min(stackSizeMultimap.keySet());
                    Collection<Integer> smallestStacks = stackSizeMultimap.get(smallestStackSize);
                    int slotWithSmallestStack = Collections.max(smallestStacks);

                    player.inventory.setInventorySlotContents(slotWithSmallestStack, null);
                    stackSizeMultimap.remove(smallestStackSize, slotWithSmallestStack);
                    localStackMap.remove(slotWithSmallestStack);
                }
            }

        } else if (mode == 2) {
            if (filledStacks >= player.inventory.mainInventory.length) {
                for (int i = 0; i < list.tagCount(); i++) {
                    Item target = (Item) Item.itemRegistry.getObject(list.getStringTagAt(i));

                    if (target == null) continue;

                    HashMap<Integer, ItemStack> localStackMap = new HashMap<Integer, ItemStack>(stackMap);
                    Multimap<Integer, Integer> stackSizeMultimap = ArrayListMultimap.create();

                    for (int slot : stackMap.keySet()) {
                        if (stackMap.get(slot)
                            .getItem() != target) {
                            localStackMap.remove(slot);
                        }
                    }

                    for (int slot : localStackMap.keySet()) {
                        stackSizeMultimap.put(localStackMap.get(slot).stackSize, slot);
                    }

                    if (localStackMap.size() > 0) {
                        int smallestStackSize = Collections.min(stackSizeMultimap.keySet());
                        Collection<Integer> smallestStacks = stackSizeMultimap.get(smallestStackSize);
                        int slotWithSmallestStack = Collections.max(smallestStacks);

                        player.inventory.setInventorySlotContents(slotWithSmallestStack, null);
                        return;
                    }
                }
            }
        }

    }

    public static void addSupersolidItem(ItemStack stone, Item item) {
        if (!stone.hasTagCompound()) {
            stone.setTagCompound(new NBTTagCompound());
        }

        NBTTagList list = stone.getTagCompound()
            .getTagList("SupersolidID", 8);
        String id = Item.itemRegistry.getNameForObject(item);
        list.appendTag(new NBTTagString(id));
        stone.getTagCompound()
            .setTag("SupersolidID", list);
    }

    public static List<String> getSupersolidIds(ItemStack stone) {
        List<String> result = new java.util.ArrayList<>();

        if (stone.hasTagCompound() && stone.getTagCompound()
            .hasKey("SupersolidID", 9)) {
            NBTTagList list = stone.getTagCompound()
                .getTagList("SupersolidID", 8);

            for (int i = 0; i < list.tagCount(); i++) {
                result.add(list.getStringTagAt(i));
            }
        }

        return result;
    }

}
