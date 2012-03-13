package de.fosd.typechef.featureexpr

// this class represents the configuration of a product
// and config holds all defined features
class Configuration(val config: List[DefinedExternal]) { }

object EmptyConfiguration extends Configuration(List())
