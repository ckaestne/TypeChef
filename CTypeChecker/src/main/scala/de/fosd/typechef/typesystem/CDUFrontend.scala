package de.fosd.typechef.typesystem


// this trait is a hook into the typesystem to preserve typing informations
// of declarations and usages
// the trait basically provides two maps: declaration -> usage and usages -> declarations
// for all identifiers that occur in a translation unit
// to do so typed elements are passed during typechecking to CDeclUse which
// stores the required information
class CDUFrontend extends CDeclUse {


}