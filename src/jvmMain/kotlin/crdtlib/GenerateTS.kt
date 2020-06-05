package crdtlib

import crdtlib.crdt.*
import crdtlib.utils.*
import me.ntrrgc.tsGenerator.TypeScriptGenerator


fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            DCId::class,
            Environment::class,
            SimpleEnvironment::class,
            Timestamp::class,
            UnexpectedTypeException::class,
            VersionVector::class,
            DeltaCRDT::class,
            Delta::class,
            Immutable::class,
            LWWMap::class,
            LWWRegister::class,
            MVRegister::class,
            PNCounter::class,
            UpdateOperation::class,
            EmptyDelta::class
        )/*,
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
        )*/
    ).definitionsText)
}
