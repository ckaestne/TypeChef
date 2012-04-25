package de.fosd.typechef.featureexpr

import ref.WeakReference

package object sat {
    class NotReference[+T <: AnyRef](x: T) extends WeakReference(x) {}

    val NotRef = WeakRef
}

