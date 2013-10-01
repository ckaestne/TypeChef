package de.fosd.typechef.crewrite

/**
 * Options for the static analysis (intraprocedural and interprocedural (to be done) dataflow analyses) part of TypeChef
 */

trait ICAnalysisOptions {

    // for convenience: group all dataflow analyses
    def staticanalyses = {
        typechecksa || warning_case_termination || warning_dangling_switch_code
    }

    // for convenience: groups all dataflow analyses that need typechecking information!
    def typechecksa = {
        warning_double_free || warning_xfree || warning_uninitialized_memory || warning_cfg_in_non_void_func || warning_stdlib_func_return || warning_dead_store
    }


    // -A doublefree
    // https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
    // MEM31-C     high     probable     medium     P12     L1
    def warning_double_free = false


    // -A xfree
    // https://www.securecoding.cert.org/confluence/display/seccode/MEM34-C.+Only+free+memory+allocated+dynamically
    // MEM34-C     high     likely       medium     P18     L1
    def warning_xfree = false


    // -A uninitializedmemory
    // https://www.securecoding.cert.org/confluence/display/seccode/EXP33-C.+Do+not+reference+uninitialized+memory
    // EXP33-C     high     probable     medium     P12     L1
    def warning_uninitialized_memory = false


    // -A casetermination
    // https://www.securecoding.cert.org/confluence/display/seccode/MSC17-C.+Finish+every+set+of+statements+associated+with+a+case+label+with+a+break+statement
    // MSC17-C     medium    likely      low        P18     L1
    def warning_case_termination = false


    // -A danglingswitchcode
    // https://www.securecoding.cert.org/confluence/display/seccode/MSC35-C.+Do+not+include+any+executable+statements+inside+a+switch+statement+before+the+first+case+label
    // MSC35-C     medium    unlikely    medium     P4      L3
    def warning_dangling_switch_code = false


    // -A cfginnonvoidfunc
    // https://www.securecoding.cert.org/confluence/display/seccode/MSC37-C.+Ensure+that+control+never+reaches+the+end+of+a+non-void+function
    // MSC37-C     high      unlikely    low        P9      L2
    def warning_cfg_in_non_void_func = false


    // -A stdlibfuncreturn
    // https://www.securecoding.cert.org/confluence/display/seccode/ERR33-C.+Detect+and+handle+standard+library+errors
    // ERR33-C     high      likely      medium     P18     L1
    def warning_stdlib_func_return = false

    // -A deadstore
    // see: http://en.wikipedia.org/wiki/Dead_store and "clang -cc1 -analyze -analyzer-checker-help"
    def warning_dead_store = false
}

trait CAnalysisOptionProvider {
    protected def opts: ICAnalysisOptions = CAnalysisDefaultOptions
}

trait CAnalysisDefaultOptions extends ICAnalysisOptions

object CAnalysisDefaultOptions extends CAnalysisDefaultOptions
