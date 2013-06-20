package de.fosd.typechef.typesystem.linker

//special features for signatures, such as weak exports

trait CFlag

object WeakExport extends CFlag {
    override def toString = "WeakExport"
}

//object ExternInlined extends CFlag

object CFlagOps {
    def mergeOnImports(a: Set[CFlag], b: Set[CFlag]) = a ++ b

}