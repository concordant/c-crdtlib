package crdtlib.utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

val timestampArb = arb { rs ->
    val dcuids = dcuidArb.values(rs)
    val cnts = Arb.int().values(rs)
    dcuids.zip(cnts).map { (dcuids, cnt) -> Timestamp(dcuids.value, cnt.value) }
}

val timestampNonMaxArb = arb { rs ->
    val dcuids = dcuidArb.values(rs)
    val cnts = Arb.int(Int.MIN_VALUE, Int.MAX_VALUE-1).values(rs)
    dcuids.zip(cnts).map { (dcuids, cnt) -> Timestamp(dcuids.value, cnt.value) }
}


val dcuidArb = arb { rs ->
    val names = Arb.string(1..2).values(rs)
    names.map { name -> DCUId(name.value)}
}

val versionVectorArb = arb { rs ->
    val tss = Arb.list(timestampArb, range = 0..10).values(rs)
    tss.map { ts ->
        val vv = VersionVector()
        ts.value.map {t -> vv.update(t)}
        vv
    }
}

val versionVectorNonMaxArb = arb { rs ->
    val tss = Arb.list(timestampNonMaxArb, range = 0..10).values(rs)
    tss.map { ts ->
        val vv = VersionVector()
        ts.value.map {t -> vv.update(t)}
        vv
    }
}


val simpleEnvironmentArb = arb { rs ->
    val dcuids = dcuidArb.values(rs)
    dcuids.map { dcuid -> SimpleEnvironment(dcuid.value)}
}
