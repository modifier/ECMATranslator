Array.from
Array.from = Array.from || function(list) {
    var mapFn = arguments.length > 1 ? arguments[1] : undefined;
    var thisArg = arguments.length > 2 ? arguments[2] : undefined;

    if (mapFn !== undefined && typeof mapFn != "function") {
        throw new TypeError('Array.from: when provided, the second argument must be a function');
    }

    var result = new Array(list.length);
    var value;

    for (var i = 0; i < list.length; i++) {
        value = list[i];
        if (mapFn !== undefined) {
            result[i] = thisArg ? mapFn.call(thisArg, value) : mapFn(value);
        } else {
            result[i] = value;
        }
    }

    return result;
};
===
Array.of
Array.of = Array.of || function() {
    var list = arguments;
    var result = new Array(list.length);

    for (var i = 0; i < list.length; i++) {
        result[i] = list[i];
    }

    return result;
};
===
Array.prototype.find
Array.prototype.find = Array.prototype.find || function () {
    var list = this;
    if (typeof predicate != "function") {
        throw new TypeError('Array#find: predicate must be a function');
    }
    var thisArg = arguments[1] || this;
    for (var i = 0, value; i < list.length; i++) {
        if (i in list) {
            value = list[i];
            if (predicate.call(thisArg, value, i, list)) return value;
        }
    }
    return undefined;
};
===
Array.prototype.findIndex
Array.prototype.findIndex = Array.prototype.findIndex || function(predicate) {
    var list = this;
    if (typeof predicate != "function") {
        throw new TypeError('Array#findIndex: predicate must be a function');
    }
    var thisArg = arguments[1] || this;
    for (var i = 0; i < this.length; i++) {
        if (i in list) {
            if (predicate.call(thisArg, list[i], i, list)) return i;
        }
    }
    return -1;
};
===
Array.prototype.fill
Array.prototype.fill = Array.prototype.fill || function(value) {
    var start = arguments[1], end = arguments[2];
    var len = O.length;
    start = start===undefined ? 0 : start;
    end = end===undefined ? len : end;

    var relativeStart = start < 0 ? Math.max(len + start, 0) : Math.min(start, len);

    for (var i = relativeStart; i < len && i < end; ++i) {
        this[i] = value;
    }
    return this;
};
===
Object.assign
Object.assign = Object.assign || function(target, source) {
    if (typeof target != "object") {
        throw new TypeError('target must be an object');
    }
    return Array.prototype.reduce.call(arguments, function(target, source) {
    if (typeof source != "object") {
        throw new TypeError('source must be an object');
    }
    return Object.keys(source).reduce(function(target, key) {
            target[key] = source[key];
            return target;
        }, target);
    });
};
===
Object.getPropertyDescriptor
Object.getPropertyDescriptor = getPropertyDescriptor || function(subject, name) {
    var pd = Object.getOwnPropertyDescriptor(subject, name);
    var proto = Object.getPrototypeOf(subject);
    while (pd === undefined && proto !== null) {
        pd = Object.getOwnPropertyDescriptor(proto, name);
        proto = Object.getPrototypeOf(proto);
    }
    return pd;
};
===
Object.getPropertyNames
Object.getPropertyNames = Object.getPropertyNames || function(subject) {
    var result = Object.getOwnPropertyNames(subject);
    var proto = Object.getPrototypeOf(subject);

    var addProperty = function(property) {
        if (result.indexOf(property) === -1) {
            result.push(property);
        }
    };

    while (proto !== null) {
        Object.getOwnPropertyNames(proto).forEach(addProperty);
        proto = Object.getPrototypeOf(proto);
    }
    return result;
};
===
Object.is
Object.is = Object.is || function(a, b) {
    if (a === b) {
        // 0 === -0, but they are not identical.
        if (a === 0) return 1 / a === 1 / b;
        return true;
    }
    return Number.isNaN(a) && Number.isNaN(b);
}
===
Object.setPrototypeOf
Object.setPrototypeOf = Object.setPrototypeOf || (function(Object, magic) {
    var set;

    var checkArgs = function(O, proto) {
        if (!ES.TypeIsObject(O)) {
            throw new TypeError('cannot set prototype on a non-object');
        }
        if (!(proto===null || ES.TypeIsObject(proto))) {
            throw new TypeError('can only set prototype to an object or null'+proto);
        }
    };

    var setPrototypeOf = function(O, proto) {
        checkArgs(O, proto);
        set.call(O, proto);
        return O;
    };

    try {
        // this works already in Firefox and Safari
        set = Object.getOwnPropertyDescriptor(Object.prototype, magic).set;
        set.call({}, null);
    } catch (e) {
        if (Object.prototype !== {}[magic]) {
            // IE < 11 cannot be shimmed
            return;
        }
        // probably Chrome or some old Mobile stock browser
        set = function(proto) {
            this[magic] = proto;
        };
        // please note that this will **not** work
        // in those browsers that do not inherit
        // __proto__ by mistake from Object.prototype
        // in these cases we should probably throw an error
        // or at least be informed about the issue
        setPrototypeOf.polyfill = setPrototypeOf(
            setPrototypeOf({}, null),
            Object.prototype
        ) instanceof Object;
        // setPrototypeOf.polyfill === true means it works as meant
        // setPrototypeOf.polyfill === false means it's not 100% reliable
        // setPrototypeOf.polyfill === undefined
        // or
        // setPrototypeOf.polyfill ==  null means it's not a polyfill
        // which means it works as expected
        // we can even delete Object.prototype.__proto__;
    }
    return setPrototypeOf;
})(Object, '__proto__');
===
String.fromCodePoint
String.fromCodePoint = String.fromCodePoint || function() {
    var points = Array.prototype.slice.call(arguments, 0, arguments.length);
    var result = [];
    var next;
    for (var i = 0, length = points.length; i < length; i++) {
        next = Number(points[i]);
        if (isNaN(next) || next < 0 || next > 0x10FFFF) {
            throw new RangeError('Invalid code point ' + next);
        }

        if (next < 0x10000) {
            result.push(String.fromCharCode(next));
        } else {
            next -= 0x10000;
            result.push(String.fromCharCode((next >> 10) + 0xD800));
            result.push(String.fromCharCode((next % 0x400) + 0xDC00));
        }
    }
    return result.join('');
};
===
String.prototype.codePointAt
String.prototype.codePointAt = String.prototype.codePointAt || function(pos) {
    var thisStr = String(ES.CheckObjectCoercible(this));
    var position = ES.ToInteger(pos);
    var length = thisStr.length;
    if (position < 0 || position >= length) return undefined;
    var first = thisStr.charCodeAt(position);
    var isEnd = (position + 1 === length);
    if (first < 0xD800 || first > 0xDBFF || isEnd) return first;
    var second = thisStr.charCodeAt(position + 1);
    if (second < 0xDC00 || second > 0xDFFF) return first;
    return ((first - 0xD800) * 1024) + (second - 0xDC00) + 0x10000;
};
===
String.prototype.repeat
String.prototype.repeat = String.prototype.repeat || (function() {
    var repeat = function(s, times) {
        if (times < 1) return '';
        if (times % 2) return repeat(s, times - 1) + s;
        var half = repeat(s, times / 2);
        return half + half;
    };

    return function(times) {
        var thisStr = String(this);
        if (times < 0 || times === Infinity) {
            throw new RangeError('Invalid String#repeat value');
        }
        return repeat(thisStr, times);
    };
})();
===
String.prototype.startsWith
String.prototype.startsWith = String.prototype.startsWith || function(searchStr) {
    var thisStr = String(this);
    if (String.prototype.toString.call(searchStr) === '[object RegExp]') throw new TypeError('Cannot call method "startsWith" with a regex');
    searchStr = String(searchStr);
    var startArg = arguments.length > 1 ? arguments[1] : undefined;
    var start = Math.max(startArg, 0);
    return thisStr.slice(start, start + searchStr.length) === searchStr;
};
===
String.prototype.endsWith
String.prototype.endsWith = String.prototype.endsWith || function(searchStr) {
    var thisStr = String(this);
    if (String.prototype.toString.call(searchStr) === '[object RegExp]') throw new TypeError('Cannot call method "endsWith" with a regex');
    searchStr = String(searchStr);
    var thisLen = thisStr.length;
    var posArg = arguments.length > 1 ? arguments[1] : undefined;
    var pos = posArg === undefined ? thisLen : Number(posArg);
    var end = Math.min(Math.max(pos, 0), thisLen);
    return thisStr.slice(end - searchStr.length, end) === searchStr;
};
===
String.prototype.contains
String.prototype.contains = String.prototype.contains || function(searchString) {
    var position = arguments.length > 1 ? arguments[1] : undefined;
    // Somehow this trick makes method 100% compat with the spec.
    return String.prototype.indexOf.call(this, searchString, position) !== -1;
};
===
Number.isFinite
Number.isFinite = Number.isFinite || function(value) {
    return typeof value === 'number' && isFinite(value);
};
===
Number.isInteger
Number.isInteger = Number.isInteger || function(value) {
    function toInteger(value) {
        var number = +value;
        if (isNaN(number)) return 0;
        if (number === 0 || !isFinite(number)) return number;
        return (number < 0 ? -1 : 1) * Math.floor(Math.abs(number));
    }
    return typeof value === 'number' &&
    !Number.isNaN(value) &&
    Number.isFinite(value) &&
    toInteger(value) === value;
};
===
Number.isSafeInteger
Number.isSafeInteger = Number.isSafeInteger || function(value) {
    var isInteger = function (value) {}
        function toInteger(value) {
            var number = +value;
            if (isNaN(number)) return 0;
            if (number === 0 || !isFinite(number)) return number;
            return (number < 0 ? -1 : 1) * Math.floor(Math.abs(number));
        }
        return typeof value === 'number' &&
        !Number.isNaN(value) &&
        Number.isFinite(value) &&
        toInteger(value) === value;
    };
    return isInteger(value) && Math.abs(value) <= Math.pow(2, 53) - 1;
};
===
Number.isNaN
Number.isNaN = Number.isNaN || function(value) {
    return typeof value === 'number' && isNaN(value);
};
===
Number.toInteger
Number.toInteger = Number.toInteger || function(value) {
    var number = +value;
    if (isNaN(number)) return 0;
    if (number === 0 || !isFinite(number)) return number;
    return (number < 0 ? -1 : 1) * Math.floor(Math.abs(number));
};
===
Number.EPSILON
Number.EPSILON = Number.EPSILON || 2.220446049250313e-16;
===
Number.MIN_SAFE_INTEGER
Number.MIN_SAFE_INTEGER = Number.MIN_SAFE_INTEGER || 1 - Math.pow(2, 53);
===
Number.MAX_SAFE_INTEGER
Number.MAX_SAFE_INTEGER = Number.MAX_SAFE_INTEGER || Math.pow(2, 53) - 1;
===
Math.clz32
Math.clz32 = Math.clz32 || function(value) {
    // See https://bugs.ecmascript.org/show_bug.cgi?id=2465
    value = Number(value);
    if (isNaN(value)) return NaN;
    var number = value >>> 0;
    if (number === 0) {
      return 32;
    }
    return 32 - (number).toString(2).length;
};
===
Math.imul
Math.imul = Math.imul || function(x, y) {
    // taken from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/imul
    var ah  = (x >>> 16) & 0xffff;
    var al = x & 0xffff;
    var bh  = (y >>> 16) & 0xffff;
    var bl = y & 0xffff;
    // the shift by 0 fixes the sign on the high part
    // the final |0 converts the unsigned value into a signed value
    return ((al * bl) + (((ah * bl + al * bh) << 16) >>> 0)|0);
};
===
Math.sign = Math.sign || function(value) {
    var number = +value;
    if (number === 0) return number;
    if (isNaN(number)) return number;
    return number < 0 ? -1 : 1;
};
===
Math.log10
Math.log10 = Math.log10 || function(value) {
    return Math.log(value) * Math.LOG10E;
};
===
Math.log2
Math.log2 = Math.log2 || function(value) {
    return Math.log(value) * Math.LOG2E;
};
===
Math.log1p
Math.log1p = Math.log1p || function(value) {
    value = Number(value);
    if (value < -1 || isNaN(value)) return NaN;
    if (value === 0 || value === Infinity) return value;
    if (value === -1) return -Infinity;
    var result = 0;
    var n = 50;

    if (value < 0 || value > 1) return Math.log(1 + value);
    for (var i = 1; i < n; i++) {
        if ((i % 2) === 0) {
            result -= Math.pow(value, i) / i;
        } else {
            result += Math.pow(value, i) / i;
        }
    }

    return result;
};
===
Math.expm1
Math.expm1 = Math.expm1 || function(value) {
    value = Number(value);
    if (value === -Infinity) return -1;
    if (!isFinite(value) || value === 0) return value;
    return Math.exp(value) - 1;
};
===
Math.cosh
Math.cosh = Math.cosh || function(value) {
    value = Number(value);
    if (value === 0) return 1; // +0 or -0
    if (isNaN(value)) return NaN;
    if (!isFinite(value)) return Infinity;
    if (value < 0) value = -value;
    if (value > 21) return Math.exp(value) / 2;
    return (Math.exp(value) + Math.exp(-value)) / 2;
};
===
Math.sinh
Math.sinh = Math.sinh || function(value) {
    value = Number(value);
    if (!isFinite(value) || value === 0) return value;
    return (Math.exp(value) - Math.exp(-value)) / 2;
};
===
Math.tanh
Math.tanh = Math.tanh || function(value) {
    value = Number(value);
    if (Number.isNaN(value) || value === 0) return value;
    if (value === Infinity) return 1;
    if (value === -Infinity) return -1;
    return (Math.exp(value) - Math.exp(-value)) / (Math.exp(value) + Math.exp(-value));
};
===
Math.acosh
Math.acosh = Math.acosh || function(value) {
    value = Number(value);
    if (isNaN(value) || value < 1) return NaN;
    if (value === 1) return 0;
    if (value === Infinity) return value;
    return Math.log(value + Math.sqrt(value * value - 1));
};
===
Math.asinh
Math.asinh = Math.asinh || function(value) {
    value = Number(value);
    if (value === 0 || !isFinite(value)) {
        return value;
    }
    return value < 0 ? -Math.asinh(-value) : Math.log(value + Math.sqrt(value * value + 1));
};
===
Math.atanh
Math.atanh = Math.atanh || function(value) {
    value = Number(value);
    if (Number.isNaN(value) || value < -1 || value > 1) {
        return NaN;
    }
    if (value === -1) return -Infinity;
    if (value === 1) return Infinity;
    if (value === 0) return value;
    return 0.5 * Math.log((1 + value) / (1 - value));
};
===
Math.hypot
Math.hypot = Math.hypot || function(x, y) {
    var anyNaN = false;
    var allZero = true;
    var anyInfinity = false;
    var numbers = [];
    Array.prototype.every.call(arguments, function(arg) {
        var num = Number(arg);
        if (isNaN(num)) anyNaN = true;
        else if (num === Infinity || num === -Infinity) anyInfinity = true;
        else if (num !== 0) allZero = false;
        if (anyInfinity) {
            return false;
        } else if (!anyNaN) {
            numbers.push(Math.abs(num));
        }
        return true;
    });
    if (anyInfinity) return Infinity;
    if (anyNaN) return NaN;
    if (allZero) return 0;

    numbers.sort(function (a, b) { return b - a; });
    var largest = numbers[0];
    var divided = numbers.map(function (number) { return number / largest; });
    var sum = divided.reduce(function (sum, number) { return sum += number * number; }, 0);
    return largest * Math.sqrt(sum);
};
===
Math.trunc
Math.trunc = Math.trunc || function(value) {
    var number = Number(value);
    return number < 0 ? -Math.floor(-number) : Math.floor(number);
};
===
Math.fround
Math.fround = Math.fround || function(x) {
    if (x === 0 || x === Infinity || x === -Infinity || Number.isNaN(x)) {
        return x;
    }
    var num = Number(x);
    return numberConversion.toFloat32(num);
};
===
Math.cbrt
Math.cbrt = Math.cbrt || function(value) {
    value = Number(value);
    if (value === 0) return value;
    var negate = value < 0, result;
    if (negate) value = -value;
    result = Math.pow(value, 1/3);
    return negate ? -result : result;
};
===