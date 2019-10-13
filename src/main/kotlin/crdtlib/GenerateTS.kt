package crdtlib

import me.ntrrgc.tsGenerator.TypeScriptGenerator
import crdtlib.utils.*
import crdtlib.deltacrdt.*


fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            DCId::class,
			Environment::class,
			SimpleEnvironment::class,
			Timestamp::class,
			VersionVector::class,
			UnexpectedTypeException::class,
			DeltaCRDT::class,
			DeltaDeltaCRDT::class,
			FullStateDelta::class,
			LWWRegister::class,
			MapDeltaCRDT::class,
			RemRecResetMap::class
        )/*,
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
        )*/
    ).definitionsText)
}
