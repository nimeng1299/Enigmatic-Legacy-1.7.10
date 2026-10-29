// Mechanical constant-name fixes: 1.20 ALL_CAPS -> 1.7.10 lowercase field names.
// Usage: node tools/port/fix_constants.js [--dry]
const fs = require('fs');
const path = require('path');

const DRY = process.argv.includes('--dry');
const ROOT = 'src/main/java';

const ITEMS = {
  SPLASH_POTION: 'potionitem', POTION: 'potionitem', LINGERING_POTION: 'potionitem',
  DRAGON_BREATH: 'glass_bottle', PHANTOM_MEMBRANE: 'leather', TRIDENT: 'iron_sword',
  NETHERITE_SCRAP: 'iron_ingot', WITHER_SKELETON_SKULL: 'skull', ZOMBIE_HEAD: 'skull',
  DRAGON_HEAD: 'skull', CREEPER_HEAD: 'skull', SKELETON_SKULL: 'skull',
  GEMS_LAPIS: 'dye', LAPIS_LAZULI: 'dye', PRISMARINE_CRYSTALS: 'diamond',
  PRISMARINE_SHARD: 'diamond', NAUTILUS_SHELL: 'diamond', HEART_OF_THE_SEA: 'diamond',
  TOTEM_OF_UNDYING: 'nether_star', WITHER_ROSE: 'double_plant', ENCHANTED_GOLDEN_APPLE: 'golden_apple',
  ENCHANTED_BOOK: 'enchanted_book', GLISTERING_MELON_SLICE: 'speckled_melon',
  GLOWSTONE_DUST: 'glowstone_dust', GLASS_BOTTLE: 'glass_bottle', BLAZE_ROD: 'blaze_rod',
  BLAZE_POWDER: 'blaze_powder', GHAST_TEAR: 'ghast_tear', ENDER_EYE: 'ender_eye',
  ENDER_PEARL: 'ender_pearl', SLIME_BALL: 'slime_ball', LEATHER: 'leather',
  GOLD_INGOT: 'gold_ingot', QUARTZ: 'quartz', EMERALD: 'emerald', REDSTONE: 'redstone',
  DIAMOND_SWORD: 'diamond_sword', DIAMOND_HOE: 'diamond_hoe', IRON_SWORD: 'iron_sword',
  IRON_SHOVEL: 'iron_shovel', IRON_PICKAXE: 'iron_pickaxe', IRON_AXE: 'iron_axe',
  GOLDEN_SWORD: 'golden_sword', GOLDEN_SHOVEL: 'golden_shovel', GOLDEN_PICKAXE: 'golden_pickaxe',
  GOLDEN_AXE: 'golden_axe', GOLDEN_APPLE: 'golden_apple', GOLDEN_CARROT: 'golden_carrot',
  CROSSBOW: 'bow', BOOK: 'book', ARROW: 'arrow', STRING: 'string', ROTTEN_FLESH: 'rotten_flesh',
  PUMPKIN_PIE: 'pumpkin_pie', PORKCHOP: 'porkchop', POISONOUS_POTATO: 'poisonous_potato',
  MUTTON: 'mutton', LAVA_BUCKET: 'lava_bucket', FIRE_CHARGE: 'fire_charge',
  GUNPOWDER: 'gunpowder', WHEAT: 'wheat', BONE: 'bone', BONE_MEAL: 'dye',
  EXPERIENCE_BOTTLE: 'experience_bottle', AMETHYST_SHARD: 'diamond',
  COPPER_INGOT: 'iron_ingot', RAW_IRON: 'iron_ore', CHARCOAL: 'coal',
  SUGAR: 'sugar', EGG: 'egg', BOWL: 'bowl', BUCKET: 'bucket', WATER_BUCKET: 'water_bucket',
  MILK_BUCKET: 'milk_bucket', PAPER: 'paper', FEATHER: 'feather', FLINT: 'flint',
  CLAY_BALL: 'clay_ball', BRICK: 'brick', NETHER_BRICK: 'netherbrick', NETHER_STAR: 'nether_star',
  SPIDER_EYE: 'spider_eye', FERMENTED_SPIDER_EYE: 'fermented_spider_eye', MAGMA_CREAM: 'magma_cream',
  BREWING_STAND: 'brewing_stand', CAULDRON: 'cauldron', SUGAR_CANE: 'reeds', REEDS: 'reeds',
  WRITABLE_BOOK: 'writable_book', WRITTEN_BOOK: 'written_book', ITEM_FRAME: 'item_frame',
  CARROT: 'carrot', POTATO: 'potato', BAKED_POTATO: 'baked_potato', BEETROOT: 'beetroot',
  COOKIE: 'cookie', CAKE: 'cake', BED: 'bed', SIGN: 'sign', BOAT: 'boat', MINECART: 'minecart',
  CHEST_MINECART: 'chest_minecart', FURNACE_MINECART: 'furnace_minecart', HOPPER_MINECART: 'hopper_minecart',
  TNT_MINECART: 'tnt_minecart', SADDLE: 'saddle', IRON_DOOR: 'iron_door', WOODEN_DOOR: 'wooden_door',
  REPEATER: 'repeater', COMPARATOR: 'comparator', COMPASS: 'compass', CLOCK: 'clock',
  MAP: 'map', FILLED_MAP: 'filled_map', SHEARS: 'shears', FISHING_ROD: 'fishing_rod',
  FLINT_AND_STEEL: 'flint_and_steel', LEAD: 'lead', NAME_TAG: 'name_tag',
  FIREWORK_ROCKET: 'fireworks', FIREWORK_STAR: 'firework_charge', SNOWBALL: 'snowball',
  ENDERPEARL: 'ender_pearl', CHORUS_FRUIT: 'chorus_fruit', SHIELD: 'shield',
  ELYTRA: 'elytra', TIPPED_ARROW: 'arrow', SPECTRAL_ARROW: 'arrow',
  HONEY_BOTTLE: 'glass_bottle', HONEYCOMB: 'slime_ball',
};

const BLOCKS = {
  FARMLAND: 'farmland', DIRT: 'dirt', AIR: 'air', GRASS_BLOCK: 'grass', GRASS: 'grass',
  OBSIDIAN: 'obsidian', FIRE: 'fire', ENCHANTING_TABLE: 'enchanting_table', CAKE: 'cake',
  CACTUS: 'cactus', NETHER_WART: 'nether_wart', SUGAR_CANE: 'reeds', SPAWNER: 'mob_spawner',
  GRASS_PATH: 'grass_path', DIRT_PATH: 'grass_path', COARSE_DIRT: 'dirt', PODZOL: 'dirt',
  SEAGRASS: 'water', TALL_SEAGRASS: 'water', KELP: 'water', KELP_PLANT: 'water',
  BAMBOO: 'reeds', BAMBOO_SAPLING: 'reeds', LANTERN: 'glowstone', TALL_GRASS: 'tallgrass',
  SHORT_GRASS: 'tallgrass', END_GATEWAY: 'end_portal', SOUL_FIRE: 'fire', SOUL_TORCH: 'torch',
  WALL_TORCH: 'torch', CAMPFIRE: 'fire', MAGMA_BLOCK: 'magma', SLIME_BLOCK: 'slime',
  HONEY_BLOCK: 'slime', COBWEB: 'web', DEAD_BUSH: 'deadbush', SNOW: 'snow', SNOW_BLOCK: 'snow',
  NETHERRACK: 'netherrack', SOUL_SAND: 'soul_sand', GLOWSTONE: 'glowstone', SEA_LANTERN: 'sea_lantern',
  ICE: 'ice', PACKED_ICE: 'packed_ice', BLUE_ICE: 'packed_ice', GRAVEL: 'gravel', SAND: 'sand',
  RED_SAND: 'sand', CLAY: 'clay', MYCELIUM: 'mycelium', STONE: 'stone', COBBLESTONE: 'cobblestone',
  BEDROCK: 'bedrock', BARRIER: 'barrier', COMMAND_BLOCK: 'command_block', CHEST: 'chest',
  ENDER_CHEST: 'ender_chest', TRAPPED_CHEST: 'trapped_chest', GLASS: 'glass', IRON_BARS: 'iron_bars',
  HOPPER: 'hopper', DROPPER: 'dropper', DISPENSER: 'dispenser', PISTON: 'piston', STICKY_PISTON: 'sticky_piston',
  REDSTONE_LAMP: 'redstone_lamp', REDSTONE_BLOCK: 'redstone_block', REDSTONE_TORCH: 'redstone_torch',
  LEVER: 'lever', STONE_BUTTON: 'stone_button', WOODEN_BUTTON: 'wooden_button', STONE_PRESSURE_PLATE: 'stone_pressure_plate',
  OAK_PRESSURE_PLATE: 'wooden_pressure_plate', OAK_DOOR: 'wooden_door', OAK_TRAPDOOR: 'trapdoor', IRON_TRAPDOOR: 'iron_trapdoor',
  OAK_FENCE: 'fence', OAK_FENCE_GATE: 'fence_gate', OAK_SLAB: 'wooden_slab', STONE_SLAB: 'stone_slab',
  OAK_STAIRS: 'oak_stairs', OAK_PLANKS: 'planks', OAK_LOG: 'log', OAK_LEAVES: 'leaves', VINE: 'vine',
  LILY_PAD: 'waterlily', BROWN_MUSHROOM: 'brown_mushroom', RED_MUSHROOM: 'red_mushroom',
  BROWN_MUSHROOM_BLOCK: 'brown_mushroom_block', RED_MUSHROOM_BLOCK: 'red_mushroom_block',
  NETHER_PORTAL: 'portal', END_PORTAL: 'end_portal', END_PORTAL_FRAME: 'end_portal_frame', DRAGON_EGG: 'dragon_egg',
  ANVIL: 'anvil', BEACON: 'beacon', BREWING_STAND: 'brewing_stand', CAULDRON: 'cauldron',
  CARVED_PUMPKIN: 'pumpkin', JACK_O_LANTERN: 'lit_pumpkin', MELON: 'melon_block',
  WHEAT: 'wheat', CARROTS: 'carrots', POTATOES: 'potatoes', BEETROOTS: 'beetroots',
  FARMLAND_MOIST: 'farmland', SPRUCE_LOG: 'log', BIRCH_LOG: 'log', JUNGLE_LOG: 'log',
};

const MISC = {
  'EnumRarity.COMMON': 'EnumRarity.common',
  'EnumRarity.UNCOMMON': 'EnumRarity.uncommon',
  'EnumRarity.RARE': 'EnumRarity.rare',
  'EnumRarity.EPIC': 'EnumRarity.epic',
  'ItemStack.EMPTY': 'null',
  'Items.AIR': 'Items.air',
};

let filesChanged = 0, replacements = 0;
function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) walk(p);
    else if (name.endsWith('.java')) fixFile(p);
  }
}

function fixFile(p) {
  let s = fs.readFileSync(p, 'utf8');
  let out = s;
  for (const [k, v] of Object.entries(MISC)) {
    const re = new RegExp(k.replace(/\./g, '\\.').replace('[', '\\[').replace(']', '\\]') + '(?![A-Za-z0-9_])', 'g');
    out = out.replace(re, v);
  }
  for (const [k, v] of Object.entries(ITEMS)) {
    out = out.replace(new RegExp('\\bItems\\.' + k + '(?![A-Za-z0-9_])', 'g'), 'Items.' + v);
  }
  for (const [k, v] of Object.entries(BLOCKS)) {
    out = out.replace(new RegExp('\\bBlocks\\.' + k + '(?![A-Za-z0-9_])', 'g'), 'Blocks.' + v);
  }
  out = out.replace(/\bEnchantments\.MOB_LOOTING\b/g, 'Enchantments.LOOTING');
  out = out.replace(/\bEnchantments\.BLOCK_FORTUNE\b/g, 'Enchantments.FORTUNE');
  if (out !== s) {
    filesChanged++;
    replacements += (s.match(/\n/g) || []).length;
    if (!DRY) fs.writeFileSync(p, out, 'utf8');
  }
}

walk(ROOT);
console.log(`constants fixer: files changed: ${filesChanged}`);
