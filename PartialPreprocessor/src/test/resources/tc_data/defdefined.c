//// expect cpp failure
//
////according to specifications, this should result in an error
////we explicitly allow this, because we generate code like this
////during parcial preprocessing. that is we are less strict.
//
//#define x
//#define y defined(x)
//#define z(a) a
//#if z(y)
//ja
//#else
//nein
//#endif
