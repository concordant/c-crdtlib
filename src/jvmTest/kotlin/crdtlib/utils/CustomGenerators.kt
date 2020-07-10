package crdtlib.utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

val timestampArb = arb { rs ->
    val dcids = dcidArb.values(rs)
    val cnts = Arb.int().values(rs)
    dcids.zip(cnts).map { (dcids, cnt) -> Timestamp(dcids.value, cnt.value) }
}

val dcidArb = arb { rs ->
    val names = Arb.string().values(rs)
    names.map { name -> DCId(name.value)}
}

val versionVectorArb = arb { rs ->
    val tss = Arb.list(timestampArb, range = 0..10).values(rs)
    tss.map { ts ->
        val vv = VersionVector()
        ts.value.map {t -> vv.addTS(t)}
        vv
    }
}

//val versionVectorArb2 = arb { rs ->
//    val maps = Arb.map(dcidArb, Arb.int(), maxSize = 10).values(rs)
//    maps.map { m ->
//        val vv = VersionVector()
//        m.value.entries.map { e -> vv.addTS(Timestamp(e.key, e.value))}
//        vv}
//}