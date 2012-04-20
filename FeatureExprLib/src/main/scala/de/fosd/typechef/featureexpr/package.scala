package de.fosd.typechef

import ref.WeakReference

package object featureexpr {
    type FeatureExprValue = FeatureExprTree[Long]

    //class NotReference[+T <: AnyRef](x: T) extends SoftReference(x) {}
    //val NotRef = SoftRef
    class NotReference[+T <: AnyRef](x: T) extends WeakReference(x) {}

//    val NotRef = WeakRef
}

