if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'p2pclient-js'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'p2pclient-js'.");
}
if (typeof this['kotlin-test'] === 'undefined') {
  throw new Error("Error loading module 'p2pclient-js'. Its dependency 'kotlin-test' was not found. Please, check whether 'kotlin-test' is loaded prior to 'p2pclient-js'.");
}
this['p2pclient-js'] = function (_, Kotlin, $module$kotlin_test) {
  'use strict';
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var test = $module$kotlin_test.kotlin.test.test;
  var suite = $module$kotlin_test.kotlin.test.suite;
  var toString = Kotlin.toString;
  var assertEquals = $module$kotlin_test.kotlin.test.assertEquals_3m0tl5$;
  var indexOf = Kotlin.kotlin.text.indexOf_8eortd$;
  var getKClass = Kotlin.getKClass;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var Exception = Kotlin.kotlin.Exception;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  UnexpectedTypeException.prototype = Object.create(Exception.prototype);
  UnexpectedTypeException.prototype.constructor = UnexpectedTypeException;
  function main(args) {
    var message = 'Starting 2 !!';
    println(message);
  }
  function main_0(args) {
    var idDc1 = new DCId('dc1');
    var dc1 = new SimpleEnvironment(idDc1);
    var ts0 = dc1.getNewTimestamp();
    var reg1 = new LWWMap();
    var idDc2 = new DCId('dc2');
    var dc2 = new SimpleEnvironment(idDc2);
    var reg2 = new LWWMap();
    var key = 'A1';
    var ts2 = dc2.getNewTimestamp();
    var op2 = reg2.put_gk9piw$(key, 'nuno', ts2, dc2.getCurrentState());
    op2.exec_11rb$(reg2);
    dc2.updateStateTS_kivsjo$(ts2);
    println('DC1 : ' + reg1.doGet_11rb$(key));
    println('DC2 : ' + reg2.doGet_11rb$(key));
    var ts1 = dc1.getNewTimestamp();
    var op1 = reg1.put_gk9piw$(key, 'valter', ts1, dc1.getCurrentState());
    op1.exec_11rb$(reg1);
    dc1.updateStateTS_kivsjo$(ts1);
    println('DC1 : ' + reg1.doGet_11rb$(key));
    println('DC2 : ' + reg2.doGet_11rb$(key));
    op1.exec_11rb$(reg2);
    dc2.updateStateTS_kivsjo$(ts1);
    println('DC1 : ' + reg1.doGet_11rb$(key));
    println('DC2 : ' + reg2.doGet_11rb$(key));
    op2.exec_11rb$(reg1);
    dc1.updateStateTS_kivsjo$(ts2);
    println('DC1 : ' + reg1.doGet_11rb$(key));
    println('DC2 : ' + reg2.doGet_11rb$(key));
    var ts3 = dc2.getNewTimestamp();
    var op3 = reg1.delete_83qlj8$(key, ts3, dc2.getCurrentState());
    op3.exec_11rb$(reg2);
    dc2.updateStateTS_kivsjo$(ts3);
    println('DC1 : ' + reg1.doGet_11rb$(key));
    println('DC2 : ' + reg2.doGet_11rb$(key));
  }
  function CRDT() {
  }
  CRDT.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CRDT',
    interfaces: []
  };
  function LWWMap() {
    this.entries_0 = LinkedHashMap_init();
  }
  LWWMap.prototype.put_gk9piw$ = function (key, value, ts, vv) {
    var tmp$;
    var reg = (tmp$ = this.entries_0.get_11rb$(key)) != null ? tmp$ : new LWWRegister(value, ts);
    return new LWWMap$PutOp(key, reg.assign_83qlj8$(value, ts, vv));
  };
  LWWMap.prototype.delete_83qlj8$ = function (key, ts, vv) {
    return this.put_gk9piw$(key, null, ts, vv);
  };
  LWWMap.prototype.get_11rb$ = function (key) {
    return new LWWMap$GetOp(key);
  };
  LWWMap.prototype.doPut_72sx2q$ = function (key, op) {
    var $receiver = this.entries_0;
    var tmp$;
    var value = $receiver.get_11rb$(key);
    if (value == null) {
      var answer = LWWRegister_init(op);
      $receiver.put_xwzc9p$(key, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var reg = tmp$;
    return op.exec_11rb$(reg);
  };
  LWWMap.prototype.doGet_11rb$ = function (key) {
    var reg = this.entries_0.get_11rb$(key);
    if (reg == null)
      return null;
    else {
      return reg.doGet();
    }
  };
  function LWWMap$PutOp(opKey, op) {
    this.opKey_8be2vx$ = opKey;
    this.op_8be2vx$ = op;
  }
  LWWMap$PutOp.prototype.exec_11rb$ = function (obj) {
    return obj.doPut_72sx2q$(this.opKey_8be2vx$, this.op_8be2vx$);
  };
  LWWMap$PutOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PutOp',
    interfaces: [Operation]
  };
  function LWWMap$GetOp(key) {
    this.key_8be2vx$ = key;
  }
  LWWMap$GetOp.prototype.exec_11rb$ = function (obj) {
    return obj.doGet_11rb$(this.key_8be2vx$);
  };
  LWWMap$GetOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GetOp',
    interfaces: [ReadOperation]
  };
  LWWMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LWWMap',
    interfaces: []
  };
  function LWWRegister(value, valueTs) {
    this.value = value;
    this.valueTs = valueTs;
  }
  LWWRegister.prototype.get = function () {
    return new LWWRegister$GetOp();
  };
  LWWRegister.prototype.doGet = function () {
    return this.value;
  };
  LWWRegister.prototype.assign_83qlj8$ = function (v, ts, vv) {
    return new LWWRegister$AssignOp(v, ts);
  };
  LWWRegister.prototype.doAssign_cpqo75$ = function (v, ts) {
    if (this.valueTs.smallerThan_kivsjo$(ts)) {
      this.valueTs = ts;
      this.value = v;
    }
    return true;
  };
  function LWWRegister$AssignOp(opValue, opTs) {
    this.opValue_8be2vx$ = opValue;
    this.opTs_8be2vx$ = opTs;
  }
  LWWRegister$AssignOp.prototype.exec_11rb$ = function (obj) {
    return obj.doAssign_cpqo75$(this.opValue_8be2vx$, this.opTs_8be2vx$);
  };
  LWWRegister$AssignOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AssignOp',
    interfaces: [Delta, Operation]
  };
  function LWWRegister$GetOp() {
  }
  LWWRegister$GetOp.prototype.exec_11rb$ = function (obj) {
    return obj.doGet();
  };
  LWWRegister$GetOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GetOp',
    interfaces: [ReadOperation]
  };
  LWWRegister.prototype.generateDelta_5nn9t5$ = function (vv) {
    if (vv.includesTS_kivsjo$(this.valueTs))
      return new EmptyDelta();
    else {
      return new LWWRegister$AssignOp(this.value, this.valueTs);
    }
  };
  LWWRegister.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LWWRegister',
    interfaces: []
  };
  function LWWRegister_init(op, $this) {
    $this = $this || Object.create(LWWRegister.prototype);
    LWWRegister.call($this, op.opValue_8be2vx$, op.opTs_8be2vx$);
    return $this;
  }
  function LWWRegisterTest() {
  }
  LWWRegisterTest.prototype.test1 = function () {
    var idDc1 = new DCId('dc1');
    var dc1 = new SimpleEnvironment(idDc1);
    var ts0 = dc1.getNewTimestamp();
    var reg1 = new LWWRegister('nuno', ts0);
    dc1.updateStateTS_kivsjo$(ts0);
    var idDc2 = new DCId('dc2');
    var dc2 = new SimpleEnvironment(idDc2);
    var reg2 = new LWWRegister('nuno', ts0);
    dc2.updateStateTS_kivsjo$(ts0);
    var ts2 = dc2.getNewTimestamp();
    var op2 = reg2.assign_83qlj8$('annette', ts2, dc2.getCurrentState());
    op2.exec_11rb$(reg2);
    dc2.updateStateTS_kivsjo$(ts2);
    println('DC1 : ' + reg1.doGet());
    println('DC2 : ' + reg2.doGet());
    var ts1 = dc1.getNewTimestamp();
    var op1 = reg1.assign_83qlj8$('valter', ts1, dc1.getCurrentState());
    op1.exec_11rb$(reg1);
    dc1.updateStateTS_kivsjo$(ts1);
    println('DC1 : ' + reg1.doGet());
    println('DC2 : ' + reg2.doGet());
    op1.exec_11rb$(reg2);
    dc2.updateStateTS_kivsjo$(ts1);
    println('DC1 : ' + reg1.doGet());
    println('DC2 : ' + reg2.doGet());
    op2.exec_11rb$(reg1);
    dc1.updateStateTS_kivsjo$(ts2);
    println('DC1 : ' + reg1.doGet());
    println('DC2 : ' + reg2.doGet());
  };
  LWWRegisterTest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LWWRegisterTest',
    interfaces: []
  };
  function Operation() {
  }
  Operation.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Operation',
    interfaces: []
  };
  function ReadOperation() {
  }
  ReadOperation.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ReadOperation',
    interfaces: []
  };
  function Delta() {
  }
  Delta.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Delta',
    interfaces: []
  };
  function EmptyDelta() {
  }
  EmptyDelta.prototype.exec_11rb$ = function (obj) {
    return true;
  };
  EmptyDelta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EmptyDelta',
    interfaces: [Delta]
  };
  function DeltaCRDT() {
  }
  DeltaCRDT.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DeltaCRDT',
    interfaces: []
  };
  function DeltaDeltaCRDT() {
  }
  DeltaDeltaCRDT.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DeltaDeltaCRDT',
    interfaces: []
  };
  function FullStateDelta(value) {
    this.value = value;
  }
  FullStateDelta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FullStateDelta',
    interfaces: [DeltaDeltaCRDT]
  };
  FullStateDelta.prototype.component1 = function () {
    return this.value;
  };
  FullStateDelta.prototype.copy_s21zfs$ = function (value) {
    return new FullStateDelta(value === void 0 ? this.value : value);
  };
  FullStateDelta.prototype.toString = function () {
    return 'FullStateDelta(value=' + Kotlin.toString(this.value) + ')';
  };
  FullStateDelta.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.value) | 0;
    return result;
  };
  FullStateDelta.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.value, other.value))));
  };
  function LWWRegister_0(value, valueTs) {
    this.value = value;
    this.valueTs = valueTs;
  }
  LWWRegister_0.prototype.get = function () {
    return this.value;
  };
  LWWRegister_0.prototype.assign_sqvq2x$ = function (v, ts) {
    if (this.valueTs.smallerThan_kivsjo$(ts)) {
      this.valueTs = ts;
      this.value = v;
    }
  };
  LWWRegister_0.prototype.getCRDT = function () {
    return this;
  };
  LWWRegister_0.prototype.getDelta_5nn9t5$ = function (vv) {
    return new FullStateDelta(this);
  };
  LWWRegister_0.prototype.mergeState_i07eez$ = function (state0) {
    if (Kotlin.isType(state0, DeltaCRDT)) {
      var state = state0.getCRDT();
      if (Kotlin.isType(state, LWWRegister_0)) {
        if (this.valueTs.smallerThan_kivsjo$(state.valueTs)) {
          this.valueTs = state.valueTs;
          this.value = state.value;
        }
      }
       else
        throw new UnexpectedTypeException('Type not expected on merge of LWWRegister : expected ' + toString(this.value) + ' was ' + toString(state));
    }
     else
      throw new UnexpectedTypeException('Type not expected on merge of LWWRegister : ' + toString(Kotlin.getKClassFromExpression(state0)));
  };
  LWWRegister_0.prototype.mergeDelta_5dynid$ = function (state0) {
    if (Kotlin.isType(state0, FullStateDelta)) {
      this.mergeState_i07eez$(state0.value);
    }
     else
      throw new UnexpectedTypeException('Type not expected on merge of LWWRegister : ' + toString(Kotlin.getKClassFromExpression(state0)));
  };
  LWWRegister_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LWWRegister',
    interfaces: [DeltaCRDT]
  };
  function LWWRegister_init_0(state, $this) {
    $this = $this || Object.create(LWWRegister_0.prototype);
    LWWRegister_0.call($this, state.value, state.valueTs);
    return $this;
  }
  function LWWRegisterTest_0() {
  }
  LWWRegisterTest_0.prototype.test1 = function () {
    var idDc1 = new DCId('dc1');
    var dc1 = new SimpleEnvironment(idDc1);
    var ts0 = dc1.getNewTimestamp();
    var reg1 = new LWWRegister_0('nuno', ts0);
    dc1.updateStateTS_kivsjo$(ts0);
    var idDc2 = new DCId('dc2');
    var dc2 = new SimpleEnvironment(idDc2);
    var reg2 = new LWWRegister_0('nuno', ts0);
    dc2.updateStateTS_kivsjo$(ts0);
    assertEquals(reg1.get(), 'nuno');
    assertEquals(reg2.get(), 'nuno');
    var ts2 = dc2.getNewTimestamp();
    reg2.assign_sqvq2x$('annette', ts2);
    dc2.updateStateTS_kivsjo$(ts2);
    assertEquals(reg1.get(), 'nuno');
    assertEquals(reg2.get(), 'annette');
    var ts1 = dc1.getNewTimestamp();
    reg1.assign_sqvq2x$('valter', ts1);
    dc1.updateStateTS_kivsjo$(ts1);
    assertEquals(reg1.get(), 'valter');
    assertEquals(reg2.get(), 'annette');
    reg2.mergeState_i07eez$(reg1);
    dc2.updateStateVV_5nn9t5$(dc1.getCurrentState());
    assertEquals(reg1.get(), 'valter');
    assertEquals(reg2.get(), 'annette');
    reg1.mergeState_i07eez$(reg2);
    dc1.updateStateVV_5nn9t5$(dc2.getCurrentState());
    assertEquals(reg1.get(), 'annette');
    assertEquals(reg2.get(), 'annette');
    println('All tests OK !!');
  };
  LWWRegisterTest_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LWWRegisterTest',
    interfaces: []
  };
  function MapDeltaCRDT() {
  }
  MapDeltaCRDT.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MapDeltaCRDT',
    interfaces: [DeltaCRDT]
  };
  function RemRecResetMap() {
    this.entries_0 = LinkedHashMap_init();
  }
  RemRecResetMap.prototype.get_hqvncd$ = function (key, type) {
    var tmp$;
    var pos = indexOf(key, 46);
    if (pos === -1) {
      var fkey = key + ':' + type.simpleName;
      return this.entries_0.get_11rb$(fkey);
    }
     else {
      var fkey_0 = key.substring(0, pos);
      var pos2 = indexOf(fkey_0, 58);
      if (pos2 === -1)
        fkey_0 = fkey_0 + ':' + Kotlin.getKClassFromExpression(this).simpleName;
      tmp$ = this.entries_0.get_11rb$(fkey_0);
      if (tmp$ == null) {
        return null;
      }
      var el = tmp$;
      if (Kotlin.isType(el, MapDeltaCRDT)) {
        var startIndex = pos + 1 | 0;
        return el.get_hqvncd$(key.substring(startIndex), type);
      }
       else
        throw new UnexpectedTypeException('Expecting map CRDT in key of : ' + toString(Kotlin.getKClassFromExpression(el)));
    }
  };
  RemRecResetMap.prototype.put_odle4o$ = function (key, value, ts, vv) {
    var pos = indexOf(key, 46);
    if (pos === -1) {
      var pos2 = indexOf(key, 58);
      var fkey = key + ':' + Kotlin.getKClassFromExpression(value).simpleName;
      this.entries_0.put_xwzc9p$(fkey, value);
    }
     else {
      var fkey_0 = key.substring(0, pos);
      var pos2_0 = indexOf(fkey_0, 58);
      if (pos2_0 === -1)
        fkey_0 = fkey_0 + ':' + Kotlin.getKClassFromExpression(this).simpleName;
      var $receiver = this.entries_0;
      var key_0 = fkey_0;
      var tmp$;
      var value_0 = $receiver.get_11rb$(key_0);
      if (value_0 == null) {
        var answer = new RemRecResetMap();
        $receiver.put_xwzc9p$(key_0, answer);
        tmp$ = answer;
      }
       else {
        tmp$ = value_0;
      }
      var el = tmp$;
      if (!Kotlin.isType(el, MapDeltaCRDT))
        throw new UnexpectedTypeException('Expecting map CRDT in key of : ' + toString(Kotlin.getKClassFromExpression(el)));
      else
        el.put_odle4o$(key, value, ts, vv);
    }
  };
  RemRecResetMap.prototype.put_p3v4e0$ = function (key, value, env) {
    var vv = env.getCurrentState();
    var ts = env.getNewTimestamp();
    this.put_odle4o$(key, value, ts, vv);
    env.updateStateTS_kivsjo$(ts);
  };
  RemRecResetMap.prototype.remove_a5h8x4$ = function (key, type, ts, vv) {
  };
  RemRecResetMap.prototype.remove_v56rmg$ = function (key, type, env) {
    var vv = env.getCurrentState();
    var ts = env.getNewTimestamp();
    this.remove_a5h8x4$(key, type, ts, vv);
    env.updateStateTS_kivsjo$(ts);
  };
  RemRecResetMap.prototype.getCRDT = function () {
    return this;
  };
  RemRecResetMap.prototype.getDelta_5nn9t5$ = function (vv) {
    return new FullStateDelta(this);
  };
  RemRecResetMap.prototype.mergeState_i07eez$ = function (state0) {
    var tmp$;
    var state = state0.getCRDT();
    if (Kotlin.isType(state, RemRecResetMap)) {
      tmp$ = state.entries_0.entries.iterator();
      while (tmp$.hasNext()) {
        var entR = tmp$.next();
        var elL = this.entries_0.get_11rb$(entR.key);
        if (elL == null) {
          var $receiver = this.entries_0;
          var key = entR.key;
          var value = entR.value;
          $receiver.put_xwzc9p$(key, value);
        }
         else {
          elL.mergeState_i07eez$(entR.value);
        }
      }
    }
     else if (state == null)
      throw new UnexpectedTypeException('Type not expected on merge of RemRecResetMap : null ');
    else
      throw new UnexpectedTypeException('Type not expected on merge of RemRecResetMap : ' + toString(Kotlin.getKClassFromExpression(state)));
  };
  RemRecResetMap.prototype.mergeDelta_5dynid$ = function (state0) {
    if (Kotlin.isType(state0, FullStateDelta)) {
      this.mergeState_i07eez$(state0.value);
    }
     else
      throw new UnexpectedTypeException('Type not expected on merge of RemRecResetMap : ' + toString(Kotlin.getKClassFromExpression(state0)));
  };
  RemRecResetMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemRecResetMap',
    interfaces: [MapDeltaCRDT]
  };
  function RemRecResetMapTest() {
  }
  RemRecResetMapTest.prototype.test1 = function () {
    var idDc1 = new DCId('dc1');
    var dc1 = new SimpleEnvironment(idDc1);
    var map1 = new RemRecResetMap();
    var ts1 = dc1.getNewTimestamp();
    var reg1 = new LWWRegister_0('nuno', ts1);
    map1.put_odle4o$('key1', reg1, ts1, dc1.getCurrentState());
    dc1.updateStateTS_kivsjo$(ts1);
    var idDc2 = new DCId('dc2');
    var dc2 = new SimpleEnvironment(idDc2);
    var map2 = new RemRecResetMap();
    var ts2 = dc2.getNewTimestamp();
    var reg2 = new LWWRegister_0('valter', ts2);
    map2.put_odle4o$('key2', reg2, ts2, dc2.getCurrentState());
    dc2.updateStateTS_kivsjo$(ts2);
    map2.mergeDelta_5dynid$(map1.getDelta_5nn9t5$(dc2.getCurrentState()));
    println('map tests');
    println(map2);
    var v1 = map2.get_hqvncd$('key1', getKClass(LWWRegister_0));
    if (Kotlin.isType(v1, LWWRegister_0))
      println(v1.get());
    v1 = map2.get_hqvncd$('key2', getKClass(LWWRegister_0));
    if (Kotlin.isType(v1, LWWRegister_0))
      println(v1.get());
  };
  RemRecResetMapTest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemRecResetMapTest',
    interfaces: []
  };
  function DCId(name) {
    this.name = name;
  }
  DCId.prototype.compareTo_68ikto$ = function (id) {
    return Kotlin.compareTo(this.name, id.name);
  };
  DCId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DCId',
    interfaces: []
  };
  DCId.prototype.component1 = function () {
    return this.name;
  };
  DCId.prototype.copy_61zpoe$ = function (name) {
    return new DCId(name === void 0 ? this.name : name);
  };
  DCId.prototype.toString = function () {
    return 'DCId(name=' + Kotlin.toString(this.name) + ')';
  };
  DCId.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    return result;
  };
  DCId.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.name, other.name))));
  };
  function Environment() {
  }
  Environment.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Environment',
    interfaces: []
  };
  function SimpleEnvironment(id) {
    this.id = id;
    this.curState_0 = VersionVector_init();
    this.lastTs_0 = 0;
  }
  SimpleEnvironment.prototype.getNewTimestamp = function () {
    this.lastTs_0 = this.curState_0.maxVal() + 1 | 0;
    return new Timestamp(this.id, this.lastTs_0);
  };
  SimpleEnvironment.prototype.getCurrentState = function () {
    return this.curState_0.copy();
  };
  SimpleEnvironment.prototype.updateStateTS_kivsjo$ = function (ts) {
    this.curState_0.addTS_kivsjo$(ts);
  };
  SimpleEnvironment.prototype.updateStateVV_5nn9t5$ = function (vv) {
    this.curState_0.pointWiseMax_5nn9t5$(vv);
  };
  SimpleEnvironment.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimpleEnvironment',
    interfaces: [Environment]
  };
  function Timestamp(id, cnt) {
    this.id = id;
    this.cnt = cnt;
  }
  Timestamp.prototype.compareTo_kivsjo$ = function (otherTs) {
    if (this.cnt < otherTs.cnt)
      return -1;
    else if (this.cnt > otherTs.cnt)
      return 1;
    else
      return this.id.compareTo_68ikto$(otherTs.id);
  };
  Timestamp.prototype.smallerThan_kivsjo$ = function (otherTs) {
    return this.compareTo_kivsjo$(otherTs) < 0;
  };
  Timestamp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Timestamp',
    interfaces: []
  };
  Timestamp.prototype.component1 = function () {
    return this.id;
  };
  Timestamp.prototype.component2 = function () {
    return this.cnt;
  };
  Timestamp.prototype.copy_yv4zby$ = function (id, cnt) {
    return new Timestamp(id === void 0 ? this.id : id, cnt === void 0 ? this.cnt : cnt);
  };
  Timestamp.prototype.toString = function () {
    return 'Timestamp(id=' + Kotlin.toString(this.id) + (', cnt=' + Kotlin.toString(this.cnt)) + ')';
  };
  Timestamp.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    result = result * 31 + Kotlin.hashCode(this.cnt) | 0;
    return result;
  };
  Timestamp.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.id, other.id) && Kotlin.equals(this.cnt, other.cnt)))));
  };
  function UnexpectedTypeException(message) {
    Exception_init(message, this);
    this.name = 'UnexpectedTypeException';
  }
  UnexpectedTypeException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnexpectedTypeException',
    interfaces: [Exception]
  };
  function VersionVector() {
    this.entries_0 = LinkedHashMap_init();
  }
  VersionVector.prototype.maxVal = function () {
    var tmp$;
    var $receiver = this.entries_0.values;
    var maxBy$result;
    maxBy$break: do {
      var iterator = $receiver.iterator();
      if (!iterator.hasNext()) {
        maxBy$result = null;
        break maxBy$break;
      }
      var maxElem = iterator.next();
      if (!iterator.hasNext()) {
        maxBy$result = maxElem;
        break maxBy$break;
      }
      var maxValue = abs(maxElem);
      do {
        var e = iterator.next();
        var v = abs(e);
        if (Kotlin.compareTo(maxValue, v) < 0) {
          maxElem = e;
          maxValue = v;
        }
      }
       while (iterator.hasNext());
      maxBy$result = maxElem;
    }
     while (false);
    return (tmp$ = maxBy$result) != null ? tmp$ : 0;
  };
  VersionVector.prototype.addTS_kivsjo$ = function (ts) {
    var $receiver = this.entries_0;
    var key = ts.id;
    var tmp$;
    var curCnt = (tmp$ = $receiver.get_11rb$(key)) != null ? tmp$ : -1;
    if (curCnt < ts.cnt) {
      var $receiver_0 = this.entries_0;
      var key_0 = ts.id;
      var value = ts.cnt;
      $receiver_0.put_xwzc9p$(key_0, value);
    }
  };
  VersionVector.prototype.includesTS_kivsjo$ = function (ts) {
    var $receiver = this.entries_0;
    var key = ts.id;
    var tmp$;
    var cnt = (tmp$ = $receiver.get_11rb$(key)) != null ? tmp$ : -1;
    return cnt >= ts.cnt;
  };
  VersionVector.prototype.pointWiseMax_5nn9t5$ = function (vv) {
    var tmp$;
    tmp$ = vv.entries_0.entries.iterator();
    while (tmp$.hasNext()) {
      var tmp$_0 = tmp$.next();
      var k = tmp$_0.key;
      var v = tmp$_0.value;
      var tmp$_1;
      if (((tmp$_1 = this.entries_0.get_11rb$(k)) != null ? tmp$_1 : 0) < v)
        this.entries_0.put_xwzc9p$(k, v);
    }
  };
  VersionVector.prototype.copy = function () {
    return VersionVector_init_0(this);
  };
  VersionVector.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VersionVector',
    interfaces: []
  };
  function VersionVector_init($this) {
    $this = $this || Object.create(VersionVector.prototype);
    VersionVector.call($this);
    return $this;
  }
  function VersionVector_init_0(vv, $this) {
    $this = $this || Object.create(VersionVector.prototype);
    VersionVector.call($this);
    $this.entries_0.putAll_a2k3zr$(vv.entries_0);
    return $this;
  }
  _.main_kand9s$ = main;
  var package$p2pclient = _.p2pclient || (_.p2pclient = {});
  var package$adhoctest = package$p2pclient.adhoctest || (package$p2pclient.adhoctest = {});
  package$adhoctest.main_kand9s$ = main_0;
  var package$crdt = package$p2pclient.crdt || (package$p2pclient.crdt = {});
  package$crdt.CRDT = CRDT;
  LWWMap.PutOp = LWWMap$PutOp;
  LWWMap.GetOp = LWWMap$GetOp;
  package$crdt.LWWMap = LWWMap;
  LWWRegister.AssignOp = LWWRegister$AssignOp;
  LWWRegister.GetOp = LWWRegister$GetOp;
  package$crdt.LWWRegister_init_qt9hjp$ = LWWRegister_init;
  package$crdt.LWWRegister = LWWRegister;
  package$crdt.LWWRegisterTest = LWWRegisterTest;
  package$crdt.Operation = Operation;
  package$crdt.ReadOperation = ReadOperation;
  package$crdt.Delta = Delta;
  package$crdt.EmptyDelta = EmptyDelta;
  var package$deltacrdt = package$p2pclient.deltacrdt || (package$p2pclient.deltacrdt = {});
  package$deltacrdt.DeltaCRDT = DeltaCRDT;
  package$deltacrdt.DeltaDeltaCRDT = DeltaDeltaCRDT;
  package$deltacrdt.FullStateDelta = FullStateDelta;
  package$deltacrdt.LWWRegister_init_hjx8cd$ = LWWRegister_init_0;
  package$deltacrdt.LWWRegister = LWWRegister_0;
  package$deltacrdt.LWWRegisterTest = LWWRegisterTest_0;
  package$deltacrdt.MapDeltaCRDT = MapDeltaCRDT;
  package$deltacrdt.RemRecResetMap = RemRecResetMap;
  var package$deltacrdt_0 = _.deltacrdt || (_.deltacrdt = {});
  package$deltacrdt_0.RemRecResetMapTest = RemRecResetMapTest;
  var package$utils = package$p2pclient.utils || (package$p2pclient.utils = {});
  package$utils.DCId = DCId;
  package$utils.Environment = Environment;
  package$utils.SimpleEnvironment = SimpleEnvironment;
  package$utils.Timestamp = Timestamp;
  var package$utils_0 = _.utils || (_.utils = {});
  package$utils_0.UnexpectedTypeException = UnexpectedTypeException;
  package$utils.VersionVector_init = VersionVector_init;
  package$utils.VersionVector_init_5nn9t5$ = VersionVector_init_0;
  package$utils.VersionVector = VersionVector;
  suite('p2pclient.crdt', false, function () {
    suite('LWWRegisterTest', false, function () {
      test('test1', false, function () {
        return (new LWWRegisterTest()).test1();
      });
    });
  });
  suite('p2pclient.deltacrdt', false, function () {
    suite('LWWRegisterTest', false, function () {
      test('test1', false, function () {
        return (new LWWRegisterTest_0()).test1();
      });
    });
  });
  suite('deltacrdt', false, function () {
    suite('RemRecResetMapTest', false, function () {
      test('test1', false, function () {
        return (new RemRecResetMapTest()).test1();
      });
    });
  });
  main([]);
  Kotlin.defineModule('p2pclient-js', _);
  return _;
}(typeof this['p2pclient-js'] === 'undefined' ? {} : this['p2pclient-js'], kotlin, this['kotlin-test']);
