package de.fosd.typechef

import de.fosd.typechef.parser.c.Id

package object typesystem {
    type UseDeclMap = java.util.IdentityHashMap[Id, List[Id]]
}
