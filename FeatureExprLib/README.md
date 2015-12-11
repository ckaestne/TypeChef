# FeatureExprLib

The feature expression library is a reasonably complete and stable implementation
of propositional formulas integrated with a SAT solver and BDDs.
A `FeatureExpr` represents a propositional formula over a set of
names.

`FeatureExprFactory` is the entry point into the library. It can be configured
to use either BDDs or SAT. Both implementations should not be intermixed.

For historic reasons `FeatureExprFactory.createDefinedExternal` is the
function to create new named variables. Boolean operations work as 
expected, e.g., `a.and(b).not` or just `(a and b).not` in Scala.
Functions as `isSatisfiable` or `isTautology` allow to reason about
satisfiability.

The SAT implementation uses some lightweight optimization or normalization
of expressions and heavy internal caching, but the memory can be 
reclaimed by the garbage collector. The BDD library leaks memory; the cache is
never cleaned. Which one is faster may be an empirical questions; BDDs typically
work well and quickly until the expressions become too large. SAT may be
slower, but scales better.

FeatureModels can be used to represent constant external conditions against
 which the formulas are checked. They support large formulas in .dimacs file
 quite efficiently. When BDDs are used, SAT solvers are additionally used for 
 handling feature models.

The library was not designed for concurrent use, but has been rudimentarily been
retrofitted. No guarantees though.