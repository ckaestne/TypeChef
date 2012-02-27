Variation Programming with TypeChef
=============

Variation programming is the idea to implement data structures and operations on them in a variability-aware fashion.
A variable may have multiple alternative values and operations will perform on all alternatives and return a new
variational result. TypeChef contains libraries for precisely this purpose.

The term *variation programming* was coined *Martin Erwig* and *Eric Walkingshaw* in their GTTSE'11 tutorial. For a
more thorough introduction see their papers "Variation Programming with the Choice Calculus" and "The Choice Calculus:
A Representation for Software Variation."

TypeChef is designed for parsing and type checking of code with #ifdef variability. In this process, we need to deal
with variational data structures. For example, after parsing, the AST contains variability. During type checking, an
expression can have alternative types depending on the configuration. For this purpose, TypeChef contains ready to use
libraries for variation programming in Scala. The design differs in some design choices from the Choice Calculus, but
follows similar ideas.




Feature Expressions
-----

The subproject FeatureExprLib of TypeChef provides facilities to reason about propositional formulas. You create a
 literal with `FeatureExpr.createDefinedExternal(name)`. `FeatureExpr.True` and `FeatureExpr.False` can be used as well.

Feature expressions can be combined with the usual operators and, not, or, implies. For example:

    val fa = FeatureExpr.createDefinedExternal("a")
    val fb = FeatureExpr.createDefinedExternal("b")
    val fx = (fa or fb) implies fa

You can check whether expressions are satisfiable, contraditions or tautologies the obvious way:

    x.isSatisfiable()

Variational Data Structures
-----

The core utils for variational data structures are defined in subproject ConditionalLib. A data structure can support
 alternative values with `Conditional` or optional values with `Opt`.

    val x: Conditional[Int]
    val y: Opt[Int]

Conditional and optional data structures depend always on feature expressions to specify under which condition which
value is used. Optional entries are simple:

    val x = Opt(fa, 3)
    val y = Opt(FeatureExpr.True, 4)

In this case, `x` has value 3 only if feature `a` is selected. Feature `y` has value 4 in all configurations.

Optional entries are especially common in lists:

    val l: List[Opt[Int]] = List(Opt(FeatureExpr.True, 1), Opt(fb, 2), Opt(fa, 3), Opt(fa.not, 5))

Conditional data structures have a value in all configurations. They are constructed with `One` and `Choice`:

    val c: Conditional[Int] = One(1)
    val d: Conditional[Int] = Choice(fa, One(1), One(2))
    val e: Conditional[Int] = Choice(fa, Choice(fb, One(1), One(2)), One(3))

In this example, `c` always has value 1; `d` has value 1 if "a" is selected and 2 otherwise; `e` has value 1
if both "a" and "b" are selected, 2 if "a" and not "b", and 3 if not "a".

Also more complex feature expressions can be used in `Choice` or `Opt` elements.

Computing with Variational Data Structures
--------

Computations are usually performed as map or fold over the data structure. For example, to add 1 to all values
 of d, we simply write

    val dd = d.map(_ + 1)
    > Choice(fa, One(2), One(3))

Furthermore, common operations apply to List[Opt[T]] lists, such as folds. Here is an example of summing all values
in a list with optional entries or counting the entries:

    ConditionalLib.conditionalFoldRight[Int, Int](l, One(0), _ + _)
    > Choice(!a,Choice(b,One(8),One(6)),Choice(b,One(6),One(4)))

    ConditionalLib.conditionalFoldRight[Int, Int](l, One(0), (a,b) => b + 1)
    > Choice(b,One(3),One(2))

There are many helper functions in the library. For example, `simplify` removes all unreachable branches from a
conditional expression and collapses choices between equal values; `lastEntry` returns the last entry from a
List[Opt[T]] list, which of course depends on the configuration and is again a conditional result.

    Choice(fa, Choice(fa, One(1), One(2)), One(3)).simplify
    > Choice(a,One(1),One(3))

    Choice(fa, Choice(fb, One(1), One(1)), One(3)).simplify
    > Choice(a,One(1),One(3))

    ConditionalLib.lastEntry(l)
    > Choice(!a,One(Some(5)),One(Some(3)))

Simply explore the library...


