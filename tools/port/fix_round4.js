// Fourth mechanical pass: sounds, cooldowns, AABBs, damage keys, potion effect imports.
const fs = require('fs');
const path = require('path');
const DRY = process.argv.includes('--dry');
const ROOT = 'src/main/java';
const LOG = 'build/compile_errors.log';

const potionCtorFiles = new Set();
const raw = fs.readFileSync(LOG, 'utf8').split(/\r?\n/);
let currentFile = null;
for (const line of raw) {
  const m = line.match(/^E:.*?src[\\/]main[\\/]java[\\/](.+?):\d+: 错误: (.+)$/);
  if (m) {
    currentFile = m[1].replace(/\\/g, '/');
    if (m[2].includes('PotionEffect(') || m[2].includes('Potion无法转换为int')) {
      potionCtorFiles.add(currentFile);
    }
  }
}

let changed = 0;

function fix(p, rel) {
  const src = fs.readFileSync(p, 'utf8');
  const lines = src.split(/\r?\n/);
  let touched = false;

  for (let i = 0; i < lines.length; i++) {
    let l = lines[i];
    const before = l;

    l = l.replace(/\.playSound\(null,\s*CompatPos\.of\(([^(),]+)\),\s*([^,]+),\s*[^,]+,\s*([^,]+),\s*([^)]+)\)/g,
      '.playSoundAtEntity($1, ($2).getName(), $3, $4)');

    l = l.replace(/\.playSound\(null,\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^)]+)\)/g,
      '.playSoundEffect($1, $2, $3, ($4).getName(), $6, $7)');

    l = l.replace(/new AxisAlignedBB\(/g, 'AxisAlignedBB.getBoundingBox(');
    l = l.replace(/ResourceKey<DamageType>/g, 'DamageType');

    l = l.replace(/([A-Za-z_$][\w$]*)\.getCooldowns\(\)\.isOnCooldown\(([^()]+)\)/g,
      'com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.isOnCooldown($1, $2)');
    l = l.replace(/([A-Za-z_$][\w$]*)\.getCooldowns\(\)\.addCooldown\(([^,]+),\s*([^)]+)\)/g,
      'com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.addCooldown($1, $2, $3)');
    l = l.replace(/([A-Za-z_$][\w$]*)\.getCooldowns\(\)\.removeCooldown\(([^,]+)\)/g,
      'com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.removeCooldown($1, $2)');

    l = l.replace(/EnchantmentHelper\.getEnchantments\(/g,
      'com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(');

    if (potionCtorFiles.has(rel)) {
      l = l.replace(/^import net\.minecraft\.potion\.PotionEffect;\s*$/,
        'import com.aizistral.enigmaticlegacy.compat.PotionEffect;');
    }

    if (l !== before) {
      lines[i] = l;
      touched = true;
    }
  }

  if (touched) {
    changed++;
    if (!DRY) fs.writeFileSync(p, lines.join(src.includes('\r\n') ? '\r\n' : '\n'), 'utf8');
  }
}

function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) walk(p);
    else if (name.endsWith('.java')) fix(p, p.slice(ROOT.length + 1).replace(/\\/g, '/'));
  }
}

walk(ROOT);
console.log('round4 changed files:', changed, 'potion-ctor files:', potionCtorFiles.size);
