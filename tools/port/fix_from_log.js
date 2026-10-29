// Applies mechanical 1.20 -> 1.7.10 fixes guided by build/compile_errors.log
// Usage: node tools/port/fix_from_log.js [--dry]
const fs = require('fs');
const path = require('path');

const DRY = process.argv.includes('--dry');
const LOG = 'build/compile_errors.log';
const SRC_PREFIX = 'E:\\code\\Enigmatic-Legacy-1.7.10\\';

const raw = fs.readFileSync(LOG, 'utf8').split(/\r?\n/);
const files = new Map(); // path -> {errors:[{line,msg}], overrides:Set(line)}
let cur = null;
for (const line of raw) {
  const m = line.match(/^(E:\\.+?\.java):(\d+): 错误: (.+)$/);
  if (m) {
    const p = m[1].startsWith(SRC_PREFIX) ? m[1].slice(SRC_PREFIX.length).replace(/\\/g, '/') : null;
    if (!p || !p.startsWith('src/main/java/')) { cur = null; continue; }
    cur = files.get(p) || { errors: [] };
    files.set(p, cur);
    cur.errors.push({ line: parseInt(m[2], 10), msg: m[3] });
    continue;
  }
  // context lines like "  符号:   方法 getX()" / "  位置: 类 Foo"
  if (cur) {
    const s = line.match(/^\s*符号:\s+(类|方法|变量|构造器)\s+(\S+)/);
    if (s) cur.errors[cur.errors.length - 1].kind = s[1];
    if (s && s[1] !== '类') cur.errors[cur.errors.length - 1].symbol = s[2].replace(/\(.*$/, '');
  }
}

// ---------- global line-level fixes (safe for 1.7.10) ----------
const GLOBAL = [
  [/Minecraft\.getInstance\(\)/g, 'Minecraft.getMinecraft()'],
  [/\.isClientSide\b/g, '.isRemote'],
  [/\.getBbHeight\(\)/g, '.height'],
  [/\.dimension\(\)/g, '.provider.dimensionId'],
  [/\.getDisplayName\(\)\.getString\(\)/g, '.getDisplayName()'],
  [/\.displayClientMessage\(/g, '.addChatMessage('],
  [/\.sendSystemMessage\(/g, '.addChatMessage('],
  [/\.getFoodData\(\)/g, '.getFoodStats()'],
  [/\.isShiftKeyDown\(\)/g, '.isSneaking()'],
  [/\.getUseItemRemainingTicks\(\)/g, '.getItemInUseCount()'],
  [/\.getItemInUseRemainingTicks\(\)/g, '.getItemInUseCount()'],
  [/\.getBoundingBox\(\)/g, '.getEntityBoundingBox()'],
  [/\.isAlive\(\)/g, '.isEntityAlive()'],
  [/\.isRemoved\(\)/g, '.isDead'],
  [/\.getLookAngle\(\)/g, '.getLookVec()'],
  [/\.noPhysics\b/g, '.noClip'],
  [/\.getMaxFallDistance\(\)/g, '.getMaxFallHeight()'],
  [/\.getMobType\(\)/g, '.getCreatureAttribute()'],
  [/\.getHealth\(\)\s*<=\s*0/g, '.getHealth() <= 0'],
  [/\.getPotionEffects\(\)/g, '.getActivePotionEffects()'],
  [/\.getActiveEffectsMap\(\)/g, '.getActivePotionEffects()'],
  [/\.getActiveEffects\(\)/g, '.getActivePotionEffects()'],
  [/\.getInventory\(\)/g, '.inventory'],
  [/\.getAbilities\(\)/g, '.capabilities'],
  [/\.getItemInHand\(\s*[^)]*\)/g, '.getHeldItem()'],
  [/\.getSource\(\)/g, '.getEntity()'],
  [/\.getDamageValue\(\)/g, '.getItemDamage()'],
  [/\.getCount\(\)/g, '.stackSize'],
];

// ---------- per-error symbol fixes, applied only on reported lines ----------
const METHOD_RULES = {
  getX: /\.getX\(\)/g, getY: /\.getY\(\)/g, getZ: /\.getZ\(\)/g,
};
const METHOD_TO = { getX: '.posX', getY: '.posY', getZ: '.posZ' };

function fixEmpty(line) {
  // !x.isEmpty() -> x.stackSize > 0 ; x.isEmpty() -> x.stackSize <= 0
  line = line.replace(/!\s*([A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)*)\.isEmpty\(\)/g, '$1.stackSize > 0');
  line = line.replace(/([A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)*)\.isEmpty\(\)/g, '$1.stackSize <= 0');
  return line;
}

let changedFiles = 0, changedLines = 0, overrideRemoved = 0;
const UNHANDLED = new Map();

for (const [rel, info] of files) {
  if (rel.endsWith('TheCube.java') || rel.endsWith('Infinimeal.java')) continue;
  const abs = path.join(process.cwd(), rel);
  if (!fs.existsSync(abs)) continue;
  let lines = fs.readFileSync(abs, 'utf8').split(/\r?\n/);
  const del = new Set();
  const before = lines.join('\n');
  const eol = fs.readFileSync(abs, 'utf8').includes('\r\n') ? '\r\n' : '\n';

  for (const e of info.errors) {
    const idx = e.line - 1;
    let line = lines[idx];
    if (line === undefined) continue;

    // Invalid imports can never become valid by keeping them.
    if (/^\s*import\s/.test(line) && (e.msg.includes('程序包') || e.msg.includes('找不到符号'))) {
      del.add(idx);
      overrideRemoved++;
      continue;
    }

    if (e.msg.includes('方法不会覆盖或实现超类型的方法')) {
      for (let k = idx; k >= Math.max(0, idx - 4); k--) {
        if (k !== idx && !/^\s*$|^\s*\/\//.test(lines[k])) break;
        if (/^\s*@Override\s*$/.test(lines[k])) { del.add(k); overrideRemoved++; break; }
        if (!/^\s*$|^\s*\/\//.test(lines[k])) break;
      }
      continue;
    }

    if (!e.kind) continue;
    const sym = e.symbol;
    let orig = line;

    if (e.kind === '方法' && sym) {
      if (sym === 'isEmpty') line = fixEmpty(line);
      else if (sym === 'getInstance') line = line.replace(/Minecraft\.getInstance\(\)/g, 'Minecraft.getMinecraft()');
      else if (METHOD_RULES[sym]) line = line.replace(METHOD_RULES[sym], METHOD_TO[sym]);
      else if (sym === 'getEyeHeight') line = line.replace(/\.getEyeHeight\([^)]*\)/g, '.getEyeHeight()');
      else if (sym === 'setPos') line = line.replace(/\.setPos\(/g, '.setPosition(');
      else if (sym === 'getInventory') line = line.replace(/\.getInventory\(\)/g, '.inventory');
      else if (sym === 'getAbilities') line = line.replace(/\.getAbilities\(\)/g, '.capabilities');
      else if (sym === 'getFoodData') line = line.replace(/\.getFoodData\(\)/g, '.getFoodStats()');
      else if (sym === 'isClientSide') line = line.replace(/\bisClientSide\b/g, 'isRemote');
      else if (sym === 'getDeltaMovement') line = line; // handled by deeper port
      else {
        UNHANDLED.set(sym, (UNHANDLED.get(sym) || 0) + 1);
      }
    } else if (e.kind === '变量' && sym) {
      if (sym === 'isClientSide') line = line.replace(/\bisClientSide\b/g, 'isRemote');
      else if (sym === 'level') line = line.replace(/\blevel\b/g, 'worldObj');
      else if (sym === 'noPhysics') line = line.replace(/\bnoPhysics\b/g, 'noClip');
      else if (sym === 'random') line = line.replace(/\brandom\b/g, 'rand');
      else if (sym === 'tickCount') line = line.replace(/\.tickCount\b/g, '.ticksExisted');
      else UNHANDLED.set('var:' + sym, (UNHANDLED.get('var:' + sym) || 0) + 1);
    }

    if (line !== orig) { lines[idx] = line; changedLines++; }
  }

  // global fixes
  for (let i = 0; i < lines.length; i++) {
    if (del.has(i)) continue;
    let l = lines[i];
    for (const [re, to] of GLOBAL) l = l.replace(re, to);
    if (l !== lines[i]) { lines[i] = l; changedLines++; }
  }

  if (del.size) lines = lines.filter((_, i) => !del.has(i));
  const after = lines.join('\n');
  if (after !== before) {
    changedFiles++;
    if (!DRY) fs.writeFileSync(abs, lines.join(eol), 'utf8');
  }
}

console.log(`files changed: ${changedFiles}, lines changed: ${changedLines}, overrides removed: ${overrideRemoved}`);
console.log('unhandled symbols:');
for (const [k, v] of [...UNHANDLED.entries()].sort((a, b) => b[1] - a[1]).slice(0, 40)) console.log(v, k);
