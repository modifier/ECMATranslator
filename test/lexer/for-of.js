function test (o) {
    for (var v of o) {
        console.log(v);
    }
}

test ({
    key: "value",
    key2: "value2",
    key3: {
        "www": "zzz"
    }
});