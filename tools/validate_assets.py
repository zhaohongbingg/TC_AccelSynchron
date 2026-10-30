import json
import pathlib

root = pathlib.Path(r'D:\game\mc\TC_AccelSynchron\src\main\resources')
assets = root / 'assets' / 'tc_accelsynchron'
props = {
    'frequency': {'off', 'x2', 'x4', 'x8', 'x16'},
    'in_structure': {'false', 'true'},
    'connected': {'false', 'true'},
}
for bs in (assets / 'blockstates').glob('*.json'):
    data = json.loads(bs.read_text(encoding='utf-8'))
    for key, val in data['variants'].items():
        parts = dict(p.split('=') for p in key.split(','))
        assert set(parts) == set(props), (bs, key)
        for p, v in parts.items():
            assert v in props[p], (bs, key, p, v)
        model = val['model'].split(':')[1]
        assert (assets / 'models' / f'{model}.json').exists(), (bs, model)
print('blockstates ok')
for m in (assets / 'models' / 'block').glob('*.json'):
    data = json.loads(m.read_text(encoding='utf-8'))
    for tex in data.get('textures', {}).values():
        p = assets / 'textures' / f"{tex.split(':')[1]}.png"
        assert p.exists(), (m, tex)
print('block models textures ok')
for m in (assets / 'models' / 'item').glob('*.json'):
    data = json.loads(m.read_text(encoding='utf-8'))
    parent = data['parent'].split(':')[1]
    assert (assets / 'models' / f'{parent}.json').exists(), (m, parent)
print('item models ok')
