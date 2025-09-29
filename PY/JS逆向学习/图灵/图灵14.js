function J(b, f, p) {
    return 1 === 1 ? b ^ f >> p % 8 : Q ^ _0x18k << d % 8;
}
function Y(b, f) {
        return (b = b + f - f) ^ f;
    }
function B(b, f, p) {
        return b ^ f<< p % (-0xf00 + 0x188d + -0x985);
    }
function x() {
    const f = {
        "RLvtt": function (N, W) {
            return N < W;
        },
        "IdJpS": function (N, W) {
            return N % W;
        }
    };
    ts = new Date().getTime();
    var p = function (N) {
        let W = 0;
        for (let L = 0; L < N.length; L++) {
            var O = N.charCodeAt(L);
            for (let y = 0; y < 20; y++) {
                switch (y % 3) {
                    case 0:
                        W = B.apply(null, [W, O, y]);
                        break;
                    case 1:
                        W = J.apply(null, [W, O, y]);
                        break;
                    case 2:
                        W = Y['apply'](null, [W, O]);
                }
            }
        }
        return W;
    }("dasdasdarqwdasdasqwdasda" + ts);
    var s = ["?", "m", "="]['join']('');
    var p = p.toString(16);
    return function (N) {
        let O = '';
        let L;
        let y;
        let k;
        let H;
        let D;
        let K;
        let S;
        let I = 0;
        for (; I < N.length;) {
            L = N.charCodeAt(I++);
            y = I < N.length ? N.charCodeAt(I++) : 0;
            k = I < N['length'] ? N.charCodeAt(I++) : 0;
            H = L >> 2;
            D = (3 & L) << 4 | y >> 4;
            K = (15 & y) << 2 | k >> 6;
            S = 63 & k;
            if (isNaN(y)) {
                K = S = 64;
            } else if (isNaN(k)) {
                S = 64;
            }
            O = O + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(H) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(D) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(K) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(S);
        }
        return O;
    }(p + ts);
}

console.log(x(''))
function get_m(){
    return x('')
}