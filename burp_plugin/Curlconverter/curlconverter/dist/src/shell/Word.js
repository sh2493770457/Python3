// Words act like strings. They're lists of characters, except some
// characters can be shell variables or expressions.
// They're implemented like this:
// ["foobar", {type: "variable", value: "baz", text: "$baz"}, "qux"]
// Except for the empty string [""], there should be no empty strings in the array.
import { CCError } from "../utils.js";
// TODO: Words should keep a list of operations that happened to them
// like .replace() so that we can generate code that also does that operation
// on the contents of the environment variable or the output of the command.
export class Word {
    constructor(tokens) {
        this.valueOf = Word.toString;
        if (typeof tokens === "string") {
            tokens = [tokens];
        }
        if (tokens === undefined || tokens.length === 0) {
            tokens = [""];
        }
        this.tokens = [];
        for (const t of tokens) {
            if (typeof t === "string") {
                if (this.tokens.length > 0 &&
                    typeof this.tokens[this.tokens.length - 1] === "string") {
                    // If we have 2+ strings in a row, merge them
                    this.tokens[this.tokens.length - 1] += t;
                }
                else if (t) {
                    // skip empty strings
                    this.tokens.push(t);
                }
            }
            else {
                this.tokens.push(t);
            }
        }
        if (this.tokens.length === 0) {
            this.tokens.push("");
        }
    }
    get length() {
        let len = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                len += t.length;
            }
            else {
                len += 1;
            }
        }
        return len;
    }
    *[Symbol.iterator]() {
        for (const t of this.tokens) {
            if (typeof t === "string") {
                for (const c of t) {
                    yield c;
                }
            }
            else {
                yield t;
            }
        }
    }
    // TODO: do we need this function?
    get(index) {
        let i = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                if (i + t.length > index) {
                    return t[index - i];
                }
                i += t.length;
            }
            else {
                if (i === index) {
                    return t;
                }
                i += 1;
            }
        }
        throw new CCError("Index out of bounds");
    }
    charAt(index = 0) {
        try {
            return this.get(index);
        }
        catch (_a) { }
        return "";
    }
    indexOf(search, start) {
        if (start === undefined) {
            start = 0;
        }
        let i = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                if (i + t.length > start) {
                    const index = t.indexOf(search, start - i);
                    if (index !== -1) {
                        return i + index;
                    }
                }
                i += t.length;
            }
            else {
                i += 1;
            }
        }
        return -1;
    }
    // Like indexOf() but accepts a string of characters and returns the index of the first one
    // it finds
    indexOfFirstChar(search) {
        let i = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                for (const c of t) {
                    if (search.includes(c)) {
                        return i;
                    }
                    i += 1;
                }
            }
            else {
                i += 1;
            }
        }
        return -1;
    }
    removeFirstChar(c) {
        if (this.length === 0) {
            return new Word();
        }
        if (this.charAt(0) === c) {
            return this.slice(1);
        }
        return this.copy();
    }
    copy() {
        return new Word(this.tokens);
    }
    slice(indexStart, indexEnd) {
        if (indexStart === undefined) {
            indexStart = this.length;
        }
        if (indexEnd === undefined) {
            indexEnd = this.length;
        }
        if (indexStart >= this.length) {
            return new Word();
        }
        if (indexStart < 0) {
            indexStart = Math.max(indexStart + this.length, 0);
        }
        if (indexEnd < 0) {
            indexEnd = Math.max(indexEnd + this.length, 0);
        }
        if (indexEnd <= indexStart) {
            return new Word();
        }
        const ret = [];
        let i = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                if (i + t.length > indexStart) {
                    if (i < indexEnd) {
                        ret.push(t.slice(Math.max(indexStart - i, 0), indexEnd - i));
                    }
                }
                i += t.length;
            }
            else {
                if (i >= indexStart && i < indexEnd) {
                    ret.push(t);
                }
                i += 1;
            }
        }
        return new Word(ret);
    }
    // TODO: check
    includes(search, start) {
        if (start === undefined) {
            start = 0;
        }
        let i = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                if (i + t.length > start) {
                    if (t.includes(search, start - i)) {
                        return true;
                    }
                }
                i += t.length;
            }
            else {
                i += 1;
            }
        }
        return false;
    }
    test(search) {
        for (const t of this.tokens) {
            if (typeof t === "string") {
                if (search.test(t)) {
                    return true;
                }
            }
        }
        return false;
    }
    prepend(c) {
        const ret = this.copy();
        if (ret.tokens.length && typeof ret.tokens[0] === "string") {
            ret.tokens[0] = c + ret.tokens[0];
        }
        else {
            ret.tokens.unshift(c);
        }
        return ret;
    }
    append(c) {
        const ret = this.copy();
        if (ret.tokens.length &&
            typeof ret.tokens[ret.tokens.length - 1] === "string") {
            ret.tokens[ret.tokens.length - 1] += c;
        }
        else {
            ret.tokens.push(c);
        }
        return ret;
    }
    // Merges two Words
    add(other) {
        return new Word([...this.tokens, ...other.tokens]);
    }
    // Returns the first match, searches each string independently
    // TODO: improve this
    match(regex) {
        for (const t of this.tokens) {
            if (typeof t === "string") {
                const match = t.match(regex);
                if (match) {
                    return match;
                }
            }
        }
        return null;
    }
    search(regex) {
        let offset = 0;
        for (const t of this.tokens) {
            if (typeof t === "string") {
                const match = t.search(regex);
                if (match !== -1) {
                    return offset + match;
                }
                offset += t.length;
            }
        }
        return -1;
    }
    // .replace() is called per-string, so it won't work through shell variables
    replace(search, replacement) {
        const ret = [];
        for (const t of this.tokens) {
            if (typeof t === "string") {
                ret.push(t.replace(search, replacement));
            }
            else {
                ret.push(t);
            }
        }
        return new Word(ret);
    }
    // splits correctly, not like String.split()
    // The last entry can contain the separator if limit entries has been reached
    split(separator, limit) {
        const ret = [];
        let i = 0;
        let start = 0;
        while (i < this.length) {
            let match = true;
            for (let j = 0; j < separator.length; j++) {
                if (this.get(i + j) !== separator.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                ret.push(this.slice(start, i));
                i += separator.length;
                start = i;
                if (limit !== undefined && ret.length === limit - 1) {
                    break;
                }
            }
            else {
                i += 1;
            }
        }
        if (start <= this.length) {
            ret.push(this.slice(start));
        }
        return ret;
    }
    toLowerCase() {
        return new Word(this.tokens.map((t) => (typeof t === "string" ? t.toLowerCase() : t)));
    }
    toUpperCase() {
        return new Word(this.tokens.map((t) => (typeof t === "string" ? t.toUpperCase() : t)));
    }
    trimStart() {
        const ret = [];
        let i, t;
        for ([i, t] of this.tokens.entries()) {
            if (typeof t === "string") {
                if (i === 0) {
                    t = t.trimStart();
                }
                if (t) {
                    ret.push(t);
                }
            }
            else {
                ret.push(t);
            }
        }
        if (ret.length === 0) {
            return new Word();
        }
        return new Word(ret);
    }
    trimEnd() {
        const ret = [];
        let i, t;
        for ([i, t] of this.tokens.entries()) {
            if (typeof t === "string") {
                if (i === this.tokens.length - 1) {
                    t = t.trimEnd();
                }
                if (t) {
                    ret.push(t);
                }
            }
            else {
                ret.push(t);
            }
        }
        if (ret.length === 0) {
            return new Word();
        }
        return new Word(ret);
    }
    trim() {
        const ret = [];
        let i, t;
        for ([i, t] of this.tokens.entries()) {
            if (typeof t === "string") {
                if (i === 0) {
                    t = t.trimStart();
                }
                if (i === this.tokens.length - 1) {
                    t = t.trimEnd();
                }
                if (t) {
                    ret.push(t);
                }
            }
            else {
                ret.push(t);
            }
        }
        if (ret.length === 0) {
            return new Word();
        }
        return new Word(ret);
    }
    isEmpty() {
        if (this.tokens.length === 0) {
            return true;
        }
        if (this.tokens.length === 1 && typeof this.tokens[0] === "string") {
            return this.tokens[0].length === 0;
        }
        return false;
    }
    toBool() {
        return !this.isEmpty();
    }
    // Returns true if .tokens contains no variables/commands
    isString() {
        for (const t of this.tokens) {
            if (typeof t !== "string") {
                return false;
            }
        }
        return true;
    }
    firstShellToken() {
        for (const t of this.tokens) {
            if (typeof t !== "string") {
                return t;
            }
        }
        return null;
    }
    startsWith(prefix) {
        if (this.tokens.length === 0) {
            return false;
        }
        if (typeof this.tokens[0] === "string") {
            return this.tokens[0].startsWith(prefix);
        }
        return false;
    }
    endsWith(suffix) {
        if (this.tokens.length === 0) {
            return false;
        }
        const lastToken = this.tokens[this.tokens.length - 1];
        if (typeof lastToken === "string") {
            return lastToken.endsWith(suffix);
        }
        return false;
    }
    // This destroys the information about the original tokenization
    toString() {
        return this.tokens
            .map((t) => (typeof t === "string" ? t : t.text))
            .join("");
    }
}
export function eq(it, other) {
    if (it === undefined ||
        it === null ||
        other === undefined ||
        other === null) {
        return it === other;
    }
    if (typeof other === "string") {
        return (it.tokens.length === 1 &&
            typeof it.tokens[0] === "string" &&
            it.tokens[0] === other);
    }
    return (it.tokens.length === other.tokens.length &&
        it.tokens.every((itToken, i) => {
            const otherToken = other.tokens[i];
            if (typeof itToken === "string") {
                return itToken === otherToken;
            }
            else if (typeof otherToken !== "string") {
                return itToken.text === otherToken.text;
            }
            return false;
        }));
}
export function firstShellToken(word) {
    if (typeof word === "string") {
        return null;
    }
    return word.firstShellToken();
}
export function mergeWords(...words) {
    const ret = [];
    for (const w of words) {
        if (w instanceof Word) {
            ret.push(...w.tokens);
        }
        else {
            ret.push(w);
        }
    }
    return new Word(ret);
}
export function joinWords(words, joinChar) {
    const ret = [];
    for (const w of words) {
        if (ret.length) {
            ret.push(joinChar);
        }
        ret.push(...w.tokens);
    }
    return new Word(ret);
}
//# sourceMappingURL=Word.js.map