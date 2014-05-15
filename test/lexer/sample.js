var duck = 5;

expression();

function Duck (arg1 = 15, arg2 = (function () { expression(); }))
{
    console.log("I like ducks");
}

var foo = function shit () {
    console.log("I call myself from the inside!" + shit());

    if (something)
        var x = 5;
    else
        var z = (function () { self(); })();

    for (var i in z) { }

    if (true) { }
};

var duck = 1, crack = 15, snack = new Duck("meat", 15);

duck = 8;

crack = {
    "test1": duck + 100500,
    "test2": hits / 255 + 55 - 255,
    "test3": allelujah[3],
    "test4": [literal1, "literal2", foo3],
    "test5": [],
    "test6": {
        "key": "value"
    },
    "test7": new hits()[15],
    "test8": [,],
    "test9": {},
    "test10": hits()[11],
    "test11": (11 + 5) / 2,
    "test12": new hits(arg1, arg2)[new shit(255 - 314 / 155)]
};