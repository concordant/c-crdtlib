package crdtlib.utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

val timestampArb = arbitrary { rs ->
    Timestamp(clientuidArb.next(rs), Arb.int().next(rs))
}

val timestampNonMaxArb = arbitrary { rs ->
    Timestamp(
        clientuidArb.next(rs),
        Arb.int(Int.MIN_VALUE, Int.MAX_VALUE - 1).next(rs)
    )
}

val clientuidArb = arbitrary { rs ->
    ClientUId(Arb.string(1..2).next(rs))
}

val versionVectorArb = arbitrary { rs ->
    val vv = VersionVector()
    for (t in Arb.list(timestampArb, range = 0..10).next(rs))
        vv.update(t)
    vv
}

val versionVectorNonMaxArb = arbitrary { rs ->
    val vv = VersionVector()
    for (t in Arb.list(timestampNonMaxArb, range = 0..10).next(rs))
        vv.update(t)
    vv
}

val simpleEnvironmentArb = arbitrary { rs ->
    SimpleEnvironment(clientuidArb.next(rs))
}
