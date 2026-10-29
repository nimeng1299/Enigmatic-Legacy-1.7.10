package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Facade mirroring modern ForgeRegistries lookups on top of 1.7.10 registries.
 */
public final class ForgeRegistries {

    public static final Items ITEMS = new Items();
    public static final Blocks BLOCKS = new Blocks();
    public static final EntityTypes ENTITY_TYPES = new EntityTypes();
    public static final MobEffects MOB_EFFECTS = new MobEffects();
    public static final Enchantments ENCHANTMENTS = new Enchantments();

    private ForgeRegistries() {
        // NO-OP
    }

    private static ResourceLocation toRL(Object name) {
        if (name == null) return null;
        String string = name.toString();
        return string.indexOf(':') >= 0 ? new ResourceLocation(string) : new ResourceLocation("minecraft", string);
    }

    public static class Items {

        public Item getValue(ResourceLocation location) {
            if (location == null) return null;
            return GameRegistry.findItem(location.getResourceDomain(), location.getResourcePath());
        }

        public ResourceLocation getKey(Item item) {
            if (item == null) return null;
            return toRL(
                GameData.getItemRegistry()
                    .getNameForObject(item));
        }
    }

    public static class Blocks {

        public Block getValue(ResourceLocation location) {
            if (location == null) return null;
            return GameRegistry.findBlock(location.getResourceDomain(), location.getResourcePath());
        }

        public ResourceLocation getKey(Block block) {
            if (block == null) return null;
            return toRL(
                GameData.getBlockRegistry()
                    .getNameForObject(block));
        }
    }

    public static class EntityTypes {

        public ResourceLocation getKey(Entity entity) {
            if (entity == null) return null;
            Object name = EntityList.classToStringMapping.get(entity.getClass());
            return name instanceof String ? new ResourceLocation((String) name) : null;
        }

        public ResourceLocation getKey(Class<? extends Entity> clazz) {
            Object name = EntityList.classToStringMapping.get(clazz);
            return name instanceof String ? new ResourceLocation((String) name) : null;
        }

        public Class<? extends Entity> getValue(ResourceLocation location) {
            if (location == null) return null;
            Object clazz = EntityList.stringToClassMapping
                .get(location.getResourceDomain() + "." + location.getResourcePath());
            if (clazz == null) {
                clazz = EntityList.stringToClassMapping.get(location.getResourcePath());
            }
            return clazz instanceof Class ? (Class<? extends Entity>) clazz : null;
        }
    }

    public static class MobEffects {

        public ResourceLocation getKey(Potion potion) {
            if (potion == null) return null;
            for (int i = 0; i < Potion.potionTypes.length; i++) {
                if (Potion.potionTypes[i] == potion) return new ResourceLocation(potion.getName());
            }
            return null;
        }

        public Potion getValue(ResourceLocation location) {
            if (location == null) return null;
            for (Potion potion : Potion.potionTypes) {
                if (potion != null && potion.getName()
                    .equals(location.getResourcePath())) return potion;
            }
            return null;
        }
    }

    public static class Enchantments {

        public ResourceLocation getKey(Enchantment enchantment) {
            if (enchantment == null) return null;
            for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
                if (Enchantment.enchantmentsList[i] == enchantment)
                    return new ResourceLocation("minecraft", "enchantment." + i);
            }
            return null;
        }

        public Enchantment getValue(ResourceLocation location) {
            if (location == null) return null;
            try {
                return Enchantment.enchantmentsList[Integer.parseInt(location.getResourcePath())];
            } catch (Exception ex) {
                return null;
            }
        }
    }

}
