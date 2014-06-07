var testFoo = function () {
    var arrayed = Array.from(arguments);
    console.log(arguments instanceof Array);
    console.log(arrayed instanceof Array);
    console.log(arrayed);
};

testFoo(1, "shit", function here (i) { here(i-1); });