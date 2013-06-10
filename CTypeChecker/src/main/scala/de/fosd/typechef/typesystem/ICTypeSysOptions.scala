package de.fosd.typechef.typesystem


/**
 * Options for the type system (what is supposed to be checked, what should issue errors or warnings)
 *
 * This is supposed to be compatible (at least partially) with the settings of GCC
 *
 * By no, options for Linux are hard coded and only documented as far as already implemented by the type system

LINUX
-Wall -Wundef
-Wstrict-prototypes

-Werror-implicit-function-declaration

-Wno-sign-compare


-Wdeclaration-after-statement
-Wno-pointer-sign

BUSYBOX
 -Wall -Wshadow
 -Wwrite-strings
 -Wundef
 -Wstrict-prototypes
 -Wunused -Wunused-parameter -Wunused-function -Wunused-value
 -Wmissing-prototypes -Wmissing-declarations
 -Wdeclaration-after-statement
 -Wold-style-definition
 -Os
 */

trait ICTypeSysOptions {

    //    def getFeatureModelTypeSystem: FeatureModel

    //-Wno-pointer-sign, -Wpointer-sign
    def warning_pointer_sign: Boolean = false

    // implements a simple version of:
    // INT30-C     high    likely    high    P9    L2
    // INT32-C 	   high    likely    high    P9    L2
    // https://www.securecoding.cert.org/confluence/display/seccode/INT30-C.+Ensure+that+unsigned+integer+operations+do+not+wrap
    // https://www.securecoding.cert.org/confluence/display/seccode/INT32-C.+Ensure+that+operations+on+signed+integers+do+not+result+in+overflow
    // type system
    def warning_potential_integer_overflow: Boolean = false

    // implements a simple version of:
    // INT31-C    high    probable    high    P6    L2
    // https://www.securecoding.cert.org/confluence/display/seccode/INT31-C.+Ensure+that+integer+conversions+do+not+result+in+lost+or+misinterpreted+data
    // type system, but structurally restricted locations
    def warning_implicit_coercion: Boolean = false

    // DCL16-C. Use "L," not "l," to indicate a long value
    // https://www.securecoding.cert.org/confluence/pages/viewpage.action?pageId=19759250
    // structural
    def warning_long_designator: Boolean = false

    // DCL31-C. Declare identifiers before using them
    // https://www.securecoding.cert.org/confluence/display/seccode/DCL31-C.+Declare+identifiers+before+using+them
    // structural (the type part is conservatively always producing errors)
    def warning_implicit_identifier: Boolean = false

    // DCL36-C. Do not declare an identifier with conflicting linkage classifications
    // https://www.securecoding.cert.org/confluence/display/seccode/DCL36-C.+Do+not+declare+an+identifier+with+conflicting+linkage+classifications
    // type system
    def warning_conflicting_linkage: Boolean = false
}

trait COptionProvider {
    //default used only in tests, overwritten by the frontend
    protected def opts: ICTypeSysOptions = LinuxDefaultOptions
}

trait LinuxDefaultOptions extends ICTypeSysOptions {
    //all false, no need to override any defaults
}

object LinuxDefaultOptions extends LinuxDefaultOptions
