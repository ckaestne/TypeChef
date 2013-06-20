package de.fosd.typechef.error

/**
 * helper stuff
 */
object Severity extends Enumeration {
    type Severity = Value
    //Type-System crashes (e.g. unimplemented parts)
    val Crash = Value("Critical")

    // severe errors during lookup of id
    val IdLookupError = Value("Id-Lookup Error")

    // severe errors during lookup of fields
    val FieldLookupError = Value("Field-Lookup Error")

    // severe errors during lookup of id
    val TypeLookupError = Value("Type-Lookup Error")

    // severe errors during lookup of id
    val RedeclarationError = Value("Redeclaration Error")

    // other severe type errors
    val OtherError = Value("Error")

    val Warning = Value("Warning")


    //results of static analysis; may contain large numbers of false positives
    val SecurityWarning = Value("Security Warning")
}