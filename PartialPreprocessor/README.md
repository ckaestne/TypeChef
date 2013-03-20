# TypeChef Lexer

This subproject contains the Variability-Aware Lexer (or Partial Preprocessor) of TypeChef.
The Lexer includes header files and expands macros, but preserves variability from
```#ifdef``` directives.


The Lexer was implemented on top of ```jcpp```, a Java implementation of the C preprocessor
but heavily modified in the process. http://www.anarres.org/projects/jcpp/

Recently, we experimented with providing the Lexer from Xtc/SuperC as alternative with
the same interface, see http://cs.nyu.edu/rgrimm/xtc/ for the original and see 
https://github.com/ckaestne/xtc for our patches. Use with parameter ```--xtc```

The Xtc/SuperC lexer improves 
over a couple of bugs that are still in the TypeChef lexer (see https://github.com/ckaestne/TypeChef/issues)
and that would likely require a complete rewrite. Those bugs are only triggered by
less common Macro/Include/Ifdef combinations, but occur for example in a few files in Linux.
We have not tested the Xtc integration very much, use on your own risk.
