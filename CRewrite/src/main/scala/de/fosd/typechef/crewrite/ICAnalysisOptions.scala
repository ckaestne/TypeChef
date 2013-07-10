package de.fosd.typechef.crewrite

/**
 * Options for the static analysis (intraprocedural and interprocedural (to be done) dataflow analyses) part of TypeChef
 */

trait ICAnalysisOptions {

    // --doublefree
    // https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
    // MEM31-C     high     probable     medium     P12     L1
    def warning_double_free = false


    // --xfree
    // https://www.securecoding.cert.org/confluence/display/seccode/MEM34-C.+Only+free+memory+allocated+dynamically
    // MEM34-C     high     likely       medium     P18     L1
    def warning_xfree = false


    // --uninitializedmemory
    // https://www.securecoding.cert.org/confluence/display/seccode/EXP33-C.+Do+not+reference+uninitialized+memory
    // EXP33-C     high     probable     medium     P12     L1
    def warning_uninitialized_memory = false


    // --casetermination
    // https://www.securecoding.cert.org/confluence/display/seccode/MSC17-C.+Finish+every+set+of+statements+associated+with+a+case+label+with+a+break+statement
    // MSC17-C     medium    likely      low        P18     L1
    def warning_case_termination = false


    // --danglingswitchcode
    // https://www.securecoding.cert.org/confluence/display/seccode/MSC35-C.+Do+not+include+any+executable+statements+inside+a+switch+statement+before+the+first+case+label
    // MSC35-C     medium    unlikely    medium     P4      L3
    def warning_dangling_switch_code = false

}
