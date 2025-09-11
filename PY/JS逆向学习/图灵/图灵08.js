function a0_0x4366() {
    var _0x4a0735 = ['\x56\x63\x6e\x41\x4c', '\x4e\x50\x73\x71\x5a', '\x69\x56\x74\x6a\x73', /* ...原数组省略... */ '\x46\x76\x70\x79\x59', '\x4d\x46\x6c\x41\x54'];
    a0_0x4366 = function() {
        return _0x4a0735;
    };
    return a0_0x4366();
}

function a0_0x218b(_0x239624, _0x18ee39) {
    var _0x1e0e96 = a0_0x4366();
    return a0_0x218b = function(_0x249f4d, _0x4c1faf) {
        _0x249f4d = _0x249f4d - 0x1da;
        var _0x1b1a50 = _0x1e0e96[_0x249f4d];
        return _0x1b1a50;
    },
    a0_0x218b(_0x239624, _0x18ee39);
}

// 将原本的 IIFE 改成普通函数
function OOOoOo(_0x240504, _0x8eefdc) {
    var _0x5d107c = a0_0x218b;
    var _0x619515 = {
        '\x70\x61\x45\x49\x64': function(a, b) { return a > b; },
        '\x6c\x6b\x41\x50\x41': function(a, b) { return a === b; },
        '\x46\x76\x70\x79\x59': function(a, b) { return a < b; },
        '\x42\x57\x73\x70\x70': '\x51\x41\x75\x5a\x62',
        '\x64\x6d\x6f\x73\x73': '\x44\x51\x66\x4d\x6a',
        '\x6e\x66\x56\x6f\x65': function(a, b) { return a + b; },
        '\x53\x77\x4b\x4e\x53': function(a, b) { return a !== b; },
        '\x44\x5a\x50\x42\x77': _0x5d107c(0x276),
        '\x57\x48\x49\x79\x54': function(a, b) { return a % b; },
        '\x71\x49\x57\x64\x52': function(a, b) { return a + b; }
    };

    const _0x3a3671 = _0x240504.split('');
    const _0x1959d4 = _0x8eefdc.split('');
    const _0x582226 = 4;
    let _0x5ad857 = [];

    for (let _0x2d33d3 = 0; _0x2d33d3 < _0x3a3671.length; _0x2d33d3 += _0x582226) {
        let _0x38ae5f = _0x3a3671.slice(_0x2d33d3, _0x2d33d3 + _0x582226);
        for (let _0x31873b = 0; _0x31873b < _0x38ae5f.length; _0x31873b++) {
            const _0x11057a = _0x38ae5f[_0x31873b].charCodeAt(0);
            const _0x1a6269 = _0x1959d4[(_0x31873b % _0x1959d4.length)].charCodeAt(0);
            const _0x25c979 = (_0x11057a + _0x1a6269) % 256;
            _0x38ae5f[_0x31873b] = String.fromCharCode(_0x25c979);
        }
        _0x5ad857 = _0x5ad857.concat(_0x38ae5f);
    }

    const _0x28d8b9 = _0x5ad857.join('');
    const _0x36bdd2 = Array.from(_0x28d8b9)
        .map(c => c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('');
    return _0x36bdd2;
}

// 调用示例
console.log(OOOoOo("oooooo175758426263343","oooooo"));
