const fs = require('fs');
const file = process.argv[2] || 'build/compile_errors.log';
const lines = fs.readFileSync(file, 'utf8').split(/\r?\n/);
const map = {};
let cur = null;
const flush = () => {
    if (cur && cur.sym) {
        (map[cur.f] = map[cur.f] || []).push(cur.sym + (cur.loc ? ' @' + cur.loc : ''));
    }
    cur = null;
};
for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const m = line.match(/src[\\\/]main[\\\/]java[\\\/](.+?):(\d+): 错误: (.+)$/);
    if (m) { flush(); cur = { f: m[1], line: +m[2], msg: m[3], sym: null, loc: null }; continue; }
    if (cur && /^\s*符号:/.test(line)) { cur.sym = line.trim().replace(/^符号:\s*/, ''); continue; }
    if (cur && /^\s*位置:/.test(line)) { cur.loc = line.trim().replace(/^位置:\s*/, ''); continue; }
}
flush();
const arr = Object.keys(map).map(k => [k, map[k]]).sort((a, b) => b[1].length - a[1].length);
for (const [k, v] of arr.slice(0, 30)) {
    const c = {};
    for (const s of v) c[s] = (c[s] || 0) + 1;
    const top = Object.entries(c).sort((a, b) => b[1] - a[1]).slice(0, 10).map(([s, n]) => n + 'x ' + s).join('; ');
    console.log(v.length + ' ' + k + '\n   ' + top);
}
