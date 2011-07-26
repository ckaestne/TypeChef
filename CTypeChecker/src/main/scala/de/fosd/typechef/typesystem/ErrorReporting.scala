package de.fosd.typechef.typesystem

/**
 * infrastructure for error reporting
 */

trait ErrorReporting {

    trait ErrorMsg {
        def toString: String
    }

    val errors: List[ErrorMsg]


}