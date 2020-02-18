package crdtlib

import me.ntrrgc.tsGenerator.TypeScriptGenerator
import crdtlib.utils.*
import crdtlib.crdt.*


fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            DCId::class,
			      Environment::class,
			      SimpleEnvironment::class,
			      Timestamp::class,
			      UnexpectedTypeException::class,
			      VersionVector::class,
            AddWinsMap::class,
			      DeltaCRDT::class,
            Delta::class,
			      LWWRegister::class,
            UpdateOperation::class,
            EmptyDelta::class
        )/*,
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
        )*/
    ).definitionsText)
}
