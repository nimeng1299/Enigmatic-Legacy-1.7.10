const fs = require('fs');
const log = process.argv[2] || 'build/compile_errors.log';
const lines = fs.readFileSync(log, 'utf8').split(/\r?\n/);
const byMsg = new Map(), byFile = new Map(), bySym = new Map();
for (const l of lines) {
  const r = l.match(/^E:.*?src[\\/]main[\\/]java[\\/](.+?):(\d+): 错误: (.+)$/);
  if (r) {
    byMsg.set(r[3], (byMsg.get(r[3]) || 0) + 1);
    byFile.set(r[1], (byFile.get(r[1]) || 0) + 1);
  }
  const s = l.match(/^\s*符号:\s+(类|方法|变量|构造器)\s+(\S+)/);
  if (s) bySym.set(s[1] + ' ' + s[2], (bySym.get(s[1] + ' ' + s[2]) || 0) + 1);
}
console.log('== TOP FILES ==');
for (const [f, c] of [...byFile.entries()].sort((a, b) => b[1] - a[1]).slice(0, 30)) console.log(c, f);
console.log('== TOP MESSAGES ==');
for (const [m, c] of [...byMsg.entries()].sort((a, b) => b[1] - a[1]).slice(0, 30)) console.log(c, m);
console.log('== TOP SYMBOLS ==');
for (const [s, c] of [...bySym.entries()].sort((a, b) => b[1] - a[1]).slice(0, 50)) console.log(c, s);
