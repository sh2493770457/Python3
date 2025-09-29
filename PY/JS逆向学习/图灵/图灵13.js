var diy_$ = {
    'ajax': function () {
    }
}
global.$ = diy_$

global.$ = {
    ajax: function () {
    },
    ajaxSettings: {}
};

$.ajaxSettings.beforeSend = function (xhr, options) {
    console.log('beforeSend called', xhr, options);
};
global._0x350b3a = _0x1e5c29()

function _0x1e5c29() {
    var _0xee3b28 = [];
    for (var _0x20183e = 0x0; _0x20183e < 0x20; _0x20183e++) {
        _0xee3b28[_0x20183e] = "0123456789abcdef".substr(Math.floor(Math.random() * 0x10), 0x1);
    }
    _0xee3b28[0xe] = "4";
    _0xee3b28[0x13] = "0123456789abcdef".substr(_0xee3b28[0x13] & 0x3 | 0x8, 0x1);
    _0xee3b28[0x8] = _0xee3b28[0xd] = _0xee3b28[0x12] = _0xee3b28[0x17];
    var _0x4b1861 = _0xee3b28.join('');
    return _0x4b1861;
}


$.ajaxSettings.beforeSend = function (_0x3565b7, _0x891154) {
    var _0x2059bf = Date.parse(new Date());
    var _0x350b3a = _0x1e5c29();
    var _0x3f9dab = x1.stringify(_0x321804(_0x10b283(_0x891154.data || "{}")));
    var _0x3104de = mmm(_0x3f9dab + _0x350b3a + _0x2059bf);
    _0x891154.data = _0x3f9dab;
    _0x3565b7.setRequestHeader("t", _0x2059bf);
    _0x3565b7.setRequestHeader("r", _0x350b3a);
    _0x3565b7.setRequestHeader("s", _0x3104de);
    if (_0x3a4216) {
        return _0x3a4216(jqXHR, _0x891154);
    }
};

function get_r() {
    return _0x1e5c29()
}

// r的值
console.log(_0x350b3a)
