// Second mechanical pass: wildcard imports, regressions from blanket rules.
const fs = require('fs');
const path = require('path');
const DRY = process.argv.includes('--dry');
const ROOT = 'src/main/java';

const FILE_REPLACEMENTS = [
  [/\bevent\.stackSize\b/g, 'event.getAmount()'],
  [/\beventXP\.stackSize\b/g, 'eventXP.getAmount()'],
  [/\battributemodifier2\.stackSize\b/g, 'attributemodifier2.getAmount()'],
  [/Minecraft\.getMinecraft\(\)\.player\b/g, 'Minecraft.getMinecraft().thePlayer'],
  [/Minecraft\.getMinecraft\(\)\.level\b/g, 'Minecraft.getMinecraft().theWorld'],
  [/Minecraft\.getMinecraft\(\)\.screen\b/g, 'Minecraft.getMinecraft().currentScreen'],
  [/Minecraft\.getMinecraft\(\)\.font\b/g, 'Minecraft.getMinecraft().fontRenderer'],
  [/Minecraft\.getMinecraft\(\)\.options\b/g, 'Minecraft.getMinecraft().gameSettings'],
];

const IMPORT_LINES = {
  'net.minecraft.world.item.*': 'net.minecraft.item.*',
  'net.minecraft.world.item.enchantment.*': 'net.minecraft.enchantment.*',
  'net.minecraft.world.entity.*': 'net.minecraft.entity.*',
  'net.minecraft.world.entity.player.*': 'net.minecraft.entity.player.*',
  'net.minecraft.world.entity.projectile.*': 'net.minecraft.entity.projectile.*',
  'net.minecraft.world.entity.item.*': 'net.minecraft.entity.item.*',
  'net.minecraft.world.entity.animal.*': 'net.minecraft.entity.passive.*',
  'net.minecraft.world.entity.monster.*': 'net.minecraft.entity.monster.*',
  'net.minecraft.world.level.*': 'net.minecraft.world.*',
  'net.minecraft.world.level.block.*': 'net.minecraft.block.*',
  'net.minecraft.world.phys.*': 'net.minecraft.util.*',
  'net.minecraft.world.food.*': 'net.minecraft.util.*',
  'net.minecraft.world.damagesource.*': 'net.minecraft.util.*',
  'net.minecraft.world.effect.*': '',
  'net.minecraft.world.inventory.*': '',
  'net.minecraft.world.item.alchemy.*': '',
  'net.minecraft.world.item.crafting.*': '',
  'net.minecraft.world.entity.ai.*': '',
  'net.minecraft.world.entity.monster.piglin.*': '',
  'net.minecraft.world.level.storage.loot.*': '',
  'net.minecraft.world.level.storage.loot.functions.*': '',
  'net.minecraft.world.level.storage.loot.entries.*': '',
  'net.minecraft.world.level.storage.loot.predicates.*': '',
  'net.minecraft.world.level.storage.loot.providers.number.*': '',
  'net.minecraft.world.level.block.state.*': '',
  'net.minecraft.world.level.material.*': '',
  'net.minecraft.world.level.gameevent.*': '',
  'net.minecraft.world.level.chunk.*': '',
  'net.minecraft.world.level.biome.*': '',
  'net.minecraft.world.level.levelgen.*': '',
  'net.minecraft.world.level.pathfinder.*': '',
  'net.minecraft.world.level.block.entity.*': '',
  'net.minecraft.world.level.storage.*': '',
  'net.minecraft.core.*': '',
  'net.minecraft.core.particles.*': '',
  'net.minecraft.core.registries.*': '',
  'net.minecraft.network.chat.*': '',
  'net.minecraft.network.protocol.*': '',
  'net.minecraft.network.syncher.*': '',
  'net.minecraft.advancements.*': '',
  'net.minecraft.advancements.critereon.*': '',
  'com.mojang.blaze3d.vertex.*': '',
  'com.mojang.blaze3d.systems.*': '',
  'net.minecraftforge.registries.*': '',
  'net.minecraftforge.common.capabilities.*': '',
};

let changed = 0;
function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) walk(p);
    else if (name.endsWith('.java')) fix(p);
  }
}

function fix(p) {
  const src = fs.readFileSync(p, 'utf8');
  const lines = src.split(/\r?\n/);
  let touched = false;
  const out = [];
  for (let line of lines) {
    let l = line;
    const m = l.match(/^import\s+(static\s+)?([\w.]+)\.\*;\s*$/);
    if (m) {
      const fq = m[2];
      if (Object.prototype.hasOwnProperty.call(IMPORT_LINES, fq)) {
        const target = IMPORT_LINES[fq];
        touched = true;
        if (target) out.push('import ' + target + '.*;');
        continue;
      }
    }
    for (const [re, to] of FILE_REPLACEMENTS) l = l.replace(re, to);
    if (l !== line) touched = true;
    out.push(l);
  }
  if (touched) {
    changed++;
    if (!DRY) fs.writeFileSync(p, out.join(src.includes('\r\n') ? '\r\n' : '\n'), 'utf8');
  }
}

walk(ROOT);
console.log('round2 changed files:', changed);
