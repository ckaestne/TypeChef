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
    def warning_pointer_sign: Boolean

    // implements a simple version of:
    // INT30-C     high    likely    high    P9    L2
    // INT32-C 	   high    likely    high    P9    L2
    // https://www.securecoding.cert.org/confluence/display/seccode/INT30-C.+Ensure+that+unsigned+integer+operations+do+not+wrap
    // https://www.securecoding.cert.org/confluence/display/seccode/INT32-C.+Ensure+that+operations+on+signed+integers+do+not+result+in+overflow
    def warning_potential_integer_overflow: Boolean

    // implements a simple version of:
    // INT31-C    high    probable    high    P6    L2
    // https://www.securecoding.cert.org/confluence/display/seccode/INT31-C.+Ensure+that+integer+conversions+do+not+result+in+lost+or+misinterpreted+data
    def warning_implicit_coercion: Boolean

    // DCL16-C. Use "L," not "l," to indicate a long value
    // https://www.securecoding.cert.org/confluence/pages/viewpage.action?pageId=19759250
    def warning_long_designator: Boolean
}

trait COptionProvider {
    //default used only in tests, overwritten by the frontend
    protected def opts: ICTypeSysOptions = LinuxDefaultOptions
}

trait LinuxDefaultOptions extends ICTypeSysOptions {
    def warning_pointer_sign = false
    def warning_potential_integer_overflow = false
    def warning_implicit_coercion = false
    def warning_long_designator = false
}

object LinuxDefaultOptions extends LinuxDefaultOptions
