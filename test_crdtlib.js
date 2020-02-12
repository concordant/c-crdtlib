var crdtlib = require('crdtlib');
var crdtlib = crdtlib.crdtlib;

var dcid = new crdtlib.utils.DCId('dcid');
var ts1 = new crdtlib.utils.Timestamp(dcid, 5);
var ts2 = new crdtlib.utils.Timestamp(dcid, 6);
console.log(ts1, ts2);
console.log(ts1.compareTo_1muy9d$(ts2));
console.log(ts2.compareTo_1muy9d$(ts1));
console.log(ts2.compareTo_1muy9d$(ts2));
