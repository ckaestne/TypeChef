package de.fosd.typechef.parser.c

/**
 * case class to have straightforward hashvalue and equals
 */
case class CTypeContext(val types: Set[String] = Set()) {
    def addType(newtype: String) = new CTypeContext(types + newtype)
    def knowsType(typename: String) = types contains typename
    def join(that: CTypeContext) = new CTypeContext(this.types ++ that.types)
}