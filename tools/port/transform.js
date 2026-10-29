const fs = require('fs');
const path = require('path');

const ROOT = process.argv[2] || 'src/main/java-pending';
const APPLY = process.argv.includes('--apply');

const IMPORT_MAP = {
  'net.minecraft.resources.ResourceLocation': 'net.minecraft.util.ResourceLocation',
  'net.minecraft.world.item.ItemStack': 'net.minecraft.item.ItemStack',
  'net.minecraft.world.item.Item': 'net.minecraft.item.Item',
  'net.minecraft.world.item.BlockItem': 'net.minecraft.item.ItemBlock',
  'net.minecraft.world.item.Items': 'net.minecraft.init.Items',
  'net.minecraft.world.item.Rarity': 'net.minecraft.item.EnumRarity',
  'net.minecraft.world.item.TooltipFlag': 'com.aizistral.enigmaticlegacy.compat.TooltipFlag',
  'net.minecraft.world.item.CreativeModeTab': 'net.minecraft.creativetab.CreativeTabs',
  'net.minecraft.world.item.ArmorMaterial': 'net.minecraft.item.ItemArmor.ArmorMaterial',
  'net.minecraft.world.item.ArmorItem': 'net.minecraft.item.ItemArmor',
  'net.minecraft.world.item.Tier': 'com.aizistral.enigmaticlegacy.compat.Tier',
  'net.minecraft.world.item.Tiers': 'com.aizistral.enigmaticlegacy.compat.Tiers',
  'net.minecraft.world.item.UseAnim': 'com.aizistral.enigmaticlegacy.compat.UseAnim',
  'net.minecraft.world.item.Equipable': 'com.aizistral.enigmaticlegacy.compat.Equipable',
  'net.minecraft.world.item.Vanishable': 'com.aizistral.enigmaticlegacy.compat.Vanishable',
  'net.minecraft.world.item.context.UseOnContext': 'com.aizistral.enigmaticlegacy.compat.UseOnContext',
  'net.minecraft.world.food.FoodProperties': 'com.aizistral.enigmaticlegacy.compat.FoodProperties',
  'net.minecraft.world.InteractionHand': 'com.aizistral.enigmaticlegacy.compat.InteractionHand',
  'net.minecraft.world.InteractionResult': 'com.aizistral.enigmaticlegacy.compat.InteractionResult',
  'net.minecraft.world.InteractionResultHolder': 'com.aizistral.enigmaticlegacy.compat.InteractionResultHolder',
  'net.minecraft.world.entity.player.Player': 'net.minecraft.entity.player.EntityPlayer',
  'net.minecraft.world.entity.player.Inventory': 'net.minecraft.entity.player.InventoryPlayer',
  'net.minecraft.server.level.ServerPlayer': 'net.minecraft.entity.player.EntityPlayerMP',
  'net.minecraft.server.level.ServerLevel': 'net.minecraft.world.WorldServer',
  'net.minecraft.world.entity.LivingEntity': 'net.minecraft.entity.EntityLivingBase',
  'net.minecraft.world.entity.Entity': 'net.minecraft.entity.Entity',
  'net.minecraft.world.entity.item.ItemEntity': 'net.minecraft.entity.item.EntityItem',
  'net.minecraft.world.entity.projectile.ThrownPotion': 'net.minecraft.entity.projectile.EntityPotion',
  'net.minecraft.world.entity.projectile.AbstractArrow': 'net.minecraft.entity.projectile.EntityArrow',
  'net.minecraft.world.entity.projectile.ThrownEgg': 'net.minecraft.entity.projectile.EntityEgg',
  'net.minecraft.world.entity.projectile.FireworkRocketEntity': 'net.minecraft.entity.projectile.EntityFireworkRocket',
  'net.minecraft.world.entity.projectile.Snowball': 'net.minecraft.entity.projectile.EntitySnowball',
  'net.minecraft.world.level.Level': 'net.minecraft.world.World',
  'net.minecraft.world.level.block.Block': 'net.minecraft.block.Block',
  'net.minecraft.world.level.block.Blocks': 'net.minecraft.init.Blocks',
  'net.minecraft.world.level.block.state.BlockState': 'com.aizistral.enigmaticlegacy.compat.BlockState',
  'net.minecraft.world.level.block.entity.BlockEntity': 'net.minecraft.tileentity.TileEntity',
  'net.minecraft.world.level.ClipContext': 'com.aizistral.enigmaticlegacy.compat.ClipContext',
  'net.minecraft.world.phys.Vec3': 'net.minecraft.util.Vec3',
  'net.minecraft.world.phys.AABB': 'net.minecraft.util.AxisAlignedBB',
  'net.minecraft.world.phys.BlockHitResult': 'net.minecraft.util.MovingObjectPosition',
  'net.minecraft.core.BlockPos': 'com.gtnewhorizon.gtnhlib.blockpos.BlockPos',
  'net.minecraft.core.Direction': 'net.minecraftforge.common.util.ForgeDirection',
  'net.minecraft.core.particles.ParticleTypes': 'com.aizistral.enigmaticlegacy.compat.ParticleTypes',
  'net.minecraft.util.Mth': 'com.aizistral.enigmaticlegacy.compat.Mth',
  'net.minecraft.network.chat.Component': 'com.aizistral.enigmaticlegacy.compat.text.Component',
  'net.minecraft.network.chat.MutableComponent': 'com.aizistral.enigmaticlegacy.compat.text.MutableComponent',
  'net.minecraft.network.chat.CommonComponents': 'com.aizistral.enigmaticlegacy.compat.text.CommonComponents',
  'net.minecraft.network.chat.contents.TranslatableContents': 'com.aizistral.enigmaticlegacy.compat.text.TranslatableContents',
  'net.minecraft.network.chat.contents.LiteralContents': 'com.aizistral.enigmaticlegacy.compat.text.LiteralContents',
  'net.minecraft.ChatFormatting': 'net.minecraft.util.EnumChatFormatting',
  'net.minecraft.client.gui.screens.Screen': 'com.aizistral.enigmaticlegacy.compat.Screen',
  'net.minecraft.client.KeyMapping': 'net.minecraft.client.settings.KeyBinding',
  'net.minecraft.client.player.LocalPlayer': 'net.minecraft.client.entity.EntityPlayerSP',
  'net.minecraft.world.effect.MobEffectInstance': 'net.minecraft.potion.PotionEffect',
  'net.minecraft.world.effect.MobEffect': 'net.minecraft.potion.Potion',
  'net.minecraft.world.effect.MobEffects': 'com.aizistral.enigmaticlegacy.compat.MobEffects',
  'net.minecraft.world.item.enchantment.Enchantment': 'net.minecraft.enchantment.Enchantment',
  'net.minecraft.world.item.enchantment.EnchantmentHelper': 'net.minecraft.enchantment.EnchantmentHelper',
  'net.minecraft.world.item.enchantment.Enchantments': 'com.aizistral.enigmaticlegacy.compat.Enchantments',
  'net.minecraft.world.item.enchantment.EnchantmentCategory': 'com.aizistral.enigmaticlegacy.compat.EnchantmentCategory',
  'net.minecraft.world.item.crafting.Ingredient': 'com.aizistral.enigmaticlegacy.compat.Ingredient',
  'net.minecraft.nbt.CompoundTag': 'com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag',
  'net.minecraft.nbt.ListTag': 'com.aizistral.enigmaticlegacy.compat.nbt.ListTag',
  'net.minecraft.nbt.Tag': 'net.minecraft.nbt.NBTBase',
  'net.minecraft.sounds.SoundEvent': 'com.aizistral.enigmaticlegacy.compat.SoundEvent',
  'net.minecraft.sounds.SoundSource': 'com.aizistral.enigmaticlegacy.compat.SoundSource',
  'net.minecraft.sounds.SoundEvents': 'com.aizistral.enigmaticlegacy.compat.SoundEvents',
  'net.minecraft.network.FriendlyByteBuf': 'com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf',
  'net.minecraftforge.network.SimpleChannel': 'com.aizistral.enigmaticlegacy.compat.network.SimpleChannel',
  'net.minecraftforge.network.NetworkEvent': 'com.aizistral.enigmaticlegacy.compat.network.NetworkEvent',
  'net.minecraftforge.network.NetworkRegistry': 'com.aizistral.enigmaticlegacy.compat.network.NetworkRegistry',
  'net.minecraftforge.network.PacketDistributor': 'com.aizistral.enigmaticlegacy.compat.network.PacketDistributor',
  'net.minecraftforge.api.distmarker.Dist': 'cpw.mods.fml.relauncher.Side',
  'net.minecraftforge.api.distmarker.OnlyIn': 'cpw.mods.fml.relauncher.SideOnly',
  'net.minecraft.stats.Stats': 'com.aizistral.enigmaticlegacy.compat.Stats',
  'net.minecraftforge.registries.ForgeRegistries': 'com.aizistral.enigmaticlegacy.compat.ForgeRegistries',
  'net.minecraftforge.registries.ObjectHolder': 'com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder',
  'net.minecraftforge.registries.RegisterEvent': 'com.aizistral.enigmaticlegacy.compat.registries.RegisterEvent',
  'net.minecraftforge.registries.IForgeRegistry': 'com.aizistral.enigmaticlegacy.compat.registries.IForgeRegistry',
  'net.minecraftforge.registries.DeferredRegister': 'com.aizistral.enigmaticlegacy.compat.registries.DeferredRegister',
  'net.minecraftforge.registries.RegistryObject': 'com.aizistral.enigmaticlegacy.compat.registries.RegistryObject',
  'net.minecraft.advancements.CriteriaTriggers': 'com.aizistral.enigmaticlegacy.compat.CriteriaTriggers',
  'top.theillusivec4.curios.api.SlotContext': 'com.aizistral.enigmaticlegacy.compat.curios.SlotContext',
  'top.theillusivec4.curios.api.type.capability.ICurio': 'com.aizistral.enigmaticlegacy.compat.curios.ICurio',
  'top.theillusivec4.curios.api.type.capability.ICurio.DropRule': 'com.aizistral.enigmaticlegacy.compat.curios.ICurio.DropRule',
  'top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo': 'com.aizistral.enigmaticlegacy.compat.curios.ICurio.SoundInfo',
  'top.theillusivec4.curios.api.type.capability.ICurioItem': 'com.aizistral.enigmaticlegacy.compat.curios.ICurioItem',
  'vazkii.patchouli.api.PatchouliConfigAccess.TextOverflowMode': 'com.aizistral.enigmaticlegacy.compat.patchouli.TextOverflowMode',
  'net.minecraftforge.eventbus.api.SubscribeEvent': 'cpw.mods.fml.common.eventhandler.SubscribeEvent',
  'net.minecraftforge.eventbus.api.EventPriority': 'cpw.mods.fml.common.eventhandler.EventPriority',
  'net.minecraftforge.eventbus.api.Cancelable': 'cpw.mods.fml.common.eventhandler.Cancelable',
  'net.minecraftforge.common.ToolAction': 'com.aizistral.enigmaticlegacy.compat.ToolAction',
  'net.minecraft.tags.BlockTags': 'com.aizistral.enigmaticlegacy.compat.BlockTags',
  'net.minecraft.tags.TagKey': 'com.aizistral.enigmaticlegacy.compat.TagKey',
  'net.minecraft.world.item.SwordItem': 'net.minecraft.item.ItemSword',
  'net.minecraft.world.item.PickaxeItem': 'net.minecraft.item.ItemPickaxe',
  'net.minecraft.world.item.AxeItem': 'net.minecraft.item.ItemAxe',
  'net.minecraft.world.item.ShovelItem': 'net.minecraft.item.ItemSpade',
  'net.minecraft.world.item.HoeItem': 'net.minecraft.item.ItemHoe',
  'net.minecraft.world.item.ShearsItem': 'net.minecraft.item.ItemShears',
  'net.minecraft.world.item.BowItem': 'net.minecraft.item.ItemBow',
  'net.minecraft.world.item.ArrowItem': 'net.minecraft.item.ItemArrow',
  'net.minecraft.world.level.block.FallingBlock': 'net.minecraft.block.BlockFalling',
  'net.minecraft.world.level.block.SoundType': 'net.minecraft.block.Block.SoundType',
  'net.minecraft.world.level.material.Material': 'net.minecraft.block.material.Material',
  'net.minecraft.world.entity.EquipmentSlot': 'com.aizistral.enigmaticlegacy.compat.EquipmentSlot',
  'net.minecraft.world.entity.Mob': 'net.minecraft.entity.EntityLiving',
  'net.minecraft.world.entity.monster.Monster': 'net.minecraft.entity.monster.IMob',
  'net.minecraft.world.entity.animal.Animal': 'net.minecraft.entity.passive.EntityAnimal',
  'net.minecraft.world.entity.npc.Villager': 'net.minecraft.entity.passive.EntityVillager',
  'net.minecraft.world.entity.projectile.Projectile': 'net.minecraft.entity.projectile.EntityThrowable',
  'net.minecraft.world.damagesource.DamageSource': 'net.minecraft.util.DamageSource',
  'net.minecraft.world.damagesource.DamageTypes': 'com.aizistral.enigmaticlegacy.compat.DamageTypes',
  'net.minecraft.world.damagesource.DamageType': 'com.aizistral.enigmaticlegacy.compat.DamageType',
  'net.minecraft.resources.ResourceKey': 'com.aizistral.enigmaticlegacy.compat.ResourceKey',
  'net.minecraft.core.NonNullList': 'java.util.ArrayList',
  'net.minecraft.util.Tuple': 'com.aizistral.enigmaticlegacy.compat.Tuple',
  'net.minecraft.world.entity.ai.attributes.Attribute': 'com.aizistral.enigmaticlegacy.compat.Attribute',
  'net.minecraft.world.entity.ai.attributes.Attributes': 'com.aizistral.enigmaticlegacy.compat.Attributes',
  'net.minecraft.world.entity.ai.attributes.AttributeModifier': 'com.aizistral.enigmaticlegacy.compat.AttributeModifier',
  'net.minecraft.world.entity.ai.attributes.AttributeMap': 'com.aizistral.enigmaticlegacy.compat.AttributeMap',
  'net.minecraft.world.entity.ai.attributes.AttributeInstance': 'com.aizistral.enigmaticlegacy.compat.AttributeInstance',
  'net.minecraft.world.entity.ai.attributes.AttributeSupplier': 'com.aizistral.enigmaticlegacy.compat.AttributeSupplier',
};

// Type identifiers whose simple name must change.
const IDENT_MAP = {
  Level: 'World',
  ServerLevel: 'WorldServer',
  ServerPlayer: 'EntityPlayerMP',
  LivingEntity: 'EntityLivingBase',
  Player: 'EntityPlayer',
  Rarity: 'EnumRarity',
  ChatFormatting: 'EnumChatFormatting',
  CreativeModeTab: 'CreativeTabs',
  OnlyIn: 'SideOnly',
  Dist: 'Side',
  MobEffectInstance: 'PotionEffect',
  MobEffect: 'Potion',
  BlockItem: 'ItemBlock',
  ArmorItem: 'ItemArmor',
  DiggerItem: 'ItemTool',
  SwordItem: 'ItemSword',
  PickaxeItem: 'ItemPickaxe',
  AxeItem: 'ItemAxe',
  ShovelItem: 'ItemSpade',
  HoeItem: 'ItemHoe',
  BowItem: 'ItemBow',
  ArrowItem: 'ItemArrow',
  LocalPlayer: 'EntityPlayerSP',
  KeyMapping: 'KeyBinding',
  AABB: 'AxisAlignedBB',
  BlockHitResult: 'MovingObjectPosition',
  Direction: 'ForgeDirection',
  Mob: 'EntityLiving',
  Monster: 'IMob',
};

function replaceOutsideLiterals(src, replacements) {
  let out = '';
  let i = 0;
  const n = src.length;

  while (i < n) {
    const c = src[i];

    if (c === '"' || c === '\'') {
      const quote = c;
      out += c;
      i++;
      while (i < n) {
        out += src[i];
        if (src[i] === '\\') {
          i++;
          if (i < n) out += src[i];
          i++;
          continue;
        }
        if (src[i] === quote) { i++; break; }
        i++;
      }
      continue;
    }

    if (c === '/' && src[i + 1] === '/') {
      while (i < n && src[i] !== '\n') { out += src[i]; i++; }
      continue;
    }

    if (c === '/' && src[i + 1] === '*') {
      out += '/*';
      i += 2;
      while (i < n && !(src[i] === '*' && src[i + 1] === '/')) { out += src[i]; i++; }
      if (i < n) { out += '*/'; i += 2; }
      continue;
    }

    let matched = false;
    for (const [from, to] of replacements) {
      if (src.startsWith(from, i)) {
        const before = i === 0 ? '' : src[i - 1];
        const after = i + from.length >= n ? '' : src[i + from.length];
        const isWordBefore = /[A-Za-z0-9_$]/.test(before);
        const isWordAfter = /[A-Za-z0-9_$]/.test(after);
        if (!isWordBefore && !isWordAfter) {
          out += to;
          i += from.length;
          matched = true;
          break;
        }
      }
    }
    if (matched) continue;

    out += c;
    i++;
  }
  return out;
}

function addImport(src, fq) {
  if (src.includes(`import ${fq};`)) return src;
  const lines = src.split('\n');
  let lastImport = -1;
  for (let i = 0; i < lines.length; i++) {
    if (lines[i].trim().startsWith('import ')) lastImport = i;
    if (lines[i].trim().startsWith('public ') || lines[i].trim().startsWith('class ')) break;
  }
  if (lastImport === -1) {
    const pkg = lines.findIndex(l => l.startsWith('package '));
    lines.splice(pkg + 1, 0, '', `import ${fq};`);
  } else {
    lines.splice(lastImport + 1, 0, `import ${fq};`);
  }
  return lines.join('\n');
}

function transform(src) {
  // Replace imports.
  src = src.replace(/^import\s+([A-Za-z0-9_.]+);/gm, (full, fq) => {
    const mapped = IMPORT_MAP[fq];
    return mapped ? `import ${mapped};` : full;
  });

  // Identifier renames outside strings/comments.
  src = replaceOutsideLiterals(src, Object.entries(IDENT_MAP));

  // Method / call site renames (code only, approximated per line).
  const lines = src.split('\n');
  for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    const t = line.trim();
    if (t.startsWith('//') || t.startsWith('*') || t.startsWith('/*')) continue;

    line = line.replace(/\.getNamespace\(\)/g, '.getResourceDomain()');
    line = line.replace(/\.getPath\(\)/g, '.getResourcePath()');
    line = line.replace(/\.level\(\)/g, '.worldObj');
    line = line.replace(/\.getMainHandItem\(\)/g, '.getHeldItem()');
    line = line.replace(/\.getOffhandItem\(\)/g, '.getHeldItemOffhand()');
    line = line.replace(/\.tickCount\b/g, '.ticksExisted');
    line = line.replace(/\.isCrouching\(\)/g, '.isSneaking()');
    line = line.replace(/\.isShiftKeyDown\(\)/g, '.isSneaking()');
    line = line.replace(/\.addFreshEntity\(/g, '.spawnEntityInWorld(');
    line = line.replace(/\.discard\(\)/g, '.setDead()');
    line = line.replace(/\.getActiveEffects\(\)/g, '.getActivePotionEffects()');
    line = line.replace(/\.removeEffect\(/g, '.removePotionEffect(');
    line = line.replace(/\.addEffect\(/g, '.addPotionEffect(');
    line = line.replace(/\.hurt\(/g, '.attackEntityFrom(');
    line = line.replace(/\.sendSystemMessage\(/g, '.addChatMessage(');
    line = line.replace(/\.getUUID\(\)/g, '.getUniqueID()');
    line = line.replace(/([A-Za-z_][A-Za-z0-9_.]*)\.blockPosition\(\)/g, 'CompatPos.of($1)');

    lines[i] = line;
  }
  src = lines.join('\n');

  if (src.includes('CompatPos.of(')) {
    src = addImport(src, 'com.aizistral.enigmaticlegacy.compat.CompatPos');
  }

  return src;
}

function walk(dir, out) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      if (entry.name === 'mixin') continue;
      walk(p, out);
    } else if (entry.name.endsWith('.java')) {
      out.push(p);
    }
  }
}

const files = [];
walk(ROOT, files);
let changed = 0;

for (const file of files) {
  const src = fs.readFileSync(file, 'utf8');
  const out = transform(src);
  if (out !== src) {
    changed++;
    if (APPLY) fs.writeFileSync(file, out);
    else console.log('would change: ' + file);
  }
}

console.log(`${APPLY ? 'Transformed' : 'Would transform'} ${changed} of ${files.length} files.`);
