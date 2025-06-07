var t, e, r, i, n, s, a, o, h, u = {
    userAgent: !1
}, c = {}, l = l || function (t, e) {
    var r = {}
        , i = r.lib = {}
        , n = i.Base = function () {
        function t() {
        }

        return {
            extend: function (e) {
                t.prototype = this;
                var r = new t;
                return e && r.mixIn(e),
                r.hasOwnProperty("init") || (r.init = function () {
                        r.$super.init.apply(this, arguments)
                    }
                ),
                    r.init.prototype = r,
                    r.$super = this,
                    r
            },
            create: function () {
                var t = this.extend();
                return t.init.apply(t, arguments),
                    t
            },
            init: function () {
            },
            mixIn: function (t) {
                for (var e in t)
                    t.hasOwnProperty(e) && (this[e] = t[e]);
                t.hasOwnProperty("toString") && (this.toString = t.toString)
            },
            clone: function () {
                return this.init.prototype.extend(this)
            }
        }
    }()
        , s = i.WordArray = n.extend({
        init: function (t, e) {
            t = this.words = t || [],
                this.sigBytes = null != e ? e : 4 * t.length
        },
        toString: function (t) {
            return (t || o).stringify(this)
        },
        concat: function (t) {
            var e = this.words
                , r = t.words
                , i = this.sigBytes
                , n = t.sigBytes;
            if (this.clamp(),
            i % 4)
                for (var s = 0; s < n; s++) {
                    var a = r[s >>> 2] >>> 24 - s % 4 * 8 & 255;
                    e[i + s >>> 2] |= a << 24 - (i + s) % 4 * 8
                }
            else
                for (s = 0; s < n; s += 4)
                    e[i + s >>> 2] = r[s >>> 2];
            return this.sigBytes += n,
                this
        },
        clamp: function () {
            var e = this.words
                , r = this.sigBytes;
            e[r >>> 2] &= 4294967295 << 32 - r % 4 * 8,
                e.length = t.ceil(r / 4)
        },
        clone: function () {
            var t = n.clone.call(this);
            return t.words = this.words.slice(0),
                t
        },
        random: function (e) {
            for (var r = [], i = 0; i < e; i += 4)
                r.push(4294967296 * t.random() | 0);
            return new s.init(r, e)
        }
    })
        , a = r.enc = {}
        , o = a.Hex = {
        stringify: function (t) {
            for (var e = t.words, r = t.sigBytes, i = [], n = 0; n < r; n++) {
                var s = e[n >>> 2] >>> 24 - n % 4 * 8 & 255;
                i.push((s >>> 4).toString(16)),
                    i.push((15 & s).toString(16))
            }
            return i.join("")
        },
        parse: function (t) {
            for (var e = t.length, r = [], i = 0; i < e; i += 2)
                r[i >>> 3] |= parseInt(t.substr(i, 2), 16) << 24 - i % 8 * 4;
            return new s.init(r, e / 2)
        }
    }
        , h = a.Latin1 = {
        stringify: function (t) {
            for (var e = t.words, r = t.sigBytes, i = [], n = 0; n < r; n++) {
                var s = e[n >>> 2] >>> 24 - n % 4 * 8 & 255;
                i.push(String.fromCharCode(s))
            }
            return i.join("")
        },
        parse: function (t) {
            for (var e = t.length, r = [], i = 0; i < e; i++)
                r[i >>> 2] |= (255 & t.charCodeAt(i)) << 24 - i % 4 * 8;
            return new s.init(r, e)
        }
    }
        , u = a.Utf8 = {
        stringify: function (t) {
            try {
                return decodeURIComponent(escape(h.stringify(t)))
            } catch (e) {
                throw new Error("Malformed UTF-8 data")
            }
        },
        parse: function (t) {
            return h.parse(unescape(encodeURIComponent(t)))
        }
    }
        , c = i.BufferedBlockAlgorithm = n.extend({
        reset: function () {
            this._data = new s.init,
                this._nDataBytes = 0
        },
        _append: function (t) {
            "string" == typeof t && (t = u.parse(t)),
                this._data.concat(t),
                this._nDataBytes += t.sigBytes
        },
        _process: function (e) {
            var r = this._data
                , i = r.words
                , n = r.sigBytes
                , a = this.blockSize
                , o = n / (4 * a)
                , h = (o = e ? t.ceil(o) : t.max((0 | o) - this._minBufferSize, 0)) * a
                , u = t.min(4 * h, n);
            if (h) {
                for (var c = 0; c < h; c += a)
                    this._doProcessBlock(i, c);
                var l = i.splice(0, h);
                r.sigBytes -= u
            }
            return new s.init(l, u)
        },
        clone: function () {
            var t = n.clone.call(this);
            return t._data = this._data.clone(),
                t
        },
        _minBufferSize: 0
    });
    i.Hasher = c.extend({
        cfg: n.extend(),
        init: function (t) {
            this.cfg = this.cfg.extend(t),
                this.reset()
        },
        reset: function () {
            c.reset.call(this),
                this._doReset()
        },
        update: function (t) {
            return this._append(t),
                this._process(),
                this
        },
        finalize: function (t) {
            return t && this._append(t),
                this._doFinalize()
        },
        blockSize: 16,
        _createHelper: function (t) {
            return function (e, r) {
                return new t.init(r).finalize(e)
            }
        },
        _createHmacHelper: function (t) {
            return function (e, r) {
                return new l.HMAC.init(t, r).finalize(e)
            }
        }
    });
    var l = r.algo = {};
    return r
}(Math);
e = (t = l).lib,
    r = e.Base,
    i = e.WordArray,
    (t = t.x64 = {}).Word = r.extend({
        init: function (t, e) {
            this.high = t,
                this.low = e
        }
    }),
    t.WordArray = r.extend({
        init: function (t, e) {
            t = this.words = t || [],
                this.sigBytes = null != e ? e : 8 * t.length
        },
        toX32: function () {
            for (var t = this.words, e = t.length, r = [], n = 0; n < e; n++) {
                var s = t[n];
                r.push(s.high),
                    r.push(s.low)
            }
            return i.create(r, this.sigBytes)
        },
        clone: function () {
            for (var t = r.clone.call(this), e = t.words = this.words.slice(0), i = e.length, n = 0; n < i; n++)
                e[n] = e[n].clone();
            return t
        }
    }),
l.lib.Cipher || function (t) {
    var e = (p = l).lib
        , r = e.Base
        , i = e.WordArray
        , n = e.BufferedBlockAlgorithm
        , s = p.enc.Base64
        , a = p.algo.EvpKDF
        , o = e.Cipher = n.extend({
        cfg: r.extend(),
        createEncryptor: function (t, e) {
            return this.create(this._ENC_XFORM_MODE, t, e)
        },
        createDecryptor: function (t, e) {
            return this.create(this._DEC_XFORM_MODE, t, e)
        },
        init: function (t, e, r) {
            this.cfg = this.cfg.extend(r),
                this._xformMode = t,
                this._key = e,
                this.reset()
        },
        reset: function () {
            n.reset.call(this),
                this._doReset()
        },
        process: function (t) {
            return this._append(t),
                this._process()
        },
        finalize: function (t) {
            return t && this._append(t),
                this._doFinalize()
        },
        keySize: 4,
        ivSize: 4,
        _ENC_XFORM_MODE: 1,
        _DEC_XFORM_MODE: 2,
        _createHelper: function (t) {
            return {
                encrypt: function (e, r, i) {
                    return ("string" == typeof r ? d : g).encrypt(t, e, r, i)
                },
                decrypt: function (e, r, i) {
                    return ("string" == typeof r ? d : g).decrypt(t, e, r, i)
                }
            }
        }
    });
    e.StreamCipher = o.extend({
        _doFinalize: function () {
            return this._process(!0)
        },
        blockSize: 1
    });
    var h = p.mode = {}
        , u = function (t, e, r) {
        var i = this._iv;
        i ? this._iv = undefined : i = this._prevBlock;
        for (var n = 0; n < r; n++)
            t[e + n] ^= i[n]
    }
        , c = (e.BlockCipherMode = r.extend({
        createEncryptor: function (t, e) {
            return this.Encryptor.create(t, e)
        },
        createDecryptor: function (t, e) {
            return this.Decryptor.create(t, e)
        },
        init: function (t, e) {
            this._cipher = t,
                this._iv = e
        }
    })).extend();
    c.Encryptor = c.extend({
        processBlock: function (t, e) {
            var r = this._cipher
                , i = r.blockSize;
            u.call(this, t, e, i),
                r.encryptBlock(t, e),
                this._prevBlock = t.slice(e, e + i)
        }
    }),
        c.Decryptor = c.extend({
            processBlock: function (t, e) {
                var r = this._cipher
                    , i = r.blockSize
                    , n = t.slice(e, e + i);
                r.decryptBlock(t, e),
                    u.call(this, t, e, i),
                    this._prevBlock = n
            }
        }),
        h = h.CBC = c,
        c = (p.pad = {}).Pkcs7 = {
            pad: function (t, e) {
                for (var r, n = (r = (r = 4 * e) - t.sigBytes % r) << 24 | r << 16 | r << 8 | r, s = [], a = 0; a < r; a += 4)
                    s.push(n);
                r = i.create(s, r),
                    t.concat(r)
            },
            unpad: function (t) {
                t.sigBytes -= 255 & t.words[t.sigBytes - 1 >>> 2]
            }
        },
        e.BlockCipher = o.extend({
            cfg: o.cfg.extend({
                mode: h,
                padding: c
            }),
            reset: function () {
                o.reset.call(this);
                var t = (e = this.cfg).iv
                    , e = e.mode;
                if (this._xformMode == this._ENC_XFORM_MODE)
                    var r = e.createEncryptor;
                else
                    r = e.createDecryptor,
                        this._minBufferSize = 1;
                this._mode = r.call(e, this, t && t.words)
            },
            _doProcessBlock: function (t, e) {
                this._mode.processBlock(t, e)
            },
            _doFinalize: function () {
                var t = this.cfg.padding;
                if (this._xformMode == this._ENC_XFORM_MODE) {
                    t.pad(this._data, this.blockSize);
                    var e = this._process(!0)
                } else
                    e = this._process(!0),
                        t.unpad(e);
                return e
            },
            blockSize: 4
        });
    var f = e.CipherParams = r.extend({
        init: function (t) {
            this.mixIn(t)
        },
        toString: function (t) {
            return (t || this.formatter).stringify(this)
        }
    })
        , g = (h = (p.format = {}).OpenSSL = {
        stringify: function (t) {
            var e = t.ciphertext;
            return ((t = t.salt) ? i.create([1398893684, 1701076831]).concat(t).concat(e) : e).toString(s)
        },
        parse: function (t) {
            var e = (t = s.parse(t)).words;
            if (1398893684 == e[0] && 1701076831 == e[1]) {
                var r = i.create(e.slice(2, 4));
                e.splice(0, 4),
                    t.sigBytes -= 16
            }
            return f.create({
                ciphertext: t,
                salt: r
            })
        }
    },
        e.SerializableCipher = r.extend({
            cfg: r.extend({
                format: h
            }),
            encrypt: function (t, e, r, i) {
                i = this.cfg.extend(i);
                var n = t.createEncryptor(r, i);
                return e = n.finalize(e),
                    n = n.cfg,
                    f.create({
                        ciphertext: e,
                        key: r,
                        iv: n.iv,
                        algorithm: t,
                        mode: n.mode,
                        padding: n.padding,
                        blockSize: t.blockSize,
                        formatter: i.format
                    })
            },
            decrypt: function (t, e, r, i) {
                return i = this.cfg.extend(i),
                    e = this._parse(e, i.format),
                    t.createDecryptor(r, i).finalize(e.ciphertext)
            },
            _parse: function (t, e) {
                return "string" == typeof t ? e.parse(t, this) : t
            }
        }))
        , p = (p.kdf = {}).OpenSSL = {
        execute: function (t, e, r, n) {
            return n || (n = i.random(8)),
                t = a.create({
                    keySize: e + r
                }).compute(t, n),
                r = i.create(t.words.slice(e), 4 * r),
                t.sigBytes = 4 * e,
                f.create({
                    key: t,
                    iv: r,
                    salt: n
                })
        }
    }
        , d = e.PasswordBasedCipher = g.extend({
        cfg: g.cfg.extend({
            kdf: p
        }),
        encrypt: function (t, e, r, i) {
            return r = (i = this.cfg.extend(i)).kdf.execute(r, t.keySize, t.ivSize),
                i.iv = r.iv,
                (t = g.encrypt.call(this, t, e, r.key, i)).mixIn(r),
                t
        },
        decrypt: function (t, e, r, i) {
            return i = this.cfg.extend(i),
                e = this._parse(e, i.format),
                r = i.kdf.execute(r, t.keySize, t.ivSize, e.salt),
                i.iv = r.iv,
                g.decrypt.call(this, t, e, r.key, i)
        }
    })
}(),
    function () {
        for (var t = l, e = t.lib.BlockCipher, r = t.algo, i = [], n = [], s = [], a = [], o = [], h = [], u = [], c = [], f = [], g = [], p = [], d = 0; 256 > d; d++)
            p[d] = 128 > d ? d << 1 : d << 1 ^ 283;
        var v = 0
            , m = 0;
        for (d = 0; 256 > d; d++) {
            var y = (y = m ^ m << 1 ^ m << 2 ^ m << 3 ^ m << 4) >>> 8 ^ 255 & y ^ 99;
            i[v] = y,
                n[y] = v;
            var x = p[v]
                , S = p[x]
                , E = p[S]
                , w = 257 * p[y] ^ 16843008 * y;
            s[v] = w << 24 | w >>> 8,
                a[v] = w << 16 | w >>> 16,
                o[v] = w << 8 | w >>> 24,
                h[v] = w,
                w = 16843009 * E ^ 65537 * S ^ 257 * x ^ 16843008 * v,
                u[y] = w << 24 | w >>> 8,
                c[y] = w << 16 | w >>> 16,
                f[y] = w << 8 | w >>> 24,
                g[y] = w,
                v ? (v = x ^ p[p[p[E ^ x]]],
                    m ^= p[p[m]]) : v = m = 1
        }
        var F = [0, 1, 2, 4, 8, 16, 32, 64, 128, 27, 54];
        r = r.AES = e.extend({
            _doReset: function () {
                for (var t = (r = this._key).words, e = r.sigBytes / 4, r = 4 * ((this._nRounds = e + 6) + 1), n = this._keySchedule = [], s = 0; s < r; s++)
                    if (s < e)
                        n[s] = t[s];
                    else {
                        var a = n[s - 1];
                        s % e ? 6 < e && 4 == s % e && (a = i[a >>> 24] << 24 | i[a >>> 16 & 255] << 16 | i[a >>> 8 & 255] << 8 | i[255 & a]) : (a = i[(a = a << 8 | a >>> 24) >>> 24] << 24 | i[a >>> 16 & 255] << 16 | i[a >>> 8 & 255] << 8 | i[255 & a],
                            a ^= F[s / e | 0] << 24),
                            n[s] = n[s - e] ^ a
                    }
                for (t = this._invKeySchedule = [],
                         e = 0; e < r; e++)
                    s = r - e,
                        a = e % 4 ? n[s] : n[s - 4],
                        t[e] = 4 > e || 4 >= s ? a : u[i[a >>> 24]] ^ c[i[a >>> 16 & 255]] ^ f[i[a >>> 8 & 255]] ^ g[i[255 & a]]
            },
            encryptBlock: function (t, e) {
                this._doCryptBlock(t, e, this._keySchedule, s, a, o, h, i)
            },
            decryptBlock: function (t, e) {
                var r = t[e + 1];
                t[e + 1] = t[e + 3],
                    t[e + 3] = r,
                    this._doCryptBlock(t, e, this._invKeySchedule, u, c, f, g, n),
                    r = t[e + 1],
                    t[e + 1] = t[e + 3],
                    t[e + 3] = r
            },
            _doCryptBlock: function (t, e, r, i, n, s, a, o) {
                for (var h = this._nRounds, u = t[e] ^ r[0], c = t[e + 1] ^ r[1], l = t[e + 2] ^ r[2], f = t[e + 3] ^ r[3], g = 4, p = 1; p < h; p++) {
                    var d = i[u >>> 24] ^ n[c >>> 16 & 255] ^ s[l >>> 8 & 255] ^ a[255 & f] ^ r[g++]
                        , v = i[c >>> 24] ^ n[l >>> 16 & 255] ^ s[f >>> 8 & 255] ^ a[255 & u] ^ r[g++]
                        , m = i[l >>> 24] ^ n[f >>> 16 & 255] ^ s[u >>> 8 & 255] ^ a[255 & c] ^ r[g++];
                    f = i[f >>> 24] ^ n[u >>> 16 & 255] ^ s[c >>> 8 & 255] ^ a[255 & l] ^ r[g++],
                        u = d,
                        c = v,
                        l = m
                }
                d = (o[u >>> 24] << 24 | o[c >>> 16 & 255] << 16 | o[l >>> 8 & 255] << 8 | o[255 & f]) ^ r[g++],
                    v = (o[c >>> 24] << 24 | o[l >>> 16 & 255] << 16 | o[f >>> 8 & 255] << 8 | o[255 & u]) ^ r[g++],
                    m = (o[l >>> 24] << 24 | o[f >>> 16 & 255] << 16 | o[u >>> 8 & 255] << 8 | o[255 & c]) ^ r[g++],
                    f = (o[f >>> 24] << 24 | o[u >>> 16 & 255] << 16 | o[c >>> 8 & 255] << 8 | o[255 & l]) ^ r[g++],
                    t[e] = d,
                    t[e + 1] = v,
                    t[e + 2] = m,
                    t[e + 3] = f
            },
            keySize: 8
        });
        t.AES = e._createHelper(r)
    }(),
    function () {
        function t(t, e) {
            var r = (this._lBlock >>> t ^ this._rBlock) & e;
            this._rBlock ^= r,
                this._lBlock ^= r << t
        }

        function e(t, e) {
            var r = (this._rBlock >>> t ^ this._lBlock) & e;
            this._lBlock ^= r,
                this._rBlock ^= r << t
        }

        var r = l
            , i = (n = r.lib).WordArray
            , n = n.BlockCipher
            , s = r.algo
            ,
            a = [57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4]
            ,
            o = [14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32]
            , h = [1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28]
            , u = [{
                0: 8421888,
                268435456: 32768,
                536870912: 8421378,
                805306368: 2,
                1073741824: 512,
                1342177280: 8421890,
                1610612736: 8389122,
                1879048192: 8388608,
                2147483648: 514,
                2415919104: 8389120,
                2684354560: 33280,
                2952790016: 8421376,
                3221225472: 32770,
                3489660928: 8388610,
                3758096384: 0,
                4026531840: 33282,
                134217728: 0,
                402653184: 8421890,
                671088640: 33282,
                939524096: 32768,
                1207959552: 8421888,
                1476395008: 512,
                1744830464: 8421378,
                2013265920: 2,
                2281701376: 8389120,
                2550136832: 33280,
                2818572288: 8421376,
                3087007744: 8389122,
                3355443200: 8388610,
                3623878656: 32770,
                3892314112: 514,
                4160749568: 8388608,
                1: 32768,
                268435457: 2,
                536870913: 8421888,
                805306369: 8388608,
                1073741825: 8421378,
                1342177281: 33280,
                1610612737: 512,
                1879048193: 8389122,
                2147483649: 8421890,
                2415919105: 8421376,
                2684354561: 8388610,
                2952790017: 33282,
                3221225473: 514,
                3489660929: 8389120,
                3758096385: 32770,
                4026531841: 0,
                134217729: 8421890,
                402653185: 8421376,
                671088641: 8388608,
                939524097: 512,
                1207959553: 32768,
                1476395009: 8388610,
                1744830465: 2,
                2013265921: 33282,
                2281701377: 32770,
                2550136833: 8389122,
                2818572289: 514,
                3087007745: 8421888,
                3355443201: 8389120,
                3623878657: 0,
                3892314113: 33280,
                4160749569: 8421378
            }, {
                0: 1074282512,
                16777216: 16384,
                33554432: 524288,
                50331648: 1074266128,
                67108864: 1073741840,
                83886080: 1074282496,
                100663296: 1073758208,
                117440512: 16,
                134217728: 540672,
                150994944: 1073758224,
                167772160: 1073741824,
                184549376: 540688,
                201326592: 524304,
                218103808: 0,
                234881024: 16400,
                251658240: 1074266112,
                8388608: 1073758208,
                25165824: 540688,
                41943040: 16,
                58720256: 1073758224,
                75497472: 1074282512,
                92274688: 1073741824,
                109051904: 524288,
                125829120: 1074266128,
                142606336: 524304,
                159383552: 0,
                176160768: 16384,
                192937984: 1074266112,
                209715200: 1073741840,
                226492416: 540672,
                243269632: 1074282496,
                260046848: 16400,
                268435456: 0,
                285212672: 1074266128,
                301989888: 1073758224,
                318767104: 1074282496,
                335544320: 1074266112,
                352321536: 16,
                369098752: 540688,
                385875968: 16384,
                402653184: 16400,
                419430400: 524288,
                436207616: 524304,
                452984832: 1073741840,
                469762048: 540672,
                486539264: 1073758208,
                503316480: 1073741824,
                520093696: 1074282512,
                276824064: 540688,
                293601280: 524288,
                310378496: 1074266112,
                327155712: 16384,
                343932928: 1073758208,
                360710144: 1074282512,
                377487360: 16,
                394264576: 1073741824,
                411041792: 1074282496,
                427819008: 1073741840,
                444596224: 1073758224,
                461373440: 524304,
                478150656: 0,
                494927872: 16400,
                511705088: 1074266128,
                528482304: 540672
            }, {
                0: 260,
                1048576: 0,
                2097152: 67109120,
                3145728: 65796,
                4194304: 65540,
                5242880: 67108868,
                6291456: 67174660,
                7340032: 67174400,
                8388608: 67108864,
                9437184: 67174656,
                10485760: 65792,
                11534336: 67174404,
                12582912: 67109124,
                13631488: 65536,
                14680064: 4,
                15728640: 256,
                524288: 67174656,
                1572864: 67174404,
                2621440: 0,
                3670016: 67109120,
                4718592: 67108868,
                5767168: 65536,
                6815744: 65540,
                7864320: 260,
                8912896: 4,
                9961472: 256,
                11010048: 67174400,
                12058624: 65796,
                13107200: 65792,
                14155776: 67109124,
                15204352: 67174660,
                16252928: 67108864,
                16777216: 67174656,
                17825792: 65540,
                18874368: 65536,
                19922944: 67109120,
                20971520: 256,
                22020096: 67174660,
                23068672: 67108868,
                24117248: 0,
                25165824: 67109124,
                26214400: 67108864,
                27262976: 4,
                28311552: 65792,
                29360128: 67174400,
                30408704: 260,
                31457280: 65796,
                32505856: 67174404,
                17301504: 67108864,
                18350080: 260,
                19398656: 67174656,
                20447232: 0,
                21495808: 65540,
                22544384: 67109120,
                23592960: 256,
                24641536: 67174404,
                25690112: 65536,
                26738688: 67174660,
                27787264: 65796,
                28835840: 67108868,
                29884416: 67109124,
                30932992: 67174400,
                31981568: 4,
                33030144: 65792
            }, {
                0: 2151682048,
                65536: 2147487808,
                131072: 4198464,
                196608: 2151677952,
                262144: 0,
                327680: 4198400,
                393216: 2147483712,
                458752: 4194368,
                524288: 2147483648,
                589824: 4194304,
                655360: 64,
                720896: 2147487744,
                786432: 2151678016,
                851968: 4160,
                917504: 4096,
                983040: 2151682112,
                32768: 2147487808,
                98304: 64,
                163840: 2151678016,
                229376: 2147487744,
                294912: 4198400,
                360448: 2151682112,
                425984: 0,
                491520: 2151677952,
                557056: 4096,
                622592: 2151682048,
                688128: 4194304,
                753664: 4160,
                819200: 2147483648,
                884736: 4194368,
                950272: 4198464,
                1015808: 2147483712,
                1048576: 4194368,
                1114112: 4198400,
                1179648: 2147483712,
                1245184: 0,
                1310720: 4160,
                1376256: 2151678016,
                1441792: 2151682048,
                1507328: 2147487808,
                1572864: 2151682112,
                1638400: 2147483648,
                1703936: 2151677952,
                1769472: 4198464,
                1835008: 2147487744,
                1900544: 4194304,
                1966080: 64,
                2031616: 4096,
                1081344: 2151677952,
                1146880: 2151682112,
                1212416: 0,
                1277952: 4198400,
                1343488: 4194368,
                1409024: 2147483648,
                1474560: 2147487808,
                1540096: 64,
                1605632: 2147483712,
                1671168: 4096,
                1736704: 2147487744,
                1802240: 2151678016,
                1867776: 4160,
                1933312: 2151682048,
                1998848: 4194304,
                2064384: 4198464
            }, {
                0: 128,
                4096: 17039360,
                8192: 262144,
                12288: 536870912,
                16384: 537133184,
                20480: 16777344,
                24576: 553648256,
                28672: 262272,
                32768: 16777216,
                36864: 537133056,
                40960: 536871040,
                45056: 553910400,
                49152: 553910272,
                53248: 0,
                57344: 17039488,
                61440: 553648128,
                2048: 17039488,
                6144: 553648256,
                10240: 128,
                14336: 17039360,
                18432: 262144,
                22528: 537133184,
                26624: 553910272,
                30720: 536870912,
                34816: 537133056,
                38912: 0,
                43008: 553910400,
                47104: 16777344,
                51200: 536871040,
                55296: 553648128,
                59392: 16777216,
                63488: 262272,
                65536: 262144,
                69632: 128,
                73728: 536870912,
                77824: 553648256,
                81920: 16777344,
                86016: 553910272,
                90112: 537133184,
                94208: 16777216,
                98304: 553910400,
                102400: 553648128,
                106496: 17039360,
                110592: 537133056,
                114688: 262272,
                118784: 536871040,
                122880: 0,
                126976: 17039488,
                67584: 553648256,
                71680: 16777216,
                75776: 17039360,
                79872: 537133184,
                83968: 536870912,
                88064: 17039488,
                92160: 128,
                96256: 553910272,
                100352: 262272,
                104448: 553910400,
                108544: 0,
                112640: 553648128,
                116736: 16777344,
                120832: 262144,
                124928: 537133056,
                129024: 536871040
            }, {
                0: 268435464,
                256: 8192,
                512: 270532608,
                768: 270540808,
                1024: 268443648,
                1280: 2097152,
                1536: 2097160,
                1792: 268435456,
                2048: 0,
                2304: 268443656,
                2560: 2105344,
                2816: 8,
                3072: 270532616,
                3328: 2105352,
                3584: 8200,
                3840: 270540800,
                128: 270532608,
                384: 270540808,
                640: 8,
                896: 2097152,
                1152: 2105352,
                1408: 268435464,
                1664: 268443648,
                1920: 8200,
                2176: 2097160,
                2432: 8192,
                2688: 268443656,
                2944: 270532616,
                3200: 0,
                3456: 270540800,
                3712: 2105344,
                3968: 268435456,
                4096: 268443648,
                4352: 270532616,
                4608: 270540808,
                4864: 8200,
                5120: 2097152,
                5376: 268435456,
                5632: 268435464,
                5888: 2105344,
                6144: 2105352,
                6400: 0,
                6656: 8,
                6912: 270532608,
                7168: 8192,
                7424: 268443656,
                7680: 270540800,
                7936: 2097160,
                4224: 8,
                4480: 2105344,
                4736: 2097152,
                4992: 268435464,
                5248: 268443648,
                5504: 8200,
                5760: 270540808,
                6016: 270532608,
                6272: 270540800,
                6528: 270532616,
                6784: 8192,
                7040: 2105352,
                7296: 2097160,
                7552: 0,
                7808: 268435456,
                8064: 268443656
            }, {
                0: 1048576,
                16: 33555457,
                32: 1024,
                48: 1049601,
                64: 34604033,
                80: 0,
                96: 1,
                112: 34603009,
                128: 33555456,
                144: 1048577,
                160: 33554433,
                176: 34604032,
                192: 34603008,
                208: 1025,
                224: 1049600,
                240: 33554432,
                8: 34603009,
                24: 0,
                40: 33555457,
                56: 34604032,
                72: 1048576,
                88: 33554433,
                104: 33554432,
                120: 1025,
                136: 1049601,
                152: 33555456,
                168: 34603008,
                184: 1048577,
                200: 1024,
                216: 34604033,
                232: 1,
                248: 1049600,
                256: 33554432,
                272: 1048576,
                288: 33555457,
                304: 34603009,
                320: 1048577,
                336: 33555456,
                352: 34604032,
                368: 1049601,
                384: 1025,
                400: 34604033,
                416: 1049600,
                432: 1,
                448: 0,
                464: 34603008,
                480: 33554433,
                496: 1024,
                264: 1049600,
                280: 33555457,
                296: 34603009,
                312: 1,
                328: 33554432,
                344: 1048576,
                360: 1025,
                376: 34604032,
                392: 33554433,
                408: 34603008,
                424: 0,
                440: 34604033,
                456: 1049601,
                472: 1024,
                488: 33555456,
                504: 1048577
            }, {
                0: 134219808,
                1: 131072,
                2: 134217728,
                3: 32,
                4: 131104,
                5: 134350880,
                6: 134350848,
                7: 2048,
                8: 134348800,
                9: 134219776,
                10: 133120,
                11: 134348832,
                12: 2080,
                13: 0,
                14: 134217760,
                15: 133152,
                2147483648: 2048,
                2147483649: 134350880,
                2147483650: 134219808,
                2147483651: 134217728,
                2147483652: 134348800,
                2147483653: 133120,
                2147483654: 133152,
                2147483655: 32,
                2147483656: 134217760,
                2147483657: 2080,
                2147483658: 131104,
                2147483659: 134350848,
                2147483660: 0,
                2147483661: 134348832,
                2147483662: 134219776,
                2147483663: 131072,
                16: 133152,
                17: 134350848,
                18: 32,
                19: 2048,
                20: 134219776,
                21: 134217760,
                22: 134348832,
                23: 131072,
                24: 0,
                25: 131104,
                26: 134348800,
                27: 134219808,
                28: 134350880,
                29: 133120,
                30: 2080,
                31: 134217728,
                2147483664: 131072,
                2147483665: 2048,
                2147483666: 134348832,
                2147483667: 133152,
                2147483668: 32,
                2147483669: 134348800,
                2147483670: 134217728,
                2147483671: 134219808,
                2147483672: 134350880,
                2147483673: 134217760,
                2147483674: 134219776,
                2147483675: 0,
                2147483676: 133120,
                2147483677: 2080,
                2147483678: 131104,
                2147483679: 134350848
            }]
            , c = [4160749569, 528482304, 33030144, 2064384, 129024, 8064, 504, 2147483679]
            , f = s.DES = n.extend({
                _doReset: function () {
                    for (var t = this._key.words, e = [], r = 0; 56 > r; r++) {
                        var i = a[r] - 1;
                        e[r] = t[i >>> 5] >>> 31 - i % 32 & 1
                    }
                    for (t = this._subKeys = [],
                             i = 0; 16 > i; i++) {
                        var n = t[i] = []
                            , s = h[i];
                        for (r = 0; 24 > r; r++)
                            n[r / 6 | 0] |= e[(o[r] - 1 + s) % 28] << 31 - r % 6,
                                n[4 + (r / 6 | 0)] |= e[28 + (o[r + 24] - 1 + s) % 28] << 31 - r % 6;
                        for (n[0] = n[0] << 1 | n[0] >>> 31,
                                 r = 1; 7 > r; r++)
                            n[r] >>>= 4 * (r - 1) + 3;
                        n[7] = n[7] << 5 | n[7] >>> 27
                    }
                    for (e = this._invSubKeys = [],
                             r = 0; 16 > r; r++)
                        e[r] = t[15 - r]
                },
                encryptBlock: function (t, e) {
                    this._doCryptBlock(t, e, this._subKeys)
                },
                decryptBlock: function (t, e) {
                    this._doCryptBlock(t, e, this._invSubKeys)
                },
                _doCryptBlock: function (r, i, n) {
                    this._lBlock = r[i],
                        this._rBlock = r[i + 1],
                        t.call(this, 4, 252645135),
                        t.call(this, 16, 65535),
                        e.call(this, 2, 858993459),
                        e.call(this, 8, 16711935),
                        t.call(this, 1, 1431655765);
                    for (var s = 0; 16 > s; s++) {
                        for (var a = n[s], o = this._lBlock, h = this._rBlock, l = 0, f = 0; 8 > f; f++)
                            l |= u[f][((h ^ a[f]) & c[f]) >>> 0];
                        this._lBlock = h,
                            this._rBlock = o ^ l
                    }
                    n = this._lBlock,
                        this._lBlock = this._rBlock,
                        this._rBlock = n,
                        t.call(this, 1, 1431655765),
                        e.call(this, 8, 16711935),
                        e.call(this, 2, 858993459),
                        t.call(this, 16, 65535),
                        t.call(this, 4, 252645135),
                        r[i] = this._lBlock,
                        r[i + 1] = this._rBlock
                },
                keySize: 2,
                ivSize: 2,
                blockSize: 2
            });
        r.DES = n._createHelper(f),
            s = s.TripleDES = n.extend({
                _doReset: function () {
                    var t = this._key.words;
                    this._des1 = f.createEncryptor(i.create(t.slice(0, 2))),
                        this._des2 = f.createEncryptor(i.create(t.slice(2, 4))),
                        this._des3 = f.createEncryptor(i.create(t.slice(4, 6)))
                },
                encryptBlock: function (t, e) {
                    this._des1.encryptBlock(t, e),
                        this._des2.decryptBlock(t, e),
                        this._des3.encryptBlock(t, e)
                },
                decryptBlock: function (t, e) {
                    this._des3.decryptBlock(t, e),
                        this._des2.encryptBlock(t, e),
                        this._des1.decryptBlock(t, e)
                },
                keySize: 6,
                ivSize: 2,
                blockSize: 2
            }),
            r.TripleDES = n._createHelper(s)
    }(),
    function () {
        var t = l
            , e = t.lib.WordArray;
        t.enc.Base64 = {
            stringify: function (t) {
                var e = t.words
                    , r = t.sigBytes
                    , i = this._map;
                t.clamp(),
                    t = [];
                for (var n = 0; n < r; n += 3)
                    for (var s = (e[n >>> 2] >>> 24 - n % 4 * 8 & 255) << 16 | (e[n + 1 >>> 2] >>> 24 - (n + 1) % 4 * 8 & 255) << 8 | e[n + 2 >>> 2] >>> 24 - (n + 2) % 4 * 8 & 255, a = 0; 4 > a && n + .75 * a < r; a++)
                        t.push(i.charAt(s >>> 6 * (3 - a) & 63));
                if (e = i.charAt(64))
                    for (; t.length % 4;)
                        t.push(e);
                return t.join("")
            },
            parse: function (t) {
                var r = t.length
                    , i = this._map;
                (n = i.charAt(64)) && (-1 != (n = t.indexOf(n)) && (r = n));
                for (var n = [], s = 0, a = 0; a < r; a++)
                    if (a % 4) {
                        var o = i.indexOf(t.charAt(a - 1)) << a % 4 * 2
                            , h = i.indexOf(t.charAt(a)) >>> 6 - a % 4 * 2;
                        n[s >>> 2] |= (o | h) << 24 - s % 4 * 8,
                            s++
                    }
                return e.create(n, s)
            },
            _map: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
        }
    }(),
    function (t) {
        function e(t, e, r, i, n, s, a) {
            return ((t = t + (e & r | ~e & i) + n + a) << s | t >>> 32 - s) + e
        }

        function r(t, e, r, i, n, s, a) {
            return ((t = t + (e & i | r & ~i) + n + a) << s | t >>> 32 - s) + e
        }

        function i(t, e, r, i, n, s, a) {
            return ((t = t + (e ^ r ^ i) + n + a) << s | t >>> 32 - s) + e
        }

        function n(t, e, r, i, n, s, a) {
            return ((t = t + (r ^ (e | ~i)) + n + a) << s | t >>> 32 - s) + e
        }

        for (var s = l, a = (h = s.lib).WordArray, o = h.Hasher, h = s.algo, u = [], c = 0; 64 > c; c++)
            u[c] = 4294967296 * t.abs(t.sin(c + 1)) | 0;
        h = h.MD5 = o.extend({
            _doReset: function () {
                this._hash = new a.init([1732584193, 4023233417, 2562383102, 271733878])
            },
            _doProcessBlock: function (t, s) {
                for (var a = 0; 16 > a; a++) {
                    var o = t[h = s + a];
                    t[h] = 16711935 & (o << 8 | o >>> 24) | 4278255360 & (o << 24 | o >>> 8)
                }
                a = this._hash.words;
                var h = t[s + 0]
                    , c = (o = t[s + 1],
                    t[s + 2])
                    , l = t[s + 3]
                    , f = t[s + 4]
                    , g = t[s + 5]
                    , p = t[s + 6]
                    , d = t[s + 7]
                    , v = t[s + 8]
                    , m = t[s + 9]
                    , y = t[s + 10]
                    , x = t[s + 11]
                    , S = t[s + 12]
                    , E = t[s + 13]
                    , w = t[s + 14]
                    , F = t[s + 15]
                    , b = e(b = a[0], I = a[1], D = a[2], A = a[3], h, 7, u[0])
                    , A = e(A, b, I, D, o, 12, u[1])
                    , D = e(D, A, b, I, c, 17, u[2])
                    , I = e(I, D, A, b, l, 22, u[3]);
                b = e(b, I, D, A, f, 7, u[4]),
                    A = e(A, b, I, D, g, 12, u[5]),
                    D = e(D, A, b, I, p, 17, u[6]),
                    I = e(I, D, A, b, d, 22, u[7]),
                    b = e(b, I, D, A, v, 7, u[8]),
                    A = e(A, b, I, D, m, 12, u[9]),
                    D = e(D, A, b, I, y, 17, u[10]),
                    I = e(I, D, A, b, x, 22, u[11]),
                    b = e(b, I, D, A, S, 7, u[12]),
                    A = e(A, b, I, D, E, 12, u[13]),
                    D = e(D, A, b, I, w, 17, u[14]),
                    b = r(b, I = e(I, D, A, b, F, 22, u[15]), D, A, o, 5, u[16]),
                    A = r(A, b, I, D, p, 9, u[17]),
                    D = r(D, A, b, I, x, 14, u[18]),
                    I = r(I, D, A, b, h, 20, u[19]),
                    b = r(b, I, D, A, g, 5, u[20]),
                    A = r(A, b, I, D, y, 9, u[21]),
                    D = r(D, A, b, I, F, 14, u[22]),
                    I = r(I, D, A, b, f, 20, u[23]),
                    b = r(b, I, D, A, m, 5, u[24]),
                    A = r(A, b, I, D, w, 9, u[25]),
                    D = r(D, A, b, I, l, 14, u[26]),
                    I = r(I, D, A, b, v, 20, u[27]),
                    b = r(b, I, D, A, E, 5, u[28]),
                    A = r(A, b, I, D, c, 9, u[29]),
                    D = r(D, A, b, I, d, 14, u[30]),
                    b = i(b, I = r(I, D, A, b, S, 20, u[31]), D, A, g, 4, u[32]),
                    A = i(A, b, I, D, v, 11, u[33]),
                    D = i(D, A, b, I, x, 16, u[34]),
                    I = i(I, D, A, b, w, 23, u[35]),
                    b = i(b, I, D, A, o, 4, u[36]),
                    A = i(A, b, I, D, f, 11, u[37]),
                    D = i(D, A, b, I, d, 16, u[38]),
                    I = i(I, D, A, b, y, 23, u[39]),
                    b = i(b, I, D, A, E, 4, u[40]),
                    A = i(A, b, I, D, h, 11, u[41]),
                    D = i(D, A, b, I, l, 16, u[42]),
                    I = i(I, D, A, b, p, 23, u[43]),
                    b = i(b, I, D, A, m, 4, u[44]),
                    A = i(A, b, I, D, S, 11, u[45]),
                    D = i(D, A, b, I, F, 16, u[46]),
                    b = n(b, I = i(I, D, A, b, c, 23, u[47]), D, A, h, 6, u[48]),
                    A = n(A, b, I, D, d, 10, u[49]),
                    D = n(D, A, b, I, w, 15, u[50]),
                    I = n(I, D, A, b, g, 21, u[51]),
                    b = n(b, I, D, A, S, 6, u[52]),
                    A = n(A, b, I, D, l, 10, u[53]),
                    D = n(D, A, b, I, y, 15, u[54]),
                    I = n(I, D, A, b, o, 21, u[55]),
                    b = n(b, I, D, A, v, 6, u[56]),
                    A = n(A, b, I, D, F, 10, u[57]),
                    D = n(D, A, b, I, p, 15, u[58]),
                    I = n(I, D, A, b, E, 21, u[59]),
                    b = n(b, I, D, A, f, 6, u[60]),
                    A = n(A, b, I, D, x, 10, u[61]),
                    D = n(D, A, b, I, c, 15, u[62]),
                    I = n(I, D, A, b, m, 21, u[63]);
                a[0] = a[0] + b | 0,
                    a[1] = a[1] + I | 0,
                    a[2] = a[2] + D | 0,
                    a[3] = a[3] + A | 0
            },
            _doFinalize: function () {
                var e = this._data
                    , r = e.words
                    , i = 8 * this._nDataBytes
                    , n = 8 * e.sigBytes;
                r[n >>> 5] |= 128 << 24 - n % 32;
                var s = t.floor(i / 4294967296);
                for (r[15 + (n + 64 >>> 9 << 4)] = 16711935 & (s << 8 | s >>> 24) | 4278255360 & (s << 24 | s >>> 8),
                         r[14 + (n + 64 >>> 9 << 4)] = 16711935 & (i << 8 | i >>> 24) | 4278255360 & (i << 24 | i >>> 8),
                         e.sigBytes = 4 * (r.length + 1),
                         this._process(),
                         r = (e = this._hash).words,
                         i = 0; 4 > i; i++)
                    n = r[i],
                        r[i] = 16711935 & (n << 8 | n >>> 24) | 4278255360 & (n << 24 | n >>> 8);
                return e
            },
            clone: function () {
                var t = o.clone.call(this);
                return t._hash = this._hash.clone(),
                    t
            }
        }),
            s.MD5 = o._createHelper(h),
            s.HmacMD5 = o._createHmacHelper(h)
    }(Math),
    s = (h = (n = l).lib).WordArray,
    a = h.Hasher,
    o = [],
    h = n.algo.SHA1 = a.extend({
        _doReset: function () {
            this._hash = new s.init([1732584193, 4023233417, 2562383102, 271733878, 3285377520])
        },
        _doProcessBlock: function (t, e) {
            for (var r = this._hash.words, i = r[0], n = r[1], s = r[2], a = r[3], h = r[4], u = 0; 80 > u; u++) {
                if (16 > u)
                    o[u] = 0 | t[e + u];
                else {
                    var c = o[u - 3] ^ o[u - 8] ^ o[u - 14] ^ o[u - 16];
                    o[u] = c << 1 | c >>> 31
                }
                c = (i << 5 | i >>> 27) + h + o[u],
                    c = 20 > u ? c + (1518500249 + (n & s | ~n & a)) : 40 > u ? c + (1859775393 + (n ^ s ^ a)) : 60 > u ? c + ((n & s | n & a | s & a) - 1894007588) : c + ((n ^ s ^ a) - 899497514),
                    h = a,
                    a = s,
                    s = n << 30 | n >>> 2,
                    n = i,
                    i = c
            }
            r[0] = r[0] + i | 0,
                r[1] = r[1] + n | 0,
                r[2] = r[2] + s | 0,
                r[3] = r[3] + a | 0,
                r[4] = r[4] + h | 0
        },
        _doFinalize: function () {
            var t = this._data
                , e = t.words
                , r = 8 * this._nDataBytes
                , i = 8 * t.sigBytes;
            return e[i >>> 5] |= 128 << 24 - i % 32,
                e[14 + (i + 64 >>> 9 << 4)] = Math.floor(r / 4294967296),
                e[15 + (i + 64 >>> 9 << 4)] = r,
                t.sigBytes = 4 * e.length,
                this._process(),
                this._hash
        },
        clone: function () {
            var t = a.clone.call(this);
            return t._hash = this._hash.clone(),
                t
        }
    }),
    n.SHA1 = a._createHelper(h),
    n.HmacSHA1 = a._createHmacHelper(h),
    function (t) {
        for (var e = l, r = (n = e.lib).WordArray, i = n.Hasher, n = e.algo, s = [], a = [], o = function (t) {
            return 4294967296 * (t - (0 | t)) | 0
        }, h = 2, u = 0; 64 > u;) {
            var c;
            t: {
                c = h;
                for (var f = t.sqrt(c), g = 2; g <= f; g++)
                    if (!(c % g)) {
                        c = !1;
                        break t
                    }
                c = !0
            }
            c && (8 > u && (s[u] = o(t.pow(h, .5))),
                a[u] = o(t.pow(h, 1 / 3)),
                u++),
                h++
        }
        var p = [];
        n = n.SHA256 = i.extend({
            _doReset: function () {
                this._hash = new r.init(s.slice(0))
            },
            _doProcessBlock: function (t, e) {
                for (var r = this._hash.words, i = r[0], n = r[1], s = r[2], o = r[3], h = r[4], u = r[5], c = r[6], l = r[7], f = 0; 64 > f; f++) {
                    if (16 > f)
                        p[f] = 0 | t[e + f];
                    else {
                        var g = p[f - 15]
                            , d = p[f - 2];
                        p[f] = ((g << 25 | g >>> 7) ^ (g << 14 | g >>> 18) ^ g >>> 3) + p[f - 7] + ((d << 15 | d >>> 17) ^ (d << 13 | d >>> 19) ^ d >>> 10) + p[f - 16]
                    }
                    g = l + ((h << 26 | h >>> 6) ^ (h << 21 | h >>> 11) ^ (h << 7 | h >>> 25)) + (h & u ^ ~h & c) + a[f] + p[f],
                        d = ((i << 30 | i >>> 2) ^ (i << 19 | i >>> 13) ^ (i << 10 | i >>> 22)) + (i & n ^ i & s ^ n & s),
                        l = c,
                        c = u,
                        u = h,
                        h = o + g | 0,
                        o = s,
                        s = n,
                        n = i,
                        i = g + d | 0
                }
                r[0] = r[0] + i | 0,
                    r[1] = r[1] + n | 0,
                    r[2] = r[2] + s | 0,
                    r[3] = r[3] + o | 0,
                    r[4] = r[4] + h | 0,
                    r[5] = r[5] + u | 0,
                    r[6] = r[6] + c | 0,
                    r[7] = r[7] + l | 0
            },
            _doFinalize: function () {
                var e = this._data
                    , r = e.words
                    , i = 8 * this._nDataBytes
                    , n = 8 * e.sigBytes;
                return r[n >>> 5] |= 128 << 24 - n % 32,
                    r[14 + (n + 64 >>> 9 << 4)] = t.floor(i / 4294967296),
                    r[15 + (n + 64 >>> 9 << 4)] = i,
                    e.sigBytes = 4 * r.length,
                    this._process(),
                    this._hash
            },
            clone: function () {
                var t = i.clone.call(this);
                return t._hash = this._hash.clone(),
                    t
            }
        });
        e.SHA256 = i._createHelper(n),
            e.HmacSHA256 = i._createHmacHelper(n)
    }(Math),
    function () {
        var t = l
            , e = t.lib.WordArray
            , r = (i = t.algo).SHA256
            , i = i.SHA224 = r.extend({
            _doReset: function () {
                this._hash = new e.init([3238371032, 914150663, 812702999, 4144912697, 4290775857, 1750603025, 1694076839, 3204075428])
            },
            _doFinalize: function () {
                var t = r._doFinalize.call(this);
                return t.sigBytes -= 4,
                    t
            }
        });
        t.SHA224 = r._createHelper(i),
            t.HmacSHA224 = r._createHmacHelper(i)
    }(),
    function () {
        function t() {
            return i.create.apply(i, arguments)
        }

        for (var e = l, r = e.lib.Hasher, i = (s = e.x64).Word, n = s.WordArray, s = e.algo, a = [t(1116352408, 3609767458), t(1899447441, 602891725), t(3049323471, 3964484399), t(3921009573, 2173295548), t(961987163, 4081628472), t(1508970993, 3053834265), t(2453635748, 2937671579), t(2870763221, 3664609560), t(3624381080, 2734883394), t(310598401, 1164996542), t(607225278, 1323610764), t(1426881987, 3590304994), t(1925078388, 4068182383), t(2162078206, 991336113), t(2614888103, 633803317), t(3248222580, 3479774868), t(3835390401, 2666613458), t(4022224774, 944711139), t(264347078, 2341262773), t(604807628, 2007800933), t(770255983, 1495990901), t(1249150122, 1856431235), t(1555081692, 3175218132), t(1996064986, 2198950837), t(2554220882, 3999719339), t(2821834349, 766784016), t(2952996808, 2566594879), t(3210313671, 3203337956), t(3336571891, 1034457026), t(3584528711, 2466948901), t(113926993, 3758326383), t(338241895, 168717936), t(666307205, 1188179964), t(773529912, 1546045734), t(1294757372, 1522805485), t(1396182291, 2643833823), t(1695183700, 2343527390), t(1986661051, 1014477480), t(2177026350, 1206759142), t(2456956037, 344077627), t(2730485921, 1290863460), t(2820302411, 3158454273), t(3259730800, 3505952657), t(3345764771, 106217008), t(3516065817, 3606008344), t(3600352804, 1432725776), t(4094571909, 1467031594), t(275423344, 851169720), t(430227734, 3100823752), t(506948616, 1363258195), t(659060556, 3750685593), t(883997877, 3785050280), t(958139571, 3318307427), t(1322822218, 3812723403), t(1537002063, 2003034995), t(1747873779, 3602036899), t(1955562222, 1575990012), t(2024104815, 1125592928), t(2227730452, 2716904306), t(2361852424, 442776044), t(2428436474, 593698344), t(2756734187, 3733110249), t(3204031479, 2999351573), t(3329325298, 3815920427), t(3391569614, 3928383900), t(3515267271, 566280711), t(3940187606, 3454069534), t(4118630271, 4000239992), t(116418474, 1914138554), t(174292421, 2731055270), t(289380356, 3203993006), t(460393269, 320620315), t(685471733, 587496836), t(852142971, 1086792851), t(1017036298, 365543100), t(1126000580, 2618297676), t(1288033470, 3409855158), t(1501505948, 4234509866), t(1607167915, 987167468), t(1816402316, 1246189591)], o = [], h = 0; 80 > h; h++)
            o[h] = t();
        s = s.SHA512 = r.extend({
            _doReset: function () {
                this._hash = new n.init([new i.init(1779033703, 4089235720), new i.init(3144134277, 2227873595), new i.init(1013904242, 4271175723), new i.init(2773480762, 1595750129), new i.init(1359893119, 2917565137), new i.init(2600822924, 725511199), new i.init(528734635, 4215389547), new i.init(1541459225, 327033209)])
            },
            _doProcessBlock: function (t, e) {
                for (var r = (l = this._hash.words)[0], i = l[1], n = l[2], s = l[3], h = l[4], u = l[5], c = l[6], l = l[7], f = r.high, g = r.low, p = i.high, d = i.low, v = n.high, m = n.low, y = s.high, x = s.low, S = h.high, E = h.low, w = u.high, F = u.low, b = c.high, A = c.low, D = l.high, I = l.low, P = f, C = g, R = p, T = d, N = v, B = m, H = y, O = x, j = S, V = E, K = w, L = F, k = b, q = A, M = D, _ = I, U = 0; 80 > U; U++) {
                    var G = o[U];
                    if (16 > U)
                        var z = G.high = 0 | t[e + 2 * U]
                            , W = G.low = 0 | t[e + 2 * U + 1];
                    else {
                        z = ((W = (z = o[U - 15]).high) >>> 1 | (J = z.low) << 31) ^ (W >>> 8 | J << 24) ^ W >>> 7;
                        var J = (J >>> 1 | W << 31) ^ (J >>> 8 | W << 24) ^ (J >>> 7 | W << 25)
                            , X = ((W = (X = o[U - 2]).high) >>> 19 | ($ = X.low) << 13) ^ (W << 3 | $ >>> 29) ^ W >>> 6
                            , $ = ($ >>> 19 | W << 13) ^ ($ << 3 | W >>> 29) ^ ($ >>> 6 | W << 26)
                            , Y = (W = o[U - 7]).high
                            , Z = (Q = o[U - 16]).high
                            , Q = Q.low;
                        z = (z = (z = z + Y + ((W = J + W.low) >>> 0 < J >>> 0 ? 1 : 0)) + X + ((W = W + $) >>> 0 < $ >>> 0 ? 1 : 0)) + Z + ((W = W + Q) >>> 0 < Q >>> 0 ? 1 : 0);
                        G.high = z,
                            G.low = W
                    }
                    Y = j & K ^ ~j & k,
                        Q = V & L ^ ~V & q,
                        G = P & R ^ P & N ^ R & N;
                    var tt = C & T ^ C & B ^ T & B
                        , et = (J = (P >>> 28 | C << 4) ^ (P << 30 | C >>> 2) ^ (P << 25 | C >>> 7),
                        X = (C >>> 28 | P << 4) ^ (C << 30 | P >>> 2) ^ (C << 25 | P >>> 7),
                        ($ = a[U]).high)
                        , rt = $.low;
                    Z = M + ((j >>> 14 | V << 18) ^ (j >>> 18 | V << 14) ^ (j << 23 | V >>> 9)) + (($ = _ + ((V >>> 14 | j << 18) ^ (V >>> 18 | j << 14) ^ (V << 23 | j >>> 9))) >>> 0 < _ >>> 0 ? 1 : 0),
                        M = k,
                        _ = q,
                        k = K,
                        q = L,
                        K = j,
                        L = V,
                        j = H + (Z = (Z = (Z = Z + Y + (($ = $ + Q) >>> 0 < Q >>> 0 ? 1 : 0)) + et + (($ = $ + rt) >>> 0 < rt >>> 0 ? 1 : 0)) + z + (($ = $ + W) >>> 0 < W >>> 0 ? 1 : 0)) + ((V = O + $ | 0) >>> 0 < O >>> 0 ? 1 : 0) | 0,
                        H = N,
                        O = B,
                        N = R,
                        B = T,
                        R = P,
                        T = C,
                        P = Z + (G = J + G + ((W = X + tt) >>> 0 < X >>> 0 ? 1 : 0)) + ((C = $ + W | 0) >>> 0 < $ >>> 0 ? 1 : 0) | 0
                }
                g = r.low = g + C,
                    r.high = f + P + (g >>> 0 < C >>> 0 ? 1 : 0),
                    d = i.low = d + T,
                    i.high = p + R + (d >>> 0 < T >>> 0 ? 1 : 0),
                    m = n.low = m + B,
                    n.high = v + N + (m >>> 0 < B >>> 0 ? 1 : 0),
                    x = s.low = x + O,
                    s.high = y + H + (x >>> 0 < O >>> 0 ? 1 : 0),
                    E = h.low = E + V,
                    h.high = S + j + (E >>> 0 < V >>> 0 ? 1 : 0),
                    F = u.low = F + L,
                    u.high = w + K + (F >>> 0 < L >>> 0 ? 1 : 0),
                    A = c.low = A + q,
                    c.high = b + k + (A >>> 0 < q >>> 0 ? 1 : 0),
                    I = l.low = I + _,
                    l.high = D + M + (I >>> 0 < _ >>> 0 ? 1 : 0)
            },
            _doFinalize: function () {
                var t = this._data
                    , e = t.words
                    , r = 8 * this._nDataBytes
                    , i = 8 * t.sigBytes;
                return e[i >>> 5] |= 128 << 24 - i % 32,
                    e[30 + (i + 128 >>> 10 << 5)] = Math.floor(r / 4294967296),
                    e[31 + (i + 128 >>> 10 << 5)] = r,
                    t.sigBytes = 4 * e.length,
                    this._process(),
                    this._hash.toX32()
            },
            clone: function () {
                var t = r.clone.call(this);
                return t._hash = this._hash.clone(),
                    t
            },
            blockSize: 32
        }),
            e.SHA512 = r._createHelper(s),
            e.HmacSHA512 = r._createHmacHelper(s)
    }(),
    function () {
        var t = l
            , e = (n = t.x64).Word
            , r = n.WordArray
            , i = (n = t.algo).SHA512
            , n = n.SHA384 = i.extend({
            _doReset: function () {
                this._hash = new r.init([new e.init(3418070365, 3238371032), new e.init(1654270250, 914150663), new e.init(2438529370, 812702999), new e.init(355462360, 4144912697), new e.init(1731405415, 4290775857), new e.init(2394180231, 1750603025), new e.init(3675008525, 1694076839), new e.init(1203062813, 3204075428)])
            },
            _doFinalize: function () {
                var t = i._doFinalize.call(this);
                return t.sigBytes -= 16,
                    t
            }
        });
        t.SHA384 = i._createHelper(n),
            t.HmacSHA384 = i._createHmacHelper(n)
    }(),
    function () {
        var t = l
            , e = (i = t.lib).WordArray
            , r = i.Hasher
            , i = t.algo
            ,
            n = e.create([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8, 3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12, 1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2, 4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13])
            ,
            s = e.create([5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12, 6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2, 15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13, 8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14, 12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11])
            ,
            a = e.create([11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8, 7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12, 11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5, 11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12, 9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6])
            ,
            o = e.create([8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6, 9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11, 9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5, 15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8, 8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11])
            , h = e.create([0, 1518500249, 1859775393, 2400959708, 2840853838])
            , u = e.create([1352829926, 1548603684, 1836072691, 2053994217, 0]);
        i = i.RIPEMD160 = r.extend({
            _doReset: function () {
                this._hash = e.create([1732584193, 4023233417, 2562383102, 271733878, 3285377520])
            },
            _doProcessBlock: function (t, e) {
                for (var r = 0; 16 > r; r++) {
                    var i = t[S = e + r];
                    t[S] = 16711935 & (i << 8 | i >>> 24) | 4278255360 & (i << 24 | i >>> 8)
                }
                var c, l, f, g, p, d, v, m, y, x, S = this._hash.words, E = (i = h.words,
                    u.words), w = n.words, F = s.words, b = a.words, A = o.words;
                d = c = S[0],
                    v = l = S[1],
                    m = f = S[2],
                    y = g = S[3],
                    x = p = S[4];
                var D;
                for (r = 0; 80 > r; r += 1)
                    D = c + t[e + w[r]] | 0,
                        D = 16 > r ? D + ((l ^ f ^ g) + i[0]) : 32 > r ? D + ((l & f | ~l & g) + i[1]) : 48 > r ? D + (((l | ~f) ^ g) + i[2]) : 64 > r ? D + ((l & g | f & ~g) + i[3]) : D + ((l ^ (f | ~g)) + i[4]),
                        D = (D = (D |= 0) << b[r] | D >>> 32 - b[r]) + p | 0,
                        c = p,
                        p = g,
                        g = f << 10 | f >>> 22,
                        f = l,
                        l = D,
                        D = d + t[e + F[r]] | 0,
                        D = 16 > r ? D + ((v ^ (m | ~y)) + E[0]) : 32 > r ? D + ((v & y | m & ~y) + E[1]) : 48 > r ? D + (((v | ~m) ^ y) + E[2]) : 64 > r ? D + ((v & m | ~v & y) + E[3]) : D + ((v ^ m ^ y) + E[4]),
                        D = (D = (D |= 0) << A[r] | D >>> 32 - A[r]) + x | 0,
                        d = x,
                        x = y,
                        y = m << 10 | m >>> 22,
                        m = v,
                        v = D;
                D = S[1] + f + y | 0,
                    S[1] = S[2] + g + x | 0,
                    S[2] = S[3] + p + d | 0,
                    S[3] = S[4] + c + v | 0,
                    S[4] = S[0] + l + m | 0,
                    S[0] = D
            },
            _doFinalize: function () {
                var t = this._data
                    , e = t.words
                    , r = 8 * this._nDataBytes
                    , i = 8 * t.sigBytes;
                for (e[i >>> 5] |= 128 << 24 - i % 32,
                         e[14 + (i + 64 >>> 9 << 4)] = 16711935 & (r << 8 | r >>> 24) | 4278255360 & (r << 24 | r >>> 8),
                         t.sigBytes = 4 * (e.length + 1),
                         this._process(),
                         e = (t = this._hash).words,
                         r = 0; 5 > r; r++)
                    i = e[r],
                        e[r] = 16711935 & (i << 8 | i >>> 24) | 4278255360 & (i << 24 | i >>> 8);
                return t
            },
            clone: function () {
                var t = r.clone.call(this);
                return t._hash = this._hash.clone(),
                    t
            }
        });
        t.RIPEMD160 = r._createHelper(i),
            t.HmacRIPEMD160 = r._createHmacHelper(i)
    }(),
    function () {
        var t = l
            , e = t.enc.Utf8;
        t.algo.HMAC = t.lib.Base.extend({
            init: function (t, r) {
                t = this._hasher = new t.init,
                "string" == typeof r && (r = e.parse(r));
                var i = t.blockSize
                    , n = 4 * i;
                r.sigBytes > n && (r = t.finalize(r)),
                    r.clamp();
                for (var s = this._oKey = r.clone(), a = this._iKey = r.clone(), o = s.words, h = a.words, u = 0; u < i; u++)
                    o[u] ^= 1549556828,
                        h[u] ^= 909522486;
                s.sigBytes = a.sigBytes = n,
                    this.reset()
            },
            reset: function () {
                var t = this._hasher;
                t.reset(),
                    t.update(this._iKey)
            },
            update: function (t) {
                return this._hasher.update(t),
                    this
            },
            finalize: function (t) {
                var e = this._hasher;
                return t = e.finalize(t),
                    e.reset(),
                    e.finalize(this._oKey.clone().concat(t))
            }
        })
    }(),
    function () {
        var t, e = l, r = (t = e.lib).Base, i = t.WordArray, n = (t = e.algo).HMAC, s = t.PBKDF2 = r.extend({
            cfg: r.extend({
                keySize: 4,
                hasher: t.SHA1,
                iterations: 1
            }),
            init: function (t) {
                this.cfg = this.cfg.extend(t)
            },
            compute: function (t, e) {
                var r = this.cfg
                    , s = n.create(r.hasher, t)
                    , a = i.create()
                    , o = i.create([1])
                    , h = a.words
                    , u = o.words
                    , c = r.keySize;
                for (r = r.iterations; h.length < c;) {
                    var l = s.update(e).finalize(o);
                    s.reset();
                    for (var f = l.words, g = f.length, p = l, d = 1; d < r; d++) {
                        p = s.finalize(p),
                            s.reset();
                        for (var v = p.words, m = 0; m < g; m++)
                            f[m] ^= v[m]
                    }
                    a.concat(l),
                        u[0]++
                }
                return a.sigBytes = 4 * c,
                    a
            }
        });
        e.PBKDF2 = function (t, e, r) {
            return s.create(r).compute(t, e)
        }
    }();
/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
var f, g = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", p = "=";

function d(t) {
    var e, r, i = "";
    for (e = 0; e + 3 <= t.length; e += 3)
        r = parseInt(t.substring(e, e + 3), 16),
            i += g.charAt(r >> 6) + g.charAt(63 & r);
    for (e + 1 == t.length ? (r = parseInt(t.substring(e, e + 1), 16),
        i += g.charAt(r << 2)) : e + 2 == t.length && (r = parseInt(t.substring(e, e + 2), 16),
        i += g.charAt(r >> 2) + g.charAt((3 & r) << 4)); (3 & i.length) > 0;)
        i += p;
    return i
}

function v(t) {
    var e, r, i, n = "", s = 0;
    for (e = 0; e < t.length && t.charAt(e) != p; ++e)
        (i = g.indexOf(t.charAt(e))) < 0 || (0 == s ? (n += F(i >> 2),
            r = 3 & i,
            s = 1) : 1 == s ? (n += F(r << 2 | i >> 4),
            r = 15 & i,
            s = 2) : 2 == s ? (n += F(r),
            n += F(i >> 2),
            r = 3 & i,
            s = 3) : (n += F(r << 2 | i >> 4),
            n += F(15 & i),
            s = 0));
    return 1 == s && (n += F(r << 2)),
        n
}

/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
*/
function m(t, e, r) {
    null != t && ("number" == typeof t ? this.fromNumber(t, e, r) : null == e && "string" != typeof t ? this.fromString(t, 256) : this.fromString(t, e))
}

function y() {
    return new m(null)
}

"Microsoft Internet Explorer" == u.appName ? (m.prototype.am = function (t, e, r, i, n, s) {
    for (var a = 32767 & e, o = e >> 15; --s >= 0;) {
        var h = 32767 & this[t]
            , u = this[t++] >> 15
            , c = o * h + u * a;
        n = ((h = a * h + ((32767 & c) << 15) + r[i] + (1073741823 & n)) >>> 30) + (c >>> 15) + o * u + (n >>> 30),
            r[i++] = 1073741823 & h
    }
    return n
}
    ,
    f = 30) : "Netscape" != u.appName ? (m.prototype.am = function (t, e, r, i, n, s) {
    for (; --s >= 0;) {
        var a = e * this[t++] + r[i] + n;
        n = Math.floor(a / 67108864),
            r[i++] = 67108863 & a
    }
    return n
}
    ,
    f = 26) : (m.prototype.am = function (t, e, r, i, n, s) {
    for (var a = 16383 & e, o = e >> 14; --s >= 0;) {
        var h = 16383 & this[t]
            , u = this[t++] >> 14
            , c = o * h + u * a;
        n = ((h = a * h + ((16383 & c) << 14) + r[i] + n) >> 28) + (c >> 14) + o * u,
            r[i++] = 268435455 & h
    }
    return n
}
    ,
    f = 28),
    m.prototype.DB = f,
    m.prototype.DM = (1 << f) - 1,
    m.prototype.DV = 1 << f;
m.prototype.FV = Math.pow(2, 52),
    m.prototype.F1 = 52 - f,
    m.prototype.F2 = 2 * f - 52;
var x, S, E = "0123456789abcdefghijklmnopqrstuvwxyz", w = new Array;
for (x = "0".charCodeAt(0),
         S = 0; S <= 9; ++S)
    w[x++] = S;
for (x = "a".charCodeAt(0),
         S = 10; S < 36; ++S)
    w[x++] = S;
for (x = "A".charCodeAt(0),
         S = 10; S < 36; ++S)
    w[x++] = S;

function F(t) {
    return E.charAt(t)
}

function b(t, e) {
    var r = w[t.charCodeAt(e)];
    return null == r ? -1 : r
}

function A(t) {
    var e = y();
    return e.fromInt(t),
        e
}

function D(t) {
    var e, r = 1;
    return 0 != (e = t >>> 16) && (t = e,
        r += 16),
    0 != (e = t >> 8) && (t = e,
        r += 8),
    0 != (e = t >> 4) && (t = e,
        r += 4),
    0 != (e = t >> 2) && (t = e,
        r += 2),
    0 != (e = t >> 1) && (t = e,
        r += 1),
        r
}

function I(t) {
    this.m = t
}

function P(t) {
    this.m = t,
        this.mp = t.invDigit(),
        this.mpl = 32767 & this.mp,
        this.mph = this.mp >> 15,
        this.um = (1 << t.DB - 15) - 1,
        this.mt2 = 2 * t.t
}

function C(t, e) {
    return t & e
}

function R(t, e) {
    return t | e
}

function T(t, e) {
    return t ^ e
}

function N(t, e) {
    return t & ~e
}

function B(t) {
    if (0 == t)
        return -1;
    var e = 0;
    return 65535 & t || (t >>= 16,
        e += 16),
    255 & t || (t >>= 8,
        e += 8),
    15 & t || (t >>= 4,
        e += 4),
    3 & t || (t >>= 2,
        e += 2),
    1 & t || ++e,
        e
}

function H(t) {
    for (var e = 0; 0 != t;)
        t &= t - 1,
            ++e;
    return e
}

function O() {
}

function j(t) {
    return t
}

function V(t) {
    this.r2 = y(),
        this.q3 = y(),
        m.ONE.dlShiftTo(2 * t.t, this.r2),
        this.mu = this.r2.divide(t),
        this.m = t
}

I.prototype.convert = function (t) {
    return t.s < 0 || t.compareTo(this.m) >= 0 ? t.mod(this.m) : t
}
    ,
    I.prototype.revert = function (t) {
        return t
    }
    ,
    I.prototype.reduce = function (t) {
        t.divRemTo(this.m, null, t)
    }
    ,
    I.prototype.mulTo = function (t, e, r) {
        t.multiplyTo(e, r),
            this.reduce(r)
    }
    ,
    I.prototype.sqrTo = function (t, e) {
        t.squareTo(e),
            this.reduce(e)
    }
    ,
    P.prototype.convert = function (t) {
        var e = y();
        return t.abs().dlShiftTo(this.m.t, e),
            e.divRemTo(this.m, null, e),
        t.s < 0 && e.compareTo(m.ZERO) > 0 && this.m.subTo(e, e),
            e
    }
    ,
    P.prototype.revert = function (t) {
        var e = y();
        return t.copyTo(e),
            this.reduce(e),
            e
    }
    ,
    P.prototype.reduce = function (t) {
        for (; t.t <= this.mt2;)
            t[t.t++] = 0;
        for (var e = 0; e < this.m.t; ++e) {
            var r = 32767 & t[e]
                , i = r * this.mpl + ((r * this.mph + (t[e] >> 15) * this.mpl & this.um) << 15) & t.DM;
            for (t[r = e + this.m.t] += this.m.am(0, i, t, e, 0, this.m.t); t[r] >= t.DV;)
                t[r] -= t.DV,
                    t[++r]++
        }
        t.clamp(),
            t.drShiftTo(this.m.t, t),
        t.compareTo(this.m) >= 0 && t.subTo(this.m, t)
    }
    ,
    P.prototype.mulTo = function (t, e, r) {
        t.multiplyTo(e, r),
            this.reduce(r)
    }
    ,
    P.prototype.sqrTo = function (t, e) {
        t.squareTo(e),
            this.reduce(e)
    }
    ,
    m.prototype.copyTo = function (t) {
        for (var e = this.t - 1; e >= 0; --e)
            t[e] = this[e];
        t.t = this.t,
            t.s = this.s
    }
    ,
    m.prototype.fromInt = function (t) {
        this.t = 1,
            this.s = t < 0 ? -1 : 0,
            t > 0 ? this[0] = t : t < -1 ? this[0] = t + this.DV : this.t = 0
    }
    ,
    m.prototype.fromString = function (t, e) {
        var r;
        if (16 == e)
            r = 4;
        else if (8 == e)
            r = 3;
        else if (256 == e)
            r = 8;
        else if (2 == e)
            r = 1;
        else if (32 == e)
            r = 5;
        else {
            if (4 != e)
                return void this.fromRadix(t, e);
            r = 2
        }
        this.t = 0,
            this.s = 0;
        for (var i = t.length, n = !1, s = 0; --i >= 0;) {
            var a = 8 == r ? 255 & t[i] : b(t, i);
            a < 0 ? "-" == t.charAt(i) && (n = !0) : (n = !1,
                0 == s ? this[this.t++] = a : s + r > this.DB ? (this[this.t - 1] |= (a & (1 << this.DB - s) - 1) << s,
                    this[this.t++] = a >> this.DB - s) : this[this.t - 1] |= a << s,
            (s += r) >= this.DB && (s -= this.DB))
        }
        8 == r && 128 & t[0] && (this.s = -1,
        s > 0 && (this[this.t - 1] |= (1 << this.DB - s) - 1 << s)),
            this.clamp(),
        n && m.ZERO.subTo(this, this)
    }
    ,
    m.prototype.clamp = function () {
        for (var t = this.s & this.DM; this.t > 0 && this[this.t - 1] == t;)
            --this.t
    }
    ,
    m.prototype.dlShiftTo = function (t, e) {
        var r;
        for (r = this.t - 1; r >= 0; --r)
            e[r + t] = this[r];
        for (r = t - 1; r >= 0; --r)
            e[r] = 0;
        e.t = this.t + t,
            e.s = this.s
    }
    ,
    m.prototype.drShiftTo = function (t, e) {
        for (var r = t; r < this.t; ++r)
            e[r - t] = this[r];
        e.t = Math.max(this.t - t, 0),
            e.s = this.s
    }
    ,
    m.prototype.lShiftTo = function (t, e) {
        var r, i = t % this.DB, n = this.DB - i, s = (1 << n) - 1, a = Math.floor(t / this.DB),
            o = this.s << i & this.DM;
        for (r = this.t - 1; r >= 0; --r)
            e[r + a + 1] = this[r] >> n | o,
                o = (this[r] & s) << i;
        for (r = a - 1; r >= 0; --r)
            e[r] = 0;
        e[a] = o,
            e.t = this.t + a + 1,
            e.s = this.s,
            e.clamp()
    }
    ,
    m.prototype.rShiftTo = function (t, e) {
        e.s = this.s;
        var r = Math.floor(t / this.DB);
        if (r >= this.t)
            e.t = 0;
        else {
            var i = t % this.DB
                , n = this.DB - i
                , s = (1 << i) - 1;
            e[0] = this[r] >> i;
            for (var a = r + 1; a < this.t; ++a)
                e[a - r - 1] |= (this[a] & s) << n,
                    e[a - r] = this[a] >> i;
            i > 0 && (e[this.t - r - 1] |= (this.s & s) << n),
                e.t = this.t - r,
                e.clamp()
        }
    }
    ,
    m.prototype.subTo = function (t, e) {
        for (var r = 0, i = 0, n = Math.min(t.t, this.t); r < n;)
            i += this[r] - t[r],
                e[r++] = i & this.DM,
                i >>= this.DB;
        if (t.t < this.t) {
            for (i -= t.s; r < this.t;)
                i += this[r],
                    e[r++] = i & this.DM,
                    i >>= this.DB;
            i += this.s
        } else {
            for (i += this.s; r < t.t;)
                i -= t[r],
                    e[r++] = i & this.DM,
                    i >>= this.DB;
            i -= t.s
        }
        e.s = i < 0 ? -1 : 0,
            i < -1 ? e[r++] = this.DV + i : i > 0 && (e[r++] = i),
            e.t = r,
            e.clamp()
    }
    ,
    m.prototype.multiplyTo = function (t, e) {
        var r = this.abs()
            , i = t.abs()
            , n = r.t;
        for (e.t = n + i.t; --n >= 0;)
            e[n] = 0;
        for (n = 0; n < i.t; ++n)
            e[n + r.t] = r.am(0, i[n], e, n, 0, r.t);
        e.s = 0,
            e.clamp(),
        this.s != t.s && m.ZERO.subTo(e, e)
    }
    ,
    m.prototype.squareTo = function (t) {
        for (var e = this.abs(), r = t.t = 2 * e.t; --r >= 0;)
            t[r] = 0;
        for (r = 0; r < e.t - 1; ++r) {
            var i = e.am(r, e[r], t, 2 * r, 0, 1);
            (t[r + e.t] += e.am(r + 1, 2 * e[r], t, 2 * r + 1, i, e.t - r - 1)) >= e.DV && (t[r + e.t] -= e.DV,
                t[r + e.t + 1] = 1)
        }
        t.t > 0 && (t[t.t - 1] += e.am(r, e[r], t, 2 * r, 0, 1)),
            t.s = 0,
            t.clamp()
    }
    ,
    m.prototype.divRemTo = function (t, e, r) {
        var i = t.abs();
        if (!(i.t <= 0)) {
            var n = this.abs();
            if (n.t < i.t)
                return null != e && e.fromInt(0),
                    void (null != r && this.copyTo(r));
            null == r && (r = y());
            var s = y()
                , a = this.s
                , o = t.s
                , h = this.DB - D(i[i.t - 1]);
            h > 0 ? (i.lShiftTo(h, s),
                n.lShiftTo(h, r)) : (i.copyTo(s),
                n.copyTo(r));
            var u = s.t
                , c = s[u - 1];
            if (0 != c) {
                var l = c * (1 << this.F1) + (u > 1 ? s[u - 2] >> this.F2 : 0)
                    , f = this.FV / l
                    , g = (1 << this.F1) / l
                    , p = 1 << this.F2
                    , d = r.t
                    , v = d - u
                    , x = null == e ? y() : e;
                for (s.dlShiftTo(v, x),
                     r.compareTo(x) >= 0 && (r[r.t++] = 1,
                         r.subTo(x, r)),
                         m.ONE.dlShiftTo(u, x),
                         x.subTo(s, s); s.t < u;)
                    s[s.t++] = 0;
                for (; --v >= 0;) {
                    var S = r[--d] == c ? this.DM : Math.floor(r[d] * f + (r[d - 1] + p) * g);
                    if ((r[d] += s.am(0, S, r, v, 0, u)) < S)
                        for (s.dlShiftTo(v, x),
                                 r.subTo(x, r); r[d] < --S;)
                            r.subTo(x, r)
                }
                null != e && (r.drShiftTo(u, e),
                a != o && m.ZERO.subTo(e, e)),
                    r.t = u,
                    r.clamp(),
                h > 0 && r.rShiftTo(h, r),
                a < 0 && m.ZERO.subTo(r, r)
            }
        }
    }
    ,
    m.prototype.invDigit = function () {
        if (this.t < 1)
            return 0;
        var t = this[0];
        if (!(1 & t))
            return 0;
        var e = 3 & t;
        return (e = (e = (e = (e = e * (2 - (15 & t) * e) & 15) * (2 - (255 & t) * e) & 255) * (2 - ((65535 & t) * e & 65535)) & 65535) * (2 - t * e % this.DV) % this.DV) > 0 ? this.DV - e : -e
    }
    ,
    m.prototype.isEven = function () {
        return 0 == (this.t > 0 ? 1 & this[0] : this.s)
    }
    ,
    m.prototype.exp = function (t, e) {
        if (t > 4294967295 || t < 1)
            return m.ONE;
        var r = y()
            , i = y()
            , n = e.convert(this)
            , s = D(t) - 1;
        for (n.copyTo(r); --s >= 0;)
            if (e.sqrTo(r, i),
            (t & 1 << s) > 0)
                e.mulTo(i, n, r);
            else {
                var a = r;
                r = i,
                    i = a
            }
        return e.revert(r)
    }
    ,
    m.prototype.toString = function (t) {
        if (this.s < 0)
            return "-" + this.negate().toString(t);
        var e;
        if (16 == t)
            e = 4;
        else if (8 == t)
            e = 3;
        else if (2 == t)
            e = 1;
        else if (32 == t)
            e = 5;
        else {
            if (4 != t)
                return this.toRadix(t);
            e = 2
        }
        var r, i = (1 << e) - 1, n = !1, s = "", a = this.t, o = this.DB - a * this.DB % e;
        if (a-- > 0)
            for (o < this.DB && (r = this[a] >> o) > 0 && (n = !0,
                s = F(r)); a >= 0;)
                o < e ? (r = (this[a] & (1 << o) - 1) << e - o,
                    r |= this[--a] >> (o += this.DB - e)) : (r = this[a] >> (o -= e) & i,
                o <= 0 && (o += this.DB,
                    --a)),
                r > 0 && (n = !0),
                n && (s += F(r));
        return n ? s : "0"
    }
    ,
    m.prototype.negate = function () {
        var t = y();
        return m.ZERO.subTo(this, t),
            t
    }
    ,
    m.prototype.abs = function () {
        return this.s < 0 ? this.negate() : this
    }
    ,
    m.prototype.compareTo = function (t) {
        var e = this.s - t.s;
        if (0 != e)
            return e;
        var r = this.t;
        if (0 != (e = r - t.t))
            return this.s < 0 ? -e : e;
        for (; --r >= 0;)
            if (0 != (e = this[r] - t[r]))
                return e;
        return 0
    }
    ,
    m.prototype.bitLength = function () {
        return this.t <= 0 ? 0 : this.DB * (this.t - 1) + D(this[this.t - 1] ^ this.s & this.DM)
    }
    ,
    m.prototype.mod = function (t) {
        var e = y();
        return this.abs().divRemTo(t, null, e),
        this.s < 0 && e.compareTo(m.ZERO) > 0 && t.subTo(e, e),
            e
    }
    ,
    m.prototype.modPowInt = function (t, e) {
        var r;
        return r = t < 256 || e.isEven() ? new I(e) : new P(e),
            this.exp(t, r)
    }
    ,
    m.ZERO = A(0),
    m.ONE = A(1),
    O.prototype.convert = j,
    O.prototype.revert = j,
    O.prototype.mulTo = function (t, e, r) {
        t.multiplyTo(e, r)
    }
    ,
    O.prototype.sqrTo = function (t, e) {
        t.squareTo(e)
    }
    ,
    V.prototype.convert = function (t) {
        if (t.s < 0 || t.t > 2 * this.m.t)
            return t.mod(this.m);
        if (t.compareTo(this.m) < 0)
            return t;
        var e = y();
        return t.copyTo(e),
            this.reduce(e),
            e
    }
    ,
    V.prototype.revert = function (t) {
        return t
    }
    ,
    V.prototype.reduce = function (t) {
        for (t.drShiftTo(this.m.t - 1, this.r2),
             t.t > this.m.t + 1 && (t.t = this.m.t + 1,
                 t.clamp()),
                 this.mu.multiplyUpperTo(this.r2, this.m.t + 1, this.q3),
                 this.m.multiplyLowerTo(this.q3, this.m.t + 1, this.r2); t.compareTo(this.r2) < 0;)
            t.dAddOffset(1, this.m.t + 1);
        for (t.subTo(this.r2, t); t.compareTo(this.m) >= 0;)
            t.subTo(this.m, t)
    }
    ,
    V.prototype.mulTo = function (t, e, r) {
        t.multiplyTo(e, r),
            this.reduce(r)
    }
    ,
    V.prototype.sqrTo = function (t, e) {
        t.squareTo(e),
            this.reduce(e)
    }
;
var K = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997]
    , L = (1 << 26) / K[K.length - 1];

/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
function k() {
    this.i = 0,
        this.j = 0,
        this.S = new Array
}

m.prototype.chunkSize = function (t) {
    return Math.floor(Math.LN2 * this.DB / Math.log(t))
}
    ,
    m.prototype.toRadix = function (t) {
        if (null == t && (t = 10),
        0 == this.signum() || t < 2 || t > 36)
            return "0";
        var e = this.chunkSize(t)
            , r = Math.pow(t, e)
            , i = A(r)
            , n = y()
            , s = y()
            , a = "";
        for (this.divRemTo(i, n, s); n.signum() > 0;)
            a = (r + s.intValue()).toString(t).substr(1) + a,
                n.divRemTo(i, n, s);
        return s.intValue().toString(t) + a
    }
    ,
    m.prototype.fromRadix = function (t, e) {
        this.fromInt(0),
        null == e && (e = 10);
        for (var r = this.chunkSize(e), i = Math.pow(e, r), n = !1, s = 0, a = 0, o = 0; o < t.length; ++o) {
            var h = b(t, o);
            h < 0 ? "-" == t.charAt(o) && 0 == this.signum() && (n = !0) : (a = e * a + h,
            ++s >= r && (this.dMultiply(i),
                this.dAddOffset(a, 0),
                s = 0,
                a = 0))
        }
        s > 0 && (this.dMultiply(Math.pow(e, s)),
            this.dAddOffset(a, 0)),
        n && m.ZERO.subTo(this, this)
    }
    ,
    m.prototype.fromNumber = function (t, e, r) {
        if ("number" == typeof e)
            if (t < 2)
                this.fromInt(1);
            else
                for (this.fromNumber(t, r),
                     this.testBit(t - 1) || this.bitwiseTo(m.ONE.shiftLeft(t - 1), R, this),
                     this.isEven() && this.dAddOffset(1, 0); !this.isProbablePrime(e);)
                    this.dAddOffset(2, 0),
                    this.bitLength() > t && this.subTo(m.ONE.shiftLeft(t - 1), this);
        else {
            var i = new Array
                , n = 7 & t;
            i.length = 1 + (t >> 3),
                e.nextBytes(i),
                n > 0 ? i[0] &= (1 << n) - 1 : i[0] = 0,
                this.fromString(i, 256)
        }
    }
    ,
    m.prototype.bitwiseTo = function (t, e, r) {
        var i, n, s = Math.min(t.t, this.t);
        for (i = 0; i < s; ++i)
            r[i] = e(this[i], t[i]);
        if (t.t < this.t) {
            for (n = t.s & this.DM,
                     i = s; i < this.t; ++i)
                r[i] = e(this[i], n);
            r.t = this.t
        } else {
            for (n = this.s & this.DM,
                     i = s; i < t.t; ++i)
                r[i] = e(n, t[i]);
            r.t = t.t
        }
        r.s = e(this.s, t.s),
            r.clamp()
    }
    ,
    m.prototype.changeBit = function (t, e) {
        var r = m.ONE.shiftLeft(t);
        return this.bitwiseTo(r, e, r),
            r
    }
    ,
    m.prototype.addTo = function (t, e) {
        for (var r = 0, i = 0, n = Math.min(t.t, this.t); r < n;)
            i += this[r] + t[r],
                e[r++] = i & this.DM,
                i >>= this.DB;
        if (t.t < this.t) {
            for (i += t.s; r < this.t;)
                i += this[r],
                    e[r++] = i & this.DM,
                    i >>= this.DB;
            i += this.s
        } else {
            for (i += this.s; r < t.t;)
                i += t[r],
                    e[r++] = i & this.DM,
                    i >>= this.DB;
            i += t.s
        }
        e.s = i < 0 ? -1 : 0,
            i > 0 ? e[r++] = i : i < -1 && (e[r++] = this.DV + i),
            e.t = r,
            e.clamp()
    }
    ,
    m.prototype.dMultiply = function (t) {
        this[this.t] = this.am(0, t - 1, this, 0, 0, this.t),
            ++this.t,
            this.clamp()
    }
    ,
    m.prototype.dAddOffset = function (t, e) {
        if (0 != t) {
            for (; this.t <= e;)
                this[this.t++] = 0;
            for (this[e] += t; this[e] >= this.DV;)
                this[e] -= this.DV,
                ++e >= this.t && (this[this.t++] = 0),
                    ++this[e]
        }
    }
    ,
    m.prototype.multiplyLowerTo = function (t, e, r) {
        var i, n = Math.min(this.t + t.t, e);
        for (r.s = 0,
                 r.t = n; n > 0;)
            r[--n] = 0;
        for (i = r.t - this.t; n < i; ++n)
            r[n + this.t] = this.am(0, t[n], r, n, 0, this.t);
        for (i = Math.min(t.t, e); n < i; ++n)
            this.am(0, t[n], r, n, 0, e - n);
        r.clamp()
    }
    ,
    m.prototype.multiplyUpperTo = function (t, e, r) {
        --e;
        var i = r.t = this.t + t.t - e;
        for (r.s = 0; --i >= 0;)
            r[i] = 0;
        for (i = Math.max(e - this.t, 0); i < t.t; ++i)
            r[this.t + i - e] = this.am(e - i, t[i], r, 0, 0, this.t + i - e);
        r.clamp(),
            r.drShiftTo(1, r)
    }
    ,
    m.prototype.modInt = function (t) {
        if (t <= 0)
            return 0;
        var e = this.DV % t
            , r = this.s < 0 ? t - 1 : 0;
        if (this.t > 0)
            if (0 == e)
                r = this[0] % t;
            else
                for (var i = this.t - 1; i >= 0; --i)
                    r = (e * r + this[i]) % t;
        return r
    }
    ,
    m.prototype.millerRabin = function (t) {
        var e = this.subtract(m.ONE)
            , r = e.getLowestSetBit();
        if (r <= 0)
            return !1;
        var i = e.shiftRight(r);
        (t = t + 1 >> 1) > K.length && (t = K.length);
        for (var n = y(), s = 0; s < t; ++s) {
            n.fromInt(K[Math.floor(Math.random() * K.length)]);
            var a = n.modPow(i, this);
            if (0 != a.compareTo(m.ONE) && 0 != a.compareTo(e)) {
                for (var o = 1; o++ < r && 0 != a.compareTo(e);)
                    if (0 == (a = a.modPowInt(2, this)).compareTo(m.ONE))
                        return !1;
                if (0 != a.compareTo(e))
                    return !1
            }
        }
        return !0
    }
    ,
    m.prototype.clone = /*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
        function () {
            var t = y();
            return this.copyTo(t),
                t
        }
    ,
    m.prototype.intValue = function () {
        if (this.s < 0) {
            if (1 == this.t)
                return this[0] - this.DV;
            if (0 == this.t)
                return -1
        } else {
            if (1 == this.t)
                return this[0];
            if (0 == this.t)
                return 0
        }
        return (this[1] & (1 << 32 - this.DB) - 1) << this.DB | this[0]
    }
    ,
    m.prototype.byteValue = function () {
        return 0 == this.t ? this.s : this[0] << 24 >> 24
    }
    ,
    m.prototype.shortValue = function () {
        return 0 == this.t ? this.s : this[0] << 16 >> 16
    }
    ,
    m.prototype.signum = function () {
        return this.s < 0 ? -1 : this.t <= 0 || 1 == this.t && this[0] <= 0 ? 0 : 1
    }
    ,
    m.prototype.toByteArray = function () {
        var t = this.t
            , e = new Array;
        e[0] = this.s;
        var r, i = this.DB - t * this.DB % 8, n = 0;
        if (t-- > 0)
            for (i < this.DB && (r = this[t] >> i) != (this.s & this.DM) >> i && (e[n++] = r | this.s << this.DB - i); t >= 0;)
                i < 8 ? (r = (this[t] & (1 << i) - 1) << 8 - i,
                    r |= this[--t] >> (i += this.DB - 8)) : (r = this[t] >> (i -= 8) & 255,
                i <= 0 && (i += this.DB,
                    --t)),
                128 & r && (r |= -256),
                0 == n && (128 & this.s) != (128 & r) && ++n,
                (n > 0 || r != this.s) && (e[n++] = r);
        return e
    }
    ,
    m.prototype.equals = function (t) {
        return 0 == this.compareTo(t)
    }
    ,
    m.prototype.min = function (t) {
        return this.compareTo(t) < 0 ? this : t
    }
    ,
    m.prototype.max = function (t) {
        return this.compareTo(t) > 0 ? this : t
    }
    ,
    m.prototype.and = function (t) {
        var e = y();
        return this.bitwiseTo(t, C, e),
            e
    }
    ,
    m.prototype.or = function (t) {
        var e = y();
        return this.bitwiseTo(t, R, e),
            e
    }
    ,
    m.prototype.xor = function (t) {
        var e = y();
        return this.bitwiseTo(t, T, e),
            e
    }
    ,
    m.prototype.andNot = function (t) {
        var e = y();
        return this.bitwiseTo(t, N, e),
            e
    }
    ,
    m.prototype.not = function () {
        for (var t = y(), e = 0; e < this.t; ++e)
            t[e] = this.DM & ~this[e];
        return t.t = this.t,
            t.s = ~this.s,
            t
    }
    ,
    m.prototype.shiftLeft = function (t) {
        var e = y();
        return t < 0 ? this.rShiftTo(-t, e) : this.lShiftTo(t, e),
            e
    }
    ,
    m.prototype.shiftRight = function (t) {
        var e = y();
        return t < 0 ? this.lShiftTo(-t, e) : this.rShiftTo(t, e),
            e
    }
    ,
    m.prototype.getLowestSetBit = function () {
        for (var t = 0; t < this.t; ++t)
            if (0 != this[t])
                return t * this.DB + B(this[t]);
        return this.s < 0 ? this.t * this.DB : -1
    }
    ,
    m.prototype.bitCount = function () {
        for (var t = 0, e = this.s & this.DM, r = 0; r < this.t; ++r)
            t += H(this[r] ^ e);
        return t
    }
    ,
    m.prototype.testBit = function (t) {
        var e = Math.floor(t / this.DB);
        return e >= this.t ? 0 != this.s : !!(this[e] & 1 << t % this.DB)
    }
    ,
    m.prototype.setBit = function (t) {
        return this.changeBit(t, R)
    }
    ,
    m.prototype.clearBit = function (t) {
        return this.changeBit(t, N)
    }
    ,
    m.prototype.flipBit = function (t) {
        return this.changeBit(t, T)
    }
    ,
    m.prototype.add = function (t) {
        var e = y();
        return this.addTo(t, e),
            e
    }
    ,
    m.prototype.subtract = function (t) {
        var e = y();
        return this.subTo(t, e),
            e
    }
    ,
    m.prototype.multiply = function (t) {
        var e = y();
        return this.multiplyTo(t, e),
            e
    }
    ,
    m.prototype.divide = function (t) {
        var e = y();
        return this.divRemTo(t, e, null),
            e
    }
    ,
    m.prototype.remainder = function (t) {
        var e = y();
        return this.divRemTo(t, null, e),
            e
    }
    ,
    m.prototype.divideAndRemainder = function (t) {
        var e = y()
            , r = y();
        return this.divRemTo(t, e, r),
            new Array(e, r)
    }
    ,
    m.prototype.modPow = function (t, e) {
        var r, i, n = t.bitLength(), s = A(1);
        if (n <= 0)
            return s;
        r = n < 18 ? 1 : n < 48 ? 3 : n < 144 ? 4 : n < 768 ? 5 : 6,
            i = n < 8 ? new I(e) : e.isEven() ? new V(e) : new P(e);
        var a = new Array
            , o = 3
            , h = r - 1
            , u = (1 << r) - 1;
        if (a[1] = i.convert(this),
        r > 1) {
            var c = y();
            for (i.sqrTo(a[1], c); o <= u;)
                a[o] = y(),
                    i.mulTo(c, a[o - 2], a[o]),
                    o += 2
        }
        var l, f, g = t.t - 1, p = !0, d = y();
        for (n = D(t[g]) - 1; g >= 0;) {
            for (n >= h ? l = t[g] >> n - h & u : (l = (t[g] & (1 << n + 1) - 1) << h - n,
            g > 0 && (l |= t[g - 1] >> this.DB + n - h)),
                     o = r; !(1 & l);)
                l >>= 1,
                    --o;
            if ((n -= o) < 0 && (n += this.DB,
                --g),
                p)
                a[l].copyTo(s),
                    p = !1;
            else {
                for (; o > 1;)
                    i.sqrTo(s, d),
                        i.sqrTo(d, s),
                        o -= 2;
                o > 0 ? i.sqrTo(s, d) : (f = s,
                    s = d,
                    d = f),
                    i.mulTo(d, a[l], s)
            }
            for (; g >= 0 && !(t[g] & 1 << n);)
                i.sqrTo(s, d),
                    f = s,
                    s = d,
                    d = f,
                --n < 0 && (n = this.DB - 1,
                    --g)
        }
        return i.revert(s)
    }
    ,
    m.prototype.modInverse = function (t) {
        var e = t.isEven();
        if (this.isEven() && e || 0 == t.signum())
            return m.ZERO;
        for (var r = t.clone(), i = this.clone(), n = A(1), s = A(0), a = A(0), o = A(1); 0 != r.signum();) {
            for (; r.isEven();)
                r.rShiftTo(1, r),
                    e ? (n.isEven() && s.isEven() || (n.addTo(this, n),
                        s.subTo(t, s)),
                        n.rShiftTo(1, n)) : s.isEven() || s.subTo(t, s),
                    s.rShiftTo(1, s);
            for (; i.isEven();)
                i.rShiftTo(1, i),
                    e ? (a.isEven() && o.isEven() || (a.addTo(this, a),
                        o.subTo(t, o)),
                        a.rShiftTo(1, a)) : o.isEven() || o.subTo(t, o),
                    o.rShiftTo(1, o);
            r.compareTo(i) >= 0 ? (r.subTo(i, r),
            e && n.subTo(a, n),
                s.subTo(o, s)) : (i.subTo(r, i),
            e && a.subTo(n, a),
                o.subTo(s, o))
        }
        return 0 != i.compareTo(m.ONE) ? m.ZERO : o.compareTo(t) >= 0 ? o.subtract(t) : o.signum() < 0 ? (o.addTo(t, o),
            o.signum() < 0 ? o.add(t) : o) : o
    }
    ,
    m.prototype.pow = function (t) {
        return this.exp(t, new O)
    }
    ,
    m.prototype.gcd = function (t) {
        var e = this.s < 0 ? this.negate() : this.clone()
            , r = t.s < 0 ? t.negate() : t.clone();
        if (e.compareTo(r) < 0) {
            var i = e;
            e = r,
                r = i
        }
        var n = e.getLowestSetBit()
            , s = r.getLowestSetBit();
        if (s < 0)
            return e;
        for (n < s && (s = n),
             s > 0 && (e.rShiftTo(s, e),
                 r.rShiftTo(s, r)); e.signum() > 0;)
            (n = e.getLowestSetBit()) > 0 && e.rShiftTo(n, e),
            (n = r.getLowestSetBit()) > 0 && r.rShiftTo(n, r),
                e.compareTo(r) >= 0 ? (e.subTo(r, e),
                    e.rShiftTo(1, e)) : (r.subTo(e, r),
                    r.rShiftTo(1, r));
        return s > 0 && r.lShiftTo(s, r),
            r
    }
    ,
    m.prototype.isProbablePrime = function (t) {
        var e, r = this.abs();
        if (1 == r.t && r[0] <= K[K.length - 1]) {
            for (e = 0; e < K.length; ++e)
                if (r[0] == K[e])
                    return !0;
            return !1
        }
        if (r.isEven())
            return !1;
        for (e = 1; e < K.length;) {
            for (var i = K[e], n = e + 1; n < K.length && i < L;)
                i *= K[n++];
            for (i = r.modInt(i); e < n;)
                if (i % K[e++] == 0)
                    return !1
        }
        return r.millerRabin(t)
    }
    ,
    m.prototype.square = function () {
        var t = y();
        return this.squareTo(t),
            t
    }
    ,
    k.prototype.init = function (t) {
        var e, r, i;
        for (e = 0; e < 256; ++e)
            this.S[e] = e;
        for (r = 0,
                 e = 0; e < 256; ++e)
            r = r + this.S[e] + t[e % t.length] & 255,
                i = this.S[e],
                this.S[e] = this.S[r],
                this.S[r] = i;
        this.i = 0,
            this.j = 0
    }
    ,
    k.prototype.next = function () {
        var t;
        return this.i = this.i + 1 & 255,
            this.j = this.j + this.S[this.i] & 255,
            t = this.S[this.i],
            this.S[this.i] = this.S[this.j],
            this.S[this.j] = t,
            this.S[t + this.S[this.i] & 255]
    }
;
var q, M, _, U = 256;

/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
function G() {
    !function (t) {
        M[_++] ^= 255 & t,
            M[_++] ^= t >> 8 & 255,
            M[_++] ^= t >> 16 & 255,
            M[_++] ^= t >> 24 & 255,
        _ >= U && (_ -= U)
    }((new Date).getTime())
}

if (null == M) {
    var z;
    if (M = new Array,
        _ = 0,
    void 0 !== c && (void 0 !== c.crypto || void 0 !== c.msCrypto)) {
        var W = c.crypto || c.msCrypto;
        if (W.getRandomValues) {
            var J = new Uint8Array(32);
            for (W.getRandomValues(J),
                     z = 0; z < 32; ++z)
                M[_++] = J[z]
        } else if ("Netscape" == u.appName && u.appVersion < "5") {
            var X = c.crypto.random(32);
            for (z = 0; z < X.length; ++z)
                M[_++] = 255 & X.charCodeAt(z)
        }
    }
    for (; _ < U;)
        z = Math.floor(65536 * Math.random()),
            M[_++] = z >>> 8,
            M[_++] = 255 & z;
    _ = 0,
        G()
}

function $() {
    if (null == q) {
        for (G(),
                 (q = new k).init(M),
                 _ = 0; _ < M.length; ++_)
            M[_] = 0;
        _ = 0
    }
    return q.next()
}

function Y() {
}

/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
function Z(t, e) {
    return new m(t, e)
}

function Q(t, e, r) {
    for (var i = "", n = 0; i.length < e;)
        i += r(String.fromCharCode.apply(String, t.concat([(4278190080 & n) >> 24, (16711680 & n) >> 16, (65280 & n) >> 8, 255 & n]))),
            n += 1;
    return i
}

function tt() {
    this.n = null,
        this.e = 0,
        this.d = null,
        this.p = null,
        this.q = null,
        this.dmp1 = null,
        this.dmq1 = null,
        this.coeff = null
}

function et(t, e, r) {
    for (var i = "", n = 0; i.length < e;)
        i += r(t + String.fromCharCode.apply(String, [(4278190080 & n) >> 24, (16711680 & n) >> 16, (65280 & n) >> 8, 255 & n])),
            n += 1;
    return i
}

/*! (c) Tom Wu | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
function rt(t, e) {
    this.x = e,
        this.q = t
}

function it(t, e, r, i) {
    this.curve = t,
        this.x = e,
        this.y = r,
        this.z = null == i ? m.ONE : i,
        this.zinv = null
}

function nt(t, e, r) {
    this.q = t,
        this.a = this.fromBigInteger(e),
        this.b = this.fromBigInteger(r),
        this.infinity = new it(this, null, null)
}

Y.prototype.nextBytes = function (t) {
    var e;
    for (e = 0; e < t.length; ++e)
        t[e] = $()
}
    ,
    tt.prototype.doPublic = function (t) {
        return t.modPowInt(this.e, this.n)
    }
    ,
    tt.prototype.setPublic = function (t, e) {
        if (this.isPublic = !0,
            this.isPrivate = !1,
        "string" != typeof t)
            this.n = t,
                this.e = e;
        else {
            if (!(null != t && null != e && t.length > 0 && e.length > 0))
                throw "Invalid RSA public key";
            this.n = Z(t, 16),
                this.e = parseInt(e, 16)
        }
    }
    ,
    tt.prototype.encrypt = function (t) {
        var e = function (t, e) {
            if (e < t.length + 11)
                throw "Message too long for RSA";
            for (var r = new Array, i = t.length - 1; i >= 0 && e > 0;) {
                var n = t.charCodeAt(i--);
                n < 128 ? r[--e] = n : n > 127 && n < 2048 ? (r[--e] = 63 & n | 128,
                    r[--e] = n >> 6 | 192) : (r[--e] = 63 & n | 128,
                    r[--e] = n >> 6 & 63 | 128,
                    r[--e] = n >> 12 | 224)
            }
            r[--e] = 0;
            for (var s = new Y, a = new Array; e > 2;) {
                for (a[0] = 0; 0 == a[0];)
                    s.nextBytes(a);
                r[--e] = a[0]
            }
            return r[--e] = 2,
                r[--e] = 0,
                new m(r)
        }(t, this.n.bitLength() + 7 >> 3);
        if (null == e)
            return null;
        var r = this.doPublic(e);
        if (null == r)
            return null;
        var i = r.toString(16);
        return 1 & i.length ? "0" + i : i
    }
    ,
    tt.prototype.encryptOAEP = function (t, e, r) {
        var i = this.n.bitLength() + 7 >> 3
            , n = function (t, e, r, i) {
            var n = at.crypto.MessageDigest
                , s = at.crypto.Util
                , a = null;
            if (r || (r = "sha1"),
            "string" == typeof r && (a = n.getCanonicalAlgName(r),
                    i = n.getHashLength(a),
                    r = function (t) {
                        return xt(s.hashHex(St(t), a))
                    }
            ),
            t.length + 2 * i + 2 > e)
                throw "Message too long for RSA";
            var o, h = "";
            for (o = 0; o < e - t.length - 2 * i - 2; o += 1)
                h += "\0";
            var u = r("") + h + "" + t
                , c = new Array(i);
            (new Y).nextBytes(c);
            var l = Q(c, u.length, r)
                , f = [];
            for (o = 0; o < u.length; o += 1)
                f[o] = u.charCodeAt(o) ^ l.charCodeAt(o);
            var g = Q(f, c.length, r)
                , p = [0];
            for (o = 0; o < c.length; o += 1)
                p[o + 1] = c[o] ^ g.charCodeAt(o);
            return new m(p.concat(f))
        }(t, i, e, r);
        if (null == n)
            return null;
        var s = this.doPublic(n);
        if (null == s)
            return null;
        for (var a = s.toString(16); a.length < 2 * i;)
            a = "0" + a;
        return a
    }
    ,
    tt.prototype.type = "RSA",
    tt.prototype.doPrivate = function (t) {
        if (null == this.p || null == this.q)
            return t.modPow(this.d, this.n);
        for (var e = t.mod(this.p).modPow(this.dmp1, this.p), r = t.mod(this.q).modPow(this.dmq1, this.q); e.compareTo(r) < 0;)
            e = e.add(this.p);
        return e.subtract(r).multiply(this.coeff).mod(this.p).multiply(this.q).add(r)
    }
    ,
    tt.prototype.setPrivate = function (t, e, r) {
        if (this.isPrivate = !0,
        "string" != typeof t)
            this.n = t,
                this.e = e,
                this.d = r;
        else {
            if (!(null != t && null != e && t.length > 0 && e.length > 0))
                throw "Invalid RSA private key";
            this.n = Z(t, 16),
                this.e = parseInt(e, 16),
                this.d = Z(r, 16)
        }
    }
    ,
    tt.prototype.setPrivateEx = function (t, e, r, i, n, s, a, o) {
        if (this.isPrivate = !0,
            this.isPublic = !1,
        null == t)
            throw "RSASetPrivateEx N == null";
        if (null == e)
            throw "RSASetPrivateEx E == null";
        if (0 == t.length)
            throw "RSASetPrivateEx N.length == 0";
        if (0 == e.length)
            throw "RSASetPrivateEx E.length == 0";
        if (!(null != t && null != e && t.length > 0 && e.length > 0))
            throw "Invalid RSA private key in RSASetPrivateEx";
        this.n = Z(t, 16),
            this.e = parseInt(e, 16),
            this.d = Z(r, 16),
            this.p = Z(i, 16),
            this.q = Z(n, 16),
            this.dmp1 = Z(s, 16),
            this.dmq1 = Z(a, 16),
            this.coeff = Z(o, 16)
    }
    ,
    tt.prototype.generate = function (t, e) {
        var r = new Y
            , i = t >> 1;
        this.e = parseInt(e, 16);
        for (var n = new m(e, 16), s = t / 2 - 100, a = m.ONE.shiftLeft(s); ;) {
            for (; this.p = new m(t - i, 1, r),
                   0 != this.p.subtract(m.ONE).gcd(n).compareTo(m.ONE) || !this.p.isProbablePrime(10);)
                ;
            for (; this.q = new m(i, 1, r),
                   0 != this.q.subtract(m.ONE).gcd(n).compareTo(m.ONE) || !this.q.isProbablePrime(10);)
                ;
            if (this.p.compareTo(this.q) <= 0) {
                var o = this.p;
                this.p = this.q,
                    this.q = o
            }
            var h = this.q.subtract(this.p).abs();
            if (!(h.bitLength() < s || h.compareTo(a) <= 0)) {
                var u = this.p.subtract(m.ONE)
                    , c = this.q.subtract(m.ONE)
                    , l = u.multiply(c);
                if (0 == l.gcd(n).compareTo(m.ONE) && (this.n = this.p.multiply(this.q),
                this.n.bitLength() == t)) {
                    this.d = n.modInverse(l),
                        this.dmp1 = this.d.mod(u),
                        this.dmq1 = this.d.mod(c),
                        this.coeff = this.q.modInverse(this.p);
                    break
                }
            }
        }
        this.isPrivate = !0
    }
    ,
    tt.prototype.decrypt = function (t) {
        if (t.length != Math.ceil(this.n.bitLength() / 4))
            throw new Error("wrong ctext length");
        var e = Z(t, 16)
            , r = this.doPrivate(e);
        return null == r ? null : /*! (c) Tom Wu, Kenji Urushima | http://www-cs-students.stanford.edu/~tjw/jsbn/
 */
            function (t, e) {
                for (var r = t.toByteArray(), i = 0; i < r.length && 0 == r[i];)
                    ++i;
                if (r.length - i != e - 1 || 2 != r[i])
                    return null;
                for (++i; 0 != r[i];)
                    if (++i >= r.length)
                        return null;
                for (var n = ""; ++i < r.length;) {
                    var s = 255 & r[i];
                    s < 128 ? n += String.fromCharCode(s) : s > 191 && s < 224 ? (n += String.fromCharCode((31 & s) << 6 | 63 & r[i + 1]),
                        ++i) : (n += String.fromCharCode((15 & s) << 12 | (63 & r[i + 1]) << 6 | 63 & r[i + 2]),
                        i += 2)
                }
                return n
            }(r, this.n.bitLength() + 7 >> 3)
    }
    ,
    tt.prototype.decryptOAEP = function (t, e, r) {
        if (t.length != Math.ceil(this.n.bitLength() / 4))
            throw new Error("wrong ctext length");
        var i = Z(t, 16)
            , n = this.doPrivate(i);
        return null == n ? null : function (t, e, r, i) {
            var n = at.crypto.MessageDigest
                , s = at.crypto.Util
                , a = null;
            for (r || (r = "sha1"),
                 "string" == typeof r && (a = n.getCanonicalAlgName(r),
                         i = n.getHashLength(a),
                         r = function (t) {
                             return xt(s.hashHex(St(t), a))
                         }
                 ),
                     t = t.toByteArray(),
                     o = 0; o < t.length; o += 1)
                t[o] &= 255;
            for (; t.length < e;)
                t.unshift(0);
            if ((t = String.fromCharCode.apply(String, t)).length < 2 * i + 2)
                throw "Cipher too short";
            var o, h = t.substr(1, i), u = t.substr(i + 1), c = et(u, i, r), l = [];
            for (o = 0; o < h.length; o += 1)
                l[o] = h.charCodeAt(o) ^ c.charCodeAt(o);
            var f = et(String.fromCharCode.apply(String, l), t.length - i, r)
                , g = [];
            for (o = 0; o < u.length; o += 1)
                g[o] = u.charCodeAt(o) ^ f.charCodeAt(o);
            if ((g = String.fromCharCode.apply(String, g)).substr(0, i) !== r(""))
                throw "Hash mismatch";
            var p = (g = g.substr(i)).indexOf("");
            if ((-1 != p ? g.substr(0, p).lastIndexOf("\0") : -1) + 1 != p)
                throw "Malformed data";
            return g.substr(p + 1)
        }(n, this.n.bitLength() + 7 >> 3, e, r)
    }
    ,
    rt.prototype.equals = function (t) {
        return t == this || this.q.equals(t.q) && this.x.equals(t.x)
    }
    ,
    rt.prototype.toBigInteger = function () {
        return this.x
    }
    ,
    rt.prototype.negate = function () {
        return new rt(this.q, this.x.negate().mod(this.q))
    }
    ,
    rt.prototype.add = function (t) {
        return new rt(this.q, this.x.add(t.toBigInteger()).mod(this.q))
    }
    ,
    rt.prototype.subtract = function (t) {
        return new rt(this.q, this.x.subtract(t.toBigInteger()).mod(this.q))
    }
    ,
    rt.prototype.multiply = function (t) {
        return new rt(this.q, this.x.multiply(t.toBigInteger()).mod(this.q))
    }
    ,
    rt.prototype.square = function () {
        return new rt(this.q, this.x.square().mod(this.q))
    }
    ,
    rt.prototype.divide = function (t) {
        return new rt(this.q, this.x.multiply(t.toBigInteger().modInverse(this.q)).mod(this.q))
    }
    ,
    rt.prototype.sqrt = function () {
        return new rt(this.q, this.x.sqrt().mod(this.q))
    }
    ,
    it.prototype.getX = function () {
        return null == this.zinv && (this.zinv = this.z.modInverse(this.curve.q)),
            this.curve.fromBigInteger(this.x.toBigInteger().multiply(this.zinv).mod(this.curve.q))
    }
    ,
    it.prototype.getY = function () {
        return null == this.zinv && (this.zinv = this.z.modInverse(this.curve.q)),
            this.curve.fromBigInteger(this.y.toBigInteger().multiply(this.zinv).mod(this.curve.q))
    }
    ,
    it.prototype.equals = function (t) {
        return t == this || (this.isInfinity() ? t.isInfinity() : t.isInfinity() ? this.isInfinity() : !!t.y.toBigInteger().multiply(this.z).subtract(this.y.toBigInteger().multiply(t.z)).mod(this.curve.q).equals(m.ZERO) && t.x.toBigInteger().multiply(this.z).subtract(this.x.toBigInteger().multiply(t.z)).mod(this.curve.q).equals(m.ZERO))
    }
    ,
    it.prototype.isInfinity = function () {
        return null == this.x && null == this.y || this.z.equals(m.ZERO) && !this.y.toBigInteger().equals(m.ZERO)
    }
    ,
    it.prototype.negate = function () {
        return new it(this.curve, this.x, this.y.negate(), this.z)
    }
    ,
    it.prototype.add = function (t) {
        if (this.isInfinity())
            return t;
        if (t.isInfinity())
            return this;
        var e = t.y.toBigInteger().multiply(this.z).subtract(this.y.toBigInteger().multiply(t.z)).mod(this.curve.q)
            , r = t.x.toBigInteger().multiply(this.z).subtract(this.x.toBigInteger().multiply(t.z)).mod(this.curve.q);
        if (m.ZERO.equals(r))
            return m.ZERO.equals(e) ? this.twice() : this.curve.getInfinity();
        var i = new m("3")
            , n = this.x.toBigInteger()
            , s = this.y.toBigInteger();
        t.x.toBigInteger(),
            t.y.toBigInteger();
        var a = r.square()
            , o = a.multiply(r)
            , h = n.multiply(a)
            , u = e.square().multiply(this.z)
            , c = u.subtract(h.shiftLeft(1)).multiply(t.z).subtract(o).multiply(r).mod(this.curve.q)
            ,
            l = h.multiply(i).multiply(e).subtract(s.multiply(o)).subtract(u.multiply(e)).multiply(t.z).add(e.multiply(o)).mod(this.curve.q)
            , f = o.multiply(this.z).multiply(t.z).mod(this.curve.q);
        return new it(this.curve, this.curve.fromBigInteger(c), this.curve.fromBigInteger(l), f)
    }
    ,
    it.prototype.twice = function () {
        if (this.isInfinity())
            return this;
        if (0 == this.y.toBigInteger().signum())
            return this.curve.getInfinity();
        var t = new m("3")
            , e = this.x.toBigInteger()
            , r = this.y.toBigInteger()
            , i = r.multiply(this.z)
            , n = i.multiply(r).mod(this.curve.q)
            , s = this.curve.a.toBigInteger()
            , a = e.square().multiply(t);
        m.ZERO.equals(s) || (a = a.add(this.z.square().multiply(s)));
        var o = (a = a.mod(this.curve.q)).square().subtract(e.shiftLeft(3).multiply(n)).shiftLeft(1).multiply(i).mod(this.curve.q)
            ,
            h = a.multiply(t).multiply(e).subtract(n.shiftLeft(1)).shiftLeft(2).multiply(n).subtract(a.square().multiply(a)).mod(this.curve.q)
            , u = i.square().multiply(i).shiftLeft(3).mod(this.curve.q);
        return new it(this.curve, this.curve.fromBigInteger(o), this.curve.fromBigInteger(h), u)
    }
    ,
    it.prototype.multiply = function (t) {
        if (this.isInfinity())
            return this;
        if (0 == t.signum())
            return this.curve.getInfinity();
        var e, r = t, i = r.multiply(new m("3")), n = this.negate(), s = this, a = this.curve.q.subtract(t),
            o = a.multiply(new m("3")), h = new it(this.curve, this.x, this.y), u = h.negate();
        for (e = i.bitLength() - 2; e > 0; --e) {
            s = s.twice();
            var c = i.testBit(e);
            c != r.testBit(e) && (s = s.add(c ? this : n))
        }
        for (e = o.bitLength() - 2; e > 0; --e) {
            h = h.twice();
            var l = o.testBit(e);
            l != a.testBit(e) && (h = h.add(l ? h : u))
        }
        return s
    }
    ,
    it.prototype.multiplyTwo = function (t, e, r) {
        var i;
        i = t.bitLength() > r.bitLength() ? t.bitLength() - 1 : r.bitLength() - 1;
        for (var n = this.curve.getInfinity(), s = this.add(e); i >= 0;)
            n = n.twice(),
                t.testBit(i) ? n = r.testBit(i) ? n.add(s) : n.add(this) : r.testBit(i) && (n = n.add(e)),
                --i;
        return n
    }
    ,
    nt.prototype.getQ = function () {
        return this.q
    }
    ,
    nt.prototype.getA = function () {
        return this.a
    }
    ,
    nt.prototype.getB = function () {
        return this.b
    }
    ,
    nt.prototype.equals = function (t) {
        return t == this || this.q.equals(t.q) && this.a.equals(t.a) && this.b.equals(t.b)
    }
    ,
    nt.prototype.getInfinity = function () {
        return this.infinity
    }
    ,
    nt.prototype.fromBigInteger = function (t) {
        return new rt(this.q, t)
    }
    ,
    nt.prototype.decodePointHex = function (t) {
        switch (parseInt(t.substr(0, 2), 16)) {
            case 0:
                return this.infinity;
            case 2:
            case 3:
                var e = t.substr(0, 2);
                t.substr(2);
                var r = this.fromBigInteger(new m(o, 16))
                    , i = this.getA()
                    , n = this.getB()
                    , s = r.square().add(i).multiply(r).add(n).sqrt();
                return "03" == e && (s = s.negate()),
                    new it(this, r, s);
            case 4:
            case 6:
            case 7:
                var a = (t.length - 2) / 2
                    , o = t.substr(2, a)
                    , h = t.substr(a + 2, a);
                return new it(this, this.fromBigInteger(new m(o, 16)), this.fromBigInteger(new m(h, 16)));
            default:
                return null
        }
    }
    ,
    /*! (c) Stefan Thomas | https://github.com/bitcoinjs/bitcoinjs-lib
 */
    rt.prototype.getByteLength = function () {
        return Math.floor((this.toBigInteger().bitLength() + 7) / 8)
    }
    ,
    it.prototype.getEncoded = function (t) {
        var e = function (t, e) {
            var r = t.toByteArrayUnsigned();
            if (e < r.length)
                r = r.slice(r.length - e);
            else
                for (; e > r.length;)
                    r.unshift(0);
            return r
        }
            , r = this.getX().toBigInteger()
            , i = this.getY().toBigInteger()
            , n = e(r, 32);
        return t ? i.isEven() ? n.unshift(2) : n.unshift(3) : (n.unshift(4),
            n = n.concat(e(i, 32))),
            n
    }
    ,
    it.decodeFrom = function (t, e) {
        e[0];
        var r = e.length - 1
            , i = e.slice(1, 1 + r / 2)
            , n = e.slice(1 + r / 2, 1 + r);
        i.unshift(0),
            n.unshift(0);
        var s = new m(i)
            , a = new m(n);
        return new it(t, t.fromBigInteger(s), t.fromBigInteger(a))
    }
    ,
    it.decodeFromHex = function (t, e) {
        e.substr(0, 2);
        var r = e.length - 2
            , i = e.substr(2, r / 2)
            , n = e.substr(2 + r / 2, r / 2)
            , s = new m(i, 16)
            , a = new m(n, 16);
        return new it(t, t.fromBigInteger(s), t.fromBigInteger(a))
    }
    ,
    it.prototype.add2D = function (t) {
        if (this.isInfinity())
            return t;
        if (t.isInfinity())
            return this;
        if (this.x.equals(t.x))
            return this.y.equals(t.y) ? this.twice() : this.curve.getInfinity();
        var e = t.x.subtract(this.x)
            , r = t.y.subtract(this.y).divide(e)
            , i = r.square().subtract(this.x).subtract(t.x)
            , n = r.multiply(this.x.subtract(i)).subtract(this.y);
        return new it(this.curve, i, n)
    }
    ,
    it.prototype.twice2D = function () {
        if (this.isInfinity())
            return this;
        if (0 == this.y.toBigInteger().signum())
            return this.curve.getInfinity();
        var t = this.curve.fromBigInteger(m.valueOf(2))
            , e = this.curve.fromBigInteger(m.valueOf(3))
            , r = this.x.square().multiply(e).add(this.curve.a).divide(this.y.multiply(t))
            , i = r.square().subtract(this.x.multiply(t))
            , n = r.multiply(this.x.subtract(i)).subtract(this.y);
        return new it(this.curve, i, n)
    }
    ,
    it.prototype.multiply2D = function (t) {
        if (this.isInfinity())
            return this;
        if (0 == t.signum())
            return this.curve.getInfinity();
        var e, r = t, i = r.multiply(new m("3")), n = this.negate(), s = this;
        for (e = i.bitLength() - 2; e > 0; --e) {
            s = s.twice();
            var a = i.testBit(e);
            a != r.testBit(e) && (s = s.add2D(a ? this : n))
        }
        return s
    }
    ,
    it.prototype.isOnCurve = function () {
        var t = this.getX().toBigInteger()
            , e = this.getY().toBigInteger()
            , r = this.curve.getA().toBigInteger()
            , i = this.curve.getB().toBigInteger()
            , n = this.curve.getQ()
            , s = e.multiply(e).mod(n)
            , a = t.multiply(t).multiply(t).add(r.multiply(t)).add(i).mod(n);
        return s.equals(a)
    }
    ,
    it.prototype.toString = function () {
        return "(" + this.getX().toBigInteger().toString() + "," + this.getY().toBigInteger().toString() + ")"
    }
    ,
    it.prototype.validate = function () {
        var t = this.curve.getQ();
        if (this.isInfinity())
            throw new Error("Point is at infinity.");
        var e = this.getX().toBigInteger()
            , r = this.getY().toBigInteger();
        if (e.compareTo(m.ONE) < 0 || e.compareTo(t.subtract(m.ONE)) > 0)
            throw new Error("x coordinate out of bounds");
        if (r.compareTo(m.ONE) < 0 || r.compareTo(t.subtract(m.ONE)) > 0)
            throw new Error("y coordinate out of bounds");
        if (!this.isOnCurve())
            throw new Error("Point is not on the curve.");
        if (this.multiply(t).isInfinity())
            throw new Error("Point is not a scalar multiple of G.");
        return !0
    }
;
/*! Mike Samuel (c) 2009 | code.google.com/p/json-sans-eval
 */
var st = function () {
    var t = new RegExp('(?:false|true|null|[\\{\\}\\[\\]]|(?:-?\\b(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?\\b)|(?:"(?:[^\\0-\\x08\\x0a-\\x1f"\\\\]|\\\\(?:["/\\\\bfnrt]|u[0-9A-Fa-f]{4}))*"))', "g")
        , e = new RegExp("\\\\(?:([^u])|u(.{4}))", "g")
        , r = {
        '"': '"',
        "/": "/",
        "\\": "\\",
        b: "\b",
        f: "\f",
        n: "\n",
        r: "\r",
        t: "\t"
    };

    function i(t, e, i) {
        return e ? r[e] : String.fromCharCode(parseInt(i, 16))
    }

    var n = new String("")
        , s = Object.hasOwnProperty;
    return function (r, a) {
        var o, h, u = r.match(t), c = u[0], l = !1;
        "{" === c ? o = {} : "[" === c ? o = [] : (o = [],
            l = !0);
        for (var f = [o], g = 1 - l, p = u.length; g < p; ++g) {
            var d;
            switch ((c = u[g]).charCodeAt(0)) {
                default:
                    (d = f[0])[h || d.length] = +c,
                        h = void 0;
                    break;
                case 34:
                    if (-1 !== (c = c.substring(1, c.length - 1)).indexOf("\\") && (c = c.replace(e, i)),
                        d = f[0],
                        !h) {
                        if (!(d instanceof Array)) {
                            h = c || n;
                            break
                        }
                        h = d.length
                    }
                    d[h] = c,
                        h = void 0;
                    break;
                case 91:
                    d = f[0],
                        f.unshift(d[h || d.length] = []),
                        h = void 0;
                    break;
                case 93:
                case 125:
                    f.shift();
                    break;
                case 102:
                    (d = f[0])[h || d.length] = !1,
                        h = void 0;
                    break;
                case 110:
                    (d = f[0])[h || d.length] = null,
                        h = void 0;
                    break;
                case 116:
                    (d = f[0])[h || d.length] = !0,
                        h = void 0;
                    break;
                case 123:
                    d = f[0],
                        f.unshift(d[h || d.length] = {}),
                        h = void 0
            }
        }
        if (l) {
            if (1 !== f.length)
                throw new Error;
            o = o[0]
        } else if (f.length)
            throw new Error;
        if (a) {
            var v = function (t, e) {
                var r = t[e];
                if (r && "object" == typeof r) {
                    var i = null;
                    for (var n in r)
                        if (s.call(r, n) && r !== t) {
                            var o = v(r, n);
                            void 0 !== o ? r[n] = o : (i || (i = []),
                                i.push(n))
                        }
                    if (i)
                        for (var h = i.length; --h >= 0;)
                            delete r[i[h]]
                }
                return a.call(t, e, r)
            };
            o = v({
                "": o
            }, "")
        }
        return o
    }
}();
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
    at.asn1.ASN1Util = new function () {
        this.integerToByteHex = function (t) {
            var e = t.toString(16);
            return e.length % 2 == 1 && (e = "0" + e),
                e
        }
            ,
            this.bigIntToMinTwosComplementsHex = function (t) {
                var e = t.toString(16);
                if ("-" != e.substr(0, 1))
                    e.length % 2 == 1 ? e = "0" + e : e.match(/^[0-7]/) || (e = "00" + e);
                else {
                    var r = e.substr(1).length;
                    r % 2 == 1 ? r += 1 : e.match(/^[0-7]/) || (r += 2);
                    for (var i = "", n = 0; n < r; n++)
                        i += "f";
                    e = new m(i, 16).xor(t).add(m.ONE).toString(16).replace(/^-/, "")
                }
                return e
            }
            ,
            this.getPEMStringFromHex = function (t, e) {
                return wt(t, e)
            }
            ,
            this.newObject = function (t) {
                var e = at.asn1
                    , r = e.ASN1Object
                    , i = e.DERBoolean
                    , n = e.DERInteger
                    , s = e.DERBitString
                    , a = e.DEROctetString
                    , o = e.DERNull
                    , h = e.DERObjectIdentifier
                    , u = e.DEREnumerated
                    , c = e.DERUTF8String
                    , l = e.DERNumericString
                    , f = e.DERPrintableString
                    , g = e.DERTeletexString
                    , p = e.DERIA5String
                    , d = e.DERUTCTime
                    , v = e.DERGeneralizedTime
                    , m = e.DERVisibleString
                    , y = e.DERBMPString
                    , x = e.DERSequence
                    , S = e.DERSet
                    , E = e.DERTaggedObject
                    , w = e.ASN1Util.newObject;
                if (t instanceof e.ASN1Object)
                    return t;
                var F = Object.keys(t);
                if (1 != F.length)
                    throw new Error("key of param shall be only one.");
                var b = F[0];
                if (-1 == ":asn1:bool:int:bitstr:octstr:null:oid:enum:utf8str:numstr:prnstr:telstr:ia5str:utctime:gentime:visstr:bmpstr:seq:set:tag:".indexOf(":" + b + ":"))
                    throw new Error("undefined key: " + b);
                if ("bool" == b)
                    return new i(t[b]);
                if ("int" == b)
                    return new n(t[b]);
                if ("bitstr" == b)
                    return new s(t[b]);
                if ("octstr" == b)
                    return new a(t[b]);
                if ("null" == b)
                    return new o(t[b]);
                if ("oid" == b)
                    return new h(t[b]);
                if ("enum" == b)
                    return new u(t[b]);
                if ("utf8str" == b)
                    return new c(t[b]);
                if ("numstr" == b)
                    return new l(t[b]);
                if ("prnstr" == b)
                    return new f(t[b]);
                if ("telstr" == b)
                    return new g(t[b]);
                if ("ia5str" == b)
                    return new p(t[b]);
                if ("utctime" == b)
                    return new d(t[b]);
                if ("gentime" == b)
                    return new v(t[b]);
                if ("visstr" == b)
                    return new m(t[b]);
                if ("bmpstr" == b)
                    return new y(t[b]);
                if ("asn1" == b)
                    return new r(t[b]);
                if ("seq" == b) {
                    for (var A = t[b], D = [], I = 0; I < A.length; I++) {
                        var P = w(A[I]);
                        D.push(P)
                    }
                    return new x({
                        array: D
                    })
                }
                if ("set" == b) {
                    for (A = t[b],
                             D = [],
                             I = 0; I < A.length; I++) {
                        P = w(A[I]);
                        D.push(P)
                    }
                    return new S({
                        array: D
                    })
                }
                if ("tag" == b) {
                    var C = t[b];
                    if ("[object Array]" === Object.prototype.toString.call(C) && 3 == C.length) {
                        var R = w(C[2]);
                        return new E({
                            tag: C[0],
                            explicit: C[1],
                            obj: R
                        })
                    }
                    return new E(C)
                }
            }
            ,
            this.jsonToASN1HEX = function (t) {
                return this.newObject(t).tohex()
            }
    }
    ,
    at.asn1.ASN1Util.oidHexToInt = function (t) {
        for (var e = "", r = parseInt(t.substr(0, 2), 16), i = (e = Math.floor(r / 40) + "." + r % 40,
            ""), n = 2; n < t.length; n += 2) {
            var s = ("00000000" + parseInt(t.substr(n, 2), 16).toString(2)).slice(-8);
            if (i += s.substr(1, 7),
            "0" == s.substr(0, 1))
                e = e + "." + new m(i, 2).toString(10),
                    i = ""
        }
        return e
    }
    ,
    at.asn1.ASN1Util.oidIntToHex = function (t) {
        var e = function (t) {
            var e = t.toString(16);
            return 1 == e.length && (e = "0" + e),
                e
        }
            , r = function (t) {
            var r = ""
                , i = new m(t, 10).toString(2)
                , n = 7 - i.length % 7;
            7 == n && (n = 0);
            for (var s = "", a = 0; a < n; a++)
                s += "0";
            i = s + i;
            for (a = 0; a < i.length - 1; a += 7) {
                var o = i.substr(a, 7);
                a != i.length - 7 && (o = "1" + o),
                    r += e(parseInt(o, 2))
            }
            return r
        };
        if (!t.match(/^[0-9.]+$/))
            throw "malformed oid string: " + t;
        var i = ""
            , n = t.split(".")
            , s = 40 * parseInt(n[0]) + parseInt(n[1]);
        i += e(s),
            n.splice(0, 2);
        for (var a = 0; a < n.length; a++)
            i += r(n[a]);
        return i
    }
    ,
    at.asn1.ASN1Object = function (t) {
        this.params = null,
            this.getLengthHexFromValue = function () {
                if (void 0 === this.hV || null == this.hV)
                    throw new Error("this.hV is null or undefined");
                if (this.hV.length % 2 == 1)
                    throw new Error("value hex must be even length: n=0,v=" + this.hV);
                var t = this.hV.length / 2
                    , e = t.toString(16);
                if (e.length % 2 == 1 && (e = "0" + e),
                t < 128)
                    return e;
                var r = e.length / 2;
                if (r > 15)
                    throw new Error("ASN.1 length too long to represent by 8x: n = " + t.toString(16));
                return (128 + r).toString(16) + e
            }
            ,
            this.tohex = function () {
                return (null == this.hTLV || this.isModified) && (this.hV = this.getFreshValueHex(),
                    this.hL = this.getLengthHexFromValue(),
                    this.hTLV = this.hT + this.hL + this.hV,
                    this.isModified = !1),
                    this.hTLV
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
            this.getValueHex = function () {
                return this.tohex(),
                    this.hV
            }
            ,
            this.getFreshValueHex = function () {
                return ""
            }
            ,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
        null != t && null != t.tlv && (this.hTLV = t.tlv,
            this.isModified = !1)
    }
    ,
    at.asn1.DERAbstractString = function (t) {
        at.asn1.DERAbstractString.superclass.constructor.call(this),
            this.getString = function () {
                return this.s
            }
            ,
            this.setString = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.s = t,
                    this.hV = vt(this.s).toLowerCase()
            }
            ,
            this.setStringHex = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.s = null,
                    this.hV = t
            }
            ,
            this.getFreshValueHex = function () {
                return this.hV
            }
            ,
        void 0 !== t && ("string" == typeof t ? this.setString(t) : void 0 !== t.str ? this.setString(t.str) : void 0 !== t.hex && this.setStringHex(t.hex))
    }
    ,
    Mt(at.asn1.DERAbstractString, at.asn1.ASN1Object),
    at.asn1.DERAbstractTime = function (t) {
        at.asn1.DERAbstractTime.superclass.constructor.call(this),
            this.localDateToUTC = function (t) {
                var e = t.getTime() + 6e4 * t.getTimezoneOffset();
                return new Date(e)
            }
            ,
            this.formatDate = function (t, e, r) {
                var i = this.zeroPadding
                    , n = this.localDateToUTC(t)
                    , s = String(n.getFullYear());
                "utc" == e && (s = s.substr(2, 2));
                var a = s + i(String(n.getMonth() + 1), 2) + i(String(n.getDate()), 2) + i(String(n.getHours()), 2) + i(String(n.getMinutes()), 2) + i(String(n.getSeconds()), 2);
                if (!0 === r) {
                    var o = n.getMilliseconds();
                    if (0 != o) {
                        var h = i(String(o), 3);
                        a = a + "." + (h = h.replace(/[0]+$/, ""))
                    }
                }
                return a + "Z"
            }
            ,
            this.zeroPadding = function (t, e) {
                return t.length >= e ? t : new Array(e - t.length + 1).join("0") + t
            }
            ,
            this.setByParam = function (t) {
                this.hV = null,
                    this.hTLV = null,
                    this.params = t
            }
            ,
            this.getString = function () {
            }
            ,
            this.setString = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                null == this.params && (this.params = {}),
                    this.params.str = t
            }
            ,
            this.setByDate = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                null == this.params && (this.params = {}),
                    this.params.date = t
            }
            ,
            this.setByDateValue = function (t, e, r, i, n, s) {
                var a = new Date(Date.UTC(t, e - 1, r, i, n, s, 0));
                this.setByDate(a)
            }
            ,
            this.getFreshValueHex = function () {
                return this.hV
            }
    }
    ,
    Mt(at.asn1.DERAbstractTime, at.asn1.ASN1Object),
    at.asn1.DERAbstractStructured = function (t) {
        at.asn1.DERAbstractString.superclass.constructor.call(this),
            this.setByASN1ObjectArray = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.asn1Array = t
            }
            ,
            this.appendASN1Object = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.asn1Array.push(t)
            }
            ,
            this.asn1Array = new Array,
        void 0 !== t && void 0 !== t.array && (this.asn1Array = t.array)
    }
    ,
    Mt(at.asn1.DERAbstractStructured, at.asn1.ASN1Object),
    at.asn1.DERBoolean = function (t) {
        at.asn1.DERBoolean.superclass.constructor.call(this),
            this.hT = "01",
            this.hTLV = 0 == t ? "010100" : "0101ff"
    }
    ,
    Mt(at.asn1.DERBoolean, at.asn1.ASN1Object),
    at.asn1.DERInteger = function (t) {
        at.asn1.DERInteger.superclass.constructor.call(this),
            this.hT = "02",
            this.params = null;
        var e = at.asn1.ASN1Util.bigIntToMinTwosComplementsHex;
        this.setByBigInteger = function (t) {
            this.isModified = !0,
                this.params = {
                    bigint: t
                }
        }
            ,
            this.setByInteger = function (t) {
                this.isModified = !0,
                    this.params = t
            }
            ,
            this.setValueHex = function (t) {
                this.isModified = !0,
                    this.params = {
                        hex: t
                    }
            }
            ,
            this.getFreshValueHex = function () {
                var t = this.params
                    , r = null;
                if (null == t)
                    throw new Error("value not set");
                if ("object" == typeof t && null != t.hex)
                    return this.hV = t.hex,
                        this.hV;
                if ("number" == typeof t)
                    r = new m(String(t), 10);
                else if (null != t.int)
                    r = new m(String(t.int), 10);
                else {
                    if (null == t.bigint)
                        throw new Error("wrong parameter");
                    r = t.bigint
                }
                return this.hV = e(r),
                    this.hV
            }
            ,
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.DERInteger, at.asn1.ASN1Object),
    at.asn1.DERBitString = function (t) {
        if (void 0 !== t && void 0 !== t.obj) {
            var e = at.asn1.ASN1Util.newObject(t.obj);
            t.hex = "00" + e.tohex()
        }
        at.asn1.DERBitString.superclass.constructor.call(this),
            this.hT = "03",
            this.setHexValueIncludingUnusedBits = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.hV = t
            }
            ,
            this.setUnusedBitsAndHexValue = function (t, e) {
                if (t < 0 || 7 < t)
                    throw "unused bits shall be from 0 to 7: u = " + t;
                var r = "0" + t;
                this.hTLV = null,
                    this.isModified = !0,
                    this.hV = r + e
            }
            ,
            this.setByBinaryString = function (t) {
                var e = 8 - (t = t.replace(/0+$/, "")).length % 8;
                8 == e && (e = 0),
                    t += "0000000".substr(0, e);
                for (var r = "", i = 0; i < t.length - 1; i += 8) {
                    var n = t.substr(i, 8)
                        , s = parseInt(n, 2).toString(16);
                    1 == s.length && (s = "0" + s),
                        r += s
                }
                this.hTLV = null,
                    this.isModified = !0,
                    this.hV = "0" + e + r
            }
            ,
            this.setByBooleanArray = function (t) {
                for (var e = "", r = 0; r < t.length; r++)
                    1 == t[r] ? e += "1" : e += "0";
                this.setByBinaryString(e)
            }
            ,
            this.newFalseArray = function (t) {
                for (var e = new Array(t), r = 0; r < t; r++)
                    e[r] = !1;
                return e
            }
            ,
            this.getFreshValueHex = function () {
                return this.hV
            }
            ,
        void 0 !== t && ("string" == typeof t && t.toLowerCase().match(/^[0-9a-f]+$/) ? this.setHexValueIncludingUnusedBits(t) : void 0 !== t.hex ? this.setHexValueIncludingUnusedBits(t.hex) : void 0 !== t.bin ? this.setByBinaryString(t.bin) : void 0 !== t.array && this.setByBooleanArray(t.array))
    }
    ,
    Mt(at.asn1.DERBitString, at.asn1.ASN1Object),
    at.asn1.DEROctetString = function (t) {
        if (void 0 !== t && void 0 !== t.obj) {
            var e = at.asn1.ASN1Util.newObject(t.obj);
            t.hex = e.tohex()
        }
        at.asn1.DEROctetString.superclass.constructor.call(this, t),
            this.hT = "04"
    }
    ,
    Mt(at.asn1.DEROctetString, at.asn1.DERAbstractString),
    at.asn1.DERNull = function () {
        at.asn1.DERNull.superclass.constructor.call(this),
            this.hT = "05",
            this.hTLV = "0500"
    }
    ,
    Mt(at.asn1.DERNull, at.asn1.ASN1Object),
    at.asn1.DERObjectIdentifier = function (t) {
        at.asn1.DERObjectIdentifier.superclass.constructor.call(this),
            this.hT = "06",
            this.setValueHex = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.s = null,
                    this.hV = t
            }
            ,
            this.setValueOidString = function (t) {
                var e = function (t) {
                    var r = function (t) {
                        var e = t.toString(16);
                        return 1 == e.length && (e = "0" + e),
                            e
                    }
                        , i = function (t) {
                        var e = ""
                            , i = parseInt(t, 10).toString(2)
                            , n = 7 - i.length % 7;
                        7 == n && (n = 0);
                        for (var s = "", a = 0; a < n; a++)
                            s += "0";
                        i = s + i;
                        for (a = 0; a < i.length - 1; a += 7) {
                            var o = i.substr(a, 7);
                            a != i.length - 7 && (o = "1" + o),
                                e += r(parseInt(o, 2))
                        }
                        return e
                    };
                    try {
                        if (!t.match(/^[0-9.]+$/))
                            return null;
                        var n = ""
                            , s = t.split(".")
                            , a = 40 * parseInt(s[0], 10) + parseInt(s[1], 10);
                        n += r(a),
                            s.splice(0, 2);
                        for (var o = 0; o < s.length; o++)
                            n += i(s[o]);
                        return n
                    } catch (e) {
                        return null
                    }
                }(t);
                if (null == e)
                    throw new Error("malformed oid string: " + t);
                this.hTLV = null,
                    this.isModified = !0,
                    this.s = null,
                    this.hV = e
            }
            ,
            this.setValueName = function (t) {
                var e = at.asn1.x509.OID.name2oid(t);
                if ("" === e)
                    throw new Error("DERObjectIdentifier oidName undefined: " + t);
                this.setValueOidString(e)
            }
            ,
            this.setValueNameOrOid = function (t) {
                t.match(/^[0-2].[0-9.]+$/) ? this.setValueOidString(t) : this.setValueName(t)
            }
            ,
            this.getFreshValueHex = function () {
                return this.hV
            }
            ,
            this.setByParam = function (t) {
                "string" == typeof t ? this.setValueNameOrOid(t) : void 0 !== t.oid ? this.setValueNameOrOid(t.oid) : void 0 !== t.name ? this.setValueNameOrOid(t.name) : void 0 !== t.hex && this.setValueHex(t.hex)
            }
            ,
        void 0 !== t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.DERObjectIdentifier, at.asn1.ASN1Object),
    at.asn1.DEREnumerated = function (t) {
        at.asn1.DEREnumerated.superclass.constructor.call(this),
            this.hT = "0a",
            this.setByBigInteger = function (t) {
                this.hTLV = null,
                    this.isModified = !0,
                    this.hV = at.asn1.ASN1Util.bigIntToMinTwosComplementsHex(t)
            }
            ,
            this.setByInteger = function (t) {
                var e = new m(String(t), 10);
                this.setByBigInteger(e)
            }
            ,
            this.setValueHex = function (t) {
                this.hV = t
            }
            ,
            this.getFreshValueHex = function () {
                return this.hV
            }
            ,
        void 0 !== t && (void 0 !== t.int ? this.setByInteger(t.int) : "number" == typeof t ? this.setByInteger(t) : void 0 !== t.hex && this.setValueHex(t.hex))
    }
    ,
    Mt(at.asn1.DEREnumerated, at.asn1.ASN1Object),
    at.asn1.DERUTF8String = function (t) {
        at.asn1.DERUTF8String.superclass.constructor.call(this, t),
            this.hT = "0c"
    }
    ,
    Mt(at.asn1.DERUTF8String, at.asn1.DERAbstractString),
    at.asn1.DERNumericString = function (t) {
        at.asn1.DERNumericString.superclass.constructor.call(this, t),
            this.hT = "12"
    }
    ,
    Mt(at.asn1.DERNumericString, at.asn1.DERAbstractString),
    at.asn1.DERPrintableString = function (t) {
        at.asn1.DERPrintableString.superclass.constructor.call(this, t),
            this.hT = "13"
    }
    ,
    Mt(at.asn1.DERPrintableString, at.asn1.DERAbstractString),
    at.asn1.DERTeletexString = function (t) {
        at.asn1.DERTeletexString.superclass.constructor.call(this, t),
            this.hT = "14"
    }
    ,
    Mt(at.asn1.DERTeletexString, at.asn1.DERAbstractString),
    at.asn1.DERIA5String = function (t) {
        at.asn1.DERIA5String.superclass.constructor.call(this, t),
            this.hT = "16"
    }
    ,
    Mt(at.asn1.DERIA5String, at.asn1.DERAbstractString),
    at.asn1.DERVisibleString = function (t) {
        at.asn1.DERIA5String.superclass.constructor.call(this, t),
            this.hT = "1a"
    }
    ,
    Mt(at.asn1.DERVisibleString, at.asn1.DERAbstractString),
    at.asn1.DERBMPString = function (t) {
        at.asn1.DERBMPString.superclass.constructor.call(this, t),
            this.hT = "1e"
    }
    ,
    Mt(at.asn1.DERBMPString, at.asn1.DERAbstractString),
    at.asn1.DERUTCTime = function (t) {
        at.asn1.DERUTCTime.superclass.constructor.call(this, t),
            this.hT = "17",
            this.params = void 0,
            this.getFreshValueHex = function () {
                var t = this.params;
                if (null == this.params && (t = {
                    date: new Date
                }),
                "string" == typeof t) {
                    if (!t.match(/^[0-9]{12}Z$/) && !t.match(/^[0-9]{12}\.[0-9]+Z$/))
                        throw new Error("malformed string for UTCTime: " + t);
                    this.hV = lt(t)
                } else if (null != t.str)
                    this.hV = lt(t.str);
                else if (null == t.date && 1 == t.millis) {
                    var e = new Date;
                    this.hV = lt(this.formatDate(e, "utc", !0))
                } else if (null != t.date && t.date instanceof Date) {
                    var r = !0 === t.millis;
                    this.hV = lt(this.formatDate(t.date, "utc", r))
                } else
                    t instanceof Date && (this.hV = lt(this.formatDate(t, "utc")));
                if (null == this.hV)
                    throw new Error("parameter not specified properly for UTCTime");
                return this.hV
            }
            ,
        null != t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.DERUTCTime, at.asn1.DERAbstractTime),
    at.asn1.DERGeneralizedTime = function (t) {
        at.asn1.DERGeneralizedTime.superclass.constructor.call(this, t),
            this.hT = "18",
            this.params = t,
            this.getFreshValueHex = function () {
                var t = this.params;
                if (null == this.params && (t = {
                    date: new Date
                }),
                "string" == typeof t) {
                    if (!t.match(/^[0-9]{14}Z$/) && !t.match(/^[0-9]{14}\.[0-9]+Z$/))
                        throw new Error("malformed string for GeneralizedTime: " + t);
                    this.hV = lt(t)
                } else if (null != t.str)
                    this.hV = lt(t.str);
                else if (null == t.date && 1 == t.millis) {
                    var e = new Date;
                    this.hV = lt(this.formatDate(e, "gen", !0))
                } else if (null != t.date && t.date instanceof Date) {
                    var r = !0 === t.millis;
                    this.hV = lt(this.formatDate(t.date, "gen", r))
                } else
                    t instanceof Date && (this.hV = lt(this.formatDate(t, "gen")));
                if (null == this.hV)
                    throw new Error("parameter not specified properly for GeneralizedTime");
                return this.hV
            }
            ,
        null != t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.DERGeneralizedTime, at.asn1.DERAbstractTime),
    at.asn1.DERSequence = function (t) {
        at.asn1.DERSequence.superclass.constructor.call(this, t),
            this.hT = "30",
            this.getFreshValueHex = function () {
                for (var t = "", e = 0; e < this.asn1Array.length; e++) {
                    t += this.asn1Array[e].tohex()
                }
                return this.hV = t,
                    this.hV
            }
    }
    ,
    Mt(at.asn1.DERSequence, at.asn1.DERAbstractStructured),
    at.asn1.DERSet = function (t) {
        at.asn1.DERSet.superclass.constructor.call(this, t),
            this.hT = "31",
            this.sortFlag = !0,
            this.getFreshValueHex = function () {
                for (var t = new Array, e = 0; e < this.asn1Array.length; e++) {
                    var r = this.asn1Array[e];
                    t.push(r.tohex())
                }
                return 1 == this.sortFlag && t.sort(),
                    this.hV = t.join(""),
                    this.hV
            }
            ,
        void 0 !== t && void 0 !== t.sortflag && 0 == t.sortflag && (this.sortFlag = !1)
    }
    ,
    Mt(at.asn1.DERSet, at.asn1.DERAbstractStructured),
    at.asn1.DERTaggedObject = function (t) {
        at.asn1.DERTaggedObject.superclass.constructor.call(this);
        var e = at.asn1
            , r = ut
            , i = r.getV;
        r.isASN1HEX;
        var n = e.ASN1Util.newObject;
        this.hT = "a0",
            this.hV = "",
            this.isExplicit = !0,
            this.asn1Object = null,
            this.params = {
                tag: "a0",
                explicit: !0
            },
            this.setASN1Object = function (t, e, r) {
                this.params = {
                    tag: e,
                    explicit: t,
                    obj: r
                }
            }
            ,
            this.getFreshValueHex = function () {
                var t = this.params;
                if (null == t.explicit && (t.explicit = !0),
                null != t.tage && (t.tag = t.tage,
                    t.explicit = !0),
                null != t.tagi && (t.tag = t.tagi,
                    t.explicit = !1),
                null != t.str)
                    this.hV = vt(t.str);
                else if (null != t.hex)
                    this.hV = t.hex;
                else {
                    if (null == t.obj)
                        throw new Error("str, hex nor obj not specified");
                    var r;
                    t.obj instanceof e.ASN1Object ? r = t.obj.tohex() : "object" == typeof t.obj && (r = n(t.obj).tohex()),
                        t.explicit ? this.hV = r : this.hV = i(r, 0)
                }
                return null == t.tag && (t.tag = "a0"),
                    this.hT = t.tag,
                    this.hTLV = null,
                    this.isModified = !0,
                    this.hV
            }
            ,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
        void 0 !== t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.DERTaggedObject, at.asn1.ASN1Object);
var at, ot, ht, ut = new function () {
    }
;

function ct(t) {
    for (var e = "", r = 0; r < t.length; r++) {
        var i = t[r].toString(16);
        1 == i.length && (i = "0" + i),
            e += i
    }
    return e
}

function lt(t) {
    return ct(function (t) {
        for (var e = new Array, r = 0; r < t.length; r++)
            e[r] = t.charCodeAt(r);
        return e
    }(t))
}

function ft(t) {
    return t = (t = (t = t.replace(/\=/g, "")).replace(/\+/g, "-")).replace(/\//g, "_")
}

function gt(t) {
    return t.length % 4 == 2 ? t += "==" : t.length % 4 == 3 && (t += "="),
        t = (t = t.replace(/-/g, "+")).replace(/_/g, "/")
}

function pt(t) {
    return t.length % 2 == 1 && (t = "0" + t),
        ft(d(t))
}

function dt(t) {
    return v(gt(t))
}

function vt(t) {
    return At(Ht(t)).toLowerCase()
}

function mt(e) {
    try {
        return decodeURIComponent(Dt(e))
    } catch (t) {
        return null
    }
}

function yt(t) {
    return mt(function (t) {
        for (var e = t.match(/.{1,2}/g), r = [], i = 0; i < e.length; i++) {
            var n = parseInt(e[i], 16);
            161 <= n && n <= 191 ? (r.push("c2"),
                r.push(e[i])) : 192 <= n && n <= 255 ? (r.push("c3"),
                r.push((n - 64).toString(16))) : r.push(e[i])
        }
        return r.join("")
    }(t))
}

function xt(t) {
    for (var e = "", r = 0; r < t.length - 1; r += 2)
        e += String.fromCharCode(parseInt(t.substr(r, 2), 16));
    return e
}

function St(t) {
    for (var e = "", r = 0; r < t.length; r++)
        e += ("0" + t.charCodeAt(r).toString(16)).slice(-2);
    return e
}

function Et(t) {
    return d(t)
}

function wt(t, e) {
    return "-----BEGIN " + e + "-----\r\n" + function (t, e) {
        return (t = t.replace(new RegExp("(.{" + e + "})", "g"), "$1\r\n")).replace(/\s+$/, "")
    }(Et(t), 64) + "\r\n-----END " + e + "-----\r\n"
}

function Ft(t, e) {
    if (-1 == t.indexOf("-----BEGIN "))
        throw new Error("can't find PEM header");
    return function (t) {
        return v(t.replace(/[^0-9A-Za-z\/+=]*/g, ""))
    }(t = void 0 !== e ? (t = t.replace(new RegExp("^[^]*-----BEGIN " + e + "-----"), "")).replace(new RegExp("-----END " + e + "-----[^]*$"), "") : (t = t.replace(/^[^]*-----BEGIN [^-]+-----/, "")).replace(/-----END [^-]+-----[^]*$/, ""))
}

function bt(t) {
    return Math.round(function (t) {
        var e, r, i, n, s, a, o, h, u, c;
        if (t = function (t) {
            return t.match(/^[0-9]{12}Z$/) || t.match(/^[0-9]{12}[.][0-9]*Z$/) ? t.match(/^[0-4]/) ? "20" + t : "19" + t : t
        }(t),
            c = t.match(/^(\d{4})(\d\d)(\d\d)(\d\d)(\d\d)(\d\d)(|\.\d+)Z$/))
            return e = parseInt(c[1]),
                r = parseInt(c[2]) - 1,
                i = parseInt(c[3]),
                n = parseInt(c[4]),
                s = parseInt(c[5]),
                a = parseInt(c[6]),
                o = 0,
            "" !== (h = c[7]) && (u = (h.substr(1) + "00").substr(0, 3),
                o = parseInt(u)),
                Date.UTC(e, r, i, n, s, a, o);
        throw new Error("unsupported zulu format: " + t)
    }(t) / 1e3)
}

function At(t) {
    return t.replace(/%/g, "")
}

function Dt(t) {
    return t.replace(/(..)/g, "%$1")
}

function It(t) {
    var e = "malformed IPv6 address";
    if (!t.match(/^[0-9A-Fa-f:]+$/))
        throw e;
    var r = (t = t.toLowerCase()).split(":").length - 1;
    if (r < 2)
        throw e;
    var i = ":".repeat(7 - r + 2)
        , n = (t = t.replace("::", i)).split(":");
    if (8 != n.length)
        throw e;
    for (var s = 0; s < 8; s++)
        n[s] = ("0000" + n[s]).slice(-4);
    return n.join("")
}

function Pt(t) {
    if (!t.match(/^[0-9A-Fa-f]{32}$/))
        throw new Error("malformed IPv6 address: " + t);
    var e = (t = t.toLowerCase()).match(/.{1,4}/g);
    e = e.map((function (t) {
            return t.replace(/^0+/, "")
        }
    )),
        e = e.map((function (t) {
                return "" == t ? "0" : t
            }
        ));
    var r = (t = ":" + e.join(":") + ":").match(/:(0:){2,}/g);
    if (null == r)
        return t.slice(1, -1);
    var i = r.sort().slice(-1)[0];
    return "::" != (t = t.replace(i.substr(0, i.length - 1), ":")).substr(0, 2) && (t = t.substr(1)),
    "::" != t.substr(-2, 2) && (t = t.substr(0, t.length - 1)),
        t
}

function Ct(e) {
    var r = new Error("malformed hex value");
    if (!e.match(/^([0-9A-Fa-f][0-9A-Fa-f]){1,}$/))
        throw r;
    if (8 == e.length) {
        try {
            return parseInt(e.substr(0, 2), 16) + "." + parseInt(e.substr(2, 2), 16) + "." + parseInt(e.substr(4, 2), 16) + "." + parseInt(e.substr(6, 2), 16)
        } catch (t) {
            throw r
        }
    } else {
        if (16 != e.length) {
            if (32 == e.length)
                return Pt(e);
            if (64 == e.length) {
                try {
                    return Pt(e.substr(0, 32)) + "/" + Rt(e.substr(32))
                } catch (t) {
                    throw r
                }
                return
            }
            return e
        }
        try {
            return Ct(e.substr(0, 8)) + "/" + Rt(e.substr(8))
        } catch (t) {
            throw r
        }
    }
}

function Rt(t) {
    var e, r = new Error("malformed mask");
    try {
        e = new m(t, 16).toString(2)
    } catch (h) {
        throw r
    }
    if (!e.match(/^1*0*$/))
        throw r;
    return e.replace(/0+$/, "").length
}

function Tt(t) {
    var e = new Error("malformed IP address");
    if (!(t = t.toLowerCase(t)).match(/^[0-9a-f.:/]+$/))
        throw e;
    if (!t.match(/^[0-9.]+$/)) {
        var i;
        if (t.match(/^[0-9.]+\/[0-9]+$/))
            return Tt((i = t.split("/"))[0]) + Nt(parseInt(i[1]), 32);
        if (t.match(/^[0-9a-f:]+$/) && -1 !== t.indexOf(":"))
            return It(t);
        if (t.match(/^[0-9a-f:]+\/[0-9]+$/) && -1 !== t.indexOf(":"))
            return It((i = t.split("/"))[0]) + Nt(parseInt(i[1]), 128);
        throw e
    }
    var n = t.split(".");
    if (4 !== n.length)
        throw e;
    var s = "";
    try {
        for (var a = 0; a < 4; a++) {
            s += ("0" + parseInt(n[a]).toString(16)).slice(-2)
        }
        return s
    } catch (r) {
        throw e
    }
}

function Nt(t, e) {
    return 32 == e && 0 == t ? "00000000" : 128 == e && 0 == t ? "00000000000000000000000000000000" : new m(Array(t + 1).join("1") + Array(e - t + 1).join("0"), 2).toString(16)
}

function Bt(t) {
    var e = t.match(/.{4}/g).map((function (t) {
            var e = parseInt(t.substr(0, 2), 16)
                , r = parseInt(t.substr(2), 16);
            if (0 == e & r < 128)
                return String.fromCharCode(r);
            if (e < 8) {
                var i = 128 | 63 & r;
                return mt((192 | (7 & e) << 3 | (192 & r) >> 6).toString(16) + i.toString(16))
            }
            i = 128 | (15 & e) << 2 | (192 & r) >> 6;
            var n = 128 | 63 & r;
            return mt((224 | (240 & e) >> 4).toString(16) + i.toString(16) + n.toString(16))
        }
    ));
    return e.join("")
}

function Ht(t) {
    for (var e = encodeURIComponent(t), r = "", i = 0; i < e.length; i++)
        "%" == e[i] ? (r += e.substr(i, 3),
            i += 2) : r = r + "%" + lt(e[i]);
    return r
}

function Ot(t) {
    return !(t.length % 2 != 0 || !t.match(/^[0-9a-f]+$/) && !t.match(/^[0-9A-F]+$/))
}

function jt(t) {
    return !!t.match(/^[0-9A-Za-z-_.]+$/)
}

function Vt(t) {
    return t.length % 2 == 1 ? "0" + t : t.substr(0, 1) > "7" ? "00" + t : t
}

function Kt(t) {
    if (!Ot(t))
        return null;
    try {
        var e = []
            , r = t.substr(0, 2)
            , i = parseInt(r, 16);
        e[0] = new String(Math.floor(i / 40)),
            e[1] = new String(i % 40);
        for (var n = t.substr(2), s = [], a = 0; a < n.length / 2; a++)
            s.push(parseInt(n.substr(2 * a, 2), 16));
        var o = []
            , h = "";
        for (a = 0; a < s.length; a++)
            128 & s[a] ? h += Lt((127 & s[a]).toString(2), 7) : (h += Lt((127 & s[a]).toString(2), 7),
                o.push(new String(parseInt(h, 2))),
                h = "");
        var u = e.join(".");
        return o.length > 0 && (u = u + "." + o.join(".")),
            u
    } catch (c) {
        return null
    }
}

ut.getLblen = function (t, e) {
    if ("8" != t.substr(e + 2, 1))
        return 1;
    var r = parseInt(t.substr(e + 3, 1));
    return 0 == r ? -1 : 0 < r && r < 10 ? r + 1 : -2
}
    ,
    ut.getL = function (t, e) {
        var r = ut.getLblen(t, e);
        return r < 1 ? "" : t.substr(e + 2, 2 * r)
    }
    ,
    ut.getVblen = function (t, e) {
        var r;
        return "" == (r = ut.getL(t, e)) ? -1 : ("8" === r.substr(0, 1) ? new m(r.substr(2), 16) : new m(r, 16)).intValue()
    }
    ,
    ut.getVidx = function (t, e) {
        var r = ut.getLblen(t, e);
        return r < 0 ? r : e + 2 * (r + 1)
    }
    ,
    ut.getV = function (t, e) {
        var r = ut.getVidx(t, e)
            , i = ut.getVblen(t, e);
        return t.substr(r, 2 * i)
    }
    ,
    ut.getTLV = function (t, e) {
        return t.substr(e, 2) + ut.getL(t, e) + ut.getV(t, e)
    }
    ,
    ut.getTLVblen = function (t, e) {
        return 2 + 2 * ut.getLblen(t, e) + 2 * ut.getVblen(t, e)
    }
    ,
    ut.getNextSiblingIdx = function (t, e) {
        return ut.getVidx(t, e) + 2 * ut.getVblen(t, e)
    }
    ,
    ut.getChildIdx = function (t, e) {
        var r, i, n, s = ut, a = [];
        r = s.getVidx(t, e),
            i = 2 * s.getVblen(t, e),
        "03" == t.substr(e, 2) && (r += 2,
            i -= 2),
            n = 0;
        for (var o = r; n <= i;) {
            var h = s.getTLVblen(t, o);
            if ((n += h) <= i && a.push(o),
                o += h,
            n >= i)
                break
        }
        return a
    }
    ,
    ut.getNthChildIdx = function (t, e, r) {
        return ut.getChildIdx(t, e)[r]
    }
    ,
    ut.getIdxbyList = function (t, e, r, i) {
        var n, s, a = ut;
        return 0 == r.length ? void 0 !== i && t.substr(e, 2) !== i ? -1 : e : (n = r.shift()) >= (s = a.getChildIdx(t, e)).length ? -1 : a.getIdxbyList(t, s[n], r, i)
    }
    ,
    ut.getIdxbyListEx = function (t, e, r, i) {
        var n, s, a = ut;
        if (0 == r.length)
            return void 0 !== i && t.substr(e, 2) !== i ? -1 : e;
        n = r.shift(),
            s = a.getChildIdx(t, e);
        for (var o = 0, h = 0; h < s.length; h++) {
            var u = t.substr(s[h], 2);
            if ("number" == typeof n && !a.isContextTag(u) && o == n || "string" == typeof n && a.isContextTag(u, n))
                return a.getIdxbyListEx(t, s[h], r, i);
            a.isContextTag(u) || o++
        }
        return -1
    }
    ,
    ut.getTLVbyList = function (t, e, r, i) {
        var n = ut
            , s = n.getIdxbyList(t, e, r, i);
        return -1 == s || s >= t.length ? null : n.getTLV(t, s)
    }
    ,
    ut.getTLVbyListEx = function (t, e, r, i) {
        var n = ut
            , s = n.getIdxbyListEx(t, e, r, i);
        return -1 == s ? null : n.getTLV(t, s)
    }
    ,
    ut.getVbyList = function (t, e, r, i, n) {
        var s, a, o = ut;
        return -1 == (s = o.getIdxbyList(t, e, r, i)) || s >= t.length ? null : (a = o.getV(t, s),
        !0 === n && (a = a.substr(2)),
            a)
    }
    ,
    ut.getVbyListEx = function (t, e, r, i, n) {
        var s, a, o = ut;
        return -1 == (s = o.getIdxbyListEx(t, e, r, i)) ? null : (a = o.getV(t, s),
        "03" == t.substr(s, 2) && !1 !== n && (a = a.substr(2)),
            a)
    }
    ,
    ut.getInt = function (t, e, r) {
        null == r && (r = -1);
        try {
            var i = t.substr(e, 2);
            if ("02" != i && "03" != i)
                return r;
            var n = ut.getV(t, e);
            return "02" == i ? parseInt(n, 16) : function (t) {
                if (t.length % 2 != 0)
                    return -1;
                if (t = t.toLowerCase(),
                null == t.match(/^[0-9a-f]+$/))
                    return -1;
                try {
                    var e = t.substr(0, 2);
                    if ("00" == e)
                        return parseInt(t.substr(2), 16);
                    var r = parseInt(e, 16);
                    if (r > 7)
                        return -1;
                    var n = t.substr(2)
                        , s = parseInt(n, 16).toString(2);
                    "0" == s && (s = "00000000"),
                        s = s.slice(0, 0 - r);
                    var a = parseInt(s, 2);
                    return NaN == a ? -1 : a
                } catch (i) {
                    return -1
                }
            }(n)
        } catch (o) {
            return r
        }
    }
    ,
    ut.getOID = function (t, e, r) {
        null == r && (r = null);
        try {
            return "06" != t.substr(e, 2) ? r : Kt(ut.getV(t, e))
        } catch (h) {
            return r
        }
    }
    ,
    ut.getOIDName = function (t, e, r) {
        null == r && (r = null);
        try {
            var i = ut.getOID(t, e, r);
            if (i == r)
                return r;
            var n = at.asn1.x509.OID.oid2name(i);
            return "" == n ? i : n
        } catch (s) {
            return r
        }
    }
    ,
    ut.getString = function (t, e, r) {
        null == r && (r = null);
        try {
            return xt(ut.getV(t, e))
        } catch (i) {
            return r
        }
    }
    ,
    ut.hextooidstr = function (t) {
        var e = function (t, e) {
            return t.length >= e ? t : new Array(e - t.length + 1).join("0") + t
        }
            , r = []
            , i = t.substr(0, 2)
            , n = parseInt(i, 16);
        r[0] = new String(Math.floor(n / 40)),
            r[1] = new String(n % 40);
        for (var s = t.substr(2), a = [], o = 0; o < s.length / 2; o++)
            a.push(parseInt(s.substr(2 * o, 2), 16));
        var h = []
            , u = "";
        for (o = 0; o < a.length; o++)
            128 & a[o] ? u += e((127 & a[o]).toString(2), 7) : (u += e((127 & a[o]).toString(2), 7),
                h.push(new String(parseInt(u, 2))),
                u = "");
        var c = r.join(".");
        return h.length > 0 && (c = c + "." + h.join(".")),
            c
    }
    ,
    ut.dump = function (t, e, r, i) {
        var n = ut
            , s = n.getV
            , a = n.dump
            , o = n.getChildIdx
            , h = t;
        t instanceof at.asn1.ASN1Object && (h = t.tohex());
        var u = function (t, e) {
            return t.length <= 2 * e ? t : t.substr(0, e) + "..(total " + t.length / 2 + "bytes).." + t.substr(t.length - e, e)
        };
        void 0 === e && (e = {
            ommit_long_octet: 32
        }),
        void 0 === r && (r = 0),
        void 0 === i && (i = "");
        var c, l = e.ommit_long_octet;
        if ("01" == (c = h.substr(r, 2)))
            return "00" == (f = s(h, r)) ? i + "BOOLEAN FALSE\n" : i + "BOOLEAN TRUE\n";
        if ("02" == c)
            return i + "INTEGER " + u(f = s(h, r), l) + "\n";
        if ("03" == c) {
            var f = s(h, r);
            if (n.isASN1HEX(f.substr(2))) {
                var g = i + "BITSTRING, encapsulates\n";
                return g += a(f.substr(2), e, 0, i + "  ")
            }
            return i + "BITSTRING " + u(f, l) + "\n"
        }
        if ("04" == c) {
            f = s(h, r);
            if (n.isASN1HEX(f)) {
                g = i + "OCTETSTRING, encapsulates\n";
                return g += a(f, e, 0, i + "  ")
            }
            return i + "OCTETSTRING " + u(f, l) + "\n"
        }
        if ("05" == c)
            return i + "NULL\n";
        if ("06" == c) {
            var p = s(h, r)
                , d = at.asn1.ASN1Util.oidHexToInt(p)
                , v = at.asn1.x509.OID.oid2name(d)
                , m = d.replace(/\./g, " ");
            return "" != v ? i + "ObjectIdentifier " + v + " (" + m + ")\n" : i + "ObjectIdentifier (" + m + ")\n"
        }
        if ("0a" == c)
            return i + "ENUMERATED " + parseInt(s(h, r)) + "\n";
        if ("0c" == c)
            return i + "UTF8String '" + mt(s(h, r)) + "'\n";
        if ("13" == c)
            return i + "PrintableString '" + mt(s(h, r)) + "'\n";
        if ("14" == c)
            return i + "TeletexString '" + mt(s(h, r)) + "'\n";
        if ("16" == c)
            return i + "IA5String '" + mt(s(h, r)) + "'\n";
        if ("17" == c)
            return i + "UTCTime " + mt(s(h, r)) + "\n";
        if ("18" == c)
            return i + "GeneralizedTime " + mt(s(h, r)) + "\n";
        if ("1a" == c)
            return i + "VisualString '" + mt(s(h, r)) + "'\n";
        if ("1e" == c)
            return i + "BMPString '" + Bt(s(h, r)) + "'\n";
        if ("30" == c) {
            if ("3000" == h.substr(r, 4))
                return i + "SEQUENCE {}\n";
            g = i + "SEQUENCE\n";
            var y = e;
            if ((2 == (E = o(h, r)).length || 3 == E.length) && "06" == h.substr(E[0], 2) && "04" == h.substr(E[E.length - 1], 2)) {
                v = n.oidname(s(h, E[0]));
                var x = JSON.parse(JSON.stringify(e));
                x.x509ExtName = v,
                    y = x
            }
            for (var S = 0; S < E.length; S++)
                g += a(h, y, E[S], i + "  ");
            return g
        }
        if ("31" == c) {
            g = i + "SET\n";
            var E = o(h, r);
            for (S = 0; S < E.length; S++)
                g += a(h, e, E[S], i + "  ");
            return g
        }
        if (128 & (c = parseInt(c, 16))) {
            var w = 31 & c;
            if (32 & c) {
                for (g = i + "[" + w + "]\n",
                         E = o(h, r),
                         S = 0; S < E.length; S++)
                    g += a(h, e, E[S], i + "  ");
                return g
            }
            f = s(h, r);
            if (ut.isASN1HEX(f)) {
                var g = i + "[" + w + "]\n";
                return g += a(f, e, 0, i + "  ")
            }
            return ("68747470" == f.substr(0, 8) || "subjectAltName" === e.x509ExtName && 2 == w) && (f = mt(f)),
                g = i + "[" + w + "] " + f + "\n"
        }
        return i + "UNKNOWN(" + c + ") " + s(h, r) + "\n"
    }
    ,
    ut.parse = function (t) {
        var e = ut
            , r = e.parse
            , i = e.isASN1HEX
            , n = e.getV
            , s = e.getTLV
            , a = e.getChildIdx
            , h = at.asn1
            , u = h.ASN1Util.oidHexToInt
            , c = h.x509.OID.oid2name
            , l = mt
            , f = Bt
            , g = yt
            , p = {
            "0c": "utf8str",
            12: "numstr",
            13: "prnstr",
            14: "telstr",
            16: "ia5str",
            17: "utctime",
            18: "gentime",
            "1a": "visstr",
            "1e": "bmpstr",
            30: "seq",
            31: "set"
        }
            , d = t.substr(0, 2)
            , v = {}
            , m = n(t, 0);
        if ("01" == d)
            return "0101ff" == t ? {
                bool: !0
            } : {
                bool: !1
            };
        if ("02" == d)
            return {
                int: {
                    hex: m
                }
            };
        if ("03" == d)
            try {
                if ("00" != m.substr(0, 2))
                    throw "not encap";
                var y = m.substr(2);
                if (!i(y))
                    throw "not encap";
                return {
                    bitstr: {
                        obj: r(y)
                    }
                }
            } catch (X) {
                var x = null;
                return m.length <= 10 && (x = function (t) {
                    if ("string" != typeof t)
                        return null;
                    if (t.length % 2 != 0)
                        return null;
                    if (!t.match(/^[0-9a-f]+$/))
                        return null;
                    try {
                        var e = parseInt(t.substr(0, 2), 16);
                        if (e < 0 || 7 < e)
                            return null;
                        for (var r = t.substr(2), i = "", n = 0; n < r.length; n += 2) {
                            var s = r.substr(n, 2)
                                , a = parseInt(s, 16).toString(2);
                            i += a = ("0000000" + a).slice(-8)
                        }
                        return i.substr(0, i.length - e)
                    } catch (o) {
                        return null
                    }
                }(m)),
                    null == x ? {
                        bitstr: {
                            hex: m
                        }
                    } : {
                        bitstr: {
                            bin: x
                        }
                    }
            }
        else if ("04" == d)
            try {
                if (!i(m))
                    throw "not encap";
                return {
                    octstr: {
                        obj: r(m)
                    }
                }
            } catch (X) {
                return {
                    octstr: {
                        hex: m
                    }
                }
            }
        else {
            if ("05" == d)
                return {
                    null: ""
                };
            if ("06" == d) {
                var S = u(m)
                    , E = c(S);
                return "" == E ? {
                    oid: S
                } : {
                    oid: E
                }
            }
            if ("0a" == d)
                return m.length > 4 ? {
                    enum: {
                        hex: m
                    }
                } : {
                    enum: parseInt(m, 16)
                };
            if ("30" == d || "31" == d)
                return v[p[d]] = function (t) {
                    for (var e = [], i = a(t, 0), n = 0; n < i.length; n++) {
                        var o = i[n]
                            , h = s(t, o)
                            , u = r(h);
                        e.push(u)
                    }
                    return e
                }(t),
                    v;
            if ("14" == d) {
                var w = g(m);
                return v[p[d]] = {
                    str: w
                },
                    v
            }
            if ("1e" == d) {
                w = f(m);
                return v[p[d]] = {
                    str: w
                },
                    v
            }
            if (-1 != ":0c:12:13:16:17:18:1a:".indexOf(d)) {
                w = l(m);
                return v[p[d]] = {
                    str: w
                },
                    v
            }
            if (d.match(/^8[0-9]$/))
                return null == (w = l(m)) | "" == w || null != w.match(/[\x00-\x1F\x7F-\x9F]/) || null != w.match(/[\u0000-\u001F\u0080–\u009F]/) ? {
                    tag: {
                        tag: d,
                        explicit: !1,
                        hex: m
                    }
                } : {
                    tag: {
                        tag: d,
                        explicit: !1,
                        str: w
                    }
                };
            if (!d.match(/^a[0-9]$/)) {
                var F = new at.asn1.ASN1Object;
                return F.hV = m,
                    {
                        asn1: {
                            tlv: d + F.getLengthHexFromValue() + m
                        }
                    }
            }
            try {
                if (!i(m))
                    throw new Error("not encap");
                return {
                    tag: {
                        tag: d,
                        explicit: !0,
                        obj: r(m)
                    }
                }
            } catch (X) {
                return {
                    tag: {
                        tag: d,
                        explicit: !0,
                        hex: m
                    }
                }
            }
        }
    }
    ,
    ut.isContextTag = function (t, e) {
        var r, i;
        t = t.toLowerCase();
        try {
            r = parseInt(t, 16)
        } catch (o) {
            return -1
        }
        if (void 0 === e)
            return 128 == (192 & r);
        try {
            return null != e.match(/^\[[0-9]+\]$/) && (!((i = parseInt(e.substr(1, e.length - 1), 10)) > 31) && (128 == (192 & r) && (31 & r) == i))
        } catch (o) {
            return !1
        }
    }
    ,
    ut.isASN1HEX = function (t) {
        var e = ut;
        if (t.length % 2 == 1)
            return !1;
        var r = e.getVblen(t, 0)
            , i = t.substr(0, 2)
            , n = e.getL(t, 0);
        return t.length - i.length - n.length == 2 * r
    }
    ,
    ut.checkStrictDER = function (t, e, r, i, n) {
        var s = ut;
        if (void 0 === r) {
            if ("string" != typeof t)
                throw new Error("not hex string");
            if (t = t.toLowerCase(),
                !at.lang.String.isHex(t))
                throw new Error("not hex string");
            r = t.length,
                n = (i = t.length / 2) < 128 ? 1 : Math.ceil(i.toString(16)) + 1
        }
        if (s.getL(t, e).length > 2 * n)
            throw new Error("L of TLV too long: idx=" + e);
        var a = s.getVblen(t, e);
        if (a > i)
            throw new Error("value of L too long than hex: idx=" + e);
        var o = s.getTLV(t, e)
            , h = o.length - 2 - s.getL(t, e).length;
        if (h !== 2 * a)
            throw new Error("V string length and L's value not the same:" + h + "/" + 2 * a);
        if (0 === e && t.length != o.length)
            throw new Error("total length and TLV length unmatch:" + t.length + "!=" + o.length);
        var u = t.substr(e, 2);
        if ("02" === u) {
            var c = s.getVidx(t, e);
            if ("00" == t.substr(c, 2) && t.charCodeAt(c + 2) < 56)
                throw new Error("not least zeros for DER INTEGER")
        }
        if (32 & parseInt(u, 16)) {
            for (var l = s.getVblen(t, e), f = 0, g = s.getChildIdx(t, e), p = 0; p < g.length; p++) {
                f += s.getTLV(t, g[p]).length,
                    s.checkStrictDER(t, g[p], r, i, n)
            }
            if (2 * l != f)
                throw new Error("sum of children's TLV length and L unmatch: " + 2 * l + "!=" + f)
        }
    }
    ,
    ut.oidname = function (t) {
        var e = at.asn1;
        at.lang.String.isHex(t) && (t = e.ASN1Util.oidHexToInt(t));
        var r = e.x509.OID.oid2name(t);
        return "" === r && (r = t),
            r
    }
    ,
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
void 0 !== at.asn1.x509 && at.asn1.x509 || (at.asn1.x509 = {}),
    at.asn1.x509.Certificate = function (t) {
        at.asn1.x509.Certificate.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERBitString
            , i = e.DERSequence
            , n = e.x509
            , s = n.TBSCertificate
            , a = n.AlgorithmIdentifier;
        this.params = void 0,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
            this.sign = function () {
                var t = this.params
                    , e = t.sigalg;
                null != t.sigalg.name && (e = t.sigalg.name);
                var r = t.tbsobj.tohex()
                    , i = new at.crypto.Signature({
                    alg: e
                });
                i.init(t.cakey),
                    i.updateHex(r),
                    t.sighex = i.sign()
            }
            ,
            this.getPEM = function () {
                return wt(this.tohex(), "CERTIFICATE")
            }
            ,
            this.tohex = function () {
                var t = this.params;
                if (null != t.tbsobj && null != t.tbsobj || (t.tbsobj = new s(t)),
                null == t.sighex && null != t.cakey && this.sign(),
                null == t.sighex)
                    throw new Error("sighex or cakey parameter not defined");
                var e = [];
                return e.push(t.tbsobj),
                    e.push(new a({
                        name: t.sigalg
                    })),
                    e.push(new r({
                        hex: "00" + t.sighex
                    })),
                    new i({
                        array: e
                    }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.Certificate, at.asn1.ASN1Object),
    at.asn1.x509.TBSCertificate = function (t) {
        at.asn1.x509.TBSCertificate.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.x509
            , i = e.DERTaggedObject
            , n = e.DERInteger
            , s = e.DERSequence
            , a = r.AlgorithmIdentifier
            , o = r.Time
            , h = r.X500Name
            , u = r.Extensions
            , c = r.SubjectPublicKeyInfo;
        this.params = null,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
            this.tohex = function () {
                var t = []
                    , e = this.params;
                if (null != e.version || 1 != e.version) {
                    var r = 2;
                    null != e.version && (r = e.version - 1);
                    var l = new i({
                        obj: new n({
                            int: r
                        })
                    });
                    t.push(l)
                }
                return t.push(new n(e.serial)),
                    t.push(new a({
                        name: e.sigalg
                    })),
                    t.push(new h(e.issuer)),
                    t.push(new s({
                        array: [new o(e.notbefore), new o(e.notafter)]
                    })),
                    t.push(new h(e.subject)),
                    t.push(new c(_t.getKey(e.sbjpubkey))),
                void 0 !== e.ext && e.ext.length > 0 && t.push(new i({
                    tag: "a3",
                    obj: new u(e.ext)
                })),
                    new at.asn1.DERSequence({
                        array: t
                    }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.x509.TBSCertificate, at.asn1.ASN1Object),
    at.asn1.x509.Extensions = function (t) {
        at.asn1.x509.Extensions.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERSequence
            , i = e.x509;
        this.aParam = [],
            this.setByParam = function (t) {
                this.aParam = t
            }
            ,
            this.tohex = function () {
                for (var t = [], e = 0; e < this.aParam.length; e++) {
                    var n = this.aParam[e]
                        , s = n.extname
                        , a = null;
                    if (null != n.extn)
                        a = new i.PrivateExtension(n);
                    else if ("subjectKeyIdentifier" == s)
                        a = new i.SubjectKeyIdentifier(n);
                    else if ("keyUsage" == s)
                        a = new i.KeyUsage(n);
                    else if ("subjectAltName" == s)
                        a = new i.SubjectAltName(n);
                    else if ("issuerAltName" == s)
                        a = new i.IssuerAltName(n);
                    else if ("basicConstraints" == s)
                        a = new i.BasicConstraints(n);
                    else if ("nameConstraints" == s)
                        a = new i.NameConstraints(n);
                    else if ("cRLDistributionPoints" == s)
                        a = new i.CRLDistributionPoints(n);
                    else if ("certificatePolicies" == s)
                        a = new i.CertificatePolicies(n);
                    else if ("policyMappings" == s)
                        a = new i.PolicyMappings(n);
                    else if ("policyConstraints" == s)
                        a = new i.PolicyConstraints(n);
                    else if ("inhibitAnyPolicy" == s)
                        a = new i.InhibitAnyPolicy(n);
                    else if ("authorityKeyIdentifier" == s)
                        a = new i.AuthorityKeyIdentifier(n);
                    else if ("extKeyUsage" == s)
                        a = new i.ExtKeyUsage(n);
                    else if ("authorityInfoAccess" == s)
                        a = new i.AuthorityInfoAccess(n);
                    else if ("cRLNumber" == s)
                        a = new i.CRLNumber(n);
                    else if ("cRLReason" == s)
                        a = new i.CRLReason(n);
                    else if ("ocspNonce" == s)
                        a = new i.OCSPNonce(n);
                    else if ("ocspNoCheck" == s)
                        a = new i.OCSPNoCheck(n);
                    else if ("adobeTimeStamp" == s)
                        a = new i.AdobeTimeStamp(n);
                    else {
                        if ("subjectDirectoryAttributes" != s)
                            throw new Error("extension not supported:" + JSON.stringify(n));
                        a = new i.SubjectDirectoryAttributes(n)
                    }
                    null != a && t.push(a)
                }
                return new r({
                    array: t
                }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        null != t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.x509.Extensions, at.asn1.ASN1Object),
    at.asn1.x509.Extension = function (t) {
        at.asn1.x509.Extension.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERObjectIdentifier
            , i = e.DEROctetString;
        e.DERBitString;
        var n = e.DERBoolean
            , s = e.DERSequence;
        this.tohex = function () {
            var t = new r({
                oid: this.oid
            })
                , e = new i({
                hex: this.getExtnValueHex()
            })
                , a = new Array;
            return a.push(t),
            this.critical && a.push(new n),
                a.push(e),
                new s({
                    array: a
                }).tohex()
        }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
            this.critical = !1,
        void 0 !== t && void 0 !== t.critical && (this.critical = t.critical)
    }
    ,
    Mt(at.asn1.x509.Extension, at.asn1.ASN1Object),
    at.asn1.x509.KeyUsage = function (t) {
        at.asn1.x509.KeyUsage.superclass.constructor.call(this, t);
        var e = Error
            , r = {
            digitalSignature: 0,
            nonRepudiation: 1,
            keyEncipherment: 2,
            dataEncipherment: 3,
            keyAgreement: 4,
            keyCertSign: 5,
            cRLSign: 6,
            encipherOnly: 7,
            decipherOnly: 8
        };
        this.getExtnValueHex = function () {
            var t = this.getBinValue();
            return this.asn1ExtnValue = new at.asn1.DERBitString({
                bin: t
            }),
                this.asn1ExtnValue.tohex()
        }
            ,
            this.getBinValue = function () {
                var t = this.params;
                if ("object" != typeof t || "object" != typeof t.names && "string" != typeof t.bin)
                    throw new e("parameter not yet set");
                if (null != t.names)
                    return kt(t.names, r);
                if (null != t.bin)
                    return t.bin;
                throw new e("parameter not set properly")
            }
            ,
            this.oid = "2.5.29.15",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.KeyUsage, at.asn1.x509.Extension),
    at.asn1.x509.BasicConstraints = function (t) {
        at.asn1.x509.BasicConstraints.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.DERBoolean
            , i = e.DERInteger
            , n = e.DERSequence;
        this.getExtnValueHex = function () {
            var t = new Array;
            this.cA && t.push(new r),
            this.pathLen > -1 && t.push(new i({
                int: this.pathLen
            }));
            var e = new n({
                array: t
            });
            return this.asn1ExtnValue = e,
                this.asn1ExtnValue.tohex()
        }
            ,
            this.oid = "2.5.29.19",
            this.cA = !1,
            this.pathLen = -1,
        void 0 !== t && (void 0 !== t.cA && (this.cA = t.cA),
        void 0 !== t.pathLen && (this.pathLen = t.pathLen))
    }
    ,
    Mt(at.asn1.x509.BasicConstraints, at.asn1.x509.Extension),
    at.asn1.x509.CRLDistributionPoints = function (t) {
        at.asn1.x509.CRLDistributionPoints.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.x509;
        this.getExtnValueHex = function () {
            return this.asn1ExtnValue.tohex()
        }
            ,
            this.setByDPArray = function (t) {
                for (var i = [], n = 0; n < t.length; n++)
                    if (t[n] instanceof at.asn1.ASN1Object)
                        i.push(t[n]);
                    else {
                        var s = new r.DistributionPoint(t[n]);
                        i.push(s)
                    }
                this.asn1ExtnValue = new e.DERSequence({
                    array: i
                })
            }
            ,
            this.setByOneURI = function (t) {
                var e = new r.DistributionPoint({
                    fulluri: t
                });
                this.setByDPArray([e])
            }
            ,
            this.oid = "2.5.29.31",
        void 0 !== t && (void 0 !== t.array ? this.setByDPArray(t.array) : void 0 !== t.uri && this.setByOneURI(t.uri))
    }
    ,
    Mt(at.asn1.x509.CRLDistributionPoints, at.asn1.x509.Extension),
    at.asn1.x509.DistributionPoint = function (t) {
        at.asn1.x509.DistributionPoint.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.x509.DistributionPointName;
        this.tohex = function () {
            var t = new e.DERSequence;
            if (null != this.asn1DP) {
                var r = new e.DERTaggedObject({
                    explicit: !0,
                    tag: "a0",
                    obj: this.asn1DP
                });
                t.appendASN1Object(r)
            }
            return this.hTLV = t.tohex(),
                this.hTLV
        }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (void 0 !== t.dpobj ? this.asn1DP = t.dpobj : void 0 !== t.dpname ? this.asn1DP = new r(t.dpname) : void 0 !== t.fulluri && (this.asn1DP = new r({
            full: [{
                uri: t.fulluri
            }]
        })))
    }
    ,
    Mt(at.asn1.x509.DistributionPoint, at.asn1.ASN1Object),
    at.asn1.x509.DistributionPointName = function (t) {
        at.asn1.x509.DistributionPointName.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERTaggedObject;
        if (this.tohex = function () {
            if ("full" != this.type)
                throw new Error("currently type shall be 'full': " + this.type);
            return this.asn1Obj = new r({
                explicit: !1,
                tag: this.tag,
                obj: this.asn1V
            }),
                this.hTLV = this.asn1Obj.tohex(),
                this.hTLV
        }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t)
            if (e.x509.GeneralNames.prototype.isPrototypeOf(t))
                this.type = "full",
                    this.tag = "a0",
                    this.asn1V = t;
            else {
                if (void 0 === t.full)
                    throw new Error("This class supports GeneralNames only as argument");
                this.type = "full",
                    this.tag = "a0",
                    this.asn1V = new e.x509.GeneralNames(t.full)
            }
    }
    ,
    Mt(at.asn1.x509.DistributionPointName, at.asn1.ASN1Object),
    at.asn1.x509.CertificatePolicies = function (t) {
        at.asn1.x509.CertificatePolicies.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.x509
            , i = e.DERSequence
            , n = r.PolicyInformation;
        this.params = null,
            this.getExtnValueHex = function () {
                for (var t = [], e = 0; e < this.params.array.length; e++)
                    t.push(new n(this.params.array[e]));
                var r = new i({
                    array: t
                });
                return this.asn1ExtnValue = r,
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.32",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.CertificatePolicies, at.asn1.x509.Extension),
    at.asn1.x509.PolicyInformation = function (t) {
        at.asn1.x509.PolicyInformation.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.DERSequence
            , i = e.DERObjectIdentifier
            , n = e.x509.PolicyQualifierInfo;
        this.params = null,
            this.tohex = function () {
                if (void 0 === this.params.policyoid && void 0 === this.params.array)
                    throw new Error("parameter oid and array missing");
                var t = [new i(this.params.policyoid)];
                if (void 0 !== this.params.array) {
                    for (var e = [], s = 0; s < this.params.array.length; s++)
                        e.push(new n(this.params.array[s]));
                    e.length > 0 && t.push(new r({
                        array: e
                    }))
                }
                return new r({
                    array: t
                }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.PolicyInformation, at.asn1.ASN1Object),
    at.asn1.x509.PolicyQualifierInfo = function (t) {
        at.asn1.x509.PolicyQualifierInfo.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.DERSequence
            , i = e.DERIA5String
            , n = e.DERObjectIdentifier
            , s = e.x509.UserNotice;
        this.params = null,
            this.tohex = function () {
                return void 0 !== this.params.cps ? new r({
                    array: [new n({
                        oid: "1.3.6.1.5.5.7.2.1"
                    }), new i({
                        str: this.params.cps
                    })]
                }).tohex() : null != this.params.unotice ? new r({
                    array: [new n({
                        oid: "1.3.6.1.5.5.7.2.2"
                    }), new s(this.params.unotice)]
                }).tohex() : void 0
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.PolicyQualifierInfo, at.asn1.ASN1Object),
    at.asn1.x509.UserNotice = function (t) {
        at.asn1.x509.UserNotice.superclass.constructor.call(this, t);
        var e = at.asn1.DERSequence;
        at.asn1.DERInteger;
        var r = at.asn1.x509.DisplayText
            , i = at.asn1.x509.NoticeReference;
        this.params = null,
            this.tohex = function () {
                var t = [];
                return void 0 !== this.params.noticeref && t.push(new i(this.params.noticeref)),
                void 0 !== this.params.exptext && t.push(new r(this.params.exptext)),
                    new e({
                        array: t
                    }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.UserNotice, at.asn1.ASN1Object),
    at.asn1.x509.NoticeReference = function (t) {
        at.asn1.x509.NoticeReference.superclass.constructor.call(this, t);
        var e = at.asn1.DERSequence
            , r = at.asn1.DERInteger
            , i = at.asn1.x509.DisplayText;
        this.params = null,
            this.tohex = function () {
                var t = [];
                if (void 0 !== this.params.org && t.push(new i(this.params.org)),
                void 0 !== this.params.noticenum) {
                    for (var n = [], s = this.params.noticenum, a = 0; a < s.length; a++)
                        n.push(new r(s[a]));
                    t.push(new e({
                        array: n
                    }))
                }
                if (0 == t.length)
                    throw new Error("parameter is empty");
                return new e({
                    array: t
                }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.NoticeReference, at.asn1.ASN1Object),
    at.asn1.x509.DisplayText = function (t) {
        at.asn1.x509.DisplayText.superclass.constructor.call(this, t),
            this.hT = "0c",
        void 0 !== t && ("ia5" === t.type ? this.hT = "16" : "vis" === t.type ? this.hT = "1a" : "bmp" === t.type && (this.hT = "1e"))
    }
    ,
    Mt(at.asn1.x509.DisplayText, at.asn1.DERAbstractString),
    at.asn1.x509.PolicyMappings = function (t) {
        at.asn1.x509.PolicyMappings.superclass.constructor.call(this, t);
        var e = at.asn1;
        e.x509;
        var r = e.ASN1Util.newObject;
        this.params = null,
            this.getExtnValueHex = function () {
                for (var t = this.params, e = [], i = 0; i < t.array.length; i++) {
                    var n = t.array[i];
                    e.push({
                        seq: [{
                            oid: n[0]
                        }, {
                            oid: n[1]
                        }]
                    })
                }
                return this.asn1ExtnValue = r({
                    seq: e
                }),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.33",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.PolicyMappings, at.asn1.x509.Extension),
    at.asn1.x509.PolicyConstraints = function (t) {
        at.asn1.x509.PolicyConstraints.superclass.constructor.call(this, t);
        var e = at.asn1;
        e.x509;
        var r = e.ASN1Util.newObject;
        this.params = null,
            this.getExtnValueHex = function () {
                var t = this.params
                    , e = [];
                return null != t.reqexp && e.push({
                    tag: {
                        tagi: "80",
                        obj: {
                            int: t.reqexp
                        }
                    }
                }),
                null != t.inhibit && e.push({
                    tag: {
                        tagi: "81",
                        obj: {
                            int: t.inhibit
                        }
                    }
                }),
                    this.asn1ExtnValue = r({
                        seq: e
                    }),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.36",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.PolicyConstraints, at.asn1.x509.Extension),
    at.asn1.x509.InhibitAnyPolicy = function (t) {
        at.asn1.x509.InhibitAnyPolicy.superclass.constructor.call(this, t);
        var e = at.asn1;
        e.x509;
        var r = e.ASN1Util.newObject;
        this.params = null,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = r({
                    int: this.params.skip
                }),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.54",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.InhibitAnyPolicy, at.asn1.x509.Extension),
    at.asn1.x509.NameConstraints = function (t) {
        at.asn1.x509.NameConstraints.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.x509
            , i = e.ASN1Util.newObject
            , n = r.GeneralSubtree;
        this.params = null,
            this.getExtnValueHex = function () {
                var t = this.params
                    , e = [];
                if (null != t.permit && null != t.permit.length) {
                    for (var r = [], s = 0; s < t.permit.length; s++)
                        r.push(new n(t.permit[s]));
                    e.push({
                        tag: {
                            tagi: "a0",
                            obj: {
                                seq: r
                            }
                        }
                    })
                }
                if (null != t.exclude && null != t.exclude.length) {
                    var a = [];
                    for (s = 0; s < t.exclude.length; s++)
                        a.push(new n(t.exclude[s]));
                    e.push({
                        tag: {
                            tagi: "a1",
                            obj: {
                                seq: a
                            }
                        }
                    })
                }
                return this.asn1ExtnValue = i({
                    seq: e
                }),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.30",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.NameConstraints, at.asn1.x509.Extension),
    at.asn1.x509.GeneralSubtree = function (t) {
        at.asn1.x509.GeneralSubtree.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.x509.GeneralName
            , i = e.ASN1Util.newObject;
        this.params = null,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
            this.tohex = function () {
                var t = this.params
                    , e = [new r(t)];
                return null != t.min && e.push({
                    tag: {
                        tagi: "80",
                        obj: {
                            int: t.min
                        }
                    }
                }),
                null != t.max && e.push({
                    tag: {
                        tagi: "81",
                        obj: {
                            int: t.max
                        }
                    }
                }),
                    i({
                        seq: e
                    }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.x509.GeneralSubtree, at.asn1.ASN1Object),
    at.asn1.x509.ExtKeyUsage = function (t) {
        at.asn1.x509.ExtKeyUsage.superclass.constructor.call(this, t);
        var e = at.asn1;
        this.setPurposeArray = function (t) {
            this.asn1ExtnValue = new e.DERSequence;
            for (var r = 0; r < t.length; r++) {
                var i = new e.DERObjectIdentifier(t[r]);
                this.asn1ExtnValue.appendASN1Object(i)
            }
        }
            ,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.37",
        void 0 !== t && void 0 !== t.array && this.setPurposeArray(t.array)
    }
    ,
    Mt(at.asn1.x509.ExtKeyUsage, at.asn1.x509.Extension),
    at.asn1.x509.AuthorityKeyIdentifier = function (t) {
        at.asn1.x509.AuthorityKeyIdentifier.superclass.constructor.call(this, t);
        var e = at
            , r = e.asn1
            , i = r.DERTaggedObject
            , n = r.x509.GeneralNames;
        e.crypto.Util.isKey,
            this.asn1KID = null,
            this.asn1CertIssuer = null,
            this.asn1CertSN = null,
            this.getExtnValueHex = function () {
                var t = new Array;
                this.asn1KID && t.push(new i({
                    explicit: !1,
                    tag: "80",
                    obj: this.asn1KID
                })),
                this.asn1CertIssuer && t.push(new i({
                    explicit: !1,
                    tag: "a1",
                    obj: new n([{
                        dn: this.asn1CertIssuer
                    }])
                })),
                this.asn1CertSN && t.push(new i({
                    explicit: !1,
                    tag: "82",
                    obj: this.asn1CertSN
                }));
                var e = new r.DERSequence({
                    array: t
                });
                return this.asn1ExtnValue = e,
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.setKIDByParam = function (t) {
                if (void 0 !== t.str || void 0 !== t.hex)
                    this.asn1KID = new at.asn1.DEROctetString(t);
                else if ("object" == typeof t && at.crypto.Util.isKey(t) || "string" == typeof t && -1 != t.indexOf("BEGIN ")) {
                    var e = t;
                    "string" == typeof t && (e = _t.getKey(t));
                    var r = _t.getKeyID(e);
                    this.asn1KID = new at.asn1.DEROctetString({
                        hex: r
                    })
                }
            }
            ,
            this.setCertIssuerByParam = function (t) {
                void 0 !== t.str || void 0 !== t.ldapstr || void 0 !== t.hex || void 0 !== t.certsubject || void 0 !== t.certissuer ? this.asn1CertIssuer = new at.asn1.x509.X500Name(t) : "string" == typeof t && -1 != t.indexOf("BEGIN ") && -1 != t.indexOf("CERTIFICATE") && (this.asn1CertIssuer = new at.asn1.x509.X500Name({
                    certissuer: t
                }))
            }
            ,
            this.setCertSNByParam = function (t) {
                if (void 0 !== t.str || void 0 !== t.bigint || void 0 !== t.hex)
                    this.asn1CertSN = new at.asn1.DERInteger(t);
                else if ("string" == typeof t && -1 != t.indexOf("BEGIN ") && t.indexOf("CERTIFICATE")) {
                    var e = new Wt;
                    e.readCertPEM(t);
                    var r = e.getSerialNumberHex();
                    this.asn1CertSN = new at.asn1.DERInteger({
                        hex: r
                    })
                }
            }
            ,
            this.oid = "2.5.29.35",
        void 0 !== t && (void 0 !== t.kid && this.setKIDByParam(t.kid),
        void 0 !== t.issuer && this.setCertIssuerByParam(t.issuer),
        void 0 !== t.sn && this.setCertSNByParam(t.sn),
        void 0 !== t.issuersn && "string" == typeof t.issuersn && -1 != t.issuersn.indexOf("BEGIN ") && t.issuersn.indexOf("CERTIFICATE") && (this.setCertSNByParam(t.issuersn),
            this.setCertIssuerByParam(t.issuersn)))
    }
    ,
    Mt(at.asn1.x509.AuthorityKeyIdentifier, at.asn1.x509.Extension),
    at.asn1.x509.SubjectKeyIdentifier = function (t) {
        at.asn1.x509.SubjectKeyIdentifier.superclass.constructor.call(this, t);
        var e = at.asn1.DEROctetString;
        this.asn1KID = null,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = this.asn1KID,
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.setKIDByParam = function (t) {
                if (void 0 !== t.str || void 0 !== t.hex)
                    this.asn1KID = new e(t);
                else if ("object" == typeof t && at.crypto.Util.isKey(t) || "string" == typeof t && -1 != t.indexOf("BEGIN")) {
                    var r = t;
                    "string" == typeof t && (r = _t.getKey(t));
                    var i = _t.getKeyID(r);
                    this.asn1KID = new at.asn1.DEROctetString({
                        hex: i
                    })
                }
            }
            ,
            this.oid = "2.5.29.14",
        void 0 !== t && void 0 !== t.kid && this.setKIDByParam(t.kid)
    }
    ,
    Mt(at.asn1.x509.SubjectKeyIdentifier, at.asn1.x509.Extension),
    at.asn1.x509.AuthorityInfoAccess = function (t) {
        at.asn1.x509.AuthorityInfoAccess.superclass.constructor.call(this, t),
            this.setAccessDescriptionArray = function (t) {
                for (var e = new Array, r = at.asn1, i = r.DERSequence, n = r.DERObjectIdentifier, s = r.x509.GeneralName, a = 0; a < t.length; a++) {
                    var o, h = t[a];
                    if (void 0 !== h.ocsp)
                        o = new i({
                            array: [new n({
                                oid: "1.3.6.1.5.5.7.48.1"
                            }), new s({
                                uri: h.ocsp
                            })]
                        });
                    else {
                        if (void 0 === h.caissuer)
                            throw new Error("unknown AccessMethod parameter: " + JSON.stringify(h));
                        o = new i({
                            array: [new n({
                                oid: "1.3.6.1.5.5.7.48.2"
                            }), new s({
                                uri: h.caissuer
                            })]
                        })
                    }
                    e.push(o)
                }
                this.asn1ExtnValue = new i({
                    array: e
                })
            }
            ,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "1.3.6.1.5.5.7.1.1",
        void 0 !== t && void 0 !== t.array && this.setAccessDescriptionArray(t.array)
    }
    ,
    Mt(at.asn1.x509.AuthorityInfoAccess, at.asn1.x509.Extension),
    at.asn1.x509.SubjectAltName = function (t) {
        at.asn1.x509.SubjectAltName.superclass.constructor.call(this, t),
            this.setNameArray = function (t) {
                this.asn1ExtnValue = new at.asn1.x509.GeneralNames(t)
            }
            ,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.17",
        void 0 !== t && void 0 !== t.array && this.setNameArray(t.array)
    }
    ,
    Mt(at.asn1.x509.SubjectAltName, at.asn1.x509.Extension),
    at.asn1.x509.IssuerAltName = function (t) {
        at.asn1.x509.IssuerAltName.superclass.constructor.call(this, t),
            this.setNameArray = function (t) {
                this.asn1ExtnValue = new at.asn1.x509.GeneralNames(t)
            }
            ,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.18",
        void 0 !== t && void 0 !== t.array && this.setNameArray(t.array)
    }
    ,
    Mt(at.asn1.x509.IssuerAltName, at.asn1.x509.Extension),
    at.asn1.x509.SubjectDirectoryAttributes = function (t) {
        at.asn1.x509.SubjectDirectoryAttributes.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.DERSequence
            , i = e.ASN1Util.newObject
            , n = e.x509.OID.name2oid;
        this.params = null,
            this.getExtnValueHex = function () {
                for (var t = [], e = 0; e < this.params.array.length; e++) {
                    var s = this.params.array[e];
                    if (null == s.attr || null == s.array) {
                        var a = {
                            seq: [{
                                oid: "1.2.3.4"
                            }, {
                                set: [{
                                    utf8str: "DE"
                                }]
                            }]
                        };
                        if ("dateOfBirth" == s.attr)
                            a.seq[0].oid = n(s.attr),
                                a.seq[1].set[0] = {
                                    gentime: s.str
                                };
                        else if ("placeOfBirth" == s.attr)
                            a.seq[0].oid = n(s.attr),
                                a.seq[1].set[0] = {
                                    utf8str: s.str
                                };
                        else if ("gender" == s.attr)
                            a.seq[0].oid = n(s.attr),
                                a.seq[1].set[0] = {
                                    prnstr: s.str
                                };
                        else if ("countryOfCitizenship" == s.attr)
                            a.seq[0].oid = n(s.attr),
                                a.seq[1].set[0] = {
                                    prnstr: s.str
                                };
                        else {
                            if ("countryOfResidence" != s.attr)
                                throw new Error("unsupported attribute: " + s.attr);
                            a.seq[0].oid = n(s.attr),
                                a.seq[1].set[0] = {
                                    prnstr: s.str
                                }
                        }
                        t.push(new i(a))
                    } else {
                        var o = {
                            seq: [{
                                oid: s.attr
                            }, {
                                set: s.array
                            }]
                        };
                        t.push(i(o))
                    }
                }
                var h = new r({
                    array: t
                });
                return this.asn1ExtnValue = h,
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.9",
        void 0 !== t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.SubjectDirectoryAttributes, at.asn1.x509.Extension),
    at.asn1.x509.PrivateExtension = function (t) {
        at.asn1.x509.PrivateExtension.superclass.constructor.call(this, t);
        var e = at
            , r = e.lang.String.isHex
            , i = e.asn1
            , n = i.x509.OID.name2oid
            , s = i.ASN1Util.newObject;
        this.params = null,
            this.setByParam = function (t) {
                this.oid = n(t.extname),
                    this.params = t
            }
            ,
            this.getExtnValueHex = function () {
                if (null == this.params.extname || null == this.params.extn)
                    throw new Error("extname or extnhex not specified");
                var t = this.params.extn;
                if ("string" == typeof t && r(t))
                    return t;
                if ("object" == typeof t)
                    try {
                        return s(t).tohex()
                    } catch (e) {
                    }
                throw new Error("unsupported extn value")
            }
            ,
        null != t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.x509.PrivateExtension, at.asn1.x509.Extension),
    at.asn1.x509.CRL = function (t) {
        at.asn1.x509.CRL.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERSequence
            , i = e.DERBitString
            , n = e.x509
            , s = n.AlgorithmIdentifier
            , a = n.TBSCertList;
        this.params = void 0,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
            this.sign = function () {
                var t = new a(this.params).tohex()
                    , e = new at.crypto.Signature({
                    alg: this.params.sigalg
                });
                e.init(this.params.cakey),
                    e.updateHex(t);
                var r = e.sign();
                this.params.sighex = r
            }
            ,
            this.getPEM = function () {
                return wt(this.tohex(), "X509 CRL")
            }
            ,
            this.tohex = function () {
                var t = this.params;
                if (null == t.tbsobj && (t.tbsobj = new a(t)),
                null == t.sighex && null != t.cakey && this.sign(),
                null == t.sighex)
                    throw new Error("sighex or cakey parameter not defined");
                var e = [];
                return e.push(t.tbsobj),
                    e.push(new s({
                        name: t.sigalg
                    })),
                    e.push(new i({
                        hex: "00" + t.sighex
                    })),
                    new r({
                        array: e
                    }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.CRL, at.asn1.ASN1Object),
    at.asn1.x509.TBSCertList = function (t) {
        at.asn1.x509.TBSCertList.superclass.constructor.call(this);
        var e = at.asn1
            , r = e.DERInteger
            , i = e.DERSequence
            , n = e.DERTaggedObject;
        e.DERObjectIdentifier;
        var s = e.x509
            , a = s.AlgorithmIdentifier
            , o = s.Time
            , h = s.Extensions
            , u = s.X500Name;
        this.params = null,
            this.setByParam = function (t) {
                this.params = t
            }
            ,
            this.getRevCertSequence = function () {
                for (var t = [], e = this.params.revcert, n = 0; n < e.length; n++) {
                    var s = [new r(e[n].sn), new o(e[n].date)];
                    null != e[n].ext && s.push(new h(e[n].ext)),
                        t.push(new i({
                            array: s
                        }))
                }
                return new i({
                    array: t
                })
            }
            ,
            this.tohex = function () {
                var t = []
                    , e = this.params;
                if (null != e.version) {
                    var s = e.version - 1
                        , c = new r({
                        int: s
                    });
                    t.push(c)
                }
                if (t.push(new a({
                    name: e.sigalg
                })),
                    t.push(new u(e.issuer)),
                    t.push(new o(e.thisupdate)),
                null != e.nextupdate && t.push(new o(e.nextupdate)),
                null != e.revcert && t.push(this.getRevCertSequence()),
                null != e.ext) {
                    var l = new h(e.ext);
                    t.push(new n({
                        tag: "a0",
                        explicit: !0,
                        obj: l
                    }))
                }
                return new i({
                    array: t
                }).tohex()
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && this.setByParam(t)
    }
    ,
    Mt(at.asn1.x509.TBSCertList, at.asn1.ASN1Object),
    at.asn1.x509.CRLEntry = function (t) {
        at.asn1.x509.CRLEntry.superclass.constructor.call(this);
        var e = at.asn1;
        this.setCertSerial = function (t) {
            this.sn = new e.DERInteger(t)
        }
            ,
            this.setRevocationDate = function (t) {
                this.time = new e.x509.Time(t)
            }
            ,
            this.tohex = function () {
                var t = new e.DERSequence({
                    array: [this.sn, this.time]
                });
                return this.TLV = t.tohex(),
                    this.TLV
            }
            ,
            this.getEncodedHex = function () {
                return this.tohex()
            }
            ,
        void 0 !== t && (void 0 !== t.time && this.setRevocationDate(t.time),
        void 0 !== t.sn && this.setCertSerial(t.sn))
    }
    ,
    Mt(at.asn1.x509.CRLEntry, at.asn1.ASN1Object),
    at.asn1.x509.CRLNumber = function (t) {
        at.asn1.x509.CRLNumber.superclass.constructor.call(this, t),
            this.params = void 0,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = new at.asn1.DERInteger(this.params.num),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.20",
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.CRLNumber, at.asn1.x509.Extension),
    at.asn1.x509.CRLReason = function (t) {
        at.asn1.x509.CRLReason.superclass.constructor.call(this, t),
            this.params = void 0,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = new at.asn1.DEREnumerated(this.params.code),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "2.5.29.21",
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.CRLReason, at.asn1.x509.Extension),
    at.asn1.x509.OCSPNonce = function (t) {
        at.asn1.x509.OCSPNonce.superclass.constructor.call(this, t),
            this.params = void 0,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = new at.asn1.DEROctetString(this.params),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "1.3.6.1.5.5.7.48.1.2",
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.OCSPNonce, at.asn1.x509.Extension),
    at.asn1.x509.OCSPNoCheck = function (t) {
        at.asn1.x509.OCSPNoCheck.superclass.constructor.call(this, t),
            this.params = void 0,
            this.getExtnValueHex = function () {
                return this.asn1ExtnValue = new at.asn1.DERNull,
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "1.3.6.1.5.5.7.48.1.5",
        null != t && (this.params = t)
    }
    ,
    Mt(at.asn1.x509.OCSPNoCheck, at.asn1.x509.Extension),
    at.asn1.x509.AdobeTimeStamp = function (t) {
        at.asn1.x509.AdobeTimeStamp.superclass.constructor.call(this, t);
        var e = at.asn1
            , r = e.DERInteger
            , i = e.DERBoolean
            , n = e.DERSequence
            , s = e.x509.GeneralName;
        this.params = null,
            this.getExtnValueHex = function () {
                var t = this.params
                    , e = [new r(1)];
                return e.push(new s({
                    uri: t.uri
                })),
                null != t.reqauth && e.push(new i(t.reqauth)),
                    this.asn1ExtnValue = new n({
                        array: e
                    }),
                    this.asn1ExtnValue.tohex()
            }
            ,
            this.oid = "1.2.840.113583.1.1.9.1",
        void 0 !== t && this.setByParam(t)
    }
,
Mt(at.asn1.x509.AdobeTimeStamp, at.asn1.x509.Extension),
at.asn1.x509.X500Name = function (t) {
    at.asn1.x509.X500Name.superclass.constructor.call(this),
        this.asn1Array = [],
        this.paramArray = [],
        this.sRule = "utf8";
    var e = at.asn1
        , r = e.x509
        , i = r.RDN;
    this.setByString = function (t, e) {
        void 0 !== e && (this.sRule = e);
        var r = t.split("/");
        r.shift();
        for (var n = [], s = 0; s < r.length; s++)
            if (r[s].match(/^[^=]+=.+$/))
                n.push(r[s]);
            else {
                var a = n.length - 1;
                n[a] = n[a] + "/" + r[s]
            }
        for (s = 0; s < n.length; s++)
            this.asn1Array.push(new i({
                str: n[s],
                rule: this.sRule
            }))
    }
        ,
        this.setByLdapString = function (t, e) {
            void 0 !== e && (this.sRule = e);
            var i = r.X500Name.ldapToCompat(t);
            this.setByString(i, e)
        }
        ,
        this.setByObject = function (t, e) {
            for (var r in void 0 !== e && (this.sRule = e),
                t)
                if (t.hasOwnProperty(r)) {
                    var n = new i({
                        str: r + "=" + t[r],
                        rule: this.sRule
                    });
                    this.asn1Array ? this.asn1Array.push(n) : this.asn1Array = [n]
                }
        }
        ,
        this.setByParam = function (t) {
            var e;
            (void 0 !== t.rule && (this.sRule = t.rule),
            void 0 !== t.array) ? this.paramArray = t.array : void 0 !== t.str ? this.setByString(t.str) : void 0 !== t.ldapstr ? this.setByLdapString(t.ldapstr) : void 0 !== t.hex ? this.hTLV = t.hex : void 0 !== t.certissuer ? ((e = new Wt).readCertPEM(t.certissuer),
                this.hTLV = e.getIssuerHex()) : void 0 !== t.certsubject ? ((e = new Wt).readCertPEM(t.certsubject),
                this.hTLV = e.getSubjectHex()) : "object" == typeof t && void 0 === t.certsubject && void 0 === t.certissuer && this.setByObject(t)
        }
        ,
        this.tohex = function () {
            if ("string" == typeof this.hTLV)
                return this.hTLV;
            if (0 == this.asn1Array.length && this.paramArray.length > 0)
                for (var t = 0; t < this.paramArray.length; t++) {
                    var r = {
                        array: this.paramArray[t]
                    };
                    "utf8" != this.sRule && (r.rule = this.sRule);
                    var n = new i(r);
                    this.asn1Array.push(n)
                }
            var s = new e.DERSequence({
                array: this.asn1Array
            });
            return this.hTLV = s.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.x509.X500Name, at.asn1.ASN1Object),
at.asn1.x509.X500Name.compatToLDAP = function (t) {
    if ("/" !== t.substr(0, 1))
        throw "malformed input";
    var e = (t = t.substr(1)).split("/");
    return e.reverse(),
        e = e.map((function (t) {
                return t.replace(/,/, "\\,")
            }
        )),
        e.join(",")
}
,
at.asn1.x509.X500Name.onelineToLDAP = function (t) {
    return at.asn1.x509.X500Name.compatToLDAP(t)
}
,
at.asn1.x509.X500Name.ldapToCompat = function (t) {
    for (var e = t.split(","), r = !1, i = [], n = 0; e.length > 0; n++) {
        var s = e.shift();
        if (!0 === r) {
            var a = (i.pop() + "," + s).replace(/\\,/g, ",");
            i.push(a),
                r = !1
        } else
            i.push(s);
        "\\" === s.substr(-1, 1) && (r = !0)
    }
    return i = i.map((function (t) {
            return t.replace("/", "\\/")
        }
    )),
        i.reverse(),
    "/" + i.join("/")
}
,
at.asn1.x509.X500Name.ldapToOneline = function (t) {
    return at.asn1.x509.X500Name.ldapToCompat(t)
}
,
at.asn1.x509.RDN = function (t) {
    at.asn1.x509.RDN.superclass.constructor.call(this),
        this.asn1Array = [],
        this.paramArray = [],
        this.sRule = "utf8";
    var e = at.asn1.x509.AttributeTypeAndValue;
    this.setByParam = function (t) {
        void 0 !== t.rule && (this.sRule = t.rule),
        void 0 !== t.str && this.addByMultiValuedString(t.str),
        void 0 !== t.array && (this.paramArray = t.array)
    }
        ,
        this.addByString = function (t) {
            this.asn1Array.push(new at.asn1.x509.AttributeTypeAndValue({
                str: t,
                rule: this.sRule
            }))
        }
        ,
        this.addByMultiValuedString = function (t) {
            for (var e = at.asn1.x509.RDN.parseString(t), r = 0; r < e.length; r++)
                this.addByString(e[r])
        }
        ,
        this.tohex = function () {
            if (0 == this.asn1Array.length && this.paramArray.length > 0)
                for (var t = 0; t < this.paramArray.length; t++) {
                    var r = this.paramArray[t];
                    void 0 !== r.rule && "utf8" != this.sRule && (r.rule = this.sRule);
                    var i = new e(r);
                    this.asn1Array.push(i)
                }
            var n = new at.asn1.DERSet({
                array: this.asn1Array
            });
            return this.TLV = n.tohex(),
                this.TLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.x509.RDN, at.asn1.ASN1Object),
at.asn1.x509.RDN.parseString = function (t) {
    for (var e = t.split(/\+/), r = !1, i = [], n = 0; e.length > 0; n++) {
        var s = e.shift();
        if (!0 === r) {
            var a = (i.pop() + "+" + s).replace(/\\\+/g, "+");
            i.push(a),
                r = !1
        } else
            i.push(s);
        "\\" === s.substr(-1, 1) && (r = !0)
    }
    var o = !1
        , h = [];
    for (n = 0; i.length > 0; n++) {
        s = i.shift();
        if (!0 === o) {
            var u = h.pop();
            if (s.match(/"$/)) {
                a = (u + "+" + s).replace(/^([^=]+)="(.*)"$/, "$1=$2");
                h.push(a),
                    o = !1
            } else
                h.push(u + "+" + s)
        } else
            h.push(s);
        s.match(/^[^=]+="/) && (o = !0)
    }
    return h
}
,
at.asn1.x509.AttributeTypeAndValue = function (t) {
    at.asn1.x509.AttributeTypeAndValue.superclass.constructor.call(this),
        this.sRule = "utf8",
        this.sType = null,
        this.sValue = null,
        this.dsType = null;
    var e = at
        , r = e.asn1
        , i = r.DERSequence
        , n = r.DERUTF8String
        , s = r.DERPrintableString
        , a = r.DERTeletexString
        , o = r.DERIA5String
        , h = r.DERVisibleString
        , u = r.DERBMPString
        , c = e.lang.String.isMail
        , l = e.lang.String.isPrintable;
    this.setByParam = function (t) {
        if (void 0 !== t.rule && (this.sRule = t.rule),
        void 0 !== t.ds && (this.dsType = t.ds),
        void 0 === t.value && void 0 !== t.str) {
            var e = t.str.match(/^([^=]+)=(.+)$/);
            if (!e)
                throw new Error("malformed attrTypeAndValueStr: " + attrTypeAndValueStr);
            this.sType = e[1],
                this.sValue = e[2]
        } else
            this.sType = t.type,
                this.sValue = t.value
    }
        ,
        this.setByString = function (t, e) {
            void 0 !== e && (this.sRule = e);
            var r = t.match(/^([^=]+)=(.+)$/);
            if (!r)
                throw new Error("malformed attrTypeAndValueStr: " + attrTypeAndValueStr);
            this.setByAttrTypeAndValueStr(r[1], r[2])
        }
        ,
        this._getDsType = function () {
            var t = this.sType
                , e = this.sValue
                , r = this.sRule;
            return "prn" === r ? "CN" == t && c(e) ? "ia5" : l(e) ? "prn" : "utf8" : "utf8" === r ? "CN" == t && c(e) ? "ia5" : "C" == t ? "prn" : "utf8" : "utf8"
        }
        ,
        this.setByAttrTypeAndValueStr = function (t, e, r) {
            void 0 !== r && (this.sRule = r),
                this.sType = t,
                this.sValue = e
        }
        ,
        this.getValueObj = function (t, e) {
            if ("utf8" == t)
                return new n({
                    str: e
                });
            if ("prn" == t)
                return new s({
                    str: e
                });
            if ("tel" == t)
                return new a({
                    str: e
                });
            if ("ia5" == t)
                return new o({
                    str: e
                });
            if ("vis" == t)
                return new h({
                    str: e
                });
            if ("bmp" == t)
                return new u({
                    str: e
                });
            throw new Error("unsupported directory string type: type=" + t + " value=" + e)
        }
        ,
        this.tohex = function () {
            null == this.dsType && (this.dsType = this._getDsType());
            var t = at.asn1.x509.OID.atype2obj(this.sType)
                , e = this.getValueObj(this.dsType, this.sValue)
                , r = new i({
                array: [t, e]
            });
            return this.TLV = r.tohex(),
                this.TLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.x509.AttributeTypeAndValue, at.asn1.ASN1Object),
at.asn1.x509.SubjectPublicKeyInfo = function (t) {
    at.asn1.x509.SubjectPublicKeyInfo.superclass.constructor.call(this);
    var e = at
        , r = e.asn1
        , i = r.DERInteger
        , n = r.DERBitString
        , s = r.DERObjectIdentifier
        , a = r.DERSequence
        , o = r.ASN1Util.newObject
        , h = r.x509.AlgorithmIdentifier
        , u = e.crypto;
    u.ECDSA,
        u.DSA,
        this.getASN1Object = function () {
            if (null == this.asn1AlgId || null == this.asn1SubjPKey)
                throw "algId and/or subjPubKey not set";
            return new a({
                array: [this.asn1AlgId, this.asn1SubjPKey]
            })
        }
        ,
        this.tohex = function () {
            var t = this.getASN1Object();
            return this.hTLV = t.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setPubKey = function (t) {
            try {
                if (t instanceof tt) {
                    var e = o({
                        seq: [{
                            int: {
                                bigint: t.n
                            }
                        }, {
                            int: {
                                int: t.e
                            }
                        }]
                    }).tohex();
                    this.asn1AlgId = new h({
                        name: "rsaEncryption"
                    }),
                        this.asn1SubjPKey = new n({
                            hex: "00" + e
                        })
                }
            } catch (u) {
            }
            try {
                if (t instanceof at.crypto.ECDSA) {
                    var r = new s({
                        name: t.curveName
                    });
                    this.asn1AlgId = new h({
                        name: "ecPublicKey",
                        asn1params: r
                    }),
                        this.asn1SubjPKey = new n({
                            hex: "00" + t.pubKeyHex
                        })
                }
            } catch (u) {
            }
            try {
                if (t instanceof at.crypto.DSA) {
                    r = new o({
                        seq: [{
                            int: {
                                bigint: t.p
                            }
                        }, {
                            int: {
                                bigint: t.q
                            }
                        }, {
                            int: {
                                bigint: t.g
                            }
                        }]
                    });
                    this.asn1AlgId = new h({
                        name: "dsa",
                        asn1params: r
                    });
                    var a = new i({
                        bigint: t.y
                    });
                    this.asn1SubjPKey = new n({
                        hex: "00" + a.tohex()
                    })
                }
            } catch (u) {
            }
        }
        ,
    void 0 !== t && this.setPubKey(t)
}
,
Mt(at.asn1.x509.SubjectPublicKeyInfo, at.asn1.ASN1Object),
at.asn1.x509.Time = function (t) {
    at.asn1.x509.Time.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERUTCTime
        , i = e.DERGeneralizedTime;
    this.params = null,
        this.type = null,
        this.setTimeParams = function (t) {
            this.timeParams = t
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.getType = function (t) {
            return t.match(/^[0-9]{12}Z$/) ? "utc" : t.match(/^[0-9]{14}Z$/) ? "gen" : t.match(/^[0-9]{12}\.[0-9]+Z$/) ? "utc" : t.match(/^[0-9]{14}\.[0-9]+Z$/) ? "gen" : null
        }
        ,
        this.tohex = function () {
            var t = this.params
                , e = null;
            if ("string" == typeof t && (t = {
                str: t
            }),
            null == t || !t.str || null != t.type && null != t.type || (t.type = this.getType(t.str)),
                null != t && t.str ? ("utc" == t.type && (e = new r(t.str)),
                "gen" == t.type && (e = new i(t.str))) : e = "gen" == this.type ? new i : new r,
            null == e)
                throw new Error("wrong setting for Time");
            return this.TLV = e.tohex(),
                this.TLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
at.asn1.x509.Time_bak = function (t) {
    at.asn1.x509.Time_bak.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERUTCTime
        , i = e.DERGeneralizedTime;
    this.setTimeParams = function (t) {
        this.timeParams = t
    }
        ,
        this.tohex = function () {
            var t = null;
            return t = null != this.timeParams ? "utc" == this.type ? new r(this.timeParams) : new i(this.timeParams) : "utc" == this.type ? new r : new i,
                this.TLV = t.tohex(),
                this.TLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.type = "utc",
    void 0 !== t && (void 0 !== t.type ? this.type = t.type : void 0 !== t.str && (t.str.match(/^[0-9]{12}Z$/) && (this.type = "utc"),
    t.str.match(/^[0-9]{14}Z$/) && (this.type = "gen")),
        this.timeParams = t)
}
,
Mt(at.asn1.x509.Time, at.asn1.ASN1Object),
at.asn1.x509.AlgorithmIdentifier = function (t) {
    at.asn1.x509.AlgorithmIdentifier.superclass.constructor.call(this),
        this.nameAlg = null,
        this.asn1Alg = null,
        this.asn1Params = null,
        this.paramEmpty = !1;
    var e = at.asn1
        , r = e.x509.AlgorithmIdentifier.PSSNAME2ASN1TLV;
    if (this.tohex = function () {
        if (null === this.nameAlg && null === this.asn1Alg)
            throw new Error("algorithm not specified");
        if (null !== this.nameAlg) {
            var t = null;
            for (var i in r)
                i === this.nameAlg && (t = r[i]);
            if (null !== t)
                return this.hTLV = t,
                    this.hTLV
        }
        null !== this.nameAlg && null === this.asn1Alg && (this.asn1Alg = e.x509.OID.name2obj(this.nameAlg));
        var n = [this.asn1Alg];
        null !== this.asn1Params && n.push(this.asn1Params);
        var s = new e.DERSequence({
            array: n
        });
        return this.hTLV = s.tohex(),
            this.hTLV
    }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && (void 0 !== t.name && (this.nameAlg = t.name),
    void 0 !== t.asn1params && (this.asn1Params = t.asn1params),
    void 0 !== t.paramempty && (this.paramEmpty = t.paramempty)),
    null === this.asn1Params && !1 === this.paramEmpty && null !== this.nameAlg) {
        void 0 !== this.nameAlg.name && (this.nameAlg = this.nameAlg.name);
        var i = this.nameAlg.toLowerCase();
        "withdsa" !== i.substr(-7, 7) && "withecdsa" !== i.substr(-9, 9) && (this.asn1Params = new e.DERNull)
    }
}
,
Mt(at.asn1.x509.AlgorithmIdentifier, at.asn1.ASN1Object),
at.asn1.x509.AlgorithmIdentifier.PSSNAME2ASN1TLV = {
    SHAwithRSAandMGF1: "300d06092a864886f70d01010a3000",
    SHA256withRSAandMGF1: "303d06092a864886f70d01010a3030a00d300b0609608648016503040201a11a301806092a864886f70d010108300b0609608648016503040201a203020120",
    SHA384withRSAandMGF1: "303d06092a864886f70d01010a3030a00d300b0609608648016503040202a11a301806092a864886f70d010108300b0609608648016503040202a203020130",
    SHA512withRSAandMGF1: "303d06092a864886f70d01010a3030a00d300b0609608648016503040203a11a301806092a864886f70d010108300b0609608648016503040203a203020140"
},
at.asn1.x509.GeneralName = function (t) {
    at.asn1.x509.GeneralName.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.x509
        , i = r.X500Name
        , n = r.OtherName
        , s = e.DERIA5String;
    e.DERPrintableString;
    var a = e.DEROctetString
        , o = e.DERTaggedObject
        , h = e.ASN1Object
        , u = Error;
    this.params = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t, e, r = this.params, c = !1;
            if (void 0 !== r.other)
                t = "a0",
                    e = new n(r.other);
            else if (void 0 !== r.rfc822)
                t = "81",
                    e = new s({
                        str: r.rfc822
                    });
            else if (void 0 !== r.dns)
                t = "82",
                    e = new s({
                        str: r.dns
                    });
            else if (void 0 !== r.dn)
                t = "a4",
                    c = !0,
                    e = "string" == typeof r.dn ? new i({
                        str: r.dn
                    }) : r.dn instanceof at.asn1.x509.X500Name ? r.dn : new i(r.dn);
            else if (void 0 !== r.ldapdn)
                t = "a4",
                    c = !0,
                    e = new i({
                        ldapstr: r.ldapdn
                    });
            else if (void 0 !== r.certissuer || void 0 !== r.certsubj) {
                var l, f;
                t = "a4",
                    c = !0;
                var g = null;
                if (void 0 !== r.certsubj ? (l = !1,
                    f = r.certsubj) : (l = !0,
                    f = r.certissuer),
                    f.match(/^[0-9A-Fa-f]+$/),
                -1 != f.indexOf("-----BEGIN ") && (g = Ft(f)),
                null == g)
                    throw new Error("certsubj/certissuer not cert");
                var p, d = new Wt;
                d.hex = g,
                    p = l ? d.getIssuerHex() : d.getSubjectHex(),
                    (e = new h).hTLV = p
            } else if (void 0 !== r.uri)
                t = "86",
                    e = new s({
                        str: r.uri
                    });
            else {
                if (void 0 === r.ip)
                    throw new u("improper params");
                var v;
                t = "87";
                var m = r.ip;
                try {
                    if (m.match(/^[0-9a-f]+$/)) {
                        var y = m.length;
                        if (8 != y && 16 != y && 32 != y && 64 != y)
                            throw "err";
                        v = m
                    } else
                        v = Tt(m)
                } catch (x) {
                    throw new u("malformed IP address: " + r.ip + ":" + x.message)
                }
                e = new a({
                    hex: v
                })
            }
            return new o({
                tag: t,
                explicit: c,
                obj: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.x509.GeneralName, at.asn1.ASN1Object),
at.asn1.x509.GeneralNames = function (t) {
    at.asn1.x509.GeneralNames.superclass.constructor.call(this);
    var e = at.asn1;
    this.setByParamArray = function (t) {
        for (var r = 0; r < t.length; r++) {
            var i = new e.x509.GeneralName(t[r]);
            this.asn1Array.push(i)
        }
    }
        ,
        this.tohex = function () {
            return new e.DERSequence({
                array: this.asn1Array
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.asn1Array = new Array,
    void 0 !== t && this.setByParamArray(t)
}
,
Mt(at.asn1.x509.GeneralNames, at.asn1.ASN1Object),
at.asn1.x509.OtherName = function (t) {
    at.asn1.x509.OtherName.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERObjectIdentifier
        , i = e.DERSequence
        , n = e.ASN1Util.newObject;
    this.params = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t = this.params;
            if (null == t.oid || null == t.value)
                throw new Error("oid or value not specified");
            var e = new r({
                oid: t.oid
            })
                , s = n({
                tag: {
                    tag: "a0",
                    explicit: !0,
                    obj: t.value
                }
            });
            return new i({
                array: [e, s]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.x509.OtherName, at.asn1.ASN1Object),
at.asn1.x509.OID = new function () {
    var t = at.asn1.DERObjectIdentifier;
    this.name2oidList = {
        sha1: "1.3.14.3.2.26",
        sha256: "2.16.840.1.101.3.4.2.1",
        sha384: "2.16.840.1.101.3.4.2.2",
        sha512: "2.16.840.1.101.3.4.2.3",
        sha224: "2.16.840.1.101.3.4.2.4",
        md5: "1.2.840.113549.2.5",
        md2: "1.3.14.7.2.2.1",
        ripemd160: "1.3.36.3.2.1",
        MD2withRSA: "1.2.840.113549.1.1.2",
        MD4withRSA: "1.2.840.113549.1.1.3",
        MD5withRSA: "1.2.840.113549.1.1.4",
        SHA1withRSA: "1.2.840.113549.1.1.5",
        "pkcs1-MGF": "1.2.840.113549.1.1.8",
        rsaPSS: "1.2.840.113549.1.1.10",
        SHA224withRSA: "1.2.840.113549.1.1.14",
        SHA256withRSA: "1.2.840.113549.1.1.11",
        SHA384withRSA: "1.2.840.113549.1.1.12",
        SHA512withRSA: "1.2.840.113549.1.1.13",
        SHA1withECDSA: "1.2.840.10045.4.1",
        SHA224withECDSA: "1.2.840.10045.4.3.1",
        SHA256withECDSA: "1.2.840.10045.4.3.2",
        SHA384withECDSA: "1.2.840.10045.4.3.3",
        SHA512withECDSA: "1.2.840.10045.4.3.4",
        dsa: "1.2.840.10040.4.1",
        SHA1withDSA: "1.2.840.10040.4.3",
        SHA224withDSA: "2.16.840.1.101.3.4.3.1",
        SHA256withDSA: "2.16.840.1.101.3.4.3.2",
        rsaEncryption: "1.2.840.113549.1.1.1",
        commonName: "2.5.4.3",
        countryName: "2.5.4.6",
        localityName: "2.5.4.7",
        stateOrProvinceName: "2.5.4.8",
        streetAddress: "2.5.4.9",
        organizationName: "2.5.4.10",
        organizationalUnitName: "2.5.4.11",
        domainComponent: "0.9.2342.19200300.100.1.25",
        userId: "0.9.2342.19200300.100.1.1",
        surname: "2.5.4.4",
        givenName: "2.5.4.42",
        title: "2.5.4.12",
        distinguishedName: "2.5.4.49",
        emailAddress: "1.2.840.113549.1.9.1",
        description: "2.5.4.13",
        businessCategory: "2.5.4.15",
        postalCode: "2.5.4.17",
        uniqueIdentifier: "2.5.4.45",
        organizationIdentifier: "2.5.4.97",
        jurisdictionOfIncorporationL: "1.3.6.1.4.1.311.60.2.1.1",
        jurisdictionOfIncorporationSP: "1.3.6.1.4.1.311.60.2.1.2",
        jurisdictionOfIncorporationC: "1.3.6.1.4.1.311.60.2.1.3",
        subjectDirectoryAttributes: "2.5.29.9",
        subjectKeyIdentifier: "2.5.29.14",
        keyUsage: "2.5.29.15",
        subjectAltName: "2.5.29.17",
        issuerAltName: "2.5.29.18",
        basicConstraints: "2.5.29.19",
        cRLNumber: "2.5.29.20",
        cRLReason: "2.5.29.21",
        nameConstraints: "2.5.29.30",
        cRLDistributionPoints: "2.5.29.31",
        certificatePolicies: "2.5.29.32",
        anyPolicy: "2.5.29.32.0",
        policyMappings: "2.5.29.33",
        authorityKeyIdentifier: "2.5.29.35",
        policyConstraints: "2.5.29.36",
        extKeyUsage: "2.5.29.37",
        inhibitAnyPolicy: "2.5.29.54",
        authorityInfoAccess: "1.3.6.1.5.5.7.1.1",
        ocsp: "1.3.6.1.5.5.7.48.1",
        ocspBasic: "1.3.6.1.5.5.7.48.1.1",
        ocspNonce: "1.3.6.1.5.5.7.48.1.2",
        ocspNoCheck: "1.3.6.1.5.5.7.48.1.5",
        caIssuers: "1.3.6.1.5.5.7.48.2",
        anyExtendedKeyUsage: "2.5.29.37.0",
        serverAuth: "1.3.6.1.5.5.7.3.1",
        clientAuth: "1.3.6.1.5.5.7.3.2",
        codeSigning: "1.3.6.1.5.5.7.3.3",
        emailProtection: "1.3.6.1.5.5.7.3.4",
        timeStamping: "1.3.6.1.5.5.7.3.8",
        ocspSigning: "1.3.6.1.5.5.7.3.9",
        smtpUTF8Mailbox: "1.3.6.1.5.5.7.8.9",
        dateOfBirth: "1.3.6.1.5.5.7.9.1",
        placeOfBirth: "1.3.6.1.5.5.7.9.2",
        gender: "1.3.6.1.5.5.7.9.3",
        countryOfCitizenship: "1.3.6.1.5.5.7.9.4",
        countryOfResidence: "1.3.6.1.5.5.7.9.5",
        ecPublicKey: "1.2.840.10045.2.1",
        "P-256": "1.2.840.10045.3.1.7",
        secp256r1: "1.2.840.10045.3.1.7",
        secp256k1: "1.3.132.0.10",
        secp384r1: "1.3.132.0.34",
        secp521r1: "1.3.132.0.35",
        pkcs5PBES2: "1.2.840.113549.1.5.13",
        pkcs5PBKDF2: "1.2.840.113549.1.5.12",
        "des-EDE3-CBC": "1.2.840.113549.3.7",
        data: "1.2.840.113549.1.7.1",
        "signed-data": "1.2.840.113549.1.7.2",
        "enveloped-data": "1.2.840.113549.1.7.3",
        "digested-data": "1.2.840.113549.1.7.5",
        "encrypted-data": "1.2.840.113549.1.7.6",
        "authenticated-data": "1.2.840.113549.1.9.16.1.2",
        tstinfo: "1.2.840.113549.1.9.16.1.4",
        signingCertificate: "1.2.840.113549.1.9.16.2.12",
        timeStampToken: "1.2.840.113549.1.9.16.2.14",
        signaturePolicyIdentifier: "1.2.840.113549.1.9.16.2.15",
        etsArchiveTimeStamp: "1.2.840.113549.1.9.16.2.27",
        signingCertificateV2: "1.2.840.113549.1.9.16.2.47",
        etsArchiveTimeStampV2: "1.2.840.113549.1.9.16.2.48",
        extensionRequest: "1.2.840.113549.1.9.14",
        contentType: "1.2.840.113549.1.9.3",
        messageDigest: "1.2.840.113549.1.9.4",
        signingTime: "1.2.840.113549.1.9.5",
        counterSignature: "1.2.840.113549.1.9.6",
        archiveTimeStampV3: "0.4.0.1733.2.4",
        pdfRevocationInfoArchival: "1.2.840.113583.1.1.8",
        adobeTimeStamp: "1.2.840.113583.1.1.9.1",
        smimeMailboxLegacy: "2.23.140.1.5.1.1",
        smimeMailboxMulti: "2.23.140.1.5.1.2",
        smimeMailboxStrict: "2.23.140.1.5.1.3",
        smimeOrganizationLegacy: "2.23.140.1.5.2.1",
        smimeOrganizationMulti: "2.23.140.1.5.2.2",
        smimeOrganizationStrict: "2.23.140.1.5.2.3",
        smimeSponsorLegacy: "2.23.140.1.5.3.1",
        smimeSponsorMulti: "2.23.140.1.5.3.2",
        smimeSponsorStrict: "2.23.140.1.5.3.3",
        smimeIndividualLegacy: "2.23.140.1.5.4.1",
        smimeIndividualMulti: "2.23.140.1.5.4.2",
        smimeIndividualStrict: "2.23.140.1.5.4.3"
    },
        this.atype2oidList = {
            CN: "2.5.4.3",
            L: "2.5.4.7",
            ST: "2.5.4.8",
            O: "2.5.4.10",
            OU: "2.5.4.11",
            C: "2.5.4.6",
            STREET: "2.5.4.9",
            DC: "0.9.2342.19200300.100.1.25",
            UID: "0.9.2342.19200300.100.1.1",
            SN: "2.5.4.4",
            T: "2.5.4.12",
            GN: "2.5.4.42",
            DN: "2.5.4.49",
            E: "1.2.840.113549.1.9.1",
            description: "2.5.4.13",
            businessCategory: "2.5.4.15",
            postalCode: "2.5.4.17",
            serialNumber: "2.5.4.5",
            uniqueIdentifier: "2.5.4.45",
            organizationIdentifier: "2.5.4.97",
            jurisdictionOfIncorporationL: "1.3.6.1.4.1.311.60.2.1.1",
            jurisdictionOfIncorporationSP: "1.3.6.1.4.1.311.60.2.1.2",
            jurisdictionOfIncorporationC: "1.3.6.1.4.1.311.60.2.1.3"
        },
        this.objCache = {},
        this.name2obj = function (e) {
            if (void 0 !== this.objCache[e])
                return this.objCache[e];
            if (void 0 === this.name2oidList[e])
                throw "Name of ObjectIdentifier not defined: " + e;
            var r = this.name2oidList[e]
                , i = new t({
                oid: r
            });
            return this.objCache[e] = i,
                i
        }
        ,
        this.atype2obj = function (e) {
            if (void 0 !== this.objCache[e])
                return this.objCache[e];
            var r;
            if (e.match(/^\d+\.\d+\.[0-9.]+$/))
                r = e;
            else if (void 0 !== this.atype2oidList[e])
                r = this.atype2oidList[e];
            else {
                if (void 0 === this.name2oidList[e])
                    throw new Error("AttributeType name undefined: " + e);
                r = this.name2oidList[e]
            }
            var i = new t({
                oid: r
            });
            return this.objCache[e] = i,
                i
        }
        ,
        this.registerOIDs = function (t) {
            if (this.checkOIDs(t))
                for (var e in t)
                    this.name2oidList[e] = t[e]
        }
        ,
        this.checkOIDs = function (t) {
            try {
                var e = Object.keys(t);
                return 0 != e.length && (e.map((function (t, e, r) {
                        if (!this[t].match(/^[0-2]\.[0-9.]+$/))
                            throw new Error("value is not OID")
                    }
                ), t),
                    !0)
            } catch (r) {
                return !1
            }
        }
}
,
at.asn1.x509.OID.oid2name = function (t) {
    var e = at.asn1.x509.OID.name2oidList;
    for (var r in e)
        if (e[r] == t)
            return r;
    return ""
}
,
at.asn1.x509.OID.oid2atype = function (t) {
    var e = at.asn1.x509.OID.atype2oidList;
    for (var r in e)
        if (e[r] == t)
            return r;
    return t
}
,
at.asn1.x509.OID.name2oid = function (t) {
    if (t.match(/^[0-9.]+$/))
        return t;
    var e = at.asn1.x509.OID.name2oidList;
    return void 0 === e[t] ? "" : e[t]
}
,
at.asn1.x509.X509Util = {},
at.asn1.x509.X509Util.newCertPEM = function (t) {
    var e = at.asn1.x509;
    return e.TBSCertificate,
        new (0,
            e.Certificate)(t).getPEM()
}
,
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
void 0 !== at.asn1.cms && at.asn1.cms || (at.asn1.cms = {}),
at.asn1.cms.Attribute = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSequence
        , n = r.DERSet
        , s = r.DERObjectIdentifier;
    this.params = null,
        this.typeOid = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.getValueArray = function () {
            throw new e("not yet implemented abstract")
        }
        ,
        this.tohex = function () {
            var t = new s({
                oid: this.typeOid
            })
                , e = new n({
                array: this.getValueArray()
            });
            return new i({
                array: [t, e]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
}
,
Mt(at.asn1.cms.Attribute, at.asn1.ASN1Object),
at.asn1.cms.ContentType = function (t) {
    var e = at.asn1;
    e.cms.ContentType.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.3",
        this.getValueArray = function () {
            return [new e.DERObjectIdentifier(this.params.type)]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.ContentType, at.asn1.cms.Attribute),
at.asn1.cms.MessageDigest = function (t) {
    var e = at.asn1
        , r = e.DEROctetString;
    e.cms.MessageDigest.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.4",
        this.getValueArray = function () {
            return [new r(this.params)]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.MessageDigest, at.asn1.cms.Attribute),
at.asn1.cms.SigningTime = function (t) {
    var e = at.asn1;
    e.cms.SigningTime.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.5",
        this.getValueArray = function () {
            return [new e.x509.Time(this.params)]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SigningTime, at.asn1.cms.Attribute),
at.asn1.cms.SigningCertificate = function (t) {
    var e = Error
        , r = at
        , i = r.asn1
        , n = i.DERSequence
        , s = i.cms
        , a = s.ESSCertID;
    r.crypto,
        s.SigningCertificate.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.16.2.12",
        this.getValueArray = function () {
            if (null == this.params || null == this.params || null == this.params.array)
                throw new e("parameter 'array' not specified");
            for (var r = this.params.array, i = [], s = 0; s < r.length; s++) {
                var o = r[s];
                0 != t.hasis || "string" != typeof o || -1 == o.indexOf("-----BEGIN") && !ut.isASN1HEX(o) || (o = {
                    cert: o
                }),
                0 != o.hasis && 0 == t.hasis && (o.hasis = !1),
                    i.push(new a(o))
            }
            var h = new n({
                array: i
            });
            return [new n({
                array: [h]
            })]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SigningCertificate, at.asn1.cms.Attribute),
at.asn1.cms.ESSCertID = function (t) {
    at.asn1.cms.ESSCertID.superclass.constructor.call(this);
    var e = Error
        , r = at
        , i = r.asn1
        , n = i.DEROctetString
        , s = i.DERSequence
        , a = i.cms.IssuerSerial;
    this.params = null,
        this.getCertHash = function (t, i) {
            if (null != t.hash)
                return t.hash;
            if ("string" == typeof t && -1 == t.indexOf("-----BEGIN") && !ut.isASN1HEX(t))
                return t;
            var n, s, a;
            if ("string" == typeof t)
                n = t;
            else {
                if (null == t.cert)
                    throw new e("hash nor cert unspecified");
                n = t.cert
            }
            if (s = -1 != n.indexOf("-----BEGIN") ? Ft(n) : n,
            "string" == typeof t && (-1 != t.indexOf("-----BEGIN") ? s = Ft(t) : ut.isASN1HEX(t) && (s = t)),
            null != t.alg)
                a = t.alg;
            else {
                if (null == i)
                    throw new e("hash alg unspecified");
                a = i
            }
            return r.crypto.Util.hashHex(s, a)
        }
        ,
        this.tohex = function () {
            var t = this.params
                , e = this.getCertHash(t, "sha1")
                , r = [];
            return r.push(new n({
                hex: e
            })),
            ("string" == typeof t && -1 != t.indexOf("-----BEGIN") || null != t.cert && 0 != t.hasis || null != t.issuer && null != t.serial) && r.push(new a(t)),
                new s({
                    array: r
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.ESSCertID, at.asn1.ASN1Object),
at.asn1.cms.SigningCertificateV2 = function (t) {
    var e = Error
        , r = at
        , i = r.asn1
        , n = i.DERSequence;
    i.x509;
    var s = i.cms
        , a = s.ESSCertIDv2;
    r.crypto,
        s.SigningCertificateV2.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.16.2.47",
        this.getValueArray = function () {
            if (null == this.params || null == this.params || null == this.params.array)
                throw new e("parameter 'array' not specified");
            for (var r = this.params.array, i = [], s = 0; s < r.length; s++) {
                var o = r[s];
                null == t.alg && 0 != t.hasis || "string" != typeof o || -1 == o.indexOf("-----BEGIN") && !ut.isASN1HEX(o) || (o = {
                    cert: o
                }),
                null == o.alg && null != t.alg && (o.alg = t.alg),
                0 != o.hasis && 0 == t.hasis && (o.hasis = !1),
                    i.push(new a(o))
            }
            var h = new n({
                array: i
            });
            return [new n({
                array: [h]
            })]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SigningCertificateV2, at.asn1.cms.Attribute),
at.asn1.cms.ESSCertIDv2 = function (t) {
    at.asn1.cms.ESSCertIDv2.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DEROctetString
        , i = e.DERSequence
        , n = e.cms.IssuerSerial
        , s = e.x509.AlgorithmIdentifier;
    this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = this.getCertHash(t, "sha256")
                , a = [];
            return null != t.alg && "sha256" != t.alg && a.push(new s({
                name: t.alg
            })),
                a.push(new r({
                    hex: e
                })),
            ("string" == typeof t && -1 != t.indexOf("-----BEGIN") || null != t.cert && 0 != t.hasis || null != t.issuer && null != t.serial) && a.push(new n(t)),
                new i({
                    array: a
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.ESSCertIDv2, at.asn1.cms.ESSCertID),
at.asn1.cms.IssuerSerial = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERInteger
        , n = r.DERSequence
        , s = r.cms
        , a = r.x509.GeneralNames
        , o = Wt;
    s.IssuerSerial.superclass.constructor.call(this),
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t, r, s = this.params;
            if ("string" == typeof s && -1 != s.indexOf("-----BEGIN") || null != s.cert) {
                var h;
                h = null != s.cert ? s.cert : s;
                var u = new o;
                u.readCertPEM(h),
                    t = u.getIssuer(),
                    r = {
                        hex: u.getSerialNumberHex()
                    }
            } else {
                if (null == s.issuer || !s.serial)
                    throw new e("cert or issuer and serial parameter not specified");
                t = s.issuer,
                    r = s.serial
            }
            var c = new a([{
                dn: t
            }])
                , l = new i(r);
            return new n({
                array: [c, l]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.IssuerSerial, at.asn1.ASN1Object),
at.asn1.cms.SignerIdentifier = function (t) {
    var e = at.asn1;
    e.DERInteger,
        e.DERSequence;
    var r = e.cms
        , i = r.IssuerAndSerialNumber
        , n = r.SubjectKeyIdentifier;
    e.x509.X500Name,
        r.SignerIdentifier.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if ("isssn" == t.type)
                return new i(t).tohex();
            if ("skid" == t.type)
                return new n(t).tohex();
            throw new Error("wrong property for isssn or skid")
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SignerIdentifier, at.asn1.ASN1Object),
at.asn1.cms.IssuerAndSerialNumber = function (t) {
    var e = at.asn1
        , r = e.DERInteger
        , i = e.DERSequence
        , n = e.cms
        , s = e.x509.X500Name
        , a = Wt
        , o = Error;
    n.IssuerAndSerialNumber.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t, e, n = this.params;
            if ("string" == typeof n && -1 != n.indexOf("-----BEGIN") || null != n.cert) {
                var h;
                h = null != n.cert ? n.cert : n;
                var u = new a;
                u.readCertPEM(h),
                    t = u.getIssuer(),
                    e = {
                        hex: u.getSerialNumberHex()
                    }
            } else {
                if (null == n.issuer || !n.serial)
                    throw new o("cert or issuer and serial parameter not specified");
                t = n.issuer,
                    e = n.serial
            }
            var c = new s(t)
                , l = new r(e);
            return new i({
                array: [c, l]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.IssuerAndSerialNumber, at.asn1.ASN1Object),
at.asn1.cms.SubjectKeyIdentifier = function (t) {
    var e = at.asn1;
    e.DERInteger,
        e.DERSequence;
    var r = e.ASN1Util.newObject
        , i = e.cms;
    i.IssuerAndSerialName,
        i.SubjectKeyIdentifier,
        e.x509.X500Name;
    var n = Wt
        , s = Error;
    i.SubjectKeyIdentifier.superclass.constructor.call(this),
        this.tohex = function () {
            var t, e = this.params;
            if (null == e.cert && null == e.skid)
                throw new s("property cert nor skid undefined");
            null != e.cert ? t = new n(e.cert).getExtSubjectKeyIdentifier().kid.hex : null != e.skid && (t = e.skid);
            return r({
                tag: {
                    tage: "a0",
                    obj: {
                        octstr: {
                            hex: t
                        }
                    }
                }
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SubjectKeyIdentifier, at.asn1.ASN1Object),
at.asn1.cms.AttributeList = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSet
        , n = r.cms;
    n.AttributeList.superclass.constructor.call(this),
        this.params = null,
        this.hTLV = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t = this.params;
            if (null != this.hTLV)
                return this.hTLV;
            var r = !0;
            null != t.sortflag && (r = t.sortflag);
            for (var s = t.array, a = [], o = 0; o < s.length; o++) {
                var h = s[o]
                    , u = h.attr;
                if ("contentType" == u)
                    a.push(new n.ContentType(h));
                else if ("messageDigest" == u)
                    a.push(new n.MessageDigest(h));
                else if ("signingTime" == u)
                    a.push(new n.SigningTime(h));
                else if ("signingCertificate" == u)
                    a.push(new n.SigningCertificate(h));
                else if ("signingCertificateV2" == u)
                    a.push(new n.SigningCertificateV2(h));
                else if ("signaturePolicyIdentifier" == u)
                    a.push(new at.asn1.cades.SignaturePolicyIdentifier(h));
                else {
                    if ("signatureTimeStamp" != u && "timeStampToken" != u)
                        throw new e("unknown attr: " + u);
                    a.push(new at.asn1.cades.SignatureTimeStamp(h))
                }
            }
            var c = new i({
                array: a,
                sortflag: r
            });
            return this.hTLV = c.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.AttributeList, at.asn1.ASN1Object),
at.asn1.cms.SignerInfo = function (t) {
    var e = Error
        , r = at
        , i = r.asn1
        , n = i.DERInteger
        , s = i.DEROctetString
        , a = i.DERSequence
        , o = i.DERTaggedObject
        , h = i.cms
        , u = h.SignerIdentifier
        , c = h.AttributeList;
    h.ContentType,
        h.EncapsulatedContentInfo,
        h.MessageDigest,
        h.SignedData;
    var l = i.x509.AlgorithmIdentifier
        , f = r.crypto
        , g = _t;
    h.SignerInfo.superclass.constructor.call(this),
        this.params = null,
        this.sign = function () {
            var t = this.params
                , e = t.sigalg
                , r = new c(t.sattrs).tohex()
                , i = g.getKey(t.signkey)
                , n = new f.Signature({
                alg: e
            });
            n.init(i),
                n.updateHex(r);
            var s = n.sign();
            t.sighex = s
        }
        ,
        this.tohex = function () {
            var t = this.params
                , r = [];
            if (r.push(new n({
                int: t.version
            })),
                r.push(new u(t.id)),
                r.push(new l({
                    name: t.hashalg
                })),
            null != t.sattrs) {
                var i = new c(t.sattrs);
                try {
                    r.push(new o({
                        tag: "a0",
                        explicit: !1,
                        obj: i
                    }))
                } catch (h) {
                    throw new e("si sattr error: " + h)
                }
            }
            if (null != t.sigalgfield ? r.push(new l({
                name: t.sigalgfield
            })) : r.push(new l({
                name: t.sigalg
            })),
            null == t.sighex && null != t.signkey && this.sign(),
                r.push(new s({
                    hex: t.sighex
                })),
            null != t.uattrs) {
                i = new c(t.uattrs);
                try {
                    r.push(new o({
                        tag: "a1",
                        explicit: !1,
                        obj: i
                    }))
                } catch (h) {
                    throw new e("si uattr error: " + h)
                }
            }
            return new a({
                array: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SignerInfo, at.asn1.ASN1Object),
at.asn1.cms.EncapsulatedContentInfo = function (t) {
    var e = at.asn1
        , r = e.DERTaggedObject
        , i = e.DERSequence
        , n = e.DERObjectIdentifier
        , s = e.DEROctetString;
    e.cms.EncapsulatedContentInfo.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = [];
            if (e.push(new n(t.type)),
            null != t.content && (null != t.content.hex || null != t.content.str) && 1 != t.isDetached) {
                var a = new s(t.content)
                    , o = new r({
                    tag: "a0",
                    explicit: !0,
                    obj: a
                });
                e.push(o)
            }
            return new i({
                array: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.EncapsulatedContentInfo, at.asn1.ASN1Object),
at.asn1.cms.ContentInfo = function (t) {
    var e = at.asn1
        , r = e.DERTaggedObject
        , i = e.DERSequence
        , n = e.DERObjectIdentifier;
    e.x509.OID.name2obj,
        at.asn1.cms.ContentInfo.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = [];
            e.push(new n(t.type));
            var s = new r({
                tag: "a0",
                explicit: !0,
                obj: t.obj
            });
            return e.push(s),
                new i({
                    array: e
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.ContentInfo, at.asn1.ASN1Object),
at.asn1.cms.SignedData = function (t) {
    var e = at.asn1;
    e.ASN1Object;
    var r = e.DERInteger
        , i = e.DERSet
        , n = e.DERSequence;
    e.DERTaggedObject;
    var s = e.cms
        , a = s.EncapsulatedContentInfo
        , o = s.SignerInfo
        , h = s.ContentInfo
        , u = s.CertificateSet
        , c = s.RevocationInfoChoices
        , l = e.x509.AlgorithmIdentifier;
    at.asn1.cms.SignedData.superclass.constructor.call(this),
        this.params = null,
        this.checkAndFixParam = function () {
            var t = this.params;
            this._setDigestAlgs(t),
                this._setContentTypeByEContent(t),
                this._setMessageDigestByEContent(t),
                this._setSignerInfoVersion(t),
                this._setSignedDataVersion(t)
        }
        ,
        this._setDigestAlgs = function (t) {
            for (var e = {}, r = t.sinfos, i = 0; i < r.length; i++) {
                e[r[i].hashalg] = 1
            }
            t.hashalgs = Object.keys(e).sort()
        }
        ,
        this._setContentTypeByEContent = function (t) {
            for (var e = t.econtent.type, r = t.sinfos, i = 0; i < r.length; i++) {
                var n = r[i];
                this._getAttrParamByName(n, "contentType").type = e
            }
        }
        ,
        this._setMessageDigestByEContent = function (t) {
            var e = t.econtent;
            t.econtent.type;
            var r = e.content.hex;
            null == r && "data" == e.type && null != e.content.str && (r = St(e.content.str));
            for (var i = t.sinfos, n = 0; n < i.length; n++) {
                var s = i[n]
                    , a = s.hashalg
                    , o = this._getAttrParamByName(s, "messageDigest")
                    , h = at.crypto.Util.hashHex(r, a);
                o.hex = h
            }
        }
        ,
        this._getAttrParamByName = function (t, e) {
            for (var r = t.sattrs.array, i = 0; i < r.length; i++)
                if (r[i].attr == e)
                    return r[i]
        }
        ,
        this._setSignerInfoVersion = function (t) {
            for (var e = t.sinfos, r = 0; r < e.length; r++) {
                var i = e[r]
                    , n = 1;
                "skid" == i.id.type && (n = 3),
                    i.version = n
            }
        }
        ,
        this._setSignedDataVersion = function (t) {
            var e = this._getSignedDataVersion(t);
            t.version = e
        }
        ,
        this._getSignedDataVersion = function (t) {
            if (null != t.revinfos)
                for (var e = t.revinfos, r = 0; r < e.length; r++) {
                    if (null != e[r].ocsp)
                        return 5
                }
            var i = t.sinfos;
            for (r = 0; r < i.length; r++) {
                if (3 == t.sinfos[r].version)
                    return 3
            }
            return "data" != t.econtent.type ? 3 : 1
        }
        ,
        this.tohex = function () {
            var t = this.params;
            null != this.getEncodedHexPrepare && this.getEncodedHexPrepare(),
            1 != t.fixed && this.checkAndFixParam();
            var e = [];
            e.push(new r({
                int: t.version
            }));
            for (var s = [], h = 0; h < t.hashalgs.length; h++) {
                var f = t.hashalgs[h];
                s.push(new l({
                    name: f
                }))
            }
            e.push(new i({
                array: s
            })),
                e.push(new a(t.econtent)),
            null != t.certs && e.push(new u(t.certs)),
            null != t.revinfos && e.push(new c(t.revinfos));
            var g = [];
            for (h = 0; h < t.sinfos.length; h++) {
                var p = t.sinfos[h];
                g.push(new o(p))
            }
            return e.push(new i({
                array: g
            })),
                new n({
                    array: e
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.getContentInfo = function () {
            return new h({
                type: "signed-data",
                obj: this
            })
        }
        ,
        this.getContentInfoEncodedHex = function () {
            return this.getContentInfo().tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.SignedData, at.asn1.ASN1Object),
at.asn1.cms.CertificateSet = function (t) {
    at.asn1.cms.CertificateSet.superclass.constructor.call(this);
    var e = Error
        , r = at.asn1
        , i = r.DERTaggedObject
        , n = r.DERSet
        , s = r.ASN1Object;
    this.params = null,
        this.tohex = function () {
            var t, r = this.params, a = [];
            if (r instanceof Array)
                t = r;
            else {
                if (null == r.array)
                    throw new e("cert array not specified");
                t = r.array
            }
            for (var o = 0; o < t.length; o++) {
                var h = Ft(t[o])
                    , u = new s;
                u.hTLV = h,
                    a.push(u)
            }
            var c = {
                array: a
            };
            0 == r.sortflag && (c.sortflag = !1);
            var l = new n(c);
            return new i({
                tag: "a0",
                explicit: !1,
                obj: l
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.CertificateSet, at.asn1.ASN1Object),
at.asn1.cms.RevocationInfoChoices = function (t) {
    at.asn1.cms.RevocationInfoChoices.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (!t instanceof Array)
                throw new Error("params is not array");
            for (var e = [], r = 0; r < t.length; r++)
                e.push(new at.asn1.cms.RevocationInfoChoice(t[r]));
            return at.asn1.ASN1Util.newObject({
                tag: {
                    tagi: "a1",
                    obj: {
                        set: e
                    }
                }
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.RevocationInfoChoices, at.asn1.ASN1Object),
at.asn1.cms.RevocationInfoChoice = function (t) {
    at.asn1.cms.RevocationInfoChoice.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (null != t.crl && "string" == typeof t.crl) {
                var e = t.crl;
                return -1 != t.crl.indexOf("-----BEGIN") && (e = Ft(t.crl)),
                    e
            }
            if (null != t.ocsp)
                return at.asn1.ASN1Util.newObject({
                    tag: {
                        tagi: "a1",
                        obj: new at.asn1.cms.OtherRevocationFormat(t)
                    }
                }).tohex();
            throw new Error("property crl or ocsp undefined")
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.RevocationInfoChoice, at.asn1.ASN1Object),
at.asn1.cms.OtherRevocationFormat = function (t) {
    at.asn1.cms.OtherRevocationFormat.superclass.constructor.call(this);
    var e = Error
        , r = at
        , i = r.asn1.ASN1Util.newObject
        , n = r.lang.String.isHex;
    this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (null == t.ocsp)
                throw new e("property ocsp not specified");
            if (!n(t.ocsp) || !ut.isASN1HEX(t.ocsp))
                throw new e("ocsp value not ASN.1 hex string");
            return i({
                seq: [{
                    oid: "1.3.6.1.5.5.7.16.2"
                }, {
                    asn1: {
                        tlv: t.ocsp
                    }
                }]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cms.OtherRevocationFormat, at.asn1.ASN1Object),
at.asn1.cms.CMSUtil = new function () {
}
,
at.asn1.cms.CMSUtil.newSignedData = function (t) {
    return new at.asn1.cms.SignedData(t)
}
,
at.asn1.cms.CMSUtil.verifySignedData = function (t) {
    var e = at
        , r = e.asn1
        , i = r.cms;
    i.SignerInfo,
        i.SignedData,
        i.SigningTime,
        i.SigningCertificate,
        i.SigningCertificateV2,
        r.cades.SignaturePolicyIdentifier;
    var n = e.lang.String.isHex
        , s = ut
        , a = s.getVbyList
        , o = s.getTLVbyList
        , h = s.getIdxbyList
        , u = s.getChildIdx
        , c = s.getTLV
        , l = s.oidname
        , f = e.crypto.Util.hashHex;
    void 0 === t.cms && n(t.cms);
    var g = t.cms
        , p = function (t, e) {
        var r = e.idx;
        e.signerid_issuer1 = o(t, r, [1, 0], "30"),
            e.signerid_serial1 = a(t, r, [1, 1], "02"),
            e.hashalg = l(a(t, r, [2, 0], "06"));
        var i = h(t, r, [3], "a0");
        e.idxSignedAttrs = i,
            d(t, e, i);
        var n = u(t, r).length;
        if (n < 6)
            throw "malformed SignerInfo";
        e.sigalg = l(a(t, r, [n - 2, 0], "06")),
            e.sigval = a(t, r, [n - 1], "04")
    }
        , d = function (t, e, r) {
        var i = u(t, r);
        e.signedAttrIdxList = i;
        for (var n = 0; n < i.length; n++) {
            var s, o = i[n], h = a(t, o, [0], "06");
            "2a864886f70d010905" === h ? (s = mt(a(t, o, [1, 0])),
                e.saSigningTime = s) : "2a864886f70d010904" === h && (s = a(t, o, [1, 0], "04"),
                e.saMessageDigest = s)
        }
    }
        , v = function (t, e, r, i) {
        r.verifyDetail = {};
        var n = r.verifyDetail
            , s = e.parse.econtent
            , a = r.hashalg
            , o = r.saMessageDigest;
        n.validMessageDigest = !1,
        f(s, a) === o && (n.validMessageDigest = !0),
            function (t, e, r, i) {
                var n, s = e.parse.certsIdx;
                if (void 0 === e.certs) {
                    n = [],
                        e.certkeys = [];
                    for (var a = u(t, s), o = 0; o < a.length; o++) {
                        var h = c(t, a[o])
                            , l = new Wt;
                        l.readCertHex(h),
                            n[o] = l,
                            e.certkeys[o] = l.getPublicKey()
                    }
                    e.certs = n
                } else
                    n = e.certs;
                for (e.cccc = n.length,
                         e.cccci = a.length,
                         o = 0; o < n.length; o++) {
                    var f = l.getIssuerHex()
                        , g = l.getSerialNumberHex();
                    r.signerid_issuer1 === f && r.signerid_serial1 === g && (r.certkey_idx = o)
                }
            }(t, e, r),
            n.validSignatureValue = !1;
        var h = r.sigalg
            , l = "31" + c(t, r.idxSignedAttrs).substr(2);
        r.signedattrshex = l;
        var g = e.certs[r.certkey_idx].getPublicKey()
            , p = new at.crypto.Signature({
            alg: h
        });
        p.init(g),
            p.updateHex(l);
        var d = p.verify(r.sigval);
        n.validSignatureValue_isValid = d,
        !0 === d && (n.validSignatureValue = !0),
            r.isValid = !1,
        n.validMessageDigest && n.validSignatureValue && (r.isValid = !0)
    }
        , m = {
        isValid: !1,
        parse: {}
    };
    return function (t, e) {
        if ("2a864886f70d010702" !== a(t, 0, [0], "06"))
            return e;
        e.cmsType = "signedData",
            e.econtent = a(t, 0, [1, 0, 2, 1, 0]),
            function (t, e) {
                for (var r, i = 3; i < 6; i++)
                    if (void 0 !== (r = h(t, 0, [1, 0, i]))) {
                        var n = t.substr(r, 2);
                        "a0" === n && (e.certsIdx = r),
                        "a1" === n && (e.revinfosIdx = r),
                        "31" === n && (e.signerinfosIdx = r)
                    }
            }(t, e),
            e.signerInfos = [],
            function (t, e) {
                var r = e.signerinfosIdx;
                if (void 0 !== r) {
                    var i = u(t, r);
                    e.signerInfoIdxList = i;
                    for (var n = 0; n < i.length; n++) {
                        var s = {
                            idx: i[n]
                        };
                        p(t, s),
                            e.signerInfos.push(s)
                    }
                }
            }(t, e)
    }(g, m.parse),
        function (t, e) {
            for (var r = e.parse.signerInfos, i = r.length, n = !0, s = 0; s < i; s++) {
                var a = r[s];
                v(t, e, a),
                a.isValid || (n = !1)
            }
            e.isValid = n
        }(g, m),
        m
}
,
at.asn1.cms.CMSParser = function () {
    var t = Error
        , e = Wt
        , r = new e
        , i = ut
        , n = i.getV
        , s = i.getTLV;
    i.getIdxbyList;
    var a = i.getTLVbyList
        , o = i.getTLVbyListEx
        , h = i.getVbyList
        , u = i.getVbyListEx
        , c = i.getChildIdx;
    this.getCMSSignedData = function (t) {
        var e = a(t, 0, [1, 0]);
        return this.getSignedData(e)
    }
        ,
        this.getSignedData = function (t) {
            var e = c(t, 0)
                , r = {}
                , i = n(t, e[0])
                , a = parseInt(i, 16);
            r.version = a;
            var h = s(t, e[1]);
            r.hashalgs = this.getHashAlgArray(h);
            var u = s(t, e[2]);
            r.econtent = this.getEContent(u);
            var l = o(t, 0, ["[0]"]);
            null != l && (r.certs = this.getCertificateSet(l)),
                o(t, 0, ["[1]"]);
            var f = o(t, 0, [3]);
            return r.sinfos = this.getSignerInfos(f),
                r
        }
        ,
        this.getHashAlgArray = function (t) {
            for (var r = c(t, 0), i = new e, n = [], a = 0; a < r.length; a++) {
                var o = s(t, r[a])
                    , h = i.getAlgorithmIdentifierName(o);
                n.push(h)
            }
            return n
        }
        ,
        this.getEContent = function (t) {
            var e = {}
                , r = h(t, 0, [0])
                , i = h(t, 0, [1, 0]);
            return e.type = at.asn1.x509.OID.oid2name(ut.hextooidstr(r)),
                e.content = {
                    hex: i
                },
                e
        }
        ,
        this.getSignerInfos = function (t) {
            for (var e = [], r = c(t, 0), i = 0; i < r.length; i++) {
                var n = s(t, r[i])
                    , a = this.getSignerInfo(n);
                e.push(a)
            }
            return e
        }
        ,
        this.getSignerInfo = function (t) {
            var e = {}
                , n = c(t, 0)
                , a = i.getInt(t, n[0], -1);
            -1 != a && (e.version = a);
            var h = s(t, n[1])
                , l = this.getIssuerAndSerialNumber(h);
            e.id = l;
            var f = s(t, n[2])
                , g = r.getAlgorithmIdentifierName(f);
            e.hashalg = g;
            var p = o(t, 0, ["[0]"]);
            if (null != p) {
                var d = this.getAttributeList(p);
                e.sattrs = d
            }
            var v = o(t, 0, [3])
                , m = r.getAlgorithmIdentifierName(v);
            e.sigalg = m;
            var y = u(t, 0, [4]);
            e.sighex = y;
            var x = o(t, 0, ["[1]"]);
            if (null != x) {
                var S = this.getAttributeList(x);
                e.uattrs = S
            }
            return e
        }
        ,
        this.getSignerIdentifier = function (t) {
            if ("30" == t.substr(0, 2))
                return this.getIssuerAndSerialNumber(t);
            throw new Error("SKID of signerIdentifier not supported")
        }
        ,
        this.getIssuerAndSerialNumber = function (t) {
            var e = {
                type: "isssn"
            }
                , i = c(t, 0)
                , a = s(t, i[0]);
            e.issuer = r.getX500Name(a);
            var o = n(t, i[1]);
            return e.serial = {
                hex: o
            },
                e
        }
        ,
        this.getAttributeList = function (t) {
            for (var e = [], r = c(t, 0), i = 0; i < r.length; i++) {
                var n = s(t, r[i])
                    , a = this.getAttribute(n);
                e.push(a)
            }
            return {
                array: e
            }
        }
        ,
        this.getAttribute = function (t) {
            var e = {}
                , r = c(t, 0)
                , n = i.getOID(t, r[0])
                , a = at.asn1.x509.OID.oid2name(n);
            e.attr = a;
            var o = s(t, r[1])
                , h = c(o, 0);
            if (1 == h.length)
                e.valhex = s(o, h[0]);
            else {
                for (var u = [], l = 0; l < h.length; l++)
                    u.push(s(o, h[l]));
                e.valhex = u
            }
            return "contentType" == a ? this.setContentType(e) : "messageDigest" == a ? this.setMessageDigest(e) : "signingTime" == a ? this.setSigningTime(e) : "signingCertificate" == a ? this.setSigningCertificate(e) : "signingCertificateV2" == a ? this.setSigningCertificateV2(e) : "signaturePolicyIdentifier" == a && this.setSignaturePolicyIdentifier(e),
                e
        }
        ,
        this.setContentType = function (t) {
            var e = i.getOIDName(t.valhex, 0, null);
            null != e && (t.type = e,
                delete t.valhex)
        }
        ,
        this.setSigningTime = function (t) {
            var e = mt(n(t.valhex, 0));
            t.str = e,
                delete t.valhex
        }
        ,
        this.setMessageDigest = function (t) {
            var e = n(t.valhex, 0);
            t.hex = e,
                delete t.valhex
        }
        ,
        this.setSigningCertificate = function (t) {
            var e = c(t.valhex, 0);
            if (e.length > 0) {
                for (var r = s(t.valhex, e[0]), i = c(r, 0), n = [], a = 0; a < i.length; a++) {
                    var o = s(r, i[a])
                        , h = this.getESSCertID(o);
                    n.push(h)
                }
                t.array = n
            }
            if (e.length > 1) {
                var u = s(t.valhex, e[1]);
                t.polhex = u
            }
            delete t.valhex
        }
        ,
        this.setSignaturePolicyIdentifier = function (t) {
            var r = c(t.valhex, 0);
            if (r.length > 0) {
                var a = i.getOID(t.valhex, r[0]);
                t.oid = a
            }
            if (r.length > 1) {
                var o = new e
                    , h = c(t.valhex, r[1])
                    , u = s(t.valhex, h[0])
                    , l = o.getAlgorithmIdentifierName(u);
                t.alg = l;
                var f = n(t.valhex, h[1]);
                t.hash = f
            }
            delete t.valhex
        }
        ,
        this.setSigningCertificateV2 = function (t) {
            var e = c(t.valhex, 0);
            if (e.length > 0) {
                for (var r = s(t.valhex, e[0]), i = c(r, 0), n = [], a = 0; a < i.length; a++) {
                    var o = s(r, i[a])
                        , h = this.getESSCertIDv2(o);
                    n.push(h)
                }
                t.array = n
            }
            if (e.length > 1) {
                var u = s(t.valhex, e[1]);
                t.polhex = u
            }
            delete t.valhex
        }
        ,
        this.getESSCertID = function (t) {
            var e = {}
                , r = c(t, 0);
            if (r.length > 0) {
                var i = n(t, r[0]);
                e.hash = i
            }
            if (r.length > 1) {
                var a = s(t, r[1])
                    , o = this.getIssuerSerial(a);
                null != o.serial && (e.serial = o.serial),
                null != o.issuer && (e.issuer = o.issuer)
            }
            return e
        }
        ,
        this.getESSCertIDv2 = function (e) {
            var i = {}
                , a = c(e, 0);
            if (a.length < 1 || 3 < a.length)
                throw new t("wrong number of elements");
            var o = 0;
            if ("30" == e.substr(a[0], 2)) {
                var h = s(e, a[0]);
                i.alg = r.getAlgorithmIdentifierName(h),
                    o++
            } else
                i.alg = "sha256";
            var u = n(e, a[o]);
            if (i.hash = u,
            a.length > o + 1) {
                var l = s(e, a[o + 1])
                    , f = this.getIssuerSerial(l);
                i.issuer = f.issuer,
                    i.serial = f.serial
            }
            return i
        }
        ,
        this.getIssuerSerial = function (t) {
            var e = {}
                , i = c(t, 0)
                , a = s(t, i[0])
                , o = r.getGeneralNames(a)[0].dn;
            e.issuer = o;
            var h = n(t, i[1]);
            return e.serial = {
                hex: h
            },
                e
        }
        ,
        this.getCertificateSet = function (t) {
            for (var e = c(t, 0), r = [], i = 0; i < e.length; i++) {
                var n = s(t, e[i]);
                if ("30" == n.substr(0, 2)) {
                    var a = wt(n, "CERTIFICATE");
                    r.push(a)
                }
            }
            return {
                array: r,
                sortflag: !1
            }
        }
}
,
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
void 0 !== at.asn1.tsp && at.asn1.tsp || (at.asn1.tsp = {}),
at.asn1.tsp.TimeStampToken = function (t) {
    var e = at.asn1.tsp;
    e.TimeStampToken.superclass.constructor.call(this),
        this.params = null,
        this.getEncodedHexPrepare = function () {
            var t = new e.TSTInfo(this.params.econtent.content);
            this.params.econtent.content.hex = t.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.TimeStampToken, at.asn1.cms.SignedData),
at.asn1.tsp.TSTInfo = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DERInteger
        , n = e.DERBoolean
        , s = e.DERGeneralizedTime
        , a = e.DERObjectIdentifier
        , o = e.DERTaggedObject
        , h = e.tsp
        , u = h.MessageImprint
        , c = h.Accuracy;
    e.x509.X500Name;
    var l = e.x509.GeneralName;
    if (h.TSTInfo.superclass.constructor.call(this),
        this.dVersion = new i({
            int: 1
        }),
        this.dPolicy = null,
        this.dMessageImprint = null,
        this.dSerial = null,
        this.dGenTime = null,
        this.dAccuracy = null,
        this.dOrdering = null,
        this.dNonce = null,
        this.dTsa = null,
        this.tohex = function () {
            var t = [this.dVersion];
            if (null == this.dPolicy)
                throw new Error("policy shall be specified.");
            if (t.push(this.dPolicy),
            null == this.dMessageImprint)
                throw new Error("messageImprint shall be specified.");
            if (t.push(this.dMessageImprint),
            null == this.dSerial)
                throw new Error("serialNumber shall be specified.");
            if (t.push(this.dSerial),
            null == this.dGenTime)
                throw new Error("genTime shall be specified.");
            t.push(this.dGenTime),
            null != this.dAccuracy && t.push(this.dAccuracy),
            null != this.dOrdering && t.push(this.dOrdering),
            null != this.dNonce && t.push(this.dNonce),
            null != this.dTsa && t.push(this.dTsa);
            var e = new r({
                array: t
            });
            return this.hTLV = e.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t) {
        if ("string" == typeof t.policy) {
            if (!t.policy.match(/^[0-9.]+$/))
                throw "policy shall be oid like 0.1.4.134";
            this.dPolicy = new a({
                oid: t.policy
            })
        }
        void 0 !== t.messageImprint && (this.dMessageImprint = new u(t.messageImprint)),
        void 0 !== t.serial && (this.dSerial = new i(t.serial)),
        void 0 !== t.genTime && (this.dGenTime = new s(t.genTime)),
        void 0 !== t.accuracy && (this.dAccuracy = new c(t.accuracy)),
        void 0 !== t.ordering && 1 == t.ordering && (this.dOrdering = new n),
        void 0 !== t.nonce && (this.dNonce = new i(t.nonce)),
        void 0 !== t.tsa && (this.dTsa = new o({
            tag: "a0",
            explicit: !0,
            obj: new l({
                dn: t.tsa
            })
        }))
    }
}
,
Mt(at.asn1.tsp.TSTInfo, at.asn1.ASN1Object),
at.asn1.tsp.Accuracy = function (t) {
    var e = at.asn1
        , r = e.ASN1Util.newObject;
    e.tsp.Accuracy.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = [];
            return null != t.seconds && "number" == typeof t.seconds && e.push({
                int: t.seconds
            }),
            null != t.millis && "number" == typeof t.millis && e.push({
                tag: {
                    tagi: "80",
                    obj: {
                        int: t.millis
                    }
                }
            }),
            null != t.micros && "number" == typeof t.micros && e.push({
                tag: {
                    tagi: "81",
                    obj: {
                        int: t.micros
                    }
                }
            }),
                r({
                    seq: e
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.Accuracy, at.asn1.ASN1Object),
at.asn1.tsp.MessageImprint = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DEROctetString
        , n = e.x509.AlgorithmIdentifier;
    e.tsp.MessageImprint.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = new n({
                name: t.alg
            })
                , s = new i({
                hex: t.hash
            });
            return new r({
                array: [e, s]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.MessageImprint, at.asn1.ASN1Object),
at.asn1.tsp.TimeStampReq = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DERInteger
        , n = e.DERBoolean;
    e.ASN1Object;
    var s = e.DERObjectIdentifier
        , a = e.tsp
        , o = a.MessageImprint;
    a.TimeStampReq.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = [];
            return e.push(new i({
                int: 1
            })),
                t.messageImprint instanceof at.asn1.ASN1Object ? e.push(t.messageImprint) : e.push(new o(t.messageImprint)),
            null != t.policy && e.push(new s(t.policy)),
            null != t.nonce && e.push(new i(t.nonce)),
            1 == t.certreq && e.push(new n),
                new r({
                    array: e
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.TimeStampReq, at.asn1.ASN1Object),
at.asn1.tsp.TimeStampResp = function (t) {
    var e = at.asn1
        , r = e.DERSequence;
    e.ASN1Object;
    var i = e.tsp
        , n = i.PKIStatusInfo;
    i.TimeStampResp.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , s = [];
            if (null != t.econtent || null != t.tst)
                if (null != t.statusinfo ? s.push(new n(t.statusinfo)) : s.push(new n("granted")),
                null != t.econtent)
                    s.push(new i.TimeStampToken(t).getContentInfo());
                else {
                    if (!(t.tst instanceof e.ASN1Object))
                        throw new Error("improper member tst value");
                    s.push(t.tst)
                }
            else {
                if (null == t.statusinfo)
                    throw new Error("parameter for token nor statusinfo not specified");
                s.push(new n(t.statusinfo))
            }
            return new r({
                array: s
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.TimeStampResp, at.asn1.ASN1Object),
at.asn1.tsp.PKIStatusInfo = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSequence
        , n = r.tsp
        , s = n.PKIStatus
        , a = n.PKIFreeText
        , o = n.PKIFailureInfo;
    n.PKIStatusInfo.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , r = [];
            if ("string" == typeof t)
                r.push(new s(t));
            else {
                if (null == t.status)
                    throw new e("property 'status' unspecified");
                r.push(new s(t.status)),
                null != t.statusstr && r.push(new a(t.statusstr)),
                null != t.failinfo && r.push(new o(t.failinfo))
            }
            return new i({
                array: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.PKIStatusInfo, at.asn1.ASN1Object),
at.asn1.tsp.PKIStatus = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERInteger;
    r.tsp.PKIStatus.superclass.constructor.call(this);
    var n = {
        granted: 0,
        grantedWithMods: 1,
        rejection: 2,
        waiting: 3,
        revocationWarning: 4,
        revocationNotification: 5
    };
    this.params = null,
        this.tohex = function () {
            var t, r = this.params;
            if ("string" == typeof r)
                try {
                    t = n[r]
                } catch (s) {
                    throw new e("undefined name: " + r)
                }
            else {
                if ("number" != typeof r)
                    throw new e("unsupported params");
                t = r
            }
            return new i({
                int: t
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.PKIStatus, at.asn1.ASN1Object),
at.asn1.tsp.PKIFreeText = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSequence
        , n = r.DERUTF8String;
    r.tsp.PKIFreeText.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (!t instanceof Array)
                throw new e("wrong params: not array");
            for (var r = [], s = 0; s < t.length; s++)
                r.push(new n({
                    str: t[s]
                }));
            return new i({
                array: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.PKIFreeText, at.asn1.ASN1Object),
at.asn1.tsp.PKIFailureInfo = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERBitString
        , n = r.tsp.PKIFailureInfo
        , s = {
        badAlg: 0,
        badRequest: 2,
        badDataFormat: 5,
        timeNotAvailable: 14,
        unacceptedPolicy: 15,
        unacceptedExtension: 16,
        addInfoNotAvailable: 17,
        systemFailure: 25
    };
    n.superclass.constructor.call(this),
        this.params = null,
        this.getBinValue = function () {
            var t = this.params
                , r = 0;
            if ("number" == typeof t && 0 <= t && t <= 25) {
                for (var i = (r |= 1 << t).toString(2), n = "", a = i.length - 1; a >= 0; a--)
                    n += i[a];
                return n
            }
            if ("string" == typeof t && null != s[t])
                return kt([t], s);
            if ("object" == typeof t && null != t.length)
                return kt(t, s);
            throw new e("wrong params")
        }
        ,
        this.tohex = function () {
            this.params;
            var t = this.getBinValue();
            return new i({
                bin: t
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.tsp.PKIFailureInfo, at.asn1.ASN1Object),
at.asn1.tsp.AbstractTSAAdapter = function (t) {
    this.getTSTHex = function (t, e) {
        throw "not implemented yet"
    }
}
,
at.asn1.tsp.SimpleTSAAdapter = function (t) {
    var e = at
        , r = e.asn1.tsp
        , i = e.crypto.Util.hashHex;
    r.SimpleTSAAdapter.superclass.constructor.call(this),
        this.params = null,
        this.serial = 0,
        this.getTSTHex = function (t, e) {
            var n = i(t, e);
            this.params.econtent.content.messageImprint = {
                alg: e,
                hash: n
            },
                this.params.econtent.content.serial = {
                    int: this.serial++
                };
            var s = Math.floor(1e9 * Math.random());
            return this.params.econtent.content.nonce = {
                int: s
            },
                new r.TimeStampToken(this.params).getContentInfoEncodedHex()
        }
        ,
    void 0 !== t && (this.params = t)
}
,
Mt(at.asn1.tsp.SimpleTSAAdapter, at.asn1.tsp.AbstractTSAAdapter),
at.asn1.tsp.FixedTSAAdapter = function (t) {
    var e = at
        , r = e.asn1.tsp
        , i = e.crypto.Util.hashHex;
    r.FixedTSAAdapter.superclass.constructor.call(this),
        this.params = null,
        this.getTSTHex = function (t, e) {
            var n = i(t, e);
            return this.params.econtent.content.messageImprint = {
                alg: e,
                hash: n
            },
                new r.TimeStampToken(this.params).getContentInfoEncodedHex()
        }
        ,
    void 0 !== t && (this.params = t)
}
,
Mt(at.asn1.tsp.FixedTSAAdapter, at.asn1.tsp.AbstractTSAAdapter),
at.asn1.tsp.TSPUtil = new function () {
}
,
at.asn1.tsp.TSPUtil.newTimeStampToken = function (t) {
    return new at.asn1.tsp.TimeStampToken(t)
}
,
at.asn1.tsp.TSPUtil.parseTimeStampReq = function (t) {
    return (new at.asn1.tsp.TSPParser).getTimeStampReq(t)
}
,
at.asn1.tsp.TSPUtil.parseMessageImprint = function (t) {
    return (new at.asn1.tsp.TSPParser).getMessageImprint(t)
}
,
at.asn1.tsp.TSPParser = function () {
    var t = new Wt
        , e = ut
        , r = e.getV
        , i = e.getTLV
        , n = e.getIdxbyList;
    e.getTLVbyListEx;
    var s = e.getChildIdx
        , a = ["granted", "grantedWithMods", "rejection", "waiting", "revocationWarning", "revocationNotification"]
        , o = {
        0: "badAlg",
        2: "badRequest",
        5: "badDataFormat",
        14: "timeNotAvailable",
        15: "unacceptedPolicy",
        16: "unacceptedExtension",
        17: "addInfoNotAvailable",
        25: "systemFailure"
    };
    this.getResponse = function (t) {
        var e = s(t, 0);
        if (1 == e.length)
            return this.getPKIStatusInfo(i(t, e[0]));
        if (e.length > 1) {
            var r = this.getPKIStatusInfo(i(t, e[0]))
                , n = i(t, e[1])
                , a = this.getToken(n);
            return a.statusinfo = r,
                a
        }
    }
        ,
        this.getToken = function (t) {
            var e = (new at.asn1.cms.CMSParser).getCMSSignedData(t);
            return this.setTSTInfo(e),
                e
        }
        ,
        this.setTSTInfo = function (t) {
            var e = t.econtent;
            if ("tstinfo" == e.type) {
                var r = e.content.hex
                    , i = this.getTSTInfo(r);
                e.content = i
            }
        }
        ,
        this.getTSTInfo = function (e) {
            var n = {}
                , a = s(e, 0)
                , o = r(e, a[1]);
            n.policy = Kt(o);
            var h = i(e, a[2]);
            n.messageImprint = this.getMessageImprint(h);
            var u = r(e, a[3]);
            n.serial = {
                hex: u
            };
            var c = r(e, a[4]);
            n.genTime = {
                str: mt(c)
            };
            var l = 0;
            if (a.length > 5 && "30" == e.substr(a[5], 2)) {
                var f = i(e, a[5]);
                n.accuracy = this.getAccuracy(f),
                    l++
            }
            a.length > 5 + l && "01" == e.substr(a[5 + l], 2) && ("ff" == r(e, a[5 + l]) && (n.ordering = !0),
                l++);
            if (a.length > 5 + l && "02" == e.substr(a[5 + l], 2)) {
                var g = r(e, a[5 + l]);
                n.nonce = {
                    hex: g
                },
                    l++
            }
            if (a.length > 5 + l && "a0" == e.substr(a[5 + l], 2)) {
                var p = i(e, a[5 + l]);
                p = "30" + p.substr(2),
                    pGeneralNames = t.getGeneralNames(p);
                var d = pGeneralNames[0].dn;
                n.tsa = d,
                    l++
            }
            if (a.length > 5 + l && "a1" == e.substr(a[5 + l], 2)) {
                var v = i(e, a[5 + l]);
                v = "30" + v.substr(2);
                var m = t.getExtParamArray(v);
                n.ext = m,
                    l++
            }
            return n
        }
        ,
        this.getAccuracy = function (t) {
            for (var e = {}, i = s(t, 0), n = 0; n < i.length; n++) {
                var a = t.substr(i[n], 2)
                    , o = r(t, i[n])
                    , h = parseInt(o, 16);
                "02" == a ? e.seconds = h : "80" == a ? e.millis = h : "81" == a && (e.micros = h)
            }
            return e
        }
        ,
        this.getMessageImprint = function (t) {
            if ("30" != t.substr(0, 2))
                throw new Error("head of messageImprint hex shall be x30");
            var i = {};
            s(t, 0);
            var a = n(t, 0, [0, 0])
                , o = r(t, a)
                , h = e.hextooidstr(o)
                , u = at.asn1.x509.OID.oid2name(h);
            if ("" == u)
                throw new Error("hashAlg name undefined: " + h);
            var c = u
                , l = n(t, 0, [1]);
            return i.alg = c,
                i.hash = r(t, l),
                i
        }
        ,
        this.getPKIStatusInfo = function (t) {
            var e = {}
                , n = s(t, 0)
                , o = 0;
            try {
                var h = r(t, n[0])
                    , u = parseInt(h, 16);
                e.status = a[u]
            } catch (f) {
            }
            if (n.length > 1 && "30" == t.substr(n[1], 2)) {
                var c = i(t, n[1]);
                e.statusstr = this.getPKIFreeText(c),
                    o++
            }
            if (n.length > o && "03" == t.substr(n[1 + o], 2)) {
                var l = i(t, n[1 + o]);
                e.failinfo = this.getPKIFailureInfo(l)
            }
            return e
        }
        ,
        this.getPKIFreeText = function (t) {
            for (var r = [], i = s(t, 0), n = 0; n < i.length; n++)
                r.push(e.getString(t, i[n]));
            return r
        }
        ,
        this.getPKIFailureInfo = function (t) {
            var r = e.getInt(t, 0);
            return null != o[r] ? o[r] : r
        }
        ,
        this.getTimeStampReq = function (t) {
            var n = {
                certreq: !1
            }
                , a = s(t, 0);
            if (a.length < 2)
                throw new Error("TimeStampReq must have at least 2 items");
            var o = i(t, a[1]);
            n.messageImprint = at.asn1.tsp.TSPUtil.parseMessageImprint(o);
            for (var h = 2; h < a.length; h++) {
                var u = a[h]
                    , c = t.substr(u, 2);
                if ("06" == c) {
                    var l = r(t, u);
                    n.policy = e.hextooidstr(l)
                }
                "02" == c && (n.nonce = r(t, u)),
                "01" == c && (n.certreq = !0)
            }
            return n
        }
}
,
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
void 0 !== at.asn1.cades && at.asn1.cades || (at.asn1.cades = {}),
at.asn1.cades.SignaturePolicyIdentifier = function (t) {
    var e = at.asn1.cades
        , r = e.SignaturePolicyId;
    e.SignaturePolicyIdentifier.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.16.2.15",
        this.params = null,
        this.getValueArray = function () {
            return [new r(this.params)]
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.SignaturePolicyIdentifier, at.asn1.cms.Attribute),
at.asn1.cades.SignaturePolicyId = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DERObjectIdentifier;
    e.x509.AlgorithmIdentifier;
    var n = e.cades
        , s = n.SignaturePolicyId
        , a = n.OtherHashAlgAndValue;
    s.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , e = [];
            return e.push(new i(t.oid)),
                e.push(new a(t)),
                new r({
                    array: e
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.SignaturePolicyId, at.asn1.ASN1Object),
at.asn1.cades.OtherHashAlgAndValue = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSequence
        , n = r.DEROctetString
        , s = r.x509.AlgorithmIdentifier;
    r.cades.OtherHashAlgAndValue.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (null == t.alg)
                throw new e("property 'alg' not specified");
            if (null == t.hash && null == t.cert)
                throw new e("property 'hash' nor 'cert' not specified");
            var r = null;
            if (null != t.hash)
                r = t.hash;
            else if (null != t.cert) {
                if ("string" != typeof t.cert)
                    throw new e("cert not string");
                var a = t.cert;
                -1 != t.cert.indexOf("-----BEGIN") && (a = Ft(t.cert)),
                    r = at.crypto.Util.hashHex(a, t.alg)
            }
            var o = [];
            return o.push(new s({
                name: t.alg
            })),
                o.push(new n({
                    hex: r
                })),
                new i({
                    array: o
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.OtherHashAlgAndValue, at.asn1.ASN1Object),
at.asn1.cades.OtherHashValue = function (t) {
    at.asn1.cades.OtherHashValue.superclass.constructor.call(this);
    var e = Error
        , r = at;
    r.lang.String.isHex;
    var i = r.asn1.DEROctetString;
    r.crypto.Util.hashHex,
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (null == t.hash && null == t.cert)
                throw new e("hash or cert not specified");
            var r = null;
            if (null != t.hash)
                r = t.hash;
            else if (null != t.cert) {
                if ("string" != typeof t.cert)
                    throw new e("cert not string");
                var n = t.cert;
                -1 != t.cert.indexOf("-----BEGIN") && (n = Ft(t.cert)),
                    r = at.crypto.Util.hashHex(n, "sha1")
            }
            return new i({
                hex: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.OtherHashValue, at.asn1.ASN1Object),
at.asn1.cades.SignatureTimeStamp = function (t) {
    var e = Error
        , r = at
        , i = r.lang.String.isHex
        , n = r.asn1
        , s = n.ASN1Object;
    n.x509,
        n.cades.SignatureTimeStamp.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.16.2.14",
        this.params = null,
        this.getValueArray = function () {
            var t = this.params;
            if (null != t.tst) {
                if (i(t.tst))
                    return (r = new s).hTLV = t.tst,
                        [r];
                if (t.tst instanceof s)
                    return [t.tst];
                throw new e("params.tst has wrong value")
            }
            if (null != t.res) {
                var r, n = t.res;
                if (n instanceof s && (n = n.tohex()),
                "string" != typeof n || !i(n))
                    throw new e("params.res has wrong value");
                return ut.getTLVbyList(n, 0, [1]),
                    (r = new s).hTLV = t.tst,
                    [r]
            }
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.SignatureTimeStamp, at.asn1.cms.Attribute),
at.asn1.cades.CompleteCertificateRefs = function (t) {
    var e = Error
        , r = at
        , i = r.asn1
        , n = i.DERSequence
        , s = i.cades
        , a = s.OtherCertID
        , o = r.lang.String.isHex;
    s.CompleteCertificateRefs.superclass.constructor.call(this),
        this.typeOid = "1.2.840.113549.1.9.16.2.21",
        this.params = null,
        this.getValueArray = function () {
            for (var t = this.params, r = [], i = 0; i < t.array.length; i++) {
                var s = t.array[i];
                if ("string" == typeof s)
                    if (-1 != s.indexOf("-----BEGIN"))
                        s = {
                            cert: s
                        };
                    else {
                        if (!o(s))
                            throw new e("unsupported value: " + s);
                        s = {
                            hash: s
                        }
                    }
                null != t.alg && null == s.alg && (s.alg = t.alg),
                null != t.hasis && null == s.hasis && (s.hasis = t.hasis);
                var h = new a(s);
                r.push(h)
            }
            return [new n({
                array: r
            })]
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.CompleteCertificateRefs, at.asn1.cms.Attribute),
at.asn1.cades.OtherCertID = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.cms.IssuerSerial
        , n = e.cades
        , s = n.OtherHashValue
        , a = n.OtherHashAlgAndValue;
    n.OtherCertID.superclass.constructor.call(this),
        this.params = t,
        this.tohex = function () {
            var t = this.params;
            "string" == typeof t && (-1 != t.indexOf("-----BEGIN") ? t = {
                cert: t
            } : _isHex(t) && (t = {
                hash: t
            }));
            var e = []
                , n = null;
            if (n = null != t.alg ? new a(t) : new s(t),
                e.push(n),
            null != t.cert && 1 == t.hasis || null != t.issuer && null != t.serial) {
                var o = new i(t);
                e.push(o)
            }
            return new r({
                array: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.OtherCertID, at.asn1.ASN1Object),
at.asn1.cades.OtherHash = function (t) {
    var e = at
        , r = e.asn1;
    r.cms;
    var i = r.cades
        , n = i.OtherHashAlgAndValue
        , s = i.OtherHashValue;
    e.crypto.Util.hashHex;
    var a = e.lang.String.isHex;
    i.OtherHash.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            "string" == typeof t && (-1 != t.indexOf("-----BEGIN") ? t = {
                cert: t
            } : a(t) && (t = {
                hash: t
            }));
            return (null != t.alg ? new n(t) : new s(t)).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.cades.OtherHash, at.asn1.ASN1Object),
at.asn1.cades.CAdESUtil = new function () {
}
,
at.asn1.cades.CAdESUtil.parseSignedDataForAddingUnsigned = function (t) {
    return (new at.asn1.cms.CMSParser).getCMSSignedData(t)
}
,
at.asn1.cades.CAdESUtil.parseSignerInfoForAddingUnsigned = function (t, e, r) {
    var i = ut
        , n = i.getChildIdx
        , s = i.getTLV
        , a = i.getV
        , o = at.asn1
        , h = o.ASN1Object
        , u = o.cms
        , c = u.AttributeList
        , l = u.SignerInfo
        , f = {}
        , g = n(t, e);
    if (6 != g.length)
        throw "not supported items for SignerInfo (!=6)";
    var p = g.shift();
    f.version = s(t, p);
    var d = g.shift();
    f.si = s(t, d);
    var v = g.shift();
    f.digalg = s(t, v);
    var m = g.shift();
    f.sattrs = s(t, m);
    var y = g.shift();
    f.sigalg = s(t, y);
    var x = g.shift();
    f.sig = s(t, x),
        f.sigval = a(t, x);
    var S = null;
    return f.obj = new l,
        (S = new h).hTLV = f.version,
        f.obj.dCMSVersion = S,
        (S = new h).hTLV = f.si,
        f.obj.dSignerIdentifier = S,
        (S = new h).hTLV = f.digalg,
        f.obj.dDigestAlgorithm = S,
        (S = new h).hTLV = f.sattrs,
        f.obj.dSignedAttrs = S,
        (S = new h).hTLV = f.sigalg,
        f.obj.dSigAlg = S,
        (S = new h).hTLV = f.sig,
        f.obj.dSig = S,
        f.obj.dUnsignedAttrs = new c,
        f
}
,
void 0 !== at.asn1.csr && at.asn1.csr || (at.asn1.csr = {}),
at.asn1.csr.CertificationRequest = function (t) {
    var e = at.asn1
        , r = e.DERBitString
        , i = e.DERSequence
        , n = e.csr;
    e.x509;
    var s = n.CertificationRequestInfo;
    n.CertificationRequest.superclass.constructor.call(this),
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.sign = function () {
            var t = new s(this.params).tohex()
                , e = new at.crypto.Signature({
                alg: this.params.sigalg
            });
            e.init(this.params.sbjprvkey),
                e.updateHex(t);
            var r = e.sign();
            this.params.sighex = r
        }
        ,
        this.getPEM = function () {
            return wt(this.tohex(), "CERTIFICATE REQUEST")
        }
        ,
        this.tohex = function () {
            var t = this.params
                , e = new at.asn1.csr.CertificationRequestInfo(this.params)
                , n = new at.asn1.x509.AlgorithmIdentifier({
                name: t.sigalg
            });
            if (null == t.sighex && null != t.sbjprvkey && this.sign(),
            null == t.sighex)
                throw new Error("sighex or sbjprvkey parameter not defined");
            var s = new r({
                hex: "00" + t.sighex
            });
            return new i({
                array: [e, n, s]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.csr.CertificationRequest, at.asn1.ASN1Object),
at.asn1.csr.CertificationRequestInfo = function (t) {
    var e = at.asn1;
    e.DERBitString;
    var r = e.DERSequence
        , i = e.DERInteger
        , n = e.DERUTF8String
        , s = e.DERTaggedObject
        , a = e.ASN1Util.newObject
        , o = e.csr
        , h = e.x509
        , u = h.X500Name
        , c = h.Extensions
        , l = h.SubjectPublicKeyInfo;
    o.AttributeList,
        o.CertificationRequestInfo.superclass.constructor.call(this),
        this.params = null,
        this.setByParam = function (t) {
            null != t && (this.params = t)
        }
        ,
        this.tohex = function () {
            var t = this.params
                , e = [];
            if (e.push(new i({
                int: 0
            })),
                e.push(new u(t.subject)),
                e.push(new l(_t.getKey(t.sbjpubkey))),
            null != t.attrs) {
                var o = function (t) {
                    for (var e = Error, r = at.asn1.x509.Extensions, i = [], n = 0; n < t.length; n++) {
                        var s = t[n]
                            , a = s.attr;
                        if ("extensionRequest" == a) {
                            var o = {
                                seq: [{
                                    oid: "1.2.840.113549.1.9.14"
                                }, {
                                    set: [new r(s.ext)]
                                }]
                            };
                            i.push(o)
                        } else if ("unstructuredName" == a) {
                            o = {
                                seq: [{
                                    oid: "1.2.840.113549.1.9.2"
                                }, {
                                    set: s.names
                                }]
                            };
                            i.push(o)
                        } else {
                            if ("challengePassword" != a)
                                throw new e("unknown CSR attribute");
                            o = {
                                seq: [{
                                    oid: "1.2.840.113549.1.9.7"
                                }, {
                                    set: [{
                                        utf8str: s.password
                                    }]
                                }]
                            };
                            i.push(o)
                        }
                    }
                    return {
                        set: i
                    }
                }(t.attrs)
                    , h = a({
                    tag: {
                        tage: "a0",
                        obj: o
                    }
                });
                e.push(h)
            } else if (null != t.extreq) {
                var f = new c(t.extreq);
                h = a({
                    tag: {
                        tage: "a0",
                        obj: {
                            seq: [{
                                oid: "1.2.840.113549.1.9.14"
                            }, {
                                set: [f]
                            }]
                        }
                    }
                });
                e.push(h)
            } else
                e.push(new s({
                    tag: "a0",
                    explicit: !1,
                    obj: new n({
                        str: ""
                    })
                }));
            return new r({
                array: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    null != t && this.setByParam(t)
}
,
Mt(at.asn1.csr.CertificationRequestInfo, at.asn1.ASN1Object),
at.asn1.csr.AttributeList = function (t) {
}
,
Mt(at.asn1.csr.AttributeList, at.asn1.ASN1Object),
at.asn1.csr.CSRUtil = new function () {
}
,
at.asn1.csr.CSRUtil.newCSRPEM = function (t) {
    return new at.asn1.csr.CertificationRequest(t).getPEM()
}
,
at.asn1.csr.CSRUtil.getParam = function (t, e) {
    var r = ut
        , i = r.getV
        , n = r.getIdxbyList
        , s = r.getTLVbyList
        , a = r.getTLVbyListEx
        , o = r.getVbyListEx
        , h = {};
    if (-1 == t.indexOf("-----BEGIN CERTIFICATE REQUEST"))
        throw new Error("argument is not PEM file");
    var u = Ft(t, "CERTIFICATE REQUEST");
    e && (h.tbs = s(u, 0, [0]));
    try {
        var c = a(u, 0, [0, 1]);
        if ("3000" == c)
            h.subject = {};
        else {
            var l = new Wt;
            h.subject = l.getX500Name(c)
        }
    } catch (m) {
    }
    var f = a(u, 0, [0, 2])
        , g = _t.getKey(f, null, "pkcs8pub");
    h.sbjpubkey = _t.getPEM(g, "PKCS8PUB");
    var p = function (t) {
        var e = n(t, 0, [0, 3, 0, 0], "06");
        return "2a864886f70d01090e" != i(t, e) ? null : s(t, 0, [0, 3, 0, 1, 0], "30")
    }(u);
    l = new Wt;
    null != p && (h.extreq = l.getExtParamArray(p));
    try {
        var d = a(u, 0, [1], "30");
        l = new Wt;
        h.sigalg = l.getAlgorithmIdentifierName(d)
    } catch (m) {
    }
    try {
        var v = o(u, 0, [2]);
        h.sighex = v
    } catch (m) {
    }
    return h
}
,
at.asn1.csr.CSRUtil.verifySignature = function (e) {
    try {
        var r = null;
        if ("string" == typeof e && -1 != e.indexOf("-----BEGIN CERTIFICATE REQUEST") ? r = at.asn1.csr.CSRUtil.getParam(e, !0) : "object" == typeof e && null != e.sbjpubkey && null != e.sigalg && null != e.sighex && null != e.tbs && (r = e),
        null == r)
            return !1;
        var i = new at.crypto.Signature({
            alg: r.sigalg
        });
        return i.init(r.sbjpubkey),
            i.updateHex(r.tbs),
            i.verify(r.sighex)
    } catch (t) {
        return alert(t),
            !1
    }
}
,
void 0 !== at && at || (at = {}),
void 0 !== at.asn1 && at.asn1 || (at.asn1 = {}),
void 0 !== at.asn1.ocsp && at.asn1.ocsp || (at.asn1.ocsp = {}),
at.asn1.ocsp.DEFAULT_HASH = "sha1",
at.asn1.ocsp.OCSPResponse = function (t) {
    at.asn1.ocsp.OCSPResponse.superclass.constructor.call(this),
        at.asn1.DEREnumerated;
    var e = at.asn1.ASN1Util.newObject
        , r = at.asn1.ocsp.ResponseBytes
        ,
        i = ["successful", "malformedRequest", "internalError", "tryLater", "_not_used_", "sigRequired", "unauthorized"];
    this.params = null,
        this._getStatusCode = function () {
            var t = this.params.resstatus;
            return "number" == typeof t ? t : "string" != typeof t ? -1 : i.indexOf(t)
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t = this.params
                , i = this._getStatusCode();
            if (-1 == i)
                throw new Error("responseStatus not supported: " + t.resstatus);
            if (0 != i)
                return e({
                    seq: [{
                        enum: {
                            int: i
                        }
                    }]
                }).tohex();
            var n = new r(t);
            return e({
                seq: [{
                    enum: {
                        int: 0
                    }
                }, {
                    tag: {
                        tag: "a0",
                        explicit: !0,
                        obj: n
                    }
                }]
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.OCSPResponse, at.asn1.ASN1Object),
at.asn1.ocsp.ResponseBytes = function (t) {
    at.asn1.ocsp.ResponseBytes.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DERObjectIdentifier
        , n = e.DEROctetString
        , s = e.ocsp.BasicOCSPResponse;
    this.params = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.tohex = function () {
            var t = this.params;
            if ("ocspBasic" != t.restype)
                throw new Error("not supported responseType: " + t.restype);
            var e = new s(t)
                , a = [];
            return a.push(new i({
                name: "ocspBasic"
            })),
                a.push(new n({
                    hex: e.tohex()
                })),
                new r({
                    array: a
                }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.ResponseBytes, at.asn1.ASN1Object),
at.asn1.ocsp.BasicOCSPResponse = function (t) {
    at.asn1.ocsp.BasicOCSPResponse.superclass.constructor.call(this);
    var e = Error
        , r = at.asn1
        , i = r.ASN1Object
        , n = r.DERSequence;
    r.DERGeneralizedTime;
    var s = r.DERTaggedObject
        , a = r.DERBitString;
    r.x509.Extensions;
    var o = r.x509.AlgorithmIdentifier
        , h = r.ocsp;
    h.ResponderID,
        _SingleResponseList = h.SingleResponseList,
        _ResponseData = h.ResponseData,
        this.params = null,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
        this.sign = function () {
            var t = this.params
                , e = t.tbsresp.tohex()
                , r = new at.crypto.Signature({
                alg: t.sigalg
            });
            r.init(t.reskey),
                r.updateHex(e),
                t.sighex = r.sign()
        }
        ,
        this.tohex = function () {
            var t = this.params;
            null == t.tbsresp && (t.tbsresp = new _ResponseData(t)),
            null == t.sighex && null != t.reskey && this.sign();
            var r = [];
            if (r.push(t.tbsresp),
                r.push(new o({
                    name: t.sigalg
                })),
                r.push(new a({
                    hex: "00" + t.sighex
                })),
            null != t.certs && null != t.certs.length) {
                for (var h = [], u = 0; u < t.certs.length; u++) {
                    var c = t.certs[u]
                        , l = null;
                    if (ut.isASN1HEX(c))
                        l = c;
                    else {
                        if (!c.match(/-----BEGIN/))
                            throw new e("certs[" + u + "] not hex or PEM");
                        l = Ft(c)
                    }
                    h.push(new i({
                        tlv: l
                    }))
                }
                var f = new n({
                    array: h
                });
                r.push(new s({
                    tag: "a0",
                    explicit: !0,
                    obj: f
                }))
            }
            return new n({
                array: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.BasicOCSPResponse, at.asn1.ASN1Object),
at.asn1.ocsp.ResponseData = function (t) {
    at.asn1.ocsp.ResponseData.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERSequence
        , i = e.DERGeneralizedTime
        , n = e.DERTaggedObject
        , s = e.x509.Extensions
        , a = e.ocsp
        , o = a.ResponderID;
    _SingleResponseList = a.SingleResponseList,
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            t.respid,
                t.prodat,
                t.array;
            var e = [];
            if (e.push(new o(t.respid)),
                e.push(new i(t.prodat)),
                e.push(new _SingleResponseList(t.array)),
            null != t.ext) {
                var a = new s(t.ext);
                e.push(new n({
                    tag: "a1",
                    explicit: !0,
                    obj: a
                }))
            }
            return new r({
                array: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.ResponseData, at.asn1.ASN1Object),
at.asn1.ocsp.ResponderID = function (t) {
    at.asn1.ocsp.ResponderID.superclass.constructor.call(this);
    var e = at
        , r = e.asn1
        , i = r.ASN1Util.newObject
        , n = r.x509.X500Name
        , s = e.lang.String.isHex
        , a = Error;
    this.params = null,
        this.tohex = function () {
            var t = this.params;
            if (null != t.key) {
                var e, r = null;
                if ("string" == typeof t.key) {
                    if (s(t.key) && (r = t.key),
                        t.key.match(/-----BEGIN CERTIFICATE/))
                        null != (e = new Wt(t.key).getExtSubjectKeyIdentifier()) && (r = e.kid.hex)
                } else if (t.key instanceof Wt)
                    null != (e = t.key.getExtSubjectKeyIdentifier()) && (r = e.kid.hex);
                if (null == r)
                    throw new a("wrong key member value");
                return i({
                    tag: {
                        tag: "a2",
                        explicit: !0,
                        obj: {
                            octstr: {
                                hex: r
                            }
                        }
                    }
                }).tohex()
            }
            if (null != t.name) {
                var o = null;
                if ("string" == typeof t.name && t.name.match(/-----BEGIN CERTIFICATE/))
                    o = new Wt(t.name).getSubject();
                else
                    t.name instanceof Wt ? o = t.name.getSubject() : "object" != typeof t.name || null == t.name.array && null == t.name.str || (o = t.name);
                if (null == o)
                    throw new a("wrong name member value");
                return i({
                    tag: {
                        tag: "a1",
                        explicit: !0,
                        obj: new n(o)
                    }
                }).tohex()
            }
            throw new a("key or name not specified")
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.ResponderID, at.asn1.ASN1Object),
at.asn1.ocsp.SingleResponseList = function (t) {
    at.asn1.ocsp.SingleResponseList.superclass.constructor.call(this);
    var e = at.asn1
        , r = e.DERSequence
        , i = e.ocsp.SingleResponse;
    this.params = null,
        this.tohex = function () {
            var t = this.params;
            if ("object" != typeof t || null == t.length)
                throw new Error("params not specified properly");
            for (var e = [], n = 0; n < t.length; n++)
                e.push(new i(t[n]));
            return new r({
                array: e
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.SingleResponseList, at.asn1.ASN1Object),
at.asn1.ocsp.SingleResponse = function (t) {
    var e = Error
        , r = at.asn1
        , i = r.DERSequence
        , n = r.DERGeneralizedTime
        , s = r.DERTaggedObject
        , a = r.ocsp
        , o = a.CertID
        , h = a.CertStatus
        , u = r.x509.Extensions;
    a.SingleResponse.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params
                , r = [];
            if (null == t.certid)
                throw new e("certid unspecified");
            if (null == t.status)
                throw new e("status unspecified");
            if (null == t.thisupdate)
                throw new e("thisupdate unspecified");
            if (r.push(new o(t.certid)),
                r.push(new h(t.status)),
                r.push(new n(t.thisupdate)),
            null != t.nextupdate) {
                var a = new n(t.nextupdate);
                r.push(new s({
                    tag: "a0",
                    explicit: !0,
                    obj: a
                }))
            }
            if (null != t.ext) {
                var c = new u(t.ext);
                r.push(new s({
                    tag: "a1",
                    explicit: !0,
                    obj: c
                }))
            }
            return new i({
                array: r
            }).tohex()
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.SingleResponse, at.asn1.ASN1Object),
at.asn1.ocsp.CertID = function (t) {
    var e = at
        , r = e.asn1
        , i = r.DEROctetString
        , n = r.DERInteger
        , s = r.DERSequence
        , a = r.x509.AlgorithmIdentifier
        , o = r.ocsp;
    o.DEFAULT_HASH;
    var h = e.crypto.Util.hashHex
        , u = Wt
        , c = ut.getVbyList;
    o.CertID.superclass.constructor.call(this),
        this.DEFAULT_HASH = "sha1",
        this.params = null,
        this.setByValue = function (t, e, r, i) {
            null == i && (i = this.DEFAULT_HASH),
                this.params = {
                    alg: i,
                    issname: t,
                    isskey: e,
                    sbjsn: r
                }
        }
        ,
        this.setByCert = function (t, e, r) {
            null == r && (r = this.DEFAULT_HASH),
                this.params = {
                    alg: r,
                    issuerCert: t,
                    subjectCert: e
                }
        }
        ,
        this.getParamByCerts = function (t, e, r) {
            null == r && (r = this.DEFAULT_HASH);
            var i = new u(t)
                , n = new u(e)
                , s = h(i.getSubjectHex(), r)
                , a = i.getPublicKeyHex();
            return {
                alg: r,
                issname: s,
                isskey: h(c(a, 0, [1], "03", !0), r),
                sbjsn: n.getSerialNumberHex()
            }
        }
        ,
        this.tohex = function () {
            if ("object" != typeof this.params)
                throw new Error("params not set");
            var t, e, r, o, h = this.params;
            if (o = null == h.alg ? this.DEFAULT_HASH : h.alg,
            null != h.issuerCert && null != h.subjectCert) {
                var u = this.getParamByCerts(h.issuerCert, h.subjectCert, o);
                t = u.issname,
                    e = u.isskey,
                    r = u.sbjsn
            } else {
                if (null == h.issname || null == h.isskey || null == h.sbjsn)
                    throw new Error("required param members not defined");
                t = h.issname,
                    e = h.isskey,
                    r = h.sbjsn
            }
            var c = new a({
                name: o
            })
                , l = new i({
                hex: t
            })
                , f = new i({
                hex: e
            })
                , g = new n({
                hex: r
            })
                , p = new s({
                array: [c, l, f, g]
            });
            return this.hTLV = p.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.CertID, at.asn1.ASN1Object),
at.asn1.ocsp.CertStatus = function (t) {
    at.asn1.ocsp.CertStatus.superclass.constructor.call(this),
        this.params = null,
        this.tohex = function () {
            var t = this.params;
            if ("good" == t.status)
                return "8000";
            if ("unknown" == t.status)
                return "8200";
            if ("revoked" == t.status) {
                var e = [{
                    gentime: {
                        str: t.time
                    }
                }];
                null != t.reason && e.push({
                    tag: {
                        tag: "a0",
                        explicit: !0,
                        obj: {
                            enum: {
                                int: t.reason
                            }
                        }
                    }
                });
                var r = {
                    tag: "a1",
                    explicit: !1,
                    obj: {
                        seq: e
                    }
                };
                return at.asn1.ASN1Util.newObject({
                    tag: r
                }).tohex()
            }
            throw new Error("bad status")
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
        this.setByParam = function (t) {
            this.params = t
        }
        ,
    void 0 !== t && this.setByParam(t)
}
,
Mt(at.asn1.ocsp.CertStatus, at.asn1.ASN1Object),
at.asn1.ocsp.Request = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.ocsp;
    if (i.Request.superclass.constructor.call(this),
        this.dReqCert = null,
        this.dExt = null,
        this.tohex = function () {
            var t = [];
            if (null === this.dReqCert)
                throw "reqCert not set";
            t.push(this.dReqCert);
            var e = new r({
                array: t
            });
            return this.hTLV = e.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t) {
        var n = new i.CertID(t);
        this.dReqCert = n
    }
}
,
Mt(at.asn1.ocsp.Request, at.asn1.ASN1Object),
at.asn1.ocsp.TBSRequest = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.ocsp;
    i.TBSRequest.superclass.constructor.call(this),
        this.version = 0,
        this.dRequestorName = null,
        this.dRequestList = [],
        this.dRequestExt = null,
        this.setRequestListByParam = function (t) {
            for (var e = [], r = 0; r < t.length; r++) {
                var n = new i.Request(t[0]);
                e.push(n)
            }
            this.dRequestList = e
        }
        ,
        this.tohex = function () {
            var t = [];
            if (0 !== this.version)
                throw "not supported version: " + this.version;
            if (null !== this.dRequestorName)
                throw "requestorName not supported";
            var e = new r({
                array: this.dRequestList
            });
            if (t.push(e),
            null !== this.dRequestExt)
                throw "requestExtensions not supported";
            var i = new r({
                array: t
            });
            return this.hTLV = i.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && void 0 !== t.reqList && this.setRequestListByParam(t.reqList)
}
,
Mt(at.asn1.ocsp.TBSRequest, at.asn1.ASN1Object),
at.asn1.ocsp.OCSPRequest = function (t) {
    var e = at.asn1
        , r = e.DERSequence
        , i = e.ocsp;
    if (i.OCSPRequest.superclass.constructor.call(this),
        this.dTbsRequest = null,
        this.dOptionalSignature = null,
        this.tohex = function () {
            var t = [];
            if (null === this.dTbsRequest)
                throw "tbsRequest not set";
            if (t.push(this.dTbsRequest),
            null !== this.dOptionalSignature)
                throw "optionalSignature not supported";
            var e = new r({
                array: t
            });
            return this.hTLV = e.tohex(),
                this.hTLV
        }
        ,
        this.getEncodedHex = function () {
            return this.tohex()
        }
        ,
    void 0 !== t && void 0 !== t.reqList) {
        var n = new i.TBSRequest(t);
        this.dTbsRequest = n
    }
}
,
Mt(at.asn1.ocsp.OCSPRequest, at.asn1.ASN1Object),
at.asn1.ocsp.OCSPUtil = {},
at.asn1.ocsp.OCSPUtil.getRequestHex = function (t, e, r) {
    var i = at.asn1.ocsp;
    void 0 === r && (r = i.DEFAULT_HASH);
    var n = {
        alg: r,
        issuerCert: t,
        subjectCert: e
    };
    return new i.OCSPRequest({
        reqList: [n]
    }).tohex()
}
,
at.asn1.ocsp.OCSPUtil.getOCSPResponseInfo = function (t) {
    var r = ut
        , i = r.getVbyList
        , n = r.getVbyListEx
        , s = r.getIdxbyList;
    r.getIdxbyListEx;
    var a = r.getV
        , o = {};
    try {
        var h = n(t, 0, [0], "0a");
        o.responseStatus = parseInt(h, 16)
    } catch (e) {
    }
    if (0 !== o.responseStatus)
        return o;
    try {
        var u = s(t, 0, [1, 0, 1, 0, 0, 2, 0, 1]);
        "80" === t.substr(u, 2) ? o.certStatus = "good" : "a1" === t.substr(u, 2) ? (o.certStatus = "revoked",
            o.revocationTime = mt(i(t, u, [0]))) : "82" === t.substr(u, 2) && (o.certStatus = "unknown")
    } catch (e) {
    }
    try {
        var c = s(t, 0, [1, 0, 1, 0, 0, 2, 0, 2]);
        o.thisUpdate = mt(a(t, c))
    } catch (e) {
    }
    try {
        var l = s(t, 0, [1, 0, 1, 0, 0, 2, 0, 3]);
        "a0" === t.substr(l, 2) && (o.nextUpdate = mt(i(t, l, [0])))
    } catch (e) {
    }
    return o
}
,
at.asn1.ocsp.OCSPParser = function () {
    var t = Error
        , e = Wt
        , r = new e
        , i = ut
        , n = i.getV
        , s = i.getTLV
        , a = i.getIdxbyList
        , o = i.getVbyList
        , h = i.getTLVbyList
        , u = i.getVbyListEx
        , c = i.getTLVbyListEx
        , l = i.getChildIdx;
    this.getOCSPRequest = function (e) {
        var r = l(e, 0);
        if (1 != r.length && 2 != r.length)
            throw new t("wrong number elements: " + r.length);
        return this.getTBSRequest(s(e, r[0]))
    }
        ,
        this.getTBSRequest = function (t) {
            var e = {}
                , i = c(t, 0, [0], "30");
            e.array = this.getRequestList(i);
            var n = c(t, 0, ["[2]", 0], "30");
            return null != n && (e.ext = r.getExtParamArray(n)),
                e
        }
        ,
        this.getRequestList = function (t) {
            for (var e = [], r = l(t, 0), i = 0; i < r.length; i++) {
                t = s(t, r[i]);
                e.push(this.getRequest(t))
            }
            return e
        }
        ,
        this.getRequest = function (e) {
            var i = l(e, 0);
            if (1 != i.length && 2 != i.length)
                throw new t("wrong number elements: " + i.length);
            var n = this.getCertID(s(e, i[0]));
            if (2 == i.length) {
                var o = a(e, 0, [1, 0]);
                n.ext = r.getExtParamArray(s(e, o))
            }
            return n
        }
        ,
        this.getCertID = function (r) {
            var i = l(r, 0);
            if (4 != i.length)
                throw new t("wrong number elements: " + i.length);
            var a = new e
                , o = {};
            return o.alg = a.getAlgorithmIdentifierName(s(r, i[0])),
                o.issname = n(r, i[1]),
                o.isskey = n(r, i[2]),
                o.sbjsn = n(r, i[3]),
                o
        }
        ,
        this.getOCSPResponse = function (t) {
            var e, r = l(t, 0), i = n(t, r[0]), s = parseInt(i);
            if (1 == r.length)
                return {
                    resstatus: s
                };
            var a = h(t, 0, [1, 0]);
            return (e = this.getResponseBytes(a)).resstatus = s,
                e
        }
        ,
        this.getResponseBytes = function (t) {
            var e, r = l(t, 0), i = h(t, 0, [1, 0]);
            e = this.getBasicOCSPResponse(i);
            var s = n(t, r[0]);
            return e.restype = at.asn1.x509.OID.oid2name(Kt(s)),
                e
        }
        ,
        this.getBasicOCSPResponse = function (t) {
            var e, r = l(t, 0);
            e = this.getResponseData(s(t, r[0]));
            var i = new Wt;
            e.alg = i.getAlgorithmIdentifierName(s(t, r[1]));
            var a = n(t, r[2]);
            e.sighex = a.substr(2);
            var o = u(t, 0, ["[0]"]);
            if (null != o) {
                for (var h = l(o, 0), c = [], f = 0; f < h.length; f++) {
                    var g = s(o, h[f]);
                    c.push(g)
                }
                e.certs = c
            }
            return e
        }
        ,
        this.getResponseData = function (t) {
            var e = l(t, 0)
                , r = e.length
                , i = {}
                , a = 0;
            "a0" == t.substr(e[0], 2) && a++,
                i.respid = this.getResponderID(s(t, e[a++]));
            var o = n(t, e[a++]);
            if (i.prodat = mt(o),
                i.array = this.getSingleResponseList(s(t, e[a++])),
            "a1" == t.substr(e[r - 1], 2)) {
                var u = h(t, e[r - 1], [0])
                    , c = new Wt;
                i.ext = c.getExtParamArray(u)
            }
            return i
        }
        ,
        this.getResponderID = function (t) {
            var e = {};
            if ("a2" == t.substr(0, 2)) {
                var r = o(t, 0, [0]);
                e.key = r
            }
            if ("a1" == t.substr(0, 2)) {
                var i = h(t, 0, [0])
                    , n = new Wt;
                e.name = n.getX500Name(i)
            }
            return e
        }
        ,
        this.getSingleResponseList = function (t) {
            for (var e = l(t, 0), r = [], i = 0; i < e.length; i++) {
                var n = this.getSingleResponse(s(t, e[i]));
                r.push(n)
            }
            return r
        }
        ,
        this.getSingleResponse = function (t) {
            var e = l(t, 0)
                , r = {}
                , i = this.getCertID(s(t, e[0]));
            r.certid = i;
            var a = this.getCertStatus(s(t, e[1]));
            if (r.status = a,
            "18" == t.substr(e[2], 2)) {
                var u = n(t, e[2]);
                r.thisupdate = mt(u)
            }
            for (var c = 3; c < e.length; c++) {
                if ("a0" == t.substr(e[c], 2)) {
                    var f = o(t, e[c], [0], "18");
                    r.nextupdate = mt(f)
                }
                if ("a1" == t.substr(e[c], 2)) {
                    var g = new Wt
                        , p = h(t, 0, [c, 0]);
                    r.ext = g.getExtParamArray(p)
                }
            }
            return r
        }
        ,
        this.getCertStatus = function (t) {
            var e = {};
            if ("8000" == t)
                return {
                    status: "good"
                };
            if ("8200" == t)
                return {
                    status: "unknown"
                };
            if ("a1" == t.substr(0, 2)) {
                e.status = "revoked";
                var r = mt(o(t, 0, [0]));
                e.time = r
            }
            return e
        }
}
,
void 0 !== at && at || (at = {}),
void 0 !== at.lang && at.lang || (at.lang = {}),
at.lang.String = function () {
}
,
"function" == typeof Buffer ? (ot = function (t) {
        return ft(Buffer.from(t, "utf8").toString("base64"))
    }
        ,
        ht = function (t) {
            return Buffer.from(gt(t), "base64").toString("utf8")
        }
) : (ot = function (t) {
        return pt(At(Ht(t)))
    }
        ,
        ht = function (t) {
            return decodeURIComponent(Dt(dt(t)))
        }
),
at.lang.String.isInteger = function (t) {
    return !!t.match(/^[0-9]+$/) || !!t.match(/^-[0-9]+$/)
}
,
at.lang.String.isHex = function (t) {
    return Ot(t)
}
,
at.lang.String.isBase64 = function (t) {
    return !(!(t = t.replace(/\s+/g, "")).match(/^[0-9A-Za-z+\/]+={0,3}$/) || t.length % 4 != 0)
}
,
at.lang.String.isBase64URL = function (t) {
    return !t.match(/[+/=]/) && (t = gt(t),
        at.lang.String.isBase64(t))
}
,
at.lang.String.isIntegerArray = function (t) {
    return !!(t = t.replace(/\s+/g, "")).match(/^\[[0-9,]+\]$/)
}
,
at.lang.String.isPrintable = function (t) {
    return null !== t.match(/^[0-9A-Za-z '()+,-./:=?]*$/)
}
,
at.lang.String.isIA5 = function (t) {
    return null !== t.match(/^[\x20-\x21\x23-\x7f]*$/)
}
,
at.lang.String.isMail = function (t) {
    return null !== t.match(/^[A-Za-z0-9]{1}[A-Za-z0-9_.-]*@{1}[A-Za-z0-9_.-]{1,}\.[A-Za-z0-9]{1,}$/)
}
;
var Lt = function (t, e, r) {
    return null == r && (r = "0"),
        t.length >= e ? t : new Array(e - t.length + 1).join(r) + t
};

function kt(t, e) {
    for (var r = 0, i = 0; i < t.length; i++)
        r |= 1 << e[t[i]];
    var n = r.toString(2)
        , s = "";
    for (i = n.length - 1; i >= 0; i--)
        s += n[i];
    return s
}

function qt(t, e, r) {
    if ("object" == typeof t) {
        e = String(e).split(".");
        for (var i = 0; i < e.length && t; i++) {
            var n = e[i];
            n.match(/^[0-9]+$/) && (n = parseInt(n)),
                t = t[n]
        }
        return t || !1 === t ? t : r
    }
}

function Mt(t, e) {
    var r = function () {
    };
    r.prototype = e.prototype,
        t.prototype = new r,
        t.prototype.constructor = t,
        t.superclass = e.prototype,
    e.prototype.constructor == Object.prototype.constructor && (e.prototype.constructor = e)
}

void 0 !== at && at || (at = {}),
void 0 !== at.crypto && at.crypto || (at.crypto = {}),
    at.crypto.Util = new function () {
        this.DIGESTINFOHEAD = {
            sha1: "3021300906052b0e03021a05000414",
            sha224: "302d300d06096086480165030402040500041c",
            sha256: "3031300d060960864801650304020105000420",
            sha384: "3041300d060960864801650304020205000430",
            sha512: "3051300d060960864801650304020305000440",
            md2: "3020300c06082a864886f70d020205000410",
            md5: "3020300c06082a864886f70d020505000410",
            ripemd160: "3021300906052b2403020105000414"
        },
            this.DEFAULTPROVIDER = {
                md5: "cryptojs",
                sha1: "cryptojs",
                sha224: "cryptojs",
                sha256: "cryptojs",
                sha384: "cryptojs",
                sha512: "cryptojs",
                ripemd160: "cryptojs",
                hmacmd5: "cryptojs",
                hmacsha1: "cryptojs",
                hmacsha224: "cryptojs",
                hmacsha256: "cryptojs",
                hmacsha384: "cryptojs",
                hmacsha512: "cryptojs",
                hmacripemd160: "cryptojs",
                MD5withRSA: "cryptojs/jsrsa",
                SHA1withRSA: "cryptojs/jsrsa",
                SHA224withRSA: "cryptojs/jsrsa",
                SHA256withRSA: "cryptojs/jsrsa",
                SHA384withRSA: "cryptojs/jsrsa",
                SHA512withRSA: "cryptojs/jsrsa",
                RIPEMD160withRSA: "cryptojs/jsrsa",
                MD5withECDSA: "cryptojs/jsrsa",
                SHA1withECDSA: "cryptojs/jsrsa",
                SHA224withECDSA: "cryptojs/jsrsa",
                SHA256withECDSA: "cryptojs/jsrsa",
                SHA384withECDSA: "cryptojs/jsrsa",
                SHA512withECDSA: "cryptojs/jsrsa",
                RIPEMD160withECDSA: "cryptojs/jsrsa",
                SHA1withDSA: "cryptojs/jsrsa",
                SHA224withDSA: "cryptojs/jsrsa",
                SHA256withDSA: "cryptojs/jsrsa",
                MD5withRSAandMGF1: "cryptojs/jsrsa",
                SHAwithRSAandMGF1: "cryptojs/jsrsa",
                SHA1withRSAandMGF1: "cryptojs/jsrsa",
                SHA224withRSAandMGF1: "cryptojs/jsrsa",
                SHA256withRSAandMGF1: "cryptojs/jsrsa",
                SHA384withRSAandMGF1: "cryptojs/jsrsa",
                SHA512withRSAandMGF1: "cryptojs/jsrsa",
                RIPEMD160withRSAandMGF1: "cryptojs/jsrsa"
            },
            this.CRYPTOJSMESSAGEDIGESTNAME = {
                md5: l.algo.MD5,
                sha1: l.algo.SHA1,
                sha224: l.algo.SHA224,
                sha256: l.algo.SHA256,
                sha384: l.algo.SHA384,
                sha512: l.algo.SHA512,
                ripemd160: l.algo.RIPEMD160
            },
            this.getDigestInfoHex = function (t, e) {
                if (void 0 === this.DIGESTINFOHEAD[e])
                    throw "alg not supported in Util.DIGESTINFOHEAD: " + e;
                return this.DIGESTINFOHEAD[e] + t
            }
            ,
            this.getPaddedDigestInfoHex = function (t, e, r) {
                var i = this.getDigestInfoHex(t, e)
                    , n = r / 4;
                if (i.length + 22 > n)
                    throw "key is too short for SigAlg: keylen=" + r + "," + e;
                for (var s = "0001", a = "00" + i, o = "", h = n - 4 - a.length, u = 0; u < h; u += 2)
                    o += "ff";
                return s + o + a
            }
            ,
            this.hashString = function (t, e) {
                return new at.crypto.MessageDigest({
                    alg: e
                }).digestString(t)
            }
            ,
            this.hashHex = function (t, e) {
                return new at.crypto.MessageDigest({
                    alg: e
                }).digestHex(t)
            }
            ,
            this.sha1 = function (t) {
                return this.hashString(t, "sha1")
            }
            ,
            this.sha256 = function (t) {
                return this.hashString(t, "sha256")
            }
            ,
            this.sha256Hex = function (t) {
                return this.hashHex(t, "sha256")
            }
            ,
            this.sha512 = function (t) {
                return this.hashString(t, "sha512")
            }
            ,
            this.sha512Hex = function (t) {
                return this.hashHex(t, "sha512")
            }
            ,
            this.isKey = function (t) {
                return t instanceof tt || t instanceof at.crypto.DSA || t instanceof at.crypto.ECDSA
            }
    }
    ,
    at.crypto.Util.md5 = function (t) {
        return new at.crypto.MessageDigest({
            alg: "md5",
            prov: "cryptojs"
        }).digestString(t)
    }
    ,
    at.crypto.Util.ripemd160 = function (t) {
        return new at.crypto.MessageDigest({
            alg: "ripemd160",
            prov: "cryptojs"
        }).digestString(t)
    }
    ,
    at.crypto.Util.SECURERANDOMGEN = new Y,
    at.crypto.Util.getRandomHexOfNbytes = function (t) {
        var e = new Array(t);
        return at.crypto.Util.SECURERANDOMGEN.nextBytes(e),
            ct(e)
    }
    ,
    at.crypto.Util.getRandomBigIntegerOfNbytes = function (t) {
        return new m(at.crypto.Util.getRandomHexOfNbytes(t), 16)
    }
    ,
    at.crypto.Util.getRandomHexOfNbits = function (t) {
        var e = t % 8
            , r = new Array((t - e) / 8 + 1);
        return at.crypto.Util.SECURERANDOMGEN.nextBytes(r),
            r[0] = (255 << e & 255 ^ 255) & r[0],
            ct(r)
    }
    ,
    at.crypto.Util.getRandomBigIntegerOfNbits = function (t) {
        return new m(at.crypto.Util.getRandomHexOfNbits(t), 16)
    }
    ,
    at.crypto.Util.getRandomBigIntegerZeroToMax = function (t) {
        for (var e = t.bitLength(); ;) {
            var r = at.crypto.Util.getRandomBigIntegerOfNbits(e);
            if (-1 != t.compareTo(r))
                return r
        }
    }
    ,
    at.crypto.Util.getRandomBigIntegerMinToMax = function (t, e) {
        var r = t.compareTo(e);
        if (1 == r)
            throw "biMin is greater than biMax";
        if (0 == r)
            return t;
        var i = e.subtract(t);
        return at.crypto.Util.getRandomBigIntegerZeroToMax(i).add(t)
    }
    ,
    at.crypto.MessageDigest = function (t) {
        this.setAlgAndProvider = function (t, e) {
            if (null !== (t = at.crypto.MessageDigest.getCanonicalAlgName(t)) && void 0 === e && (e = at.crypto.Util.DEFAULTPROVIDER[t]),
            -1 != ":md5:sha1:sha224:sha256:sha384:sha512:ripemd160:".indexOf(t) && "cryptojs" == e) {
                try {
                    this.md = at.crypto.Util.CRYPTOJSMESSAGEDIGESTNAME[t].create()
                } catch (r) {
                    throw "setAlgAndProvider hash alg set fail alg=" + t + "/" + r
                }
                this.updateString = function (t) {
                    this.md.update(t)
                }
                    ,
                    this.updateHex = function (t) {
                        var e = l.enc.Hex.parse(t);
                        this.md.update(e)
                    }
                    ,
                    this.digest = function () {
                        return this.md.finalize().toString(l.enc.Hex)
                    }
                    ,
                    this.digestString = function (t) {
                        return this.updateString(t),
                            this.digest()
                    }
                    ,
                    this.digestHex = function (t) {
                        return this.updateHex(t),
                            this.digest()
                    }
            }
            if (-1 != ":sha256:".indexOf(t) && "sjcl" == e) {
                try {
                    this.md = new sjcl.hash.sha256
                } catch (r) {
                    throw "setAlgAndProvider hash alg set fail alg=" + t + "/" + r
                }
                this.updateString = function (t) {
                    this.md.update(t)
                }
                    ,
                    this.updateHex = function (t) {
                        var e = sjcl.codec.hex.toBits(t);
                        this.md.update(e)
                    }
                    ,
                    this.digest = function () {
                        var t = this.md.finalize();
                        return sjcl.codec.hex.fromBits(t)
                    }
                    ,
                    this.digestString = function (t) {
                        return this.updateString(t),
                            this.digest()
                    }
                    ,
                    this.digestHex = function (t) {
                        return this.updateHex(t),
                            this.digest()
                    }
            }
        }
            ,
            this.updateString = function (t) {
                throw "updateString(str) not supported for this alg/prov: " + this.algName + "/" + this.provName
            }
            ,
            this.updateHex = function (t) {
                throw "updateHex(hex) not supported for this alg/prov: " + this.algName + "/" + this.provName
            }
            ,
            this.digest = function () {
                throw "digest() not supported for this alg/prov: " + this.algName + "/" + this.provName
            }
            ,
            this.digestString = function (t) {
                throw "digestString(str) not supported for this alg/prov: " + this.algName + "/" + this.provName
            }
            ,
            this.digestHex = function (t) {
                throw "digestHex(hex) not supported for this alg/prov: " + this.algName + "/" + this.provName
            }
            ,
        void 0 !== t && void 0 !== t.alg && (this.algName = t.alg,
        void 0 === t.prov && (this.provName = at.crypto.Util.DEFAULTPROVIDER[this.algName]),
            this.setAlgAndProvider(this.algName, this.provName))
    }
    ,
    at.crypto.MessageDigest.getCanonicalAlgName = function (t) {
        return "string" == typeof t && (t = (t = t.toLowerCase()).replace(/-/, "")),
            t
    }
    ,
    at.crypto.MessageDigest.getHashLength = function (t) {
        var e = at.crypto.MessageDigest
            , r = e.getCanonicalAlgName(t);
        if (void 0 === e.HASHLENGTH[r])
            throw "not supported algorithm: " + t;
        return e.HASHLENGTH[r]
    }
    ,
    at.crypto.MessageDigest.HASHLENGTH = {
        md5: 16,
        sha1: 20,
        sha224: 28,
        sha256: 32,
        sha384: 48,
        sha512: 64,
        ripemd160: 20
    },
    at.crypto.Mac = function (t) {
        this.setAlgAndProvider = function (t, e) {
            if (null == (t = t.toLowerCase()) && (t = "hmacsha1"),
            "hmac" != (t = t.toLowerCase()).substr(0, 4))
                throw "setAlgAndProvider unsupported HMAC alg: " + t;
            void 0 === e && (e = at.crypto.Util.DEFAULTPROVIDER[t]),
                this.algProv = t + "/" + e;
            var r = t.substr(4);
            if (-1 != ":md5:sha1:sha224:sha256:sha384:sha512:ripemd160:".indexOf(r) && "cryptojs" == e) {
                try {
                    var n = at.crypto.Util.CRYPTOJSMESSAGEDIGESTNAME[r];
                    this.mac = l.algo.HMAC.create(n, this.pass)
                } catch (i) {
                    throw "setAlgAndProvider hash alg set fail hashAlg=" + r + "/" + i
                }
                this.updateString = function (t) {
                    this.mac.update(t)
                }
                    ,
                    this.updateHex = function (t) {
                        var e = l.enc.Hex.parse(t);
                        this.mac.update(e)
                    }
                    ,
                    this.doFinal = function () {
                        return this.mac.finalize().toString(l.enc.Hex)
                    }
                    ,
                    this.doFinalString = function (t) {
                        return this.updateString(t),
                            this.doFinal()
                    }
                    ,
                    this.doFinalHex = function (t) {
                        return this.updateHex(t),
                            this.doFinal()
                    }
            }
        }
            ,
            this.updateString = function (t) {
                throw "updateString(str) not supported for this alg/prov: " + this.algProv
            }
            ,
            this.updateHex = function (t) {
                throw "updateHex(hex) not supported for this alg/prov: " + this.algProv
            }
            ,
            this.doFinal = function () {
                throw "digest() not supported for this alg/prov: " + this.algProv
            }
            ,
            this.doFinalString = function (t) {
                throw "digestString(str) not supported for this alg/prov: " + this.algProv
            }
            ,
            this.doFinalHex = function (t) {
                throw "digestHex(hex) not supported for this alg/prov: " + this.algProv
            }
            ,
            this.setPassword = function (t) {
                if ("string" == typeof t) {
                    var e = t;
                    return t.length % 2 != 1 && t.match(/^[0-9A-Fa-f]+$/) || (e = St(t)),
                        void (this.pass = l.enc.Hex.parse(e))
                }
                if ("object" != typeof t)
                    throw "KJUR.crypto.Mac unsupported password type: " + t;
                e = null;
                if (void 0 !== t.hex) {
                    if (t.hex.length % 2 != 0 || !t.hex.match(/^[0-9A-Fa-f]+$/))
                        throw "Mac: wrong hex password: " + t.hex;
                    e = t.hex
                }
                if (void 0 !== t.utf8 && (e = vt(t.utf8)),
                void 0 !== t.rstr && (e = St(t.rstr)),
                void 0 !== t.b64 && (e = v(t.b64)),
                void 0 !== t.b64u && (e = dt(t.b64u)),
                null == e)
                    throw "KJUR.crypto.Mac unsupported password type: " + t;
                this.pass = l.enc.Hex.parse(e)
            }
            ,
        void 0 !== t && (void 0 !== t.pass && this.setPassword(t.pass),
        void 0 !== t.alg && (this.algName = t.alg,
        void 0 === t.prov && (this.provName = at.crypto.Util.DEFAULTPROVIDER[this.algName]),
            this.setAlgAndProvider(this.algName, this.provName)))
    }
    ,
    at.crypto.Signature = function (t) {
        var e = null;
        if (this._setAlgNames = function () {
            var t = this.algName.match(/^(.+)with(.+)$/);
            t && (this.mdAlgName = t[1].toLowerCase(),
                this.pubkeyAlgName = t[2].toLowerCase(),
            "rsaandmgf1" == this.pubkeyAlgName && "sha" == this.mdAlgName && (this.mdAlgName = "sha1"))
        }
            ,
            this._zeroPaddingOfSignature = function (t, e) {
                for (var r = "", i = e / 4 - t.length, n = 0; n < i; n++)
                    r += "0";
                return r + t
            }
            ,
            this.setAlgAndProvider = function (t, e) {
                if (this._setAlgNames(),
                "cryptojs/jsrsa" != e)
                    throw new Error("provider not supported: " + e);
                if (-1 != ":md5:sha1:sha224:sha256:sha384:sha512:ripemd160:".indexOf(this.mdAlgName)) {
                    try {
                        this.md = new at.crypto.MessageDigest({
                            alg: this.mdAlgName
                        })
                    } catch (r) {
                        throw new Error("setAlgAndProvider hash alg set fail alg=" + this.mdAlgName + "/" + r)
                    }
                    this.init = function (t, e) {
                        var r = null;
                        try {
                            r = void 0 === e ? _t.getKey(t) : _t.getKey(t, e)
                        } catch (i) {
                            throw "init failed:" + i
                        }
                        if (!0 === r.isPrivate)
                            this.prvKey = r,
                                this.state = "SIGN";
                        else {
                            if (!0 !== r.isPublic)
                                throw "init failed.:" + r;
                            this.pubKey = r,
                                this.state = "VERIFY"
                        }
                    }
                        ,
                        this.updateString = function (t) {
                            this.md.updateString(t)
                        }
                        ,
                        this.updateHex = function (t) {
                            this.md.updateHex(t)
                        }
                        ,
                        this.sign = function () {
                            if (this.sHashHex = this.md.digest(),
                            void 0 === this.prvKey && void 0 !== this.ecprvhex && void 0 !== this.eccurvename && void 0 !== at.crypto.ECDSA && (this.prvKey = new at.crypto.ECDSA({
                                curve: this.eccurvename,
                                prv: this.ecprvhex
                            })),
                            this.prvKey instanceof tt && "rsaandmgf1" === this.pubkeyAlgName)
                                this.hSign = this.prvKey.signWithMessageHashPSS(this.sHashHex, this.mdAlgName, this.pssSaltLen);
                            else if (this.prvKey instanceof tt && "rsa" === this.pubkeyAlgName)
                                this.hSign = this.prvKey.signWithMessageHash(this.sHashHex, this.mdAlgName);
                            else if (this.prvKey instanceof at.crypto.ECDSA)
                                this.hSign = this.prvKey.signWithMessageHash(this.sHashHex);
                            else {
                                if (!(this.prvKey instanceof at.crypto.DSA))
                                    throw "Signature: unsupported private key alg: " + this.pubkeyAlgName;
                                this.hSign = this.prvKey.signWithMessageHash(this.sHashHex)
                            }
                            return this.hSign
                        }
                        ,
                        this.signString = function (t) {
                            return this.updateString(t),
                                this.sign()
                        }
                        ,
                        this.signHex = function (t) {
                            return this.updateHex(t),
                                this.sign()
                        }
                        ,
                        this.verify = function (t) {
                            if (this.sHashHex = this.md.digest(),
                            void 0 === this.pubKey && void 0 !== this.ecpubhex && void 0 !== this.eccurvename && void 0 !== at.crypto.ECDSA && (this.pubKey = new at.crypto.ECDSA({
                                curve: this.eccurvename,
                                pub: this.ecpubhex
                            })),
                            this.pubKey instanceof tt && "rsaandmgf1" === this.pubkeyAlgName)
                                return this.pubKey.verifyWithMessageHashPSS(this.sHashHex, t, this.mdAlgName, this.pssSaltLen);
                            if (this.pubKey instanceof tt && "rsa" === this.pubkeyAlgName)
                                return this.pubKey.verifyWithMessageHash(this.sHashHex, t);
                            if (void 0 !== at.crypto.ECDSA && this.pubKey instanceof at.crypto.ECDSA)
                                return this.pubKey.verifyWithMessageHash(this.sHashHex, t);
                            if (void 0 !== at.crypto.DSA && this.pubKey instanceof at.crypto.DSA)
                                return this.pubKey.verifyWithMessageHash(this.sHashHex, t);
                            throw "Signature: unsupported public key alg: " + this.pubkeyAlgName
                        }
                }
            }
            ,
            this.init = function (t, e) {
                throw "init(key, pass) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.updateString = function (t) {
                throw "updateString(str) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.updateHex = function (t) {
                throw "updateHex(hex) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.sign = function () {
                throw "sign() not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.signString = function (t) {
                throw "digestString(str) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.signHex = function (t) {
                throw "digestHex(hex) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.verify = function (t) {
                throw "verify(hSigVal) not supported for this alg:prov=" + this.algProvName
            }
            ,
            this.initParams = t,
        void 0 !== t && (void 0 !== t.alg && (this.algName = t.alg,
            void 0 === t.prov ? this.provName = at.crypto.Util.DEFAULTPROVIDER[this.algName] : this.provName = t.prov,
            this.algProvName = this.algName + ":" + this.provName,
            this.setAlgAndProvider(this.algName, this.provName),
            this._setAlgNames()),
        void 0 !== t.psssaltlen && (this.pssSaltLen = t.psssaltlen),
        void 0 !== t.prvkeypem)) {
            if (void 0 !== t.prvkeypas)
                throw "both prvkeypem and prvkeypas parameters not supported";
            try {
                e = _t.getKey(t.prvkeypem);
                this.init(e)
            } catch (s) {
                throw "fatal error to load pem private key: " + s
            }
        }
    }
    ,
    at.crypto.Cipher = function (t) {
    }
    ,
    at.crypto.Cipher.encrypt = function (t, e, r) {
        if (e instanceof tt && e.isPublic) {
            var i = at.crypto.Cipher.getAlgByKeyAndName(e, r);
            if ("RSA" === i)
                return e.encrypt(t);
            if ("RSAOAEP" === i)
                return e.encryptOAEP(t, "sha1");
            var n = i.match(/^RSAOAEP(\d+)$/);
            if (null !== n)
                return e.encryptOAEP(t, "sha" + n[1]);
            throw "Cipher.encrypt: unsupported algorithm for RSAKey: " + r
        }
        throw "Cipher.encrypt: unsupported key or algorithm"
    }
    ,
    at.crypto.Cipher.decrypt = function (t, e, r) {
        if (e instanceof tt && e.isPrivate) {
            var i = at.crypto.Cipher.getAlgByKeyAndName(e, r);
            if ("RSA" === i)
                return e.decrypt(t);
            if ("RSAOAEP" === i)
                return e.decryptOAEP(t, "sha1");
            var n = i.match(/^RSAOAEP(\d+)$/);
            if (null !== n)
                return e.decryptOAEP(t, "sha" + n[1]);
            throw "Cipher.decrypt: unsupported algorithm for RSAKey: " + r
        }
        throw "Cipher.decrypt: unsupported key or algorithm"
    }
    ,
    at.crypto.Cipher.getAlgByKeyAndName = function (t, e) {
        if (t instanceof tt) {
            if (-1 != ":RSA:RSAOAEP:RSAOAEP224:RSAOAEP256:RSAOAEP384:RSAOAEP512:".indexOf(e))
                return e;
            if (null == e)
                return "RSA";
            throw "getAlgByKeyAndName: not supported algorithm name for RSAKey: " + e
        }
        throw "getAlgByKeyAndName: not supported algorithm name: " + e
    }
    ,
    at.crypto.OID = new function () {
        this.oidhex2name = {
            "2a864886f70d010101": "rsaEncryption",
            "2a8648ce3d0201": "ecPublicKey",
            "2a8648ce380401": "dsa",
            "2a8648ce3d030107": "secp256r1",
            "2b8104001f": "secp192k1",
            "2b81040021": "secp224r1",
            "2b8104000a": "secp256k1",
            "2b81040022": "secp384r1",
            "2b81040023": "secp521r1",
            "2a8648ce380403": "SHA1withDSA",
            "608648016503040301": "SHA224withDSA",
            "608648016503040302": "SHA256withDSA"
        }
    }
    ,
void 0 !== at && at || (at = {}),
void 0 !== at.crypto && at.crypto || (at.crypto = {}),
    at.crypto.ECDSA = function (t) {
        var e = Error
            , r = m
            , i = it
            , n = at.crypto.ECDSA
            , s = at.crypto.ECParameterDB
            , a = n.getName
            , o = ut
            , h = o.getVbyListEx
            , u = o.isASN1HEX
            , c = new Y;
        this.type = "EC",
            this.isPrivate = !1,
            this.isPublic = !1,
            this.getBigRandom = function (t) {
                return new r(t.bitLength(), c).mod(t.subtract(r.ONE)).add(r.ONE)
            }
            ,
            this.setNamedCurve = function (t) {
                this.ecparams = s.getByName(t),
                    this.prvKeyHex = null,
                    this.pubKeyHex = null,
                    this.curveName = t
            }
            ,
            this.setPrivateKeyHex = function (t) {
                this.isPrivate = !0,
                    this.prvKeyHex = t
            }
            ,
            this.setPublicKeyHex = function (t) {
                this.isPublic = !0,
                    this.pubKeyHex = t
            }
            ,
            this.getPublicKeyXYHex = function () {
                var t = this.pubKeyHex;
                if ("04" !== t.substr(0, 2))
                    throw "this method supports uncompressed format(04) only";
                var e = this.ecparams.keycharlen;
                if (t.length !== 2 + 2 * e)
                    throw "malformed public key hex length";
                var r = {};
                return r.x = t.substr(2, e),
                    r.y = t.substr(2 + e),
                    r
            }
            ,
            this.getShortNISTPCurveName = function () {
                var t = this.curveName;
                return "secp256r1" === t || "NIST P-256" === t || "P-256" === t || "prime256v1" === t ? "P-256" : "secp384r1" === t || "NIST P-384" === t || "P-384" === t ? "P-384" : "secp521r1" === t || "NIST P-521" === t || "P-521" === t ? "P-521" : null
            }
            ,
            this.generateKeyPairHex = function () {
                var t = this.ecparams.n
                    , e = this.getBigRandom(t)
                    , r = this.ecparams.keycharlen
                    , i = ("0000000000" + e.toString(16)).slice(-r);
                return this.setPrivateKeyHex(i),
                    {
                        ecprvhex: i,
                        ecpubhex: this.generatePublicKeyHex()
                    }
            }
            ,
            this.generatePublicKeyHex = function () {
                var t = new r(this.prvKeyHex, 16)
                    , e = this.ecparams.G.multiply(t)
                    , i = e.getX().toBigInteger()
                    , n = e.getY().toBigInteger()
                    , s = this.ecparams.keycharlen
                    , a = "04" + ("0000000000" + i.toString(16)).slice(-s) + ("0000000000" + n.toString(16)).slice(-s);
                return this.setPublicKeyHex(a),
                    a
            }
            ,
            this.signWithMessageHash = function (t) {
                return this.signHex(t, this.prvKeyHex)
            }
            ,
            this.signHex = function (t, e) {
                var i = new r(e, 16)
                    , s = this.ecparams.n
                    , a = new r(t.substring(0, this.ecparams.keycharlen), 16);
                do {
                    var o = this.getBigRandom(s)
                        , h = this.ecparams.G.multiply(o).getX().toBigInteger().mod(s)
                } while (h.compareTo(r.ZERO) <= 0);
                var u = o.modInverse(s).multiply(a.add(i.multiply(h))).mod(s);
                return n.biRSSigToASN1Sig(h, u)
            }
            ,
            this.sign = function (t, e) {
                var i = e
                    , n = this.ecparams.n
                    , s = r.fromByteArrayUnsigned(t);
                do {
                    var a = this.getBigRandom(n)
                        , o = this.ecparams.G.multiply(a).getX().toBigInteger().mod(n)
                } while (o.compareTo(m.ZERO) <= 0);
                var h = a.modInverse(n).multiply(s.add(i.multiply(o))).mod(n);
                return this.serializeSig(o, h)
            }
            ,
            this.verifyWithMessageHash = function (t, e) {
                return this.verifyHex(t, e, this.pubKeyHex)
            }
            ,
            this.verifyHex = function (t, e, s) {
                try {
                    var a, o, h = n.parseSigHex(e);
                    a = h.r,
                        o = h.s;
                    var u = i.decodeFromHex(this.ecparams.curve, s)
                        , c = new r(t.substring(0, this.ecparams.keycharlen), 16);
                    return this.verifyRaw(c, a, o, u)
                } catch (l) {
                    return !1
                }
            }
            ,
            this.verify = function (t, e, n) {
                var s, a, o;
                if (Bitcoin.Util.isArray(e)) {
                    var h = this.parseSig(e);
                    s = h.r,
                        a = h.s
                } else {
                    if ("object" != typeof e || !e.r || !e.s)
                        throw "Invalid value for signature";
                    s = e.r,
                        a = e.s
                }
                if (n instanceof it)
                    o = n;
                else {
                    if (!Bitcoin.Util.isArray(n))
                        throw "Invalid format for pubkey value, must be byte array or ECPointFp";
                    o = i.decodeFrom(this.ecparams.curve, n)
                }
                var u = r.fromByteArrayUnsigned(t);
                return this.verifyRaw(u, s, a, o)
            }
            ,
            this.verifyRaw = function (t, e, i, n) {
                var s = this.ecparams.n
                    , a = this.ecparams.G;
                if (e.compareTo(r.ONE) < 0 || e.compareTo(s) >= 0)
                    return !1;
                if (i.compareTo(r.ONE) < 0 || i.compareTo(s) >= 0)
                    return !1;
                var o = i.modInverse(s)
                    , h = t.multiply(o).mod(s)
                    , u = e.multiply(o).mod(s);
                return a.multiply(h).add(n.multiply(u)).getX().toBigInteger().mod(s).equals(e)
            }
            ,
            this.serializeSig = function (t, e) {
                var r = t.toByteArraySigned()
                    , i = e.toByteArraySigned()
                    , n = [];
                return n.push(2),
                    n.push(r.length),
                    (n = n.concat(r)).push(2),
                    n.push(i.length),
                    (n = n.concat(i)).unshift(n.length),
                    n.unshift(48),
                    n
            }
            ,
            this.parseSig = function (t) {
                var e;
                if (48 != t[0])
                    throw new Error("Signature not a valid DERSequence");
                if (2 != t[e = 2])
                    throw new Error("First element in signature must be a DERInteger");
                var i = t.slice(e + 2, e + 2 + t[e + 1]);
                if (2 != t[e += 2 + t[e + 1]])
                    throw new Error("Second element in signature must be a DERInteger");
                var n = t.slice(e + 2, e + 2 + t[e + 1]);
                return e += 2 + t[e + 1],
                    {
                        r: r.fromByteArrayUnsigned(i),
                        s: r.fromByteArrayUnsigned(n)
                    }
            }
            ,
            this.parseSigCompact = function (t) {
                if (65 !== t.length)
                    throw "Signature has the wrong length";
                var e = t[0] - 27;
                if (e < 0 || e > 7)
                    throw "Invalid signature type";
                var i = this.ecparams.n;
                return {
                    r: r.fromByteArrayUnsigned(t.slice(1, 33)).mod(i),
                    s: r.fromByteArrayUnsigned(t.slice(33, 65)).mod(i),
                    i: e
                }
            }
            ,
            this.readPKCS5PrvKeyHex = function (t) {
                if (!1 === u(t))
                    throw new Error("not ASN.1 hex string");
                var e, r, i;
                try {
                    e = h(t, 0, ["[0]", 0], "06"),
                        r = h(t, 0, [1], "04");
                    try {
                        i = h(t, 0, ["[1]", 0], "03")
                    } catch (n) {
                    }
                } catch (n) {
                    throw new Error("malformed PKCS#1/5 plain ECC private key")
                }
                if (this.curveName = a(e),
                void 0 === this.curveName)
                    throw "unsupported curve name";
                this.setNamedCurve(this.curveName),
                    this.setPublicKeyHex(i),
                    this.setPrivateKeyHex(r),
                    this.isPublic = !1
            }
            ,
            this.readPKCS8PrvKeyHex = function (t) {
                if (!1 === u(t))
                    throw new e("not ASN.1 hex string");
                var r, i, n;
                try {
                    h(t, 0, [1, 0], "06"),
                        r = h(t, 0, [1, 1], "06"),
                        i = h(t, 0, [2, 0, 1], "04");
                    try {
                        n = h(t, 0, [2, 0, "[1]", 0], "03")
                    } catch (s) {
                    }
                } catch (s) {
                    throw new e("malformed PKCS#8 plain ECC private key")
                }
                if (this.curveName = a(r),
                void 0 === this.curveName)
                    throw new e("unsupported curve name");
                this.setNamedCurve(this.curveName),
                    this.setPublicKeyHex(n),
                    this.setPrivateKeyHex(i),
                    this.isPublic = !1
            }
            ,
            this.readPKCS8PubKeyHex = function (t) {
                if (!1 === u(t))
                    throw new e("not ASN.1 hex string");
                var r, i;
                try {
                    h(t, 0, [0, 0], "06"),
                        r = h(t, 0, [0, 1], "06"),
                        i = h(t, 0, [1], "03")
                } catch (n) {
                    throw new e("malformed PKCS#8 ECC public key")
                }
                if (this.curveName = a(r),
                null === this.curveName)
                    throw new e("unsupported curve name");
                this.setNamedCurve(this.curveName),
                    this.setPublicKeyHex(i)
            }
            ,
            this.readCertPubKeyHex = function (t, r) {
                if (!1 === u(t))
                    throw new e("not ASN.1 hex string");
                var i, n;
                try {
                    i = h(t, 0, [0, 5, 0, 1], "06"),
                        n = h(t, 0, [0, 5, 1], "03")
                } catch (s) {
                    throw new e("malformed X.509 certificate ECC public key")
                }
                if (this.curveName = a(i),
                null === this.curveName)
                    throw new e("unsupported curve name");
                this.setNamedCurve(this.curveName),
                    this.setPublicKeyHex(n)
            }
            ,
        void 0 !== t && void 0 !== t.curve && (this.curveName = t.curve),
        void 0 === this.curveName && (this.curveName = "secp256r1"),
            this.setNamedCurve(this.curveName),
        void 0 !== t && (void 0 !== t.prv && this.setPrivateKeyHex(t.prv),
        void 0 !== t.pub && this.setPublicKeyHex(t.pub))
    }
    ,
    at.crypto.ECDSA.parseSigHex = function (t) {
        var e = at.crypto.ECDSA.parseSigHexInHexRS(t);
        return {
            r: new m(e.r, 16),
            s: new m(e.s, 16)
        }
    }
    ,
    at.crypto.ECDSA.parseSigHexInHexRS = function (t) {
        var e = ut
            , r = e.getChildIdx
            , i = e.getV;
        if (e.checkStrictDER(t, 0),
        "30" != t.substr(0, 2))
            throw new Error("signature is not a ASN.1 sequence");
        var n = r(t, 0);
        if (2 != n.length)
            throw new Error("signature shall have two elements");
        var s = n[0]
            , a = n[1];
        if ("02" != t.substr(s, 2))
            throw new Error("1st item not ASN.1 integer");
        if ("02" != t.substr(a, 2))
            throw new Error("2nd item not ASN.1 integer");
        return {
            r: i(t, s),
            s: i(t, a)
        }
    }
    ,
    at.crypto.ECDSA.asn1SigToConcatSig = function (t) {
        var e = at.crypto.ECDSA.parseSigHexInHexRS(t)
            , r = e.r
            , i = e.s;
        if (r.length >= 130 && r.length <= 134) {
            if (r.length % 2 != 0)
                throw Error("unknown ECDSA sig r length error");
            if (i.length % 2 != 0)
                throw Error("unknown ECDSA sig s length error");
            "00" == r.substr(0, 2) && (r = r.substr(2)),
            "00" == i.substr(0, 2) && (i = i.substr(2));
            var n = Math.max(r.length, i.length);
            return (r = ("000000" + r).slice(-n)) + (i = ("000000" + i).slice(-n))
        }
        if ("00" == r.substr(0, 2) && r.length % 32 == 2 && (r = r.substr(2)),
        "00" == i.substr(0, 2) && i.length % 32 == 2 && (i = i.substr(2)),
        r.length % 32 == 30 && (r = "00" + r),
        i.length % 32 == 30 && (i = "00" + i),
        r.length % 32 != 0)
            throw Error("unknown ECDSA sig r length error");
        if (i.length % 32 != 0)
            throw Error("unknown ECDSA sig s length error");
        return r + i
    }
    ,
    at.crypto.ECDSA.concatSigToASN1Sig = function (t) {
        if (t.length % 4 != 0)
            throw Error("unknown ECDSA concatinated r-s sig length error");
        var e = t.substr(0, t.length / 2)
            , r = t.substr(t.length / 2);
        return at.crypto.ECDSA.hexRSSigToASN1Sig(e, r)
    }
    ,
    at.crypto.ECDSA.hexRSSigToASN1Sig = function (t, e) {
        var r = new m(t, 16)
            , i = new m(e, 16);
        return at.crypto.ECDSA.biRSSigToASN1Sig(r, i)
    }
    ,
    at.crypto.ECDSA.biRSSigToASN1Sig = function (t, e) {
        var r = at.asn1
            , i = new r.DERInteger({
            bigint: t
        })
            , n = new r.DERInteger({
            bigint: e
        });
        return new r.DERSequence({
            array: [i, n]
        }).tohex()
    }
    ,
    at.crypto.ECDSA.getName = function (t) {
        return "2b8104001f" === t ? "secp192k1" : "2a8648ce3d030107" === t ? "secp256r1" : "2b8104000a" === t ? "secp256k1" : "2b81040021" === t ? "secp224r1" : "2b81040022" === t ? "secp384r1" : "2b81040023" === t ? "secp521r1" : -1 !== "|secp256r1|NIST P-256|P-256|prime256v1|".indexOf(t) ? "secp256r1" : -1 !== "|secp256k1|".indexOf(t) ? "secp256k1" : -1 !== "|secp224r1|NIST P-224|P-224|".indexOf(t) ? "secp224r1" : -1 !== "|secp384r1|NIST P-384|P-384|".indexOf(t) ? "secp384r1" : -1 !== "|secp521r1|NIST P-521|P-521|".indexOf(t) ? "secp521r1" : null
    }
    ,
void 0 !== at && at || (at = {}),
void 0 !== at.crypto && at.crypto || (at.crypto = {}),
    at.crypto.ECParameterDB = new function () {
        var t = {}
            , e = {};

        function r(t) {
            return new m(t, 16)
        }

        this.getByName = function (r) {
            var i = r;
            if (void 0 !== e[i] && (i = e[r]),
            void 0 !== t[i])
                return t[i];
            throw "unregistered EC curve name: " + i
        }
            ,
            this.regist = function (i, n, s, a, o, h, u, c, l, f, g, p) {
                t[i] = {};
                var d = r(s)
                    , v = r(a)
                    , m = r(o)
                    , y = r(h)
                    , x = r(u)
                    , S = new nt(d, v, m)
                    , E = S.decodePointHex("04" + c + l);
                t[i].name = i,
                    t[i].keylen = n,
                    t[i].keycharlen = 2 * Math.ceil(n / 8),
                    t[i].curve = S,
                    t[i].G = E,
                    t[i].n = y,
                    t[i].h = x,
                    t[i].oid = g,
                    t[i].info = p;
                for (var w = 0; w < f.length; w++)
                    e[f[w]] = i
            }
    }
    ,
    at.crypto.ECParameterDB.regist("secp128r1", 128, "FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFC", "E87579C11079F43DD824993C2CEE5ED3", "FFFFFFFE0000000075A30D1B9038A115", "1", "161FF7528B899B2D0C28607CA52C5B86", "CF5AC8395BAFEB13C02DA292DDED7A83", [], "", "secp128r1 : SECG curve over a 128 bit prime field"),
    at.crypto.ECParameterDB.regist("secp160k1", 160, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFAC73", "0", "7", "0100000000000000000001B8FA16DFAB9ACA16B6B3", "1", "3B4C382CE37AA192A4019E763036F4F5DD4D7EBB", "938CF935318FDCED6BC28286531733C3F03C4FEE", [], "", "secp160k1 : SECG curve over a 160 bit prime field"),
    at.crypto.ECParameterDB.regist("secp160r1", 160, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFC", "1C97BEFC54BD7A8B65ACF89F81D4D4ADC565FA45", "0100000000000000000001F4C8F927AED3CA752257", "1", "4A96B5688EF573284664698968C38BB913CBFC82", "23A628553168947D59DCC912042351377AC5FB32", [], "", "secp160r1 : SECG curve over a 160 bit prime field"),
    at.crypto.ECParameterDB.regist("secp192k1", 192, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37", "0", "3", "FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D", "1", "DB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D", "9B2F2F6D9C5628A7844163D015BE86344082AA88D95E2F9D", []),
    at.crypto.ECParameterDB.regist("secp192r1", 192, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC", "64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1", "FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", "1", "188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012", "07192B95FFC8DA78631011ED6B24CDD573F977A11E794811", []),
    at.crypto.ECParameterDB.regist("secp224r1", 224, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE", "B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4", "FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D", "1", "B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21", "BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34", []),
    at.crypto.ECParameterDB.regist("secp256k1", 256, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", "0", "7", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", "1", "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", []),
    at.crypto.ECParameterDB.regist("secp256r1", 256, "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", "1", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", ["NIST P-256", "P-256", "prime256v1"]),
    at.crypto.ECParameterDB.regist("secp384r1", 384, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", "B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973", "1", "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", "3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", ["NIST P-384", "P-384"]),
    at.crypto.ECParameterDB.regist("secp521r1", 521, "1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC", "051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00", "1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409", "1", "00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66", "011839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", ["NIST P-521", "P-521"]),
void 0 !== at && at || (at = {}),
void 0 !== at.crypto && at.crypto || (at.crypto = {}),
    at.crypto.DSA = function () {
        var t = ut;
        t.getVbyList;
        var r = t.getVbyListEx
            , i = t.isASN1HEX
            , n = m;
        this.p = null,
            this.q = null,
            this.g = null,
            this.y = null,
            this.x = null,
            this.type = "DSA",
            this.isPrivate = !1,
            this.isPublic = !1,
            this.setPrivate = function (t, e, r, i, n) {
                this.isPrivate = !0,
                    this.p = t,
                    this.q = e,
                    this.g = r,
                    this.y = i,
                    this.x = n
            }
            ,
            this.setPrivateHex = function (t, e, r, i, n) {
                var s, a, o, h, u;
                s = new m(t, 16),
                    a = new m(e, 16),
                    o = new m(r, 16),
                    h = "string" == typeof i && i.length > 1 ? new m(i, 16) : null,
                    u = new m(n, 16),
                    this.setPrivate(s, a, o, h, u)
            }
            ,
            this.setPublic = function (t, e, r, i) {
                this.isPublic = !0,
                    this.p = t,
                    this.q = e,
                    this.g = r,
                    this.y = i,
                    this.x = null
            }
            ,
            this.setPublicHex = function (t, e, r, i) {
                var n, s, a, o;
                n = new m(t, 16),
                    s = new m(e, 16),
                    a = new m(r, 16),
                    o = new m(i, 16),
                    this.setPublic(n, s, a, o)
            }
            ,
            this.signWithMessageHash = function (t) {
                var e = this.p
                    , r = this.q
                    , i = this.g;
                this.y;
                var n = this.x
                    , s = at.crypto.Util.getRandomBigIntegerMinToMax(m.ONE.add(m.ONE), r.subtract(m.ONE))
                    , a = new m(t.substr(0, r.bitLength() / 4), 16)
                    , o = i.modPow(s, e).mod(r)
                    , h = s.modInverse(r).multiply(a.add(n.multiply(o))).mod(r);
                return at.asn1.ASN1Util.jsonToASN1HEX({
                    seq: [{
                        int: {
                            bigint: o
                        }
                    }, {
                        int: {
                            bigint: h
                        }
                    }]
                })
            }
            ,
            this.verifyWithMessageHash = function (t, e) {
                var r = this.p
                    , i = this.q
                    , n = this.g
                    , s = this.y
                    , a = this.parseASN1Signature(e)
                    , o = a[0]
                    , h = a[1]
                    , u = new m(t.substr(0, i.bitLength() / 4), 16);
                if (m.ZERO.compareTo(o) > 0 || o.compareTo(i) > 0)
                    throw "invalid DSA signature";
                if (m.ZERO.compareTo(h) >= 0 || h.compareTo(i) > 0)
                    throw "invalid DSA signature";
                var c = h.modInverse(i)
                    , l = u.multiply(c).mod(i)
                    , f = o.multiply(c).mod(i);
                return 0 == n.modPow(l, r).multiply(s.modPow(f, r)).mod(r).mod(i).compareTo(o)
            }
            ,
            this.parseASN1Signature = function (t) {
                try {
                    return [new n(r(t, 0, [0], "02"), 16), new n(r(t, 0, [1], "02"), 16)]
                } catch (e) {
                    throw new Error("malformed ASN.1 DSA signature")
                }
            }
            ,
            this.readPKCS5PrvKeyHex = function (t) {
                var n, s, a, o, h;
                if (!1 === i(t))
                    throw new Error("not ASN.1 hex string");
                try {
                    n = r(t, 0, [1], "02"),
                        s = r(t, 0, [2], "02"),
                        a = r(t, 0, [3], "02"),
                        o = r(t, 0, [4], "02"),
                        h = r(t, 0, [5], "02")
                } catch (e) {
                    throw new Error("malformed PKCS#1/5 plain DSA private key")
                }
                this.setPrivateHex(n, s, a, o, h)
            }
            ,
            this.readPKCS8PrvKeyHex = function (t) {
                var n, s, a, o;
                if (!1 === i(t))
                    throw new Error("not ASN.1 hex string");
                try {
                    n = r(t, 0, [1, 1, 0], "02"),
                        s = r(t, 0, [1, 1, 1], "02"),
                        a = r(t, 0, [1, 1, 2], "02"),
                        o = r(t, 0, [2, 0], "02")
                } catch (e) {
                    throw new Error("malformed PKCS#8 plain DSA private key")
                }
                this.setPrivateHex(n, s, a, null, o)
            }
            ,
            this.readPKCS8PubKeyHex = function (t) {
                var n, s, a, o;
                if (!1 === i(t))
                    throw new Error("not ASN.1 hex string");
                try {
                    n = r(t, 0, [0, 1, 0], "02"),
                        s = r(t, 0, [0, 1, 1], "02"),
                        a = r(t, 0, [0, 1, 2], "02"),
                        o = r(t, 0, [1, 0], "02")
                } catch (e) {
                    throw new Error("malformed PKCS#8 DSA public key")
                }
                this.setPublicHex(n, s, a, o)
            }
            ,
            this.readCertPubKeyHex = function (t, n) {
                var s, a, o, h;
                if (!1 === i(t))
                    throw new Error("not ASN.1 hex string");
                try {
                    s = r(t, 0, [0, 5, 0, 1, 0], "02"),
                        a = r(t, 0, [0, 5, 0, 1, 1], "02"),
                        o = r(t, 0, [0, 5, 0, 1, 2], "02"),
                        h = r(t, 0, [0, 5, 1, 0], "02")
                } catch (e) {
                    throw new Error("malformed X.509 certificate DSA public key")
                }
                this.setPublicHex(s, a, o, h)
            }
    }
;
var _t = function () {
    var t = function (t, r, i) {
        return e(l.AES, t, r, i)
    }
        , e = function (t, e, r, i) {
        var n = l.enc.Hex.parse(e)
            , s = l.enc.Hex.parse(r)
            , a = l.enc.Hex.parse(i)
            , o = {};
        o.key = s,
            o.iv = a,
            o.ciphertext = n;
        var h = t.decrypt(o, s, {
            iv: a
        });
        return l.enc.Hex.stringify(h)
    }
        , r = function (t, e, r) {
        return i(l.AES, t, e, r)
    }
        , i = function (t, e, r, i) {
        var n = l.enc.Hex.parse(e)
            , s = l.enc.Hex.parse(r)
            , a = l.enc.Hex.parse(i)
            , o = t.encrypt(n, s, {
            iv: a
        })
            , h = l.enc.Hex.parse(o.toString());
        return l.enc.Base64.stringify(h)
    }
        , n = {
        "AES-256-CBC": {
            proc: t,
            eproc: r,
            keylen: 32,
            ivlen: 16
        },
        "AES-192-CBC": {
            proc: t,
            eproc: r,
            keylen: 24,
            ivlen: 16
        },
        "AES-128-CBC": {
            proc: t,
            eproc: r,
            keylen: 16,
            ivlen: 16
        },
        "DES-EDE3-CBC": {
            proc: function (t, r, i) {
                return e(l.TripleDES, t, r, i)
            },
            eproc: function (t, e, r) {
                return i(l.TripleDES, t, e, r)
            },
            keylen: 24,
            ivlen: 8
        },
        "DES-CBC": {
            proc: function (t, r, i) {
                return e(l.DES, t, r, i)
            },
            eproc: function (t, e, r) {
                return i(l.DES, t, e, r)
            },
            keylen: 8,
            ivlen: 8
        }
    }
        , s = function (t) {
        var e = {}
            , r = t.match(new RegExp("DEK-Info: ([^,]+),([0-9A-Fa-f]+)", "m"));
        r && (e.cipher = r[1],
            e.ivsalt = r[2]);
        var i = t.match(new RegExp("-----BEGIN ([A-Z]+) PRIVATE KEY-----"));
        i && (e.type = i[1]);
        var n = -1
            , s = 0;
        -1 != t.indexOf("\r\n\r\n") && (n = t.indexOf("\r\n\r\n"),
            s = 2),
        -1 != t.indexOf("\n\n") && (n = t.indexOf("\n\n"),
            s = 1);
        var a = t.indexOf("-----END");
        if (-1 != n && -1 != a) {
            var o = t.substring(n + 2 * s, a - s);
            o = o.replace(/\s+/g, ""),
                e.data = o
        }
        return e
    }
        , a = function (t, e, r) {
        for (var i = r.substring(0, 16), s = l.enc.Hex.parse(i), a = l.enc.Utf8.parse(e), o = n[t].keylen + n[t].ivlen, h = "", u = null; ;) {
            var c = l.algo.MD5.create();
            if (null != u && c.update(u),
                c.update(a),
                c.update(s),
                u = c.finalize(),
            (h += l.enc.Hex.stringify(u)).length >= 2 * o)
                break
        }
        var f = {};
        return f.keyhex = h.substr(0, 2 * n[t].keylen),
            f.ivhex = h.substr(2 * n[t].keylen, 2 * n[t].ivlen),
            f
    }
        , o = function (t, e, r, i) {
        var s = l.enc.Base64.parse(t)
            , a = l.enc.Hex.stringify(s);
        return (0,
            n[e].proc)(a, r, i)
    };
    return {
        version: "1.0.0",
        parsePKCS5PEM: function (t) {
            return s(t)
        },
        getKeyAndUnusedIvByPasscodeAndIvsalt: function (t, e, r) {
            return a(t, e, r)
        },
        decryptKeyB64: function (t, e, r, i) {
            return o(t, e, r, i)
        },
        getDecryptedKeyHex: function (t, e) {
            var r = s(t)
                , i = r.cipher
                , n = r.ivsalt
                , h = r.data
                , u = a(i, e, n).keyhex;
            return o(h, i, u, n)
        },
        getEncryptedPKCS5PEMFromPrvKeyHex: function (t, e, r, i, s) {
            var o = "";
            if (void 0 !== i && null != i || (i = "AES-256-CBC"),
            void 0 === n[i])
                throw new Error("KEYUTIL unsupported algorithm: " + i);
            if (void 0 === s || null == s) {
                var h = function (t) {
                    var e = l.lib.WordArray.random(t);
                    return l.enc.Hex.stringify(e)
                }(n[i].ivlen);
                s = h.toUpperCase()
            }
            var u = function (t, e, r, i) {
                return (0,
                    n[e].eproc)(t, r, i)
            }(e, i, a(i, r, s).keyhex, s);
            o = "-----BEGIN " + t + " PRIVATE KEY-----\r\n";
            return o += "Proc-Type: 4,ENCRYPTED\r\n",
                o += "DEK-Info: " + i + "," + s + "\r\n",
                o += "\r\n",
                o += u.replace(/(.{64})/g, "$1\r\n"),
                o += "\r\n-----END " + t + " PRIVATE KEY-----\r\n"
        },
        parseHexOfEncryptedPKCS8: function (t) {
            var e = ut
                , r = e.getChildIdx
                , i = e.getV
                , n = {}
                , s = r(t, 0);
            if (2 != s.length)
                throw new Error("malformed format: SEQUENCE(0).items != 2: " + s.length);
            n.ciphertext = i(t, s[1]);
            var a = r(t, s[0]);
            if (2 != a.length)
                throw new Error("malformed format: SEQUENCE(0.0).items != 2: " + a.length);
            if ("2a864886f70d01050d" != i(t, a[0]))
                throw new Error("this only supports pkcs5PBES2");
            var o = r(t, a[1]);
            if (2 != a.length)
                throw new Error("malformed format: SEQUENCE(0.0.1).items != 2: " + o.length);
            var h = r(t, o[1]);
            if (2 != h.length)
                throw new Error("malformed format: SEQUENCE(0.0.1.1).items != 2: " + h.length);
            if ("2a864886f70d0307" != i(t, h[0]))
                throw "this only supports TripleDES";
            n.encryptionSchemeAlg = "TripleDES",
                n.encryptionSchemeIV = i(t, h[1]);
            var u = r(t, o[0]);
            if (2 != u.length)
                throw new Error("malformed format: SEQUENCE(0.0.1.0).items != 2: " + u.length);
            if ("2a864886f70d01050c" != i(t, u[0]))
                throw new Error("this only supports pkcs5PBKDF2");
            var c = r(t, u[1]);
            if (c.length < 2)
                throw new Error("malformed format: SEQUENCE(0.0.1.0.1).items < 2: " + c.length);
            n.pbkdf2Salt = i(t, c[0]);
            var l = i(t, c[1]);
            try {
                n.pbkdf2Iter = parseInt(l, 16)
            } catch (f) {
                throw new Error("malformed format pbkdf2Iter: " + l)
            }
            return n
        },
        getPBKDF2KeyHexFromParam: function (t, e) {
            var r = l.enc.Hex.parse(t.pbkdf2Salt)
                , i = t.pbkdf2Iter
                , n = l.PBKDF2(e, r, {
                keySize: 6,
                iterations: i
            });
            return l.enc.Hex.stringify(n)
        },
        _getPlainPKCS8HexFromEncryptedPKCS8PEM: function (t, e) {
            var r = Ft(t, "ENCRYPTED PRIVATE KEY")
                , i = this.parseHexOfEncryptedPKCS8(r)
                , n = _t.getPBKDF2KeyHexFromParam(i, e)
                , s = {};
            s.ciphertext = l.enc.Hex.parse(i.ciphertext);
            var a = l.enc.Hex.parse(n)
                , o = l.enc.Hex.parse(i.encryptionSchemeIV)
                , h = l.TripleDES.decrypt(s, a, {
                iv: o
            });
            return l.enc.Hex.stringify(h)
        },
        getKeyFromEncryptedPKCS8PEM: function (t, e) {
            var r = this._getPlainPKCS8HexFromEncryptedPKCS8PEM(t, e);
            return this.getKeyFromPlainPrivatePKCS8Hex(r)
        },
        parsePlainPrivatePKCS8Hex: function (t) {
            var e = ut
                , r = e.getChildIdx
                , i = e.getV
                , n = {
                algparam: null
            };
            if ("30" != t.substr(0, 2))
                throw new Error("malformed plain PKCS8 private key(code:001)");
            var s = r(t, 0);
            if (s.length < 3)
                throw new Error("malformed plain PKCS8 private key(code:002)");
            if ("30" != t.substr(s[1], 2))
                throw new Error("malformed PKCS8 private key(code:003)");
            var a = r(t, s[1]);
            if (2 != a.length)
                throw new Error("malformed PKCS8 private key(code:004)");
            if ("06" != t.substr(a[0], 2))
                throw new Error("malformed PKCS8 private key(code:005)");
            if (n.algoid = i(t, a[0]),
            "06" == t.substr(a[1], 2) && (n.algparam = i(t, a[1])),
            "04" != t.substr(s[2], 2))
                throw new Error("malformed PKCS8 private key(code:006)");
            return n.keyidx = e.getVidx(t, s[2]),
                n
        },
        getKeyFromPlainPrivatePKCS8PEM: function (t) {
            var e = Ft(t, "PRIVATE KEY");
            return this.getKeyFromPlainPrivatePKCS8Hex(e)
        },
        getKeyFromPlainPrivatePKCS8Hex: function (t) {
            var e, r = this.parsePlainPrivatePKCS8Hex(t);
            if ("2a864886f70d010101" == r.algoid)
                e = new tt;
            else if ("2a8648ce380401" == r.algoid)
                e = new at.crypto.DSA;
            else {
                if ("2a8648ce3d0201" != r.algoid)
                    throw new Error("unsupported private key algorithm");
                e = new at.crypto.ECDSA
            }
            return e.readPKCS8PrvKeyHex(t),
                e
        },
        _getKeyFromPublicPKCS8Hex: function (t) {
            var e, r = ut.getVbyList(t, 0, [0, 0], "06");
            if ("2a864886f70d010101" === r)
                e = new tt;
            else if ("2a8648ce380401" === r)
                e = new at.crypto.DSA;
            else {
                if ("2a8648ce3d0201" !== r)
                    throw new Error("unsupported PKCS#8 public key hex");
                e = new at.crypto.ECDSA
            }
            return e.readPKCS8PubKeyHex(t),
                e
        },
        parsePublicRawRSAKeyHex: function (t) {
            var e = ut
                , r = e.getChildIdx
                , i = e.getV
                , n = {};
            if ("30" != t.substr(0, 2))
                throw new Error("malformed RSA key(code:001)");
            var s = r(t, 0);
            if (2 != s.length)
                throw new Error("malformed RSA key(code:002)");
            if ("02" != t.substr(s[0], 2))
                throw new Error("malformed RSA key(code:003)");
            if (n.n = i(t, s[0]),
            "02" != t.substr(s[1], 2))
                throw new Error("malformed RSA key(code:004)");
            return n.e = i(t, s[1]),
                n
        },
        parsePublicPKCS8Hex: function (t) {
            var e = ut
                , r = e.getChildIdx
                , i = e.getV
                , n = {
                algparam: null
            }
                , s = r(t, 0);
            if (2 != s.length)
                throw new Error("outer DERSequence shall have 2 elements: " + s.length);
            var a = s[0];
            if ("30" != t.substr(a, 2))
                throw new Error("malformed PKCS8 public key(code:001)");
            var o = r(t, a);
            if (2 != o.length)
                throw new Error("malformed PKCS8 public key(code:002)");
            if ("06" != t.substr(o[0], 2))
                throw new Error("malformed PKCS8 public key(code:003)");
            if (n.algoid = i(t, o[0]),
                "06" == t.substr(o[1], 2) ? n.algparam = i(t, o[1]) : "30" == t.substr(o[1], 2) && (n.algparam = {},
                    n.algparam.p = e.getVbyList(t, o[1], [0], "02"),
                    n.algparam.q = e.getVbyList(t, o[1], [1], "02"),
                    n.algparam.g = e.getVbyList(t, o[1], [2], "02")),
            "03" != t.substr(s[1], 2))
                throw new Error("malformed PKCS8 public key(code:004)");
            return n.key = i(t, s[1]).substr(2),
                n
        }
    }
}();

function Ut(t, e) {
    for (var r = "", i = e / 4 - t.length, n = 0; n < i; n++)
        r += "0";
    return r + t
}

function Gt(t, e, r) {
    for (var i = "", n = 0; i.length < e;)
        i += xt(r(St(t + String.fromCharCode.apply(String, [(4278190080 & n) >> 24, (16711680 & n) >> 16, (65280 & n) >> 8, 255 & n])))),
            n += 1;
    return i
}

function zt(t) {
    for (var e in at.crypto.Util.DIGESTINFOHEAD) {
        var r = at.crypto.Util.DIGESTINFOHEAD[e]
            , i = r.length;
        if (t.substring(0, i) == r)
            return [e, t.substring(i)]
    }
    return []
}

function Wt(t) {
    var e = ut
        , r = e.getChildIdx
        , i = e.getV;
    e.dump;
    var n, s = e.parse, a = e.getTLV, o = e.getVbyList, h = e.getVbyListEx, u = e.getTLVbyList, c = e.getTLVbyListEx,
        l = e.getIdxbyList, f = e.getIdxbyListEx, g = e.getVidx, p = e.getInt, d = e.oidname, v = e.hextooidstr, m = Ft,
        y = Error;
    try {
        n = at.asn1.x509.AlgorithmIdentifier.PSSNAME2ASN1TLV
    } catch (z) {
    }
    this.HEX2STAG = {
        "0c": "utf8",
        13: "prn",
        16: "ia5",
        "1a": "vis",
        "1e": "bmp"
    },
        this.hex = null,
        this.version = 0,
        this.foffset = 0,
        this.aExtInfo = null,
        this.getVersion = function () {
            if (null === this.hex || 0 !== this.version)
                return this.version;
            var t = u(this.hex, 0, [0, 0]);
            if ("a0" == t.substr(0, 2)) {
                var e = u(t, 0, [0])
                    , r = p(e, 0);
                if (r < 0 || 2 < r)
                    throw new Error("malformed version field");
                return this.version = r + 1,
                    this.version
            }
            return this.version = 1,
                this.foffset = -1,
                1
        }
        ,
        this.getSerialNumberHex = function () {
            return h(this.hex, 0, [0, 0], "02")
        }
        ,
        this.getSignatureAlgorithmField = function () {
            var t = c(this.hex, 0, [0, 1]);
            return this.getAlgorithmIdentifierName(t)
        }
        ,
        this.getAlgorithmIdentifierName = function (t) {
            for (var e in n)
                if (t === n[e])
                    return e;
            return d(h(t, 0, [0], "06"))
        }
        ,
        this.getIssuer = function (t, e) {
            return this.getX500Name(this.getIssuerHex(), t, e)
        }
        ,
        this.getIssuerHex = function () {
            return u(this.hex, 0, [0, 3 + this.foffset], "30")
        }
        ,
        this.getIssuerString = function () {
            return this.getIssuer().str
        }
        ,
        this.getSubject = function (t, e) {
            return this.getX500Name(this.getSubjectHex(), t, e)
        }
        ,
        this.getSubjectHex = function () {
            return u(this.hex, 0, [0, 5 + this.foffset], "30")
        }
        ,
        this.getSubjectString = function () {
            return this.getSubject().str
        }
        ,
        this.getNotBefore = function () {
            var t = o(this.hex, 0, [0, 4 + this.foffset, 0]);
            return t = t.replace(/(..)/g, "%$1"),
                t = decodeURIComponent(t)
        }
        ,
        this.getNotAfter = function () {
            var t = o(this.hex, 0, [0, 4 + this.foffset, 1]);
            return t = t.replace(/(..)/g, "%$1"),
                t = decodeURIComponent(t)
        }
        ,
        this.getPublicKeyHex = function () {
            return this.getSPKI()
        }
        ,
        this.getSPKI = function () {
            return u(this.hex, 0, [0, 6 + this.foffset], "30")
        }
        ,
        this.getSPKIValue = function () {
            var t = this.getSPKI();
            return null == t ? null : o(t, 0, [1], "03", !0)
        }
        ,
        this.getPublicKeyIdx = function () {
            return l(this.hex, 0, [0, 6 + this.foffset], "30")
        }
        ,
        this.getPublicKeyContentIdx = function () {
            var t = this.getPublicKeyIdx();
            return l(this.hex, t, [1, 0], "30")
        }
        ,
        this.getPublicKey = function () {
            return _t.getKey(this.getPublicKeyHex(), null, "pkcs8pub")
        }
        ,
        this.getSignatureAlgorithmName = function () {
            var t = u(this.hex, 0, [1], "30");
            return this.getAlgorithmIdentifierName(t)
        }
        ,
        this.getSignatureValueHex = function () {
            return o(this.hex, 0, [2], "03", !0)
        }
        ,
        this.verifySignature = function (t) {
            var e = this.getSignatureAlgorithmField()
                , r = this.getSignatureValueHex()
                , i = u(this.hex, 0, [0], "30")
                , n = new at.crypto.Signature({
                alg: e
            });
            return n.init(t),
                n.updateHex(i),
                n.verify(r)
        }
        ,
        this.parseExt = function (t) {
            var n, s, a;
            if (void 0 === t) {
                if (a = this.hex,
                3 !== this.version)
                    return -1;
                n = l(a, 0, [0, 7, 0], "30"),
                    s = r(a, n)
            } else {
                a = Ft(t);
                var h = l(a, 0, [0, 3, 0, 0], "06");
                if ("2a864886f70d01090e" != i(a, h))
                    return void (this.aExtInfo = new Array);
                n = l(a, 0, [0, 3, 0, 1, 0], "30"),
                    s = r(a, n),
                    this.hex = a
            }
            this.aExtInfo = new Array;
            for (var u = 0; u < s.length; u++) {
                var c = {
                    critical: !1
                }
                    , f = 0;
                3 === r(a, s[u]).length && (c.critical = !0,
                    f = 1),
                    c.oid = e.hextooidstr(o(a, s[u], [0], "06"));
                var p = l(a, s[u], [1 + f]);
                c.vidx = g(a, p),
                    this.aExtInfo.push(c)
            }
        }
        ,
        this.getExtInfo = function (t) {
            var e = this.aExtInfo
                , r = t;
            if (t.match(/^[0-9.]+$/) || (r = at.asn1.x509.OID.name2oid(t)),
            "" !== r)
                for (var i = 0; i < e.length; i++)
                    if (e[i].oid === r)
                        return e[i]
        }
        ,
        this.getCriticalExtV = function (t, e, r) {
            if (null != e)
                return [e, r];
            var i = this.getExtInfo(t);
            return null == i ? [null, null] : [a(this.hex, i.vidx), i.critical]
        }
        ,
        this.getExtBasicConstraints = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var r = this.getExtInfo("basicConstraints");
                if (void 0 === r)
                    return;
                t = a(this.hex, r.vidx),
                    e = r.critical
            }
            var n = {
                extname: "basicConstraints"
            };
            if (e && (n.critical = !0),
            "3000" === t)
                return n;
            if ("30030101ff" === t)
                return n.cA = !0,
                    n;
            if ("30060101ff02" === t.substr(0, 12)) {
                var s = i(t, 10)
                    , o = parseInt(s, 16);
                return n.cA = !0,
                    n.pathLen = o,
                    n
            }
            throw new Error("hExtV parse error: " + t)
        }
        ,
        this.getExtNameConstraints = function (t, e) {
            var i = this.getCriticalExtV("nameConstraints", t, e);
            if (t = i[0],
                e = i[1],
            null != t) {
                var n = {
                    extname: "nameConstraints"
                };
                e && (n.critical = !0);
                for (var s = r(t, 0), o = 0; o < s.length; o++) {
                    for (var h = [], u = r(t, s[o]), c = 0; c < u.length; c++) {
                        var l = a(t, u[c])
                            , f = this.getGeneralSubtree(l);
                        h.push(f)
                    }
                    var g = t.substr(s[o], 2);
                    "a0" == g ? n.permit = h : "a1" == g && (n.exclude = h)
                }
                return n
            }
        }
        ,
        this.getGeneralSubtree = function (t) {
            var e = r(t, 0)
                , n = e.length;
            if (n < 1 || 2 < n)
                throw new Error("wrong num elements");
            for (var s = this.getGeneralName(a(t, e[0])), o = 1; o < n; o++) {
                var h = t.substr(e[o], 2)
                    , u = i(t, e[o])
                    , c = parseInt(u, 16);
                "80" == h && (s.min = c),
                "81" == h && (s.max = c)
            }
            return s
        }
        ,
        this.getExtKeyUsage = function (t, e) {
            var r = this.getCriticalExtV("keyUsage", t, e);
            if (t = r[0],
                e = r[1],
            null != t) {
                var i = {
                    extname: "keyUsage"
                };
                return e && (i.critical = !0),
                    i.names = this.getExtKeyUsageString(t).split(","),
                    i
            }
        }
        ,
        this.getExtKeyUsageBin = function (t) {
            if (void 0 === t) {
                var e = this.getExtInfo("keyUsage");
                if (void 0 === e)
                    return "";
                t = a(this.hex, e.vidx)
            }
            if (8 != t.length && 10 != t.length)
                throw new Error("malformed key usage value: " + t);
            var r = "000000000000000" + parseInt(t.substr(6), 16).toString(2);
            return 8 == t.length && (r = r.slice(-8)),
            10 == t.length && (r = r.slice(-16)),
            "" == (r = r.replace(/0+$/, "")) && (r = "0"),
                r
        }
        ,
        this.getExtKeyUsageString = function (t) {
            for (var e = this.getExtKeyUsageBin(t), r = new Array, i = 0; i < e.length; i++)
                "1" == e.substr(i, 1) && r.push(Wt.KEYUSAGE_NAME[i]);
            return r.join(",")
        }
        ,
        this.getExtSubjectKeyIdentifier = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var r = this.getExtInfo("subjectKeyIdentifier");
                if (void 0 === r)
                    return;
                t = a(this.hex, r.vidx),
                    e = r.critical
            }
            var n = {
                extname: "subjectKeyIdentifier"
            };
            e && (n.critical = !0);
            var s = i(t, 0);
            return n.kid = {
                hex: s
            },
                n
        }
        ,
        this.getExtAuthorityKeyIdentifier = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var n = this.getExtInfo("authorityKeyIdentifier");
                if (void 0 === n)
                    return;
                t = a(this.hex, n.vidx),
                    e = n.critical
            }
            var s = {
                extname: "authorityKeyIdentifier"
            };
            e && (s.critical = !0);
            for (var o = r(t, 0), h = 0; h < o.length; h++) {
                var u = t.substr(o[h], 2);
                if ("80" === u && (s.kid = {
                    hex: i(t, o[h])
                }),
                "a1" === u) {
                    var c = a(t, o[h])
                        , l = this.getGeneralNames(c);
                    s.issuer = l[0].dn
                }
                "82" === u && (s.sn = {
                    hex: i(t, o[h])
                })
            }
            return s
        }
        ,
        this.getExtExtKeyUsage = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var n = this.getExtInfo("extKeyUsage");
                if (void 0 === n)
                    return;
                t = a(this.hex, n.vidx),
                    e = n.critical
            }
            var s = {
                extname: "extKeyUsage",
                array: []
            };
            e && (s.critical = !0);
            for (var o = r(t, 0), h = 0; h < o.length; h++)
                s.array.push(d(i(t, o[h])));
            return s
        }
        ,
        this.getExtExtKeyUsageName = function () {
            var t = this.getExtInfo("extKeyUsage");
            if (void 0 === t)
                return t;
            var e = new Array
                , n = a(this.hex, t.vidx);
            if ("" === n)
                return e;
            for (var s = r(n, 0), o = 0; o < s.length; o++)
                e.push(d(i(n, s[o])));
            return e
        }
        ,
        this.getExtSubjectAltName = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var r = this.getExtInfo("subjectAltName");
                if (void 0 === r)
                    return;
                t = a(this.hex, r.vidx),
                    e = r.critical
            }
            var i = {
                extname: "subjectAltName",
                array: []
            };
            return e && (i.critical = !0),
                i.array = this.getGeneralNames(t),
                i
        }
        ,
        this.getExtIssuerAltName = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var r = this.getExtInfo("issuerAltName");
                if (void 0 === r)
                    return;
                t = a(this.hex, r.vidx),
                    e = r.critical
            }
            var i = {
                extname: "issuerAltName",
                array: []
            };
            return e && (i.critical = !0),
                i.array = this.getGeneralNames(t),
                i
        }
        ,
        this.getGeneralNames = function (t) {
            for (var e = r(t, 0), i = [], n = 0; n < e.length; n++) {
                var s = this.getGeneralName(a(t, e[n]));
                void 0 !== s && i.push(s)
            }
            return i
        }
        ,
        this.getGeneralName = function (t) {
            var e = t.substr(0, 2)
                , r = i(t, 0)
                , n = xt(r);
            return "81" == e ? {
                rfc822: n
            } : "82" == e ? {
                dns: n
            } : "86" == e ? {
                uri: n
            } : "87" == e ? {
                ip: Ct(r)
            } : "a4" == e ? {
                dn: this.getX500Name(r)
            } : "a0" == e ? {
                other: this.getOtherName(t)
            } : void 0
        }
        ,
        this.getExtSubjectAltName2 = function () {
            var t, e, n, s = this.getExtInfo("subjectAltName");
            if (void 0 === s)
                return s;
            for (var o = new Array, h = a(this.hex, s.vidx), u = r(h, 0), c = 0; c < u.length; c++)
                n = h.substr(u[c], 2),
                    t = i(h, u[c]),
                "81" === n && (e = mt(t),
                    o.push(["MAIL", e])),
                "82" === n && (e = mt(t),
                    o.push(["DNS", e])),
                "84" === n && (e = Wt.hex2dn(t, 0),
                    o.push(["DN", e])),
                "86" === n && (e = mt(t),
                    o.push(["URI", e])),
                "87" === n && (e = Ct(t),
                    o.push(["IP", e]));
            return o
        }
        ,
        this.getExtCRLDistributionPoints = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var i = this.getExtInfo("cRLDistributionPoints");
                if (void 0 === i)
                    return;
                t = a(this.hex, i.vidx),
                    e = i.critical
            }
            var n = {
                extname: "cRLDistributionPoints",
                array: []
            };
            e && (n.critical = !0);
            for (var s = r(t, 0), o = 0; o < s.length; o++) {
                var h = a(t, s[o]);
                n.array.push(this.getDistributionPoint(h))
            }
            return n
        }
        ,
        this.getDistributionPoint = function (t) {
            for (var e = {}, i = r(t, 0), n = 0; n < i.length; n++) {
                var s = t.substr(i[n], 2)
                    , o = a(t, i[n]);
                "a0" == s && (e.dpname = this.getDistributionPointName(o))
            }
            return e
        }
        ,
        this.getDistributionPointName = function (t) {
            for (var e = {}, i = r(t, 0), n = 0; n < i.length; n++) {
                var s = t.substr(i[n], 2)
                    , o = a(t, i[n]);
                "a0" == s && (e.full = this.getGeneralNames(o))
            }
            return e
        }
        ,
        this.getExtCRLDistributionPointsURI = function () {
            var t = this.getExtCRLDistributionPoints();
            if (null == t)
                return t;
            for (var e = t.array, r = [], i = 0; i < e.length; i++)
                try {
                    null != e[i].dpname.full[0].uri && r.push(e[i].dpname.full[0].uri)
                } catch (n) {
                }
            return r
        }
        ,
        this.getExtAIAInfo = function () {
            var t = this.getExtInfo("authorityInfoAccess");
            if (void 0 === t)
                return t;
            for (var e = {
                ocsp: [],
                caissuer: []
            }, i = r(this.hex, t.vidx), n = 0; n < i.length; n++) {
                var s = o(this.hex, i[n], [0], "06")
                    , a = o(this.hex, i[n], [1], "86");
                "2b06010505073001" === s && e.ocsp.push(mt(a)),
                "2b06010505073002" === s && e.caissuer.push(mt(a))
            }
            return e
        }
        ,
        this.getExtAuthorityInfoAccess = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var i = this.getExtInfo("authorityInfoAccess");
                if (void 0 === i)
                    return;
                t = a(this.hex, i.vidx),
                    e = i.critical
            }
            var n = {
                extname: "authorityInfoAccess",
                array: []
            };
            e && (n.critical = !0);
            for (var s = r(t, 0), u = 0; u < s.length; u++) {
                var c = h(t, s[u], [0], "06")
                    , l = mt(o(t, s[u], [1], "86"));
                if ("2b06010505073001" == c)
                    n.array.push({
                        ocsp: l
                    });
                else {
                    if ("2b06010505073002" != c)
                        throw new Error("unknown method: " + c);
                    n.array.push({
                        caissuer: l
                    })
                }
            }
            return n
        }
        ,
        this.getExtCertificatePolicies = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var i = this.getExtInfo("certificatePolicies");
                if (void 0 === i)
                    return;
                t = a(this.hex, i.vidx),
                    e = i.critical
            }
            var n = {
                extname: "certificatePolicies",
                array: []
            };
            e && (n.critical = !0);
            for (var s = r(t, 0), o = 0; o < s.length; o++) {
                var h = a(t, s[o])
                    , u = this.getPolicyInformation(h);
                n.array.push(u)
            }
            return n
        }
        ,
        this.getPolicyInformation = function (t) {
            var e = {}
                , i = o(t, 0, [0], "06");
            e.policyoid = d(i);
            var n = f(t, 0, [1], "30");
            if (-1 != n) {
                e.array = [];
                for (var s = r(t, n), h = 0; h < s.length; h++) {
                    var u = a(t, s[h])
                        , c = this.getPolicyQualifierInfo(u);
                    e.array.push(c)
                }
            }
            return e
        }
        ,
        this.getOtherName = function (t) {
            var e = {}
                , i = r(t, 0)
                , n = o(t, i[0], [], "06")
                , a = o(t, i[1], []);
            return e.oid = d(n),
                e.value = s(a),
                e
        }
        ,
        this.getPolicyQualifierInfo = function (t) {
            var e = {}
                , r = o(t, 0, [0], "06");
            if ("2b06010505070201" === r) {
                var i = h(t, 0, [1], "16");
                e.cps = xt(i)
            } else if ("2b06010505070202" === r) {
                var n = u(t, 0, [1], "30");
                e.unotice = this.getUserNotice(n)
            }
            return e
        }
        ,
        this.getUserNotice = function (t) {
            var r = null;
            try {
                return r = e.parse(t),
                    this._asn1ToUnotice(r)
            } catch (i) {
                return
            }
        }
        ,
        this._asn1ToUnotice = function (t) {
            try {
                for (var e = {}, r = qt(t, "seq"), i = 0; i < r.length; i++) {
                    var n = this._asn1ToNoticeRef(r[i]);
                    null != n && (e.noticeref = n);
                    var s = this.asn1ToDisplayText(r[i]);
                    null != s && (e.exptext = s)
                }
                return Object.keys(e).length > 0 ? e : void 0
            } catch (a) {
                return
            }
        }
        ,
        this._asn1ToNoticeRef = function (t) {
            try {
                for (var e = {}, r = qt(t, "seq"), i = 0; i < r.length; i++) {
                    var n = this._asn1ToNoticeNum(r[i]);
                    null != n && (e.noticenum = n);
                    var s = this.asn1ToDisplayText(r[i]);
                    null != s && (e.org = s)
                }
                return Object.keys(e).length > 0 ? e : void 0
            } catch (a) {
                return
            }
        }
        ,
        this._asn1ToNoticeNum = function (t) {
            try {
                for (var e = qt(t, "seq"), r = [], i = 0; i < e.length; i++) {
                    var n = e[i];
                    r.push(parseInt(qt(n, "int.hex"), 16))
                }
                return r
            } catch (s) {
                return
            }
        }
        ,
        this.getDisplayText = function (t) {
            var e = {};
            return e.type = {
                "0c": "utf8",
                16: "ia5",
                "1a": "vis",
                "1e": "bmp"
            }[t.substr(0, 2)],
                e.str = xt(i(t, 0)),
                e
        }
        ,
        this.asn1ToDisplayText = function (t) {
            return null != t.utf8str ? {
                type: "utf8",
                str: t.utf8str.str
            } : null != t.ia5str ? {
                type: "ia5",
                str: t.ia5str.str
            } : null != t.visstr ? {
                type: "vis",
                str: t.visstr.str
            } : null != t.bmpstr ? {
                type: "bmp",
                str: t.bmpstr.str
            } : null != t.prnstr ? {
                type: "prn",
                str: t.prnstr.str
            } : void 0
        }
        ,
        this.getExtPolicyMappings = function (t, e) {
            var r = this.getCriticalExtV("policyMappings", t, e);
            if (t = r[0],
                e = r[1],
            null != t) {
                var i = {
                    extname: "policyMappings"
                };
                e && (i.critical = !0);
                try {
                    for (var n = s(t).seq, a = [], o = 0; o < n.length; o++) {
                        var h = n[o].seq;
                        a.push([h[0].oid, h[1].oid])
                    }
                    i.array = a
                } catch (u) {
                    throw new y("malformed policyMappings")
                }
                return i
            }
        }
        ,
        this.getExtPolicyConstraints = function (t, e) {
            var r = this.getCriticalExtV("policyConstraints", t, e);
            if (t = r[0],
                e = r[1],
            null != t) {
                var i = {
                    extname: "policyConstraints"
                };
                e && (i.critical = !0);
                var n = s(t);
                try {
                    for (var a = n.seq, o = 0; o < a.length; o++) {
                        var h = a[o].tag;
                        0 == h.explicit && ("80" == h.tag && (i.reqexp = parseInt(h.hex, 16)),
                        "81" == h.tag && (i.inhibit = parseInt(h.hex, 16)))
                    }
                } catch (u) {
                    return new y("malformed policyConstraints value")
                }
                return i
            }
        }
        ,
        this.getExtInhibitAnyPolicy = function (t, e) {
            var r = this.getCriticalExtV("inhibitAnyPolicy", t, e);
            if (t = r[0],
                e = r[1],
            null != t) {
                var i = {
                    extname: "inhibitAnyPolicy"
                };
                e && (i.critical = !0);
                var n = p(t, 0);
                return -1 == n ? new y("wrong value") : (i.skip = n,
                    i)
            }
        }
        ,
        this.getExtCRLNumber = function (t, e) {
            var r = {
                extname: "cRLNumber"
            };
            if (e && (r.critical = !0),
            "02" == t.substr(0, 2))
                return r.num = {
                    hex: i(t, 0)
                },
                    r;
            throw new y("hExtV parse error: " + t)
        }
        ,
        this.getExtCRLReason = function (t, e) {
            var r = {
                extname: "cRLReason"
            };
            if (e && (r.critical = !0),
            "0a" == t.substr(0, 2))
                return r.code = parseInt(i(t, 0), 16),
                    r;
            throw new Error("hExtV parse error: " + t)
        }
        ,
        this.getExtOcspNonce = function (t, e) {
            var r = {
                extname: "ocspNonce"
            };
            e && (r.critical = !0);
            var n = i(t, 0);
            return r.hex = n,
                r
        }
        ,
        this.getExtOcspNoCheck = function (t, e) {
            var r = {
                extname: "ocspNoCheck"
            };
            return e && (r.critical = !0),
                r
        }
        ,
        this.getExtAdobeTimeStamp = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var i = this.getExtInfo("adobeTimeStamp");
                if (void 0 === i)
                    return;
                t = a(this.hex, i.vidx),
                    e = i.critical
            }
            var n = {
                extname: "adobeTimeStamp"
            };
            e && (n.critical = !0);
            var s = r(t, 0);
            if (s.length > 1) {
                var o = a(t, s[1])
                    , h = this.getGeneralName(o);
                null != h.uri && (n.uri = h.uri)
            }
            if (s.length > 2) {
                var u = a(t, s[2]);
                "0101ff" == u && (n.reqauth = !0),
                "010100" == u && (n.reqauth = !1)
            }
            return n
        }
        ,
        this.getExtSubjectDirectoryAttributes = function (t, e) {
            if (void 0 === t && void 0 === e) {
                var r = this.getExtInfo("subjectDirectoryAttributes");
                if (void 0 === r)
                    return;
                t = a(this.hex, r.vidx),
                    e = r.critical
            }
            var i = {
                extname: "subjectDirectoryAttributes"
            };
            e && (i.critical = !0);
            try {
                for (var n = s(t), o = [], h = 0; h < n.seq.length; h++) {
                    var u = n.seq[h]
                        , c = qt(u, "seq.0.oid")
                        , l = qt(u, "seq.1.set");
                    if (null == c || null == l)
                        throw "error";
                    o.push({
                        attr: c,
                        array: l
                    })
                }
                return i.array = o,
                    i
            } catch (f) {
                throw new Error("malformed subjectDirectoryAttributes extension value")
            }
        }
    ;
    var x = function (t) {
        var e = {};
        try {
            var r = t.seq[0].oid
                , i = at.asn1.x509.OID.name2oid(r);
            e.type = at.asn1.x509.OID.oid2atype(i);
            var n = t.seq[1];
            if (null != n.utf8str)
                e.ds = "utf8",
                    e.value = n.utf8str.str;
            else if (null != n.numstr)
                e.ds = "num",
                    e.value = n.numstr.str;
            else if (null != n.telstr)
                e.ds = "tel",
                    e.value = n.telstr.str;
            else if (null != n.prnstr)
                e.ds = "prn",
                    e.value = n.prnstr.str;
            else if (null != n.ia5str)
                e.ds = "ia5",
                    e.value = n.ia5str.str;
            else if (null != n.visstr)
                e.ds = "vis",
                    e.value = n.visstr.str;
            else {
                if (null == n.bmpstr)
                    throw "error";
                e.ds = "bmp",
                    e.value = n.bmpstr.str
            }
            return e
        } catch (s) {
            throw new Erorr("improper ASN.1 parsed AttrTypeAndValue")
        }
    }
        , S = function (t) {
        try {
            return t.set.map((function (t) {
                    return x(t)
                }
            ))
        } catch (X) {
            throw new Error("improper ASN.1 parsed RDN: " + X)
        }
    };
    this.getX500NameRule = function (t) {
        for (var e = null, r = [], i = 0; i < t.length; i++)
            for (var n = t[i], s = 0; s < n.length; s++)
                r.push(n[s]);
        for (i = 0; i < r.length; i++) {
            var a = r[i]
                , o = a.ds
                , h = a.value
                , u = a.type;
            if ("prn" != o && "utf8" != o && "ia5" != o)
                return "mixed";
            if ("ia5" == o) {
                if ("CN" != u)
                    return "mixed";
                if (at.lang.String.isMail(h))
                    continue;
                return "mixed"
            }
            if ("C" == u) {
                if ("prn" == o)
                    continue;
                return "mixed"
            }
            if (null == e)
                e = o;
            else if (e !== o)
                return "mixed"
        }
        return null == e ? "prn" : e
    }
        ,
        this.getAttrTypeAndValue = function (t) {
            var e = s(t);
            return x(e)
        }
        ,
        this.getRDN = function (t) {
            var e = s(t);
            return S(e)
        }
        ,
        this.getX500NameArray = function (t) {
            return function (e) {
                try {
                    return e.seq.map((function (t) {
                            return S(t)
                        }
                    ))
                } catch (t) {
                    throw new Error("improper ASN.1 parsed X500Name: " + t)
                }
            }(s(t))
        }
        ,
        this.getX500Name = function (t, e, r) {
            var i = this.getX500NameArray(t)
                , n = {
                str: this.dnarraytostr(i)
            };
            return n.array = i,
            1 == r && (n.hex = t),
            1 == e && (n.canon = this.c14nRDNArray(i)),
                n
        }
        ,
        this.readCertPEM = function (t) {
            this.readCertHex(m(t))
        }
        ,
        this.readCertHex = function (t) {
            this.hex = t,
                this.getVersion();
            try {
                l(this.hex, 0, [0, 7], "a3"),
                    this.parseExt()
            } catch (e) {
            }
        }
        ,
        this.getParam = function (t) {
            var e = {};
            return null == t && (t = {}),
                e.version = this.getVersion(),
                e.serial = {
                    hex: this.getSerialNumberHex()
                },
                e.sigalg = this.getSignatureAlgorithmField(),
                e.issuer = this.getIssuer(t.dncanon, t.dnhex),
                e.notbefore = this.getNotBefore(),
                e.notafter = this.getNotAfter(),
                e.subject = this.getSubject(t.dncanon, t.dnhex),
                e.sbjpubkey = wt(this.getPublicKeyHex(), "PUBLIC KEY"),
            null != this.aExtInfo && this.aExtInfo.length > 0 && (e.ext = this.getExtParamArray()),
                e.sighex = this.getSignatureValueHex(),
            1 == t.tbshex && (e.tbshex = u(this.hex, 0, [0])),
            1 == t.nodnarray && (delete e.issuer.array,
                delete e.subject.array),
                e
        }
        ,
        this.getExtParamArray = function (t) {
            null == t && (-1 != f(this.hex, 0, [0, "[3]"]) && (t = c(this.hex, 0, [0, "[3]", 0], "30")));
            for (var e = [], i = r(t, 0), n = 0; n < i.length; n++) {
                var s = a(t, i[n])
                    , o = this.getExtParam(s);
                null != o && e.push(o)
            }
            return e
        }
        ,
        this.getExtParam = function (t) {
            var e = r(t, 0).length;
            if (2 != e && 3 != e)
                throw new Error("wrong number elements in Extension: " + e + " " + t);
            var i = v(o(t, 0, [0], "06"))
                , n = !1;
            3 == e && "0101ff" == u(t, 0, [1]) && (n = !0);
            var a = u(t, 0, [e - 1, 0])
                , h = void 0;
            if ("2.5.29.14" == i ? h = this.getExtSubjectKeyIdentifier(a, n) : "2.5.29.15" == i ? h = this.getExtKeyUsage(a, n) : "2.5.29.17" == i ? h = this.getExtSubjectAltName(a, n) : "2.5.29.18" == i ? h = this.getExtIssuerAltName(a, n) : "2.5.29.19" == i ? h = this.getExtBasicConstraints(a, n) : "2.5.29.30" == i ? h = this.getExtNameConstraints(a, n) : "2.5.29.31" == i ? h = this.getExtCRLDistributionPoints(a, n) : "2.5.29.32" == i ? h = this.getExtCertificatePolicies(a, n) : "2.5.29.33" == i ? h = this.getExtPolicyMappings(a, n) : "2.5.29.35" == i ? h = this.getExtAuthorityKeyIdentifier(a, n) : "2.5.29.36" == i ? h = this.getExtPolicyConstraints(a, n) : "2.5.29.37" == i ? h = this.getExtExtKeyUsage(a, n) : "2.5.29.54" == i ? h = this.getExtInhibitAnyPolicy(a, n) : "1.3.6.1.5.5.7.1.1" == i ? h = this.getExtAuthorityInfoAccess(a, n) : "2.5.29.20" == i ? h = this.getExtCRLNumber(a, n) : "2.5.29.21" == i ? h = this.getExtCRLReason(a, n) : "2.5.29.9" == i ? h = this.getExtSubjectDirectoryAttributes(a, n) : "1.3.6.1.5.5.7.48.1.2" == i ? h = this.getExtOcspNonce(a, n) : "1.3.6.1.5.5.7.48.1.5" == i ? h = this.getExtOcspNoCheck(a, n) : "1.2.840.113583.1.1.9.1" == i ? h = this.getExtAdobeTimeStamp(a, n) : null != Wt.EXT_PARSER[i] && (h = Wt.EXT_PARSER[i](i, n, a)),
            null != h)
                return h;
            var c = {
                extname: i,
                extn: a
            };
            try {
                c.extn = s(a)
            } catch (l) {
            }
            return n && (c.critical = !0),
                c
        }
        ,
        this.findExt = function (t, e) {
            for (var r = 0; r < t.length; r++)
                if (t[r].extname == e)
                    return t[r];
            return null
        }
        ,
        this.updateExtCDPFullURI = function (t, e) {
            var r = this.findExt(t, "cRLDistributionPoints");
            if (null != r && null != r.array)
                for (var i = r.array, n = 0; n < i.length; n++)
                    if (null != i[n].dpname && null != i[n].dpname.full)
                        for (var s = i[n].dpname.full, a = 0; a < s.length; a++) {
                            var o = s[n];
                            null != o.uri && (o.uri = e)
                        }
        }
        ,
        this.updateExtAIAOCSP = function (t, e) {
            var r = this.findExt(t, "authorityInfoAccess");
            if (null != r && null != r.array)
                for (var i = r.array, n = 0; n < i.length; n++)
                    null != i[n].ocsp && (i[n].ocsp = e)
        }
        ,
        this.updateExtAIACAIssuer = function (t, e) {
            var r = this.findExt(t, "authorityInfoAccess");
            if (null != r && null != r.array)
                for (var i = r.array, n = 0; n < i.length; n++)
                    null != i[n].caissuer && (i[n].caissuer = e)
        }
        ,
        this.dnarraytostr = function (t) {
            return "/" + t.map((function (t) {
                    return function (t) {
                        return t.map((function (t) {
                                return function (t) {
                                    return t.type + "=" + t.value
                                }(t).replace(/\+/, "\\+")
                            }
                        )).join("+")
                    }(t).replace(/\//, "\\/")
                }
            )).join("/")
        }
        ,
        this.setCanonicalizedDN = function (t) {
            var e;
            if (null != t.str && null == t.array) {
                var r = new at.asn1.x509.X500Name({
                    str: t.str
                }).tohex();
                e = this.getX500NameArray(r)
            } else
                e = t.array;
            null == t.canon && (t.canon = this.c14nRDNArray(e))
        }
        ,
        this.c14nRDNArray = function (t) {
            for (var e = [], r = 0; r < t.length; r++) {
                for (var i = t[r], n = [], s = 0; s < i.length; s++) {
                    var a = i[s]
                        , o = a.value;
                    o = (o = (o = (o = o.replace(/^\s*/, "")).replace(/\s*$/, "")).replace(/\s+/g, " ")).toLowerCase(),
                        n.push(a.type.toLowerCase() + "=" + o)
                }
                e.push(n.join("+"))
            }
            return "/" + e.join("/")
        }
        ,
        this.getInfo = function () {
            var t, e, r, i = function (t) {
                for (var e = "", r = "    ", i = "\n", n = t.array, s = 0; s < n.length; s++) {
                    var a = n[s];
                    if (null != a.dn && (e += r + "dn: " + a.dn.str + i),
                    null != a.ip && (e += r + "ip: " + a.ip + i),
                    null != a.rfc822 && (e += r + "rfc822: " + a.rfc822 + i),
                    null != a.dns && (e += r + "dns: " + a.dns + i),
                    null != a.uri && (e += r + "uri: " + a.uri + i),
                    null != a.other)
                        e += r + "other: " + a.other.oid + "=" + JSON.stringify(a.other.value).replace(/\"/g, "") + i
                }
                return e = e.replace(/\n$/, "")
            }, n = function (t) {
                for (var e = "", r = t.array, i = 0; i < r.length; i++) {
                    var n = r[i];
                    if (e += "    policy oid: " + n.policyoid + "\n",
                    void 0 !== n.array)
                        for (var s = 0; s < n.array.length; s++) {
                            var a = n.array[s];
                            void 0 !== a.cps && (e += "    cps: " + a.cps + "\n")
                        }
                }
                return e
            }, s = function (t) {
                for (var e = "", r = t.array, i = 0; i < r.length; i++) {
                    var n = r[i];
                    try {
                        void 0 !== n.dpname.full[0].uri && (e += "    " + n.dpname.full[0].uri + "\n")
                    } catch (s) {
                    }
                    try {
                        void 0 !== n.dname.full[0].dn.hex && (e += "    " + Wt.hex2dn(n.dpname.full[0].dn.hex) + "\n")
                    } catch (s) {
                    }
                }
                return e
            }, a = function (t) {
                for (var e = "", r = t.array, i = 0; i < r.length; i++) {
                    var n = r[i];
                    void 0 !== n.caissuer && (e += "    caissuer: " + n.caissuer + "\n"),
                    void 0 !== n.ocsp && (e += "    ocsp: " + n.ocsp + "\n")
                }
                return e
            };
            if (t = "Basic Fields\n",
                t += "  serial number: " + this.getSerialNumberHex() + "\n",
                t += "  signature algorithm: " + this.getSignatureAlgorithmField() + "\n",
                t += "  issuer: " + this.getIssuerString() + "\n",
                t += "  notBefore: " + this.getNotBefore() + "\n",
                t += "  notAfter: " + this.getNotAfter() + "\n",
                t += "  subject: " + this.getSubjectString() + "\n",
                t += "  subject public key info: \n",
                t += "    key algorithm: " + (e = this.getPublicKey()).type + "\n",
            "RSA" === e.type && (t += "    n=" + Vt(e.n.toString(16)).substr(0, 16) + "...\n",
                t += "    e=" + Vt(e.e.toString(16)) + "\n"),
            null != (r = this.aExtInfo)) {
                t += "X509v3 Extensions:\n";
                for (var o = 0; o < r.length; o++) {
                    var h = r[o]
                        , u = at.asn1.x509.OID.oid2name(h.oid);
                    "" === u && (u = h.oid);
                    var c = "";
                    if (!0 === h.critical && (c = "CRITICAL"),
                        t += "  " + u + " " + c + ":\n",
                    "basicConstraints" === u) {
                        var l = this.getExtBasicConstraints();
                        void 0 === l.cA ? t += "    {}\n" : (t += "    cA=true",
                        void 0 !== l.pathLen && (t += ", pathLen=" + l.pathLen),
                            t += "\n")
                    } else {
                        var f;
                        if ("policyMappings" == u)
                            t += "    " + this.getExtPolicyMappings().array.map((function (t) {
                                    var e = t;
                                    return e[0] + ":" + e[1]
                                }
                            )).join(", ") + "\n";
                        else if ("policyConstraints" == u)
                            t += "    ",
                            null != (f = this.getExtPolicyConstraints()).reqexp && (t += " reqexp=" + f.reqexp),
                            null != f.inhibit && (t += " inhibit=" + f.inhibit),
                                t += "\n";
                        else if ("inhibitAnyPolicy" == u)
                            t += "    skip=" + (f = this.getExtInhibitAnyPolicy()).skip + "\n";
                        else if ("keyUsage" == u)
                            t += "    " + this.getExtKeyUsageString() + "\n";
                        else if ("subjectKeyIdentifier" == u)
                            t += "    " + this.getExtSubjectKeyIdentifier().kid.hex + "\n";
                        else if ("authorityKeyIdentifier" == u) {
                            var g = this.getExtAuthorityKeyIdentifier();
                            void 0 !== g.kid && (t += "    kid=" + g.kid.hex + "\n")
                        } else {
                            if ("extKeyUsage" == u)
                                t += "    " + this.getExtExtKeyUsage().array.join(", ") + "\n";
                            else if ("subjectAltName" == u)
                                t += i(this.getExtSubjectAltName()) + "\n";
                            else if ("cRLDistributionPoints" == u)
                                t += s(this.getExtCRLDistributionPoints());
                            else if ("authorityInfoAccess" == u)
                                t += a(this.getExtAuthorityInfoAccess());
                            else
                                "certificatePolicies" == u && (t += n(this.getExtCertificatePolicies()))
                        }
                    }
                }
            }
            return t += "signature algorithm: " + this.getSignatureAlgorithmName() + "\n",
                t += "signature: " + this.getSignatureValueHex().substr(0, 16) + "...\n"
        }
        ,
    "string" == typeof t && (-1 != t.indexOf("-----BEGIN") ? this.readCertPEM(t) : at.lang.String.isHex(t) && this.readCertHex(t))
}

_t.getKey = function (t, e, r) {
    var i = (v = ut).getChildIdx;
    v.getV;
    var n = v.getVbyList
        , s = at.crypto
        , a = s.ECDSA
        , o = s.DSA
        , h = tt
        , u = Ft
        , c = _t;
    if (void 0 !== h && t instanceof h)
        return t;
    if (void 0 !== a && t instanceof a)
        return t;
    if (void 0 !== o && t instanceof o)
        return t;
    if (void 0 !== t.curve && void 0 !== t.xy && void 0 === t.d)
        return new a({
            pub: t.xy,
            curve: t.curve
        });
    if (void 0 !== t.curve && void 0 !== t.d)
        return new a({
            prv: t.d,
            curve: t.curve
        });
    if (void 0 === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 === t.d)
        return (P = new h).setPublic(t.n, t.e),
            P;
    if (void 0 === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 !== t.d && void 0 !== t.p && void 0 !== t.q && void 0 !== t.dp && void 0 !== t.dq && void 0 !== t.co && void 0 === t.qi)
        return (P = new h).setPrivateEx(t.n, t.e, t.d, t.p, t.q, t.dp, t.dq, t.co),
            P;
    if (void 0 === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 !== t.d && void 0 === t.p)
        return (P = new h).setPrivate(t.n, t.e, t.d),
            P;
    if (void 0 !== t.p && void 0 !== t.q && void 0 !== t.g && void 0 !== t.y && void 0 === t.x)
        return (P = new o).setPublic(t.p, t.q, t.g, t.y),
            P;
    if (void 0 !== t.p && void 0 !== t.q && void 0 !== t.g && void 0 !== t.y && void 0 !== t.x)
        return (P = new o).setPrivate(t.p, t.q, t.g, t.y, t.x),
            P;
    if ("RSA" === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 === t.d)
        return (P = new h).setPublic(dt(t.n), dt(t.e)),
            P;
    if ("RSA" === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 !== t.d && void 0 !== t.p && void 0 !== t.q && void 0 !== t.dp && void 0 !== t.dq && void 0 !== t.qi)
        return (P = new h).setPrivateEx(dt(t.n), dt(t.e), dt(t.d), dt(t.p), dt(t.q), dt(t.dp), dt(t.dq), dt(t.qi)),
            P;
    if ("RSA" === t.kty && void 0 !== t.n && void 0 !== t.e && void 0 !== t.d)
        return (P = new h).setPrivate(dt(t.n), dt(t.e), dt(t.d)),
            P;
    if ("EC" === t.kty && void 0 !== t.crv && void 0 !== t.x && void 0 !== t.y && void 0 === t.d) {
        var l = (I = new a({
            curve: t.crv
        })).ecparams.keycharlen
            , f = "04" + ("0000000000" + dt(t.x)).slice(-l) + ("0000000000" + dt(t.y)).slice(-l);
        return I.setPublicKeyHex(f),
            I
    }
    if ("EC" === t.kty && void 0 !== t.crv && void 0 !== t.x && void 0 !== t.y && void 0 !== t.d) {
        l = (I = new a({
            curve: t.crv
        })).ecparams.keycharlen,
            f = "04" + ("0000000000" + dt(t.x)).slice(-l) + ("0000000000" + dt(t.y)).slice(-l);
        var g = ("0000000000" + dt(t.d)).slice(-l);
        return I.setPublicKeyHex(f),
            I.setPrivateKeyHex(g),
            I
    }
    if ("pkcs5prv" === r) {
        var p, d = t, v = ut;
        if (9 === (p = i(d, 0)).length)
            (P = new h).readPKCS5PrvKeyHex(d);
        else if (6 === p.length)
            (P = new o).readPKCS5PrvKeyHex(d);
        else {
            if (!(p.length > 2 && "04" === d.substr(p[1], 2)))
                throw new Error("unsupported PKCS#1/5 hexadecimal key");
            (P = new a).readPKCS5PrvKeyHex(d)
        }
        return P
    }
    if ("pkcs8prv" === r)
        return P = c.getKeyFromPlainPrivatePKCS8Hex(t);
    if ("pkcs8pub" === r)
        return c._getKeyFromPublicPKCS8Hex(t);
    if ("x509pub" === r)
        return Wt.getPublicKeyFromCertHex(t);
    if (-1 != t.indexOf("-END CERTIFICATE-", 0) || -1 != t.indexOf("-END X509 CERTIFICATE-", 0) || -1 != t.indexOf("-END TRUSTED CERTIFICATE-", 0))
        return Wt.getPublicKeyFromCertPEM(t);
    if (-1 != t.indexOf("-END PUBLIC KEY-")) {
        var y = Ft(t, "PUBLIC KEY");
        return c._getKeyFromPublicPKCS8Hex(y)
    }
    if (-1 != t.indexOf("-END RSA PRIVATE KEY-") && -1 == t.indexOf("4,ENCRYPTED")) {
        var x = u(t, "RSA PRIVATE KEY");
        return c.getKey(x, null, "pkcs5prv")
    }
    if (-1 != t.indexOf("-END DSA PRIVATE KEY-") && -1 == t.indexOf("4,ENCRYPTED")) {
        var S = n(T = u(t, "DSA PRIVATE KEY"), 0, [1], "02")
            , E = n(T, 0, [2], "02")
            , w = n(T, 0, [3], "02")
            , F = n(T, 0, [4], "02")
            , b = n(T, 0, [5], "02");
        return (P = new o).setPrivate(new m(S, 16), new m(E, 16), new m(w, 16), new m(F, 16), new m(b, 16)),
            P
    }
    if (-1 != t.indexOf("-END EC PRIVATE KEY-") && -1 == t.indexOf("4,ENCRYPTED")) {
        x = u(t, "EC PRIVATE KEY");
        return c.getKey(x, null, "pkcs5prv")
    }
    if (-1 != t.indexOf("-END PRIVATE KEY-"))
        return c.getKeyFromPlainPrivatePKCS8PEM(t);
    if (-1 != t.indexOf("-END RSA PRIVATE KEY-") && -1 != t.indexOf("4,ENCRYPTED")) {
        var A = c.getDecryptedKeyHex(t, e)
            , D = new tt;
        return D.readPKCS5PrvKeyHex(A),
            D
    }
    if (-1 != t.indexOf("-END EC PRIVATE KEY-") && -1 != t.indexOf("4,ENCRYPTED")) {
        var I, P = n(T = c.getDecryptedKeyHex(t, e), 0, [1], "04"), C = n(T, 0, [2, 0], "06"),
            R = n(T, 0, [3, 0], "03").substr(2);
        if (void 0 === at.crypto.OID.oidhex2name[C])
            throw new Error("undefined OID(hex) in KJUR.crypto.OID: " + C);
        return (I = new a({
            curve: at.crypto.OID.oidhex2name[C]
        })).setPublicKeyHex(R),
            I.setPrivateKeyHex(P),
            I.isPublic = !1,
            I
    }
    if (-1 != t.indexOf("-END DSA PRIVATE KEY-") && -1 != t.indexOf("4,ENCRYPTED")) {
        var T;
        S = n(T = c.getDecryptedKeyHex(t, e), 0, [1], "02"),
            E = n(T, 0, [2], "02"),
            w = n(T, 0, [3], "02"),
            F = n(T, 0, [4], "02"),
            b = n(T, 0, [5], "02");
        return (P = new o).setPrivate(new m(S, 16), new m(E, 16), new m(w, 16), new m(F, 16), new m(b, 16)),
            P
    }
    if (-1 != t.indexOf("-END ENCRYPTED PRIVATE KEY-"))
        return c.getKeyFromEncryptedPKCS8PEM(t, e);
    throw new Error("not supported argument")
}
    ,
    _t.generateKeypair = function (t, e) {
        if ("RSA" == t) {
            var r = e;
            (a = new tt).generate(r, "10001"),
                a.isPrivate = !0,
                a.isPublic = !0;
            var i = new tt
                , n = a.n.toString(16)
                , s = a.e.toString(16);
            return i.setPublic(n, s),
                i.isPrivate = !1,
                i.isPublic = !0,
                (o = {}).prvKeyObj = a,
                o.pubKeyObj = i,
                o
        }
        if ("EC" == t) {
            var a, o, h = e, u = new at.crypto.ECDSA({
                curve: h
            }).generateKeyPairHex();
            return (a = new at.crypto.ECDSA({
                curve: h
            })).setPublicKeyHex(u.ecpubhex),
                a.setPrivateKeyHex(u.ecprvhex),
                a.isPrivate = !0,
                a.isPublic = !1,
                (i = new at.crypto.ECDSA({
                    curve: h
                })).setPublicKeyHex(u.ecpubhex),
                i.isPrivate = !1,
                i.isPublic = !0,
                (o = {}).prvKeyObj = a,
                o.pubKeyObj = i,
                o
        }
        throw new Error("unknown algorithm: " + t)
    }
    ,
    _t.getPEM = function (t, e, r, i, n, s) {
        var a = at
            , o = a.asn1
            , h = o.DERObjectIdentifier
            , u = o.DERInteger
            , c = o.ASN1Util.newObject
            , f = o.x509.SubjectPublicKeyInfo
            , g = a.crypto
            , p = g.DSA
            , d = g.ECDSA
            , v = tt;

        function m(t) {
            return c({
                seq: [{
                    int: 0
                }, {
                    int: {
                        bigint: t.n
                    }
                }, {
                    int: t.e
                }, {
                    int: {
                        bigint: t.d
                    }
                }, {
                    int: {
                        bigint: t.p
                    }
                }, {
                    int: {
                        bigint: t.q
                    }
                }, {
                    int: {
                        bigint: t.dmp1
                    }
                }, {
                    int: {
                        bigint: t.dmq1
                    }
                }, {
                    int: {
                        bigint: t.coeff
                    }
                }]
            })
        }

        function y(t) {
            return c({
                seq: [{
                    int: 1
                }, {
                    octstr: {
                        hex: t.prvKeyHex
                    }
                }, {
                    tag: ["a0", !0, {
                        oid: {
                            name: t.curveName
                        }
                    }]
                }, {
                    tag: ["a1", !0, {
                        bitstr: {
                            hex: "00" + t.pubKeyHex
                        }
                    }]
                }]
            })
        }

        function x(t) {
            return c({
                seq: [{
                    int: 0
                }, {
                    int: {
                        bigint: t.p
                    }
                }, {
                    int: {
                        bigint: t.q
                    }
                }, {
                    int: {
                        bigint: t.g
                    }
                }, {
                    int: {
                        bigint: t.y
                    }
                }, {
                    int: {
                        bigint: t.x
                    }
                }]
            })
        }

        if ((void 0 !== v && t instanceof v || void 0 !== p && t instanceof p || void 0 !== d && t instanceof d) && 1 == t.isPublic && (void 0 === e || "PKCS8PUB" == e))
            return wt(F = new f(t).tohex(), "PUBLIC KEY");
        if ("PKCS1PRV" == e && void 0 !== v && t instanceof v && (void 0 === r || null == r) && 1 == t.isPrivate)
            return wt(F = m(t).tohex(), "RSA PRIVATE KEY");
        if ("PKCS1PRV" == e && void 0 !== d && t instanceof d && (void 0 === r || null == r) && 1 == t.isPrivate) {
            var S = new h({
                name: t.curveName
            }).tohex()
                , E = y(t).tohex()
                , w = "";
            return w += wt(S, "EC PARAMETERS"),
                w += wt(E, "EC PRIVATE KEY")
        }
        if ("PKCS1PRV" == e && void 0 !== p && t instanceof p && (void 0 === r || null == r) && 1 == t.isPrivate)
            return wt(F = x(t).tohex(), "DSA PRIVATE KEY");
        if ("PKCS5PRV" == e && void 0 !== v && t instanceof v && void 0 !== r && null != r && 1 == t.isPrivate) {
            var F = m(t).tohex();
            return void 0 === i && (i = "DES-EDE3-CBC"),
                this.getEncryptedPKCS5PEMFromPrvKeyHex("RSA", F, r, i, s)
        }
        if ("PKCS5PRV" == e && void 0 !== d && t instanceof d && void 0 !== r && null != r && 1 == t.isPrivate) {
            F = y(t).tohex();
            return void 0 === i && (i = "DES-EDE3-CBC"),
                this.getEncryptedPKCS5PEMFromPrvKeyHex("EC", F, r, i, s)
        }
        if ("PKCS5PRV" == e && void 0 !== p && t instanceof p && void 0 !== r && null != r && 1 == t.isPrivate) {
            F = x(t).tohex();
            return void 0 === i && (i = "DES-EDE3-CBC"),
                this.getEncryptedPKCS5PEMFromPrvKeyHex("DSA", F, r, i, s)
        }
        var b = function (t, e) {
            var r = A(t, e);
            return new c({
                seq: [{
                    seq: [{
                        oid: {
                            name: "pkcs5PBES2"
                        }
                    }, {
                        seq: [{
                            seq: [{
                                oid: {
                                    name: "pkcs5PBKDF2"
                                }
                            }, {
                                seq: [{
                                    octstr: {
                                        hex: r.pbkdf2Salt
                                    }
                                }, {
                                    int: r.pbkdf2Iter
                                }]
                            }]
                        }, {
                            seq: [{
                                oid: {
                                    name: "des-EDE3-CBC"
                                }
                            }, {
                                octstr: {
                                    hex: r.encryptionSchemeIV
                                }
                            }]
                        }]
                    }]
                }, {
                    octstr: {
                        hex: r.ciphertext
                    }
                }]
            }).tohex()
        }
            , A = function (t, e) {
            var r = l.lib.WordArray.random(8)
                , i = l.lib.WordArray.random(8)
                , n = l.PBKDF2(e, r, {
                keySize: 6,
                iterations: 100
            })
                , s = l.enc.Hex.parse(t)
                , a = l.TripleDES.encrypt(s, n, {
                iv: i
            }) + ""
                , o = {};
            return o.ciphertext = a,
                o.pbkdf2Salt = l.enc.Hex.stringify(r),
                o.pbkdf2Iter = 100,
                o.encryptionSchemeAlg = "DES-EDE3-CBC",
                o.encryptionSchemeIV = l.enc.Hex.stringify(i),
                o
        };
        if ("PKCS8PRV" == e && null != v && t instanceof v && 1 == t.isPrivate) {
            var D = m(t).tohex();
            F = c({
                seq: [{
                    int: 0
                }, {
                    seq: [{
                        oid: {
                            name: "rsaEncryption"
                        }
                    }, {
                        null: !0
                    }]
                }, {
                    octstr: {
                        hex: D
                    }
                }]
            }).tohex();
            return void 0 === r || null == r ? wt(F, "PRIVATE KEY") : wt(E = b(F, r), "ENCRYPTED PRIVATE KEY")
        }
        if ("PKCS8PRV" == e && void 0 !== d && t instanceof d && 1 == t.isPrivate) {
            var I = {
                seq: [{
                    int: 1
                }, {
                    octstr: {
                        hex: t.prvKeyHex
                    }
                }]
            };
            "string" == typeof t.pubKeyHex && I.seq.push({
                tag: ["a1", !0, {
                    bitstr: {
                        hex: "00" + t.pubKeyHex
                    }
                }]
            });
            D = new c(I).tohex(),
                F = c({
                    seq: [{
                        int: 0
                    }, {
                        seq: [{
                            oid: {
                                name: "ecPublicKey"
                            }
                        }, {
                            oid: {
                                name: t.curveName
                            }
                        }]
                    }, {
                        octstr: {
                            hex: D
                        }
                    }]
                }).tohex();
            return void 0 === r || null == r ? wt(F, "PRIVATE KEY") : wt(E = b(F, r), "ENCRYPTED PRIVATE KEY")
        }
        if ("PKCS8PRV" == e && void 0 !== p && t instanceof p && 1 == t.isPrivate) {
            D = new u({
                bigint: t.x
            }).tohex(),
                F = c({
                    seq: [{
                        int: 0
                    }, {
                        seq: [{
                            oid: {
                                name: "dsa"
                            }
                        }, {
                            seq: [{
                                int: {
                                    bigint: t.p
                                }
                            }, {
                                int: {
                                    bigint: t.q
                                }
                            }, {
                                int: {
                                    bigint: t.g
                                }
                            }]
                        }]
                    }, {
                        octstr: {
                            hex: D
                        }
                    }]
                }).tohex();
            return void 0 === r || null == r ? wt(F, "PRIVATE KEY") : wt(E = b(F, r), "ENCRYPTED PRIVATE KEY")
        }
        throw new Error("unsupported object nor format")
    }
    ,
    _t.getKeyFromCSRPEM = function (t) {
        var e = Ft(t, "CERTIFICATE REQUEST");
        return _t.getKeyFromCSRHex(e)
    }
    ,
    _t.getKeyFromCSRHex = function (t) {
        var e = _t.parseCSRHex(t);
        return _t.getKey(e.p8pubkeyhex, null, "pkcs8pub")
    }
    ,
    _t.parseCSRHex = function (t) {
        var e = ut
            , r = e.getChildIdx
            , i = e.getTLV
            , n = {}
            , s = t;
        if ("30" != s.substr(0, 2))
            throw new Error("malformed CSR(code:001)");
        var a = r(s, 0);
        if (a.length < 1)
            throw new Error("malformed CSR(code:002)");
        if ("30" != s.substr(a[0], 2))
            throw new Error("malformed CSR(code:003)");
        var o = r(s, a[0]);
        if (o.length < 3)
            throw new Error("malformed CSR(code:004)");
        return n.p8pubkeyhex = i(s, o[2]),
            n
    }
    ,
    _t.getKeyID = function (t) {
        var e = _t
            , r = ut;
        "string" == typeof t && -1 != t.indexOf("BEGIN ") && (t = e.getKey(t));
        var i = Ft(e.getPEM(t))
            , n = r.getIdxbyList(i, 0, [1])
            , s = r.getV(i, n).substring(2);
        return at.crypto.Util.hashHex(s, "sha1")
    }
    ,
    _t.getJWK = function (t, e, r, i, n) {
        var s, a, o = {}, h = at.crypto.Util.hashHex;
        if ("string" == typeof t)
            s = _t.getKey(t),
            -1 != t.indexOf("CERTIFICATE") && (a = Ft(t));
        else {
            if ("object" != typeof t)
                throw new Error("unsupported keyinfo type");
            t instanceof Wt ? (s = t.getPublicKey(),
                a = t.hex) : s = t
        }
        if (s instanceof tt && s.isPrivate)
            o.kty = "RSA",
                o.n = pt(s.n.toString(16)),
                o.e = pt(s.e.toString(16)),
                o.d = pt(s.d.toString(16)),
                o.p = pt(s.p.toString(16)),
                o.q = pt(s.q.toString(16)),
                o.dp = pt(s.dmp1.toString(16)),
                o.dq = pt(s.dmq1.toString(16)),
                o.qi = pt(s.coeff.toString(16));
        else if (s instanceof tt && s.isPublic)
            o.kty = "RSA",
                o.n = pt(s.n.toString(16)),
                o.e = pt(s.e.toString(16));
        else if (s instanceof at.crypto.ECDSA && s.isPrivate) {
            if ("P-256" !== (c = s.getShortNISTPCurveName()) && "P-384" !== c && "P-521" !== c)
                throw new Error("unsupported curve name for JWT: " + c);
            var u = s.getPublicKeyXYHex();
            o.kty = "EC",
                o.crv = c,
                o.x = pt(u.x),
                o.y = pt(u.y),
                o.d = pt(s.prvKeyHex)
        } else if (s instanceof at.crypto.ECDSA && s.isPublic) {
            var c;
            if ("P-256" !== (c = s.getShortNISTPCurveName()) && "P-384" !== c && "P-521" !== c)
                throw new Error("unsupported curve name for JWT: " + c);
            u = s.getPublicKeyXYHex();
            o.kty = "EC",
                o.crv = c,
                o.x = pt(u.x),
                o.y = pt(u.y)
        }
        if (null == o.kty)
            throw new Error("unsupported keyinfo");
        return s.isPrivate || 1 == e || (o.kid = at.jws.JWS.getJWKthumbprint(o)),
        null != a && 1 != r && (o.x5c = [d(a)]),
        null != a && 1 != i && (o.x5t = ft(d(h(a, "sha1")))),
        null != a && 1 != n && (o["x5t#S256"] = ft(d(h(a, "sha256")))),
            o
    }
    ,
    _t.getJWKFromKey = function (t) {
        return _t.getJWK(t, !0, !0, !0, !0)
    }
    ,
    tt.getPosArrayOfChildrenFromHex = function (t) {
        return ut.getChildIdx(t, 0)
    }
    ,
    tt.getHexValueArrayOfChildrenFromHex = function (t) {
        var e, r = ut.getV, i = r(t, (e = tt.getPosArrayOfChildrenFromHex(t))[0]), n = r(t, e[1]), s = r(t, e[2]),
            a = r(t, e[3]), o = r(t, e[4]), h = r(t, e[5]), u = r(t, e[6]), c = r(t, e[7]), l = r(t, e[8]);
        return (e = new Array).push(i, n, s, a, o, h, u, c, l),
            e
    }
    ,
    tt.prototype.readPrivateKeyFromPEMString = function (t) {
        var e = Ft(t)
            , r = tt.getHexValueArrayOfChildrenFromHex(e);
        this.setPrivateEx(r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8])
    }
    ,
    tt.prototype.readPKCS5PrvKeyHex = function (t) {
        var e = tt.getHexValueArrayOfChildrenFromHex(t);
        this.setPrivateEx(e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8])
    }
    ,
    tt.prototype.readPKCS8PrvKeyHex = function (t) {
        var e, r, i, n, s, a, o, h, u = ut, c = u.getVbyListEx;
        if (!1 === u.isASN1HEX(t))
            throw new Error("not ASN.1 hex string");
        try {
            e = c(t, 0, [2, 0, 1], "02"),
                r = c(t, 0, [2, 0, 2], "02"),
                i = c(t, 0, [2, 0, 3], "02"),
                n = c(t, 0, [2, 0, 4], "02"),
                s = c(t, 0, [2, 0, 5], "02"),
                a = c(t, 0, [2, 0, 6], "02"),
                o = c(t, 0, [2, 0, 7], "02"),
                h = c(t, 0, [2, 0, 8], "02")
        } catch (l) {
            throw new Error("malformed PKCS#8 plain RSA private key")
        }
        this.setPrivateEx(e, r, i, n, s, a, o, h)
    }
    ,
    tt.prototype.readPKCS5PubKeyHex = function (t) {
        var e = ut
            , r = e.getV;
        if (!1 === e.isASN1HEX(t))
            throw new Error("keyHex is not ASN.1 hex string");
        var i = e.getChildIdx(t, 0);
        if (2 !== i.length || "02" !== t.substr(i[0], 2) || "02" !== t.substr(i[1], 2))
            throw new Error("wrong hex for PKCS#5 public key");
        var n = r(t, i[0])
            , s = r(t, i[1]);
        this.setPublic(n, s)
    }
    ,
    tt.prototype.readPKCS8PubKeyHex = function (t) {
        var e = ut;
        if (!1 === e.isASN1HEX(t))
            throw new Error("not ASN.1 hex string");
        if ("06092a864886f70d010101" !== e.getTLVbyListEx(t, 0, [0, 0]))
            throw new Error("not PKCS8 RSA public key");
        var r = e.getTLVbyListEx(t, 0, [1, 0]);
        this.readPKCS5PubKeyHex(r)
    }
    ,
    tt.prototype.readCertPubKeyHex = function (t, e) {
        var r, i;
        (r = new Wt).readCertHex(t),
            i = r.getPublicKeyHex(),
            this.readPKCS8PubKeyHex(i)
    }
    ,
    tt.prototype.sign = function (t, e) {
        var r = function (t) {
            return at.crypto.Util.hashString(t, e)
        }(t);
        return this.signWithMessageHash(r, e)
    }
    ,
    tt.prototype.signWithMessageHash = function (t, e) {
        var r = Z(at.crypto.Util.getPaddedDigestInfoHex(t, e, this.n.bitLength()), 16);
        return Ut(this.doPrivate(r).toString(16), this.n.bitLength())
    }
    ,
    tt.prototype.signPSS = function (t, e, r) {
        var i = function (t) {
            return at.crypto.Util.hashHex(t, e)
        }(St(t));
        return void 0 === r && (r = -1),
            this.signWithMessageHashPSS(i, e, r)
    }
    ,
    tt.prototype.signWithMessageHashPSS = function (t, e, r) {
        var i, n = xt(t), s = n.length, a = this.n.bitLength() - 1, o = Math.ceil(a / 8), h = function (t) {
            return at.crypto.Util.hashHex(t, e)
        };
        if (-1 === r || void 0 === r)
            r = s;
        else if (-2 === r)
            r = o - s - 2;
        else if (r < -2)
            throw new Error("invalid salt length");
        if (o < s + r + 2)
            throw new Error("data too long");
        var u = "";
        r > 0 && (u = new Array(r),
            (new Y).nextBytes(u),
            u = String.fromCharCode.apply(String, u));
        var c = xt(h(St("\0\0\0\0\0\0\0\0" + n + u)))
            , l = [];
        for (i = 0; i < o - r - s - 2; i += 1)
            l[i] = 0;
        var f = String.fromCharCode.apply(String, l) + "" + u
            , g = Gt(c, f.length, h)
            , p = [];
        for (i = 0; i < f.length; i += 1)
            p[i] = f.charCodeAt(i) ^ g.charCodeAt(i);
        var d = 65280 >> 8 * o - a & 255;
        for (p[0] &= ~d,
                 i = 0; i < s; i++)
            p.push(c.charCodeAt(i));
        return p.push(188),
            Ut(this.doPrivate(new m(p)).toString(16), this.n.bitLength())
    }
    ,
    tt.prototype.verify = function (t, e) {
        if (null == (e = e.toLowerCase()).match(/^[0-9a-f]+$/))
            return !1;
        var r = Z(e, 16)
            , i = this.n.bitLength();
        if (r.bitLength() > i)
            return !1;
        var n = this.doPublic(r).toString(16);
        if (n.length + 3 != i / 4)
            return !1;
        var s = zt(n.replace(/^1f+00/, ""));
        if (0 == s.length)
            return !1;
        var a = s[0]
            , o = s[1]
            , h = function (t) {
            return at.crypto.Util.hashString(t, a)
        }(t);
        return o == h
    }
    ,
    tt.prototype.verifyWithMessageHash = function (t, e) {
        if (e.length != Math.ceil(this.n.bitLength() / 4))
            return !1;
        var r = Z(e, 16);
        if (r.bitLength() > this.n.bitLength())
            return 0;
        var i = zt(this.doPublic(r).toString(16).replace(/^1f+00/, ""));
        return 0 != i.length && (i[0],
        i[1] == t)
    }
    ,
    tt.prototype.verifyPSS = function (t, e, r, i) {
        var n, s = (n = St(t),
            at.crypto.Util.hashHex(n, r));
        return void 0 === i && (i = -1),
            this.verifyWithMessageHashPSS(s, e, r, i)
    }
    ,
    tt.prototype.verifyWithMessageHashPSS = function (t, e, r, i) {
        if (e.length != Math.ceil(this.n.bitLength() / 4))
            return !1;
        var n, s = new m(e, 16), a = function (t) {
            return at.crypto.Util.hashHex(t, r)
        }, o = xt(t), h = o.length, u = this.n.bitLength() - 1, c = Math.ceil(u / 8);
        if (-1 === i || void 0 === i)
            i = h;
        else if (-2 === i)
            i = c - h - 2;
        else if (i < -2)
            throw new Error("invalid salt length");
        if (c < h + i + 2)
            throw new Error("data too long");
        var l = this.doPublic(s).toByteArray();
        for (n = 0; n < l.length; n += 1)
            l[n] &= 255;
        for (; l.length < c;)
            l.unshift(0);
        if (188 !== l[c - 1])
            throw new Error("encoded message does not end in 0xbc");
        var f = (l = String.fromCharCode.apply(String, l)).substr(0, c - h - 1)
            , g = l.substr(f.length, h)
            , p = 65280 >> 8 * c - u & 255;
        if (f.charCodeAt(0) & p)
            throw new Error("bits beyond keysize not zero");
        var d = Gt(g, f.length, a)
            , v = [];
        for (n = 0; n < f.length; n += 1)
            v[n] = f.charCodeAt(n) ^ d.charCodeAt(n);
        v[0] &= ~p;
        var y = c - h - i - 2;
        for (n = 0; n < y; n += 1)
            if (0 !== v[n])
                throw new Error("leftmost octets not zero");
        if (1 !== v[y])
            throw new Error("0x01 marker not found");
        return g === xt(a(St("\0\0\0\0\0\0\0\0" + o + String.fromCharCode.apply(String, v.slice(-i)))))
    }
    ,
    tt.SALT_LEN_HLEN = -1,
    tt.SALT_LEN_MAX = -2,
    tt.SALT_LEN_RECOVER = -2,
    Wt.EXT_PARSER = {},
    Wt.registExtParser = function (t, e) {
        Wt.EXT_PARSER[t] = e
    }
    ,
    Wt.hex2dn = function (t, e) {
        void 0 === e && (e = 0);
        var r = new Wt;
        return ut.getTLV(t, e),
            r.getX500Name(t).str
    }
    ,
    Wt.hex2rdn = function (t, e) {
        if (void 0 === e && (e = 0),
        "31" !== t.substr(e, 2))
            throw new Error("malformed RDN");
        for (var r = new Array, i = ut.getChildIdx(t, e), n = 0; n < i.length; n++)
            r.push(Wt.hex2attrTypeValue(t, i[n]));
        return r = r.map((function (t) {
                return t.replace("+", "\\+")
            }
        )),
            r.join("+")
    }
    ,
    Wt.hex2attrTypeValue = function (t, e) {
        var r = ut
            , i = r.getV;
        if (void 0 === e && (e = 0),
        "30" !== t.substr(e, 2))
            throw new Error("malformed attribute type and value");
        var n = r.getChildIdx(t, e);
        2 !== n.length || t.substr(n[0], 2);
        var s = i(t, n[0])
            , a = at.asn1.ASN1Util.oidHexToInt(s);
        return at.asn1.x509.OID.oid2atype(a) + "=" + xt(i(t, n[1]))
    }
    ,
    Wt.getPublicKeyFromCertHex = function (t) {
        var e = new Wt;
        return e.readCertHex(t),
            e.getPublicKey()
    }
    ,
    Wt.getPublicKeyFromCertPEM = function (t) {
        var e = new Wt;
        return e.readCertPEM(t),
            e.getPublicKey()
    }
    ,
    Wt.getPublicKeyInfoPropOfCertPEM = function (t) {
        var e, r, i = ut.getVbyList, n = {};
        return n.algparam = null,
            (e = new Wt).readCertPEM(t),
            r = e.getPublicKeyHex(),
            n.keyhex = i(r, 0, [1], "03").substr(2),
            n.algoid = i(r, 0, [0, 0], "06"),
        "2a8648ce3d0201" === n.algoid && (n.algparam = i(r, 0, [0, 1], "06")),
            n
    }
    ,
    Wt.KEYUSAGE_NAME = ["digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly"],
void 0 !== at && at || (at = {}),
void 0 !== at.jws && at.jws || (at.jws = {}),
    at.jws.JWS = function () {
        var t = at.jws.JWS.isSafeJSONString;
        this.parseJWS = function (e, r) {
            if (void 0 === this.parsedJWS || !r && void 0 === this.parsedJWS.sigvalH) {
                var i = e.match(/^([^.]+)\.([^.]+)\.([^.]+)$/);
                if (null == i)
                    throw "JWS signature is not a form of 'Head.Payload.SigValue'.";
                var n = i[1]
                    , s = i[2]
                    , a = i[3]
                    , o = n + "." + s;
                if (this.parsedJWS = {},
                    this.parsedJWS.headB64U = n,
                    this.parsedJWS.payloadB64U = s,
                    this.parsedJWS.sigvalB64U = a,
                    this.parsedJWS.si = o,
                    !r) {
                    var h = dt(a)
                        , u = Z(h, 16);
                    this.parsedJWS.sigvalH = h,
                        this.parsedJWS.sigvalBI = u
                }
                var c = ht(n)
                    , l = ht(s);
                if (this.parsedJWS.headS = c,
                    this.parsedJWS.payloadS = l,
                    !t(c, this.parsedJWS, "headP"))
                    throw "malformed JSON string for JWS Head: " + c
            }
        }
    }
    ,
    at.jws.JWS.sign = function (t, e, r, i, n) {
        var s = at
            , a = s.jws.JWS
            , o = a.readSafeJSONString
            , h = a.isSafeJSONString
            , u = s.crypto;
        u.ECDSA;
        var c, l, f, g = u.Mac, p = u.Signature, d = JSON;
        if ("string" != typeof e && "object" != typeof e)
            throw "spHeader must be JSON string or object: " + e;
        if ("object" == typeof e && (l = e,
            c = d.stringify(l)),
        "string" == typeof e) {
            if (!h(c = e))
                throw "JWS Head is not safe JSON string: " + c;
            l = o(c)
        }
        if (f = r,
        "object" == typeof r && (f = d.stringify(r)),
        "" != t && null != t || void 0 === l.alg || (t = l.alg),
        "" != t && null != t && void 0 === l.alg && (l.alg = t,
            c = d.stringify(l)),
        t !== l.alg)
            throw "alg and sHeader.alg doesn't match: " + t + "!=" + l.alg;
        var v = null;
        if (void 0 === a.jwsalg2sigalg[t])
            throw "unsupported alg name: " + t;
        v = a.jwsalg2sigalg[t];
        var m = ot(c) + "." + ot(f)
            , y = "";
        if ("Hmac" == v.substr(0, 4)) {
            if (void 0 === i)
                throw "mac key shall be specified for HS* alg";
            var x = new g({
                alg: v,
                prov: "cryptojs",
                pass: i
            });
            x.updateString(m),
                y = x.doFinal()
        } else if (-1 != v.indexOf("withECDSA")) {
            (E = new p({
                alg: v
            })).init(i, n),
                E.updateString(m);
            var S = E.sign();
            y = at.crypto.ECDSA.asn1SigToConcatSig(S)
        } else {
            var E;
            if ("none" != v)
                (E = new p({
                    alg: v
                })).init(i, n),
                    E.updateString(m),
                    y = E.sign()
        }
        return m + "." + pt(y)
    }
    ,
    at.jws.JWS.verify = function (t, e, r) {
        var i, n = at, s = n.jws.JWS, a = s.readSafeJSONString, o = n.crypto, h = o.ECDSA, u = o.Mac, c = o.Signature;
        if (i = tt,
            !jt(t))
            return !1;
        var l = t.split(".");
        if (3 !== l.length)
            return !1;
        var f = l[0] + "." + l[1]
            , g = dt(l[2])
            , p = a(ht(l[0]))
            , d = null
            , v = null;
        if (void 0 === p.alg)
            throw "algorithm not specified in header";
        if ((v = (d = p.alg).substr(0, 2),
        null != r && "[object Array]" === Object.prototype.toString.call(r) && r.length > 0) && -1 == (":" + r.join(":") + ":").indexOf(":" + d + ":"))
            throw "algorithm '" + d + "' not accepted in the list";
        if ("none" != d && null === e)
            throw "key shall be specified to verify.";
        if ("string" == typeof e && -1 != e.indexOf("-----BEGIN ") && (e = _t.getKey(e)),
            !("RS" != v && "PS" != v || e instanceof i))
            throw "key shall be a RSAKey obj for RS* and PS* algs";
        if ("ES" == v && !(e instanceof h))
            throw "key shall be a ECDSA obj for ES* algs";
        var m = null;
        if (void 0 === s.jwsalg2sigalg[p.alg])
            throw "unsupported alg name: " + d;
        if ("none" == (m = s.jwsalg2sigalg[d]))
            throw "not supported";
        if ("Hmac" == m.substr(0, 4)) {
            if (void 0 === e)
                throw "hexadecimal key shall be specified for HMAC";
            var y = new u({
                alg: m,
                pass: e
            });
            return y.updateString(f),
            g == y.doFinal()
        }
        if (-1 != m.indexOf("withECDSA")) {
            var x, S = null;
            try {
                S = h.concatSigToASN1Sig(g)
            } catch (E) {
                return !1
            }
            return (x = new c({
                alg: m
            })).init(e),
                x.updateString(f),
                x.verify(S)
        }
        return (x = new c({
            alg: m
        })).init(e),
            x.updateString(f),
            x.verify(g)
    }
    ,
    at.jws.JWS.parse = function (t) {
        var e, r, i, n = t.split("."), s = {};
        if (2 != n.length && 3 != n.length)
            throw "malformed sJWS: wrong number of '.' splitted elements";
        return e = n[0],
            r = n[1],
        3 == n.length && (i = n[2]),
            s.headerObj = at.jws.JWS.readSafeJSONString(ht(e)),
            s.payloadObj = at.jws.JWS.readSafeJSONString(ht(r)),
            s.headerPP = JSON.stringify(s.headerObj, null, "  "),
            null == s.payloadObj ? s.payloadPP = ht(r) : s.payloadPP = JSON.stringify(s.payloadObj, null, "  "),
        void 0 !== i && (s.sigHex = dt(i)),
            s
    }
    ,
    at.jws.JWS.verifyJWT = function (t, e, r) {
        var i = at.jws
            , n = i.JWS
            , s = n.readSafeJSONString
            , a = n.inArray
            , o = n.includedArray;
        if (!jt(t))
            return !1;
        var h = t.split(".");
        if (3 != h.length)
            return !1;
        var u = h[0]
            , c = h[1];
        dt(h[2]);
        var l = s(ht(u))
            , f = s(ht(c));
        if (void 0 === l.alg)
            return !1;
        if (void 0 === r.alg)
            throw "acceptField.alg shall be specified";
        if (!a(l.alg, r.alg))
            return !1;
        if (void 0 !== f.iss && "object" == typeof r.iss && !a(f.iss, r.iss))
            return !1;
        if (void 0 !== f.sub && "object" == typeof r.sub && !a(f.sub, r.sub))
            return !1;
        if (void 0 !== f.aud && "object" == typeof r.aud)
            if ("string" == typeof f.aud) {
                if (!a(f.aud, r.aud))
                    return !1
            } else if ("object" == typeof f.aud && !o(f.aud, r.aud))
                return !1;
        var g = i.IntDate.getNow();
        return void 0 !== r.verifyAt && "number" == typeof r.verifyAt && (g = r.verifyAt),
        void 0 !== r.gracePeriod && "number" == typeof r.gracePeriod || (r.gracePeriod = 0),
        !(void 0 !== f.exp && "number" == typeof f.exp && f.exp + r.gracePeriod < g) && (!(void 0 !== f.nbf && "number" == typeof f.nbf && g < f.nbf - r.gracePeriod) && (!(void 0 !== f.iat && "number" == typeof f.iat && g < f.iat - r.gracePeriod) && ((void 0 === f.jti || void 0 === r.jti || f.jti === r.jti) && !!n.verify(t, e, r.alg))))
    }
    ,
    at.jws.JWS.includedArray = function (t, e) {
        var r = at.jws.JWS.inArray;
        if (null === t)
            return !1;
        if ("object" != typeof t)
            return !1;
        if ("number" != typeof t.length)
            return !1;
        for (var i = 0; i < t.length; i++)
            if (!r(t[i], e))
                return !1;
        return !0
    }
    ,
    at.jws.JWS.inArray = function (t, e) {
        if (null === e)
            return !1;
        if ("object" != typeof e)
            return !1;
        if ("number" != typeof e.length)
            return !1;
        for (var r = 0; r < e.length; r++)
            if (e[r] == t)
                return !0;
        return !1
    }
    ,
    at.jws.JWS.jwsalg2sigalg = {
        HS256: "HmacSHA256",
        HS384: "HmacSHA384",
        HS512: "HmacSHA512",
        RS256: "SHA256withRSA",
        RS384: "SHA384withRSA",
        RS512: "SHA512withRSA",
        ES256: "SHA256withECDSA",
        ES384: "SHA384withECDSA",
        ES512: "SHA512withECDSA",
        PS256: "SHA256withRSAandMGF1",
        PS384: "SHA384withRSAandMGF1",
        PS512: "SHA512withRSAandMGF1",
        none: "none"
    },
    at.jws.JWS.isSafeJSONString = function (e, r, i) {
        var n = null;
        try {
            return "object" != typeof (n = st(e)) || n.constructor === Array ? 0 : (r && (r[i] = n),
                1)
        } catch (t) {
            return 0
        }
    }
    ,
    at.jws.JWS.readSafeJSONString = function (e) {
        var r = null;
        try {
            return "object" != typeof (r = st(e)) || r.constructor === Array ? null : r
        } catch (t) {
            return null
        }
    }
    ,
    at.jws.JWS.getEncodedSignatureValueFromJWS = function (t) {
        var e = t.match(/^[^.]+\.[^.]+\.([^.]+)$/);
        if (null == e)
            throw "JWS signature is not a form of 'Head.Payload.SigValue'.";
        return e[1]
    }
    ,
    at.jws.JWS.getJWKthumbprint = function (t) {
        if ("RSA" !== t.kty && "EC" !== t.kty && "oct" !== t.kty)
            throw "unsupported algorithm for JWK Thumprint";
        var e = "{";
        if ("RSA" === t.kty) {
            if ("string" != typeof t.n || "string" != typeof t.e)
                throw "wrong n and e value for RSA key";
            e += '"e":"' + t.e + '",',
                e += '"kty":"' + t.kty + '",',
                e += '"n":"' + t.n + '"}'
        } else if ("EC" === t.kty) {
            if ("string" != typeof t.crv || "string" != typeof t.x || "string" != typeof t.y)
                throw "wrong crv, x and y value for EC key";
            e += '"crv":"' + t.crv + '",',
                e += '"kty":"' + t.kty + '",',
                e += '"x":"' + t.x + '",',
                e += '"y":"' + t.y + '"}'
        } else if ("oct" === t.kty) {
            if ("string" != typeof t.k)
                throw "wrong k value for oct(symmetric) key";
            e += '"kty":"' + t.kty + '",',
                e += '"k":"' + t.k + '"}'
        }
        var r = St(e);
        return pt(at.crypto.Util.hashHex(r, "sha256"))
    }
    ,
    at.jws.IntDate = {},
    at.jws.IntDate.get = function (t) {
        var e = at.jws.IntDate
            , r = e.getNow
            , i = e.getZulu;
        if ("now" == t)
            return r();
        if ("now + 1hour" == t)
            return r() + 3600;
        if ("now + 1day" == t)
            return r() + 86400;
        if ("now + 1month" == t)
            return r() + 2592e3;
        if ("now + 1year" == t)
            return r() + 31536e3;
        if (t.match(/Z$/))
            return i(t);
        if (t.match(/^[0-9]+$/))
            return parseInt(t);
        throw "unsupported format: " + t
    }
    ,
    at.jws.IntDate.getZulu = function (t) {
        return bt(t)
    }
    ,
    at.jws.IntDate.getNow = function () {
        return ~~(new Date / 1e3)
    }
    ,
    at.jws.IntDate.intDate2UTCString = function (t) {
        return new Date(1e3 * t).toUTCString()
    }
    ,
    at.jws.IntDate.intDate2Zulu = function (t) {
        var e = new Date(1e3 * t);
        return ("0000" + e.getUTCFullYear()).slice(-4) + ("00" + (e.getUTCMonth() + 1)).slice(-2) + ("00" + e.getUTCDate()).slice(-2) + ("00" + e.getUTCHours()).slice(-2) + ("00" + e.getUTCMinutes()).slice(-2) + ("00" + e.getUTCSeconds()).slice(-2) + "Z"
    }
    ,
void 0 !== at && at || (at = {}),
void 0 !== at.jws && at.jws || (at.jws = {}),
    at.jws.JWSJS = function () {
        var t = at.jws.JWS
            , e = t.readSafeJSONString;
        this.aHeader = [],
            this.sPayload = "",
            this.aSignature = [],
            this.init = function () {
                this.aHeader = [],
                    this.sPayload = void 0,
                    this.aSignature = []
            }
            ,
            this.initWithJWS = function (t) {
                this.init();
                var e = t.split(".");
                if (3 != e.length)
                    throw "malformed input JWS";
                this.aHeader.push(e[0]),
                    this.sPayload = e[1],
                    this.aSignature.push(e[2])
            }
            ,
            this.addSignature = function (t, e, r, i) {
                if (void 0 === this.sPayload || null === this.sPayload)
                    throw "there's no JSON-JS signature to add.";
                var n = this.aHeader.length;
                if (this.aHeader.length != this.aSignature.length)
                    throw "aHeader.length != aSignature.length";
                try {
                    var s = at.jws.JWS.sign(t, e, this.sPayload, r, i).split(".");
                    s[0],
                        s[2];
                    this.aHeader.push(s[0]),
                        this.aSignature.push(s[2])
                } catch (a) {
                    throw this.aHeader.length > n && this.aHeader.pop(),
                    this.aSignature.length > n && this.aSignature.pop(),
                    "addSignature failed: " + a
                }
            }
            ,
            this.verifyAll = function (t) {
                if (this.aHeader.length !== t.length || this.aSignature.length !== t.length)
                    return !1;
                for (var e = 0; e < t.length; e++) {
                    var r = t[e];
                    if (2 !== r.length)
                        return !1;
                    if (!1 === this.verifyNth(e, r[0], r[1]))
                        return !1
                }
                return !0
            }
            ,
            this.verifyNth = function (e, r, i) {
                if (this.aHeader.length <= e || this.aSignature.length <= e)
                    return !1;
                var n = this.aHeader[e]
                    , s = this.aSignature[e]
                    , a = n + "." + this.sPayload + "." + s
                    , o = !1;
                try {
                    o = t.verify(a, r, i)
                } catch (h) {
                    return !1
                }
                return o
            }
            ,
            this.readJWSJS = function (t) {
                if ("string" == typeof t) {
                    var i = e(t);
                    if (null == i)
                        throw "argument is not safe JSON object string";
                    this.aHeader = i.headers,
                        this.sPayload = i.payload,
                        this.aSignature = i.signatures
                } else
                    try {
                        if (!(t.headers.length > 0))
                            throw "malformed header";
                        if (this.aHeader = t.headers,
                        "string" != typeof t.payload)
                            throw "malformed signatures";
                        if (this.sPayload = t.payload,
                            !(t.signatures.length > 0))
                            throw "malformed signatures";
                        this.aSignature = t.signatures
                    } catch (r) {
                        throw "malformed JWS-JS JSON object: " + r
                    }
            }
            ,
            this.getJSON = function () {
                return {
                    headers: this.aHeader,
                    payload: this.sPayload,
                    signatures: this.aSignature
                }
            }
            ,
            this.isEmpty = function () {
                return 0 == this.aHeader.length ? 1 : 0
            }
    }
    ,
    at.crypto.ECDSA,
    at.crypto.DSA,
    at.crypto.Signature,
    at.crypto.MessageDigest,
    at.crypto.Mac,
    at.crypto.Cipher;
var Jt = _t
    , Xt = Et
    , $t = at;
at.crypto,
    at.asn1,
    at.jws,
    at.lang;
// export {Jt as K, $t as a, Xt as h};
// import {K as x, a as K, h as q} from "./jsrsasign-cf827c25.js";


//----------------------------------------------------------------------------------------------------------------------
const pe = e => {
    let n = ""
        , t = e.url;
    if (e.params) {
        let t = {}
            , r = JSON.stringify(e.params)
            , o = JSON.parse(r)
            , a = Object.keys(o).sort();
        for (let e = 0; e < a.length; e++) {
            let n = o[a[e]];
            null != n && (t[a[e]] = String(o[a[e]]))
        }
        n = JSON.stringify(t)
    } else if ("post" === e.method || "put" === e.method)
        n = JSON.stringify(e.data);
    else {
        let {params: e, reqUrl: r} = be(t);
        "" != e && (n = e),
        "" != r && (t = r)
    }
    let r = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (e => {
            const n = 16 * Math.random() | 0;
            return ("x" == e ? n : 3 & n | 8).toString(16)
        }
    ))
        , o = (new Date).getTime()
        , a = de(n);
    return {
        signature: fe("-----BEGIN PRIVATE KEY-----\n  MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdgJNfUFPDNJsL\n  HObB1JMu7E1+nuwkFHmXnBU2AOM2dweE+tpmViZo90w+YQIuIS8MoVz60AGHbLE8\n  BYcdxQEKmPsqq0Lq/1ltIdp1YcO9W60qSxwpZS+7o73ljRrrtOXcE1UUpH5l07Fh\n  ziCIRDI/4ODCA8AJ1kV6IyfPNM2Fes3BEqhMOgw4Z5i4pZHnb4Nm+4kEXmyM+UgQ\n  cShcXZA/dx5MXKA2Bbb0I0G6HS3D4nMhnm6IgYWEyT8ngenMOyy+ysBuHWt2j9Cp\n  AGLWRyqHigFcKTlP5BSIkU+8sqssab1jvDg2F8MXWuupwF43OVARgHofiwQBAHPo\n  PfTfPlMvAgMBAAECggEBAJKQpZNasrfCak0LFgllgZl2uB6OUPy6OPRGgM6CQO3c\n  EhlDPp1gqdmf10ltCJRYuOmt91JG4kVddgh+tF+VhgSQm5n3SQxZlqQhjqMQ2Q+L\n  Ejd7Mberu6GHHB1TE6wn6IbFTrUo5Z5oQnbbVBa6L3CWGVEyIDCHPpwLvu3pGx+L\n  083dNQUiF8WcSGybl1h4ZapAGdndPYJReKYccNBYu5IzTEjtG3VpMHl56hD8fPV8\n  SStYv4sEffyCbze5/KvG3WlG+8n1WzBRMAN1U8Qk3JlMM/g5Y2tL1elI2pQRmjH8\n  EVxNUzB9Ob/qk2N6pF4KwhDWjILkHdoXilHMgP5x0gECgYEAy9O9ShtRNwXdFzHe\n  v+buyjvWWvwTVRUBehe8BWO1QaZ4c/INw1Ks4pgoKvXyU1DRx5OloIx6BWDbs00O\n  1W1cDue2I/Ymvx5Q/XJmZK4eR2U3a2dmKLKVhCXhJ3y02R/OZ2xQHV3NZXqz88kf\n  rEmEKYTW9q2gVsZa82XpQhKnBYECgYEAxdFJ55AkU7VfzpV1x68NUetomB3OWxyq\n  Cugn3STLNx7Jw6FaK3dwRz0eKIbwCRxtlluZmxWX0jWSvj3cyLRBIKTD8atUfJW2\n  +ESKZb/i961HhhQjXqNfGQpmMdEazNqv0sDzQ5jHHIjc63oty/FjckcC+AaDGZIJ\n  VGCet5J5kK8CgYBm2R/Bfgk792R5KLvaHz/MoebmoB1tKB1HqyQ/n/E9AC/1aWUS\n  cuwzpk1WaCXvbm98Af9oBJopjpctYSuj+/ugtcDNYo5oj3aUfJ44HTfAFM2jD1iY\n  HoydUrPKxf1HNepje17tgoB6vTCCSbEGsU3T2WjSrgei4ZHREVJi+aB3gQKBgEy8\n  rm2sxdrPHjZWVlU6+/DOYEm6LkW77d7DRkuMLWTZha1lF0SLVbvc4qkYB1+RbpWI\n  PSMjEj0SWTWBa/dTrXwLTpOeQez+avcOJ53m/RXVW0yQ3VOmDor5NMGYe0wCfXhF\n  L1kGmB7inMigIcnefxRipa0vYYX217WqsYdGw++zAoGBALKswyV5j1GjVjN+fS1t\n  N9R0x+S7cKBqW6Bwj6aAdo4+spmRn9WK4h9Zk2k7BMUiqJKTce6RdW0Ep+aTErRs\n  LL0sBHArhQdaQvq0yS57BJUZm3ASrOpp3wkQdDejS3YEKiIQSG2kNFRanh8RbtbA\n  ac7pfLikyQm795/qF0H9YHgF\n  -----END PRIVATE KEY-----", "Timestamp&" + o + "&" + t + "&" + r + "&" + a),
        timestamp: o,
        nonce: r
    }
}
const de = e => {
    let n = {};
    if (null == e || "" === e)
        return "";
    let t = JSON.parse(e)
        , r = Object.keys(t).sort();
    for (let o = 0; o < r.length; o++) {
        let e = t[r[o]];
        null != e && (n[r[o]] = t[r[o]])
    }
    return JSON.stringify(n)
}
const be = e => {
    let n = {}
        , t = "";
    if (null != e && null != e && e.indexOf("?") > 0) {
        t = e.substring(0, e.indexOf("?"));
        let r = e.split("?")[1];
        if (null != r && null != r) {
            r.split("&").forEach((e => {
                    let t = e.split("=")[0]
                        , r = e.split("=")[1];
                    n[t] = String(encodeURIComponent(r))
                }
            ))
        } else
            t = e
    }
    return {
        params: 0 === Object.keys(n).length ? "" : JSON.stringify(n),
        reqUrl: t
    }
}
const fe = (e, n) => {
    const t = Jt.getKey(e)
        , r = new $t.crypto.Signature({
        alg: "SHA256withRSA"
    });
    return r.init(t),
        r.updateString(n),
        Xt(r.sign())
}

//TODO:调用函数
function get_headers(data) {
    let e = {
        "transitional": {
            "silentJSONParsing": true,
            "forcedJSONParsing": true,
            "clarifyTimeoutError": false
        },
        "adapter": [
            "xhr",
            "http"
        ],
        "transformRequest": [
            null
        ],
        "transformResponse": [
            null
        ],
        "timeout": 30000,
        "xsrfCookieName": "XSRF-TOKEN",
        "xsrfHeaderName": "X-XSRF-TOKEN",
        "maxContentLength": -1,
        "maxBodyLength": -1,
        "env": {},
        "headers": {
            "Accept": "application/json, text/plain, */*",
            "Content-Type": "application/json;charset=utf-8"
        },
        "baseURL": "/",
        "url": "/byairport-flight/flight/list",
        "method": "post",
        "data": data
    }
    return pe(e)
}


//测试接口
e = {
    "transitional": {
        "silentJSONParsing": true,
        "forcedJSONParsing": true,
        "clarifyTimeoutError": false
    },
    "adapter": [
        "xhr",
        "http"
    ],
    "transformRequest": [
        null
    ],
    "transformResponse": [
        null
    ],
    "timeout": 30000,
    "xsrfCookieName": "XSRF-TOKEN",
    "xsrfHeaderName": "X-XSRF-TOKEN",
    "maxContentLength": -1,
    "maxBodyLength": -1,
    "env": {},
    "headers": {
        "Accept": "application/json, text/plain, */*",
        "Content-Type": "application/json;charset=utf-8"
    },
    "baseURL": "/",
    "url": "/byairport-flight/flight/list",
    "method": "post",
    "data": {
        "type": "1",
        "terminal": "",
        "day": 0,
        "depOrArr": "1",
        "pageNum": 1,
        "pageSize": 15
    }
}
console.log(pe(e))