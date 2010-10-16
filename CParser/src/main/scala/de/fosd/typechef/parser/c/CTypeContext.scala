package de.fosd.typechef.parser.c

class CTypeContext(val types: Set[String] = Set()) {
    def addType(newtype: String) = new CTypeContext(types + newtype)
    def knowsType(typename: String) = types contains typename
}