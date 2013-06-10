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

    def warning_potential_integer_overflow: Boolean

    def warning_implicit_coercion: Boolean

}

trait COptionProvider {
    //default used only in tests, overwritten by the frontend
    protected def opts: ICTypeSysOptions = LinuxDefaultOptions
}

trait LinuxDefaultOptions extends ICTypeSysOptions {
    def warning_pointer_sign = false
    def warning_potential_integer_overflow = false
    def warning_implicit_coercion = false
}

object LinuxDefaultOptions extends LinuxDefaultOptions
