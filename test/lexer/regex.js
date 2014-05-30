function foo () {
    var reg = /\w+@\w+\.\w{2,4}/;
    var str = "To contact me please write at \"evgeniyamiraslanov@sperasoft.com\".";
    var str2 = "I try to emulate string escaping I use \regexes\ inside the \"string\".";
    if (reg.test(str))
    {
        return str2.match(reg);
    }
    return {};
}