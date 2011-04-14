TypeChef
========

TypeChef is a research project with the goal of type checking ifdef variability in C code with the target of
type checking the entire Linux kernel with several thousand features (or configuration options).

Instead of type checking each variant for each feature combination in isolation, TypeChef parses the
entire source code containing all variability in a variability-aware fashion without preprocessing.
The resulting abstract syntax tree contains the variability in form of choice nodes. Eventually, a
variability-aware type system performs type checking on these trees.

TypeChef detects syntax and type errors in all possible feature combinations.


Architecture and Subprojects
----------------------------

The TypeChef project contains of four main components and several helper libraries.

* A **variability-aware lexer** (also called partial preprocessor; subproject *PartialPreprocessor*) that
  reads unpreprocessed code and produces a conditional token stream. The variability-aware lexer is responsible
  for resolving macros and file inclusions and for normalizing `#ifdef` conditions

* A **variability-aware parser framework** provides parser combinators to build variability-aware parsers
  (subproject *ParserFramework*).

* The **variability-aware parsers** for GNU C and Java (subprojects *CParser* and *JavaParser*) use the parser
  framework to build parsers for the corresponding languages. The parsers read a conditional token stream and
  produce abstract syntax trees with corresponding choice nodes.

* A **variability-aware type system** (subproject *CTypeChecker*) eventually checks variability in the abstract
  syntax tree. This will be supported by variability-aware linker checks.

* All tasks are supported by a library for feature expressions and reasoning about feature expressions
  (subproject *FeatureExprLib*). Internally the library uses the SAT solver *sat4j*.

* Evaluation specific parts, mostly for Linux are provided in the frontend subproject *LinuxAnalysis*.

Installation and Usage
----------------------

For simple experimentation, try our online version at http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/online/

To install TypeChef, build it from scratch using [http://code.google.com/p/simple-build-tool/](sbt). Install
*sbt* and *git* and download and compile the code as follows

    git clone git://github.com/ckaestne/TypeChef.git
    cd TypeChef
    sbt compile

TypeChef can be run normally in the Java VM (use `sbt package` to build jar files) or from within `sbt` which
simplifies path management significantly. Example:

    sbt
    > project CParser
    > run filename

Useful frontend classes are:

> org.anarres.cpp.Main                  -- the variability-aware lexer (or partial preprocessor)  
> de.fosd.typechef.parser.c.ParserMain  -- the GNU C parser (calls the lexer internally)  
> de.fosd.typechef.typesystem.Main      -- type system for C (calls the parser internally)  
> de.fosd.typechef.linux.WebFrontend    -- output of our online version

or the specific ones for the Linux evaluation:

> de.fosd.typechef.linux.LinuxParser  
> de.fosd.typechef.linux.LinuxPreprocessorFrontend

Evaluation
----------

Details on our Linux analysis are currently under review for publication.
You find additional information on that evaluation at http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/


Credits
-------

The project was only possible with fruitful collaboration of many researchers.

* [http://www.uni-marburg.de/fb12/ps/team/kaestner](Christian Kaestner) (Philipps University Marburg, project leader)
* [http://www.informatik.uni-marburg.de/~pgiarrusso/](Paolo G. Giarrusso) (Philipps University Marburg)
* [http://www.informatik.uni-marburg.de/~rendel/](Tillmann Rendel) (Philipps University Marburg)
* [http://www.informatik.uni-marburg.de/~seba/](Sebastian Erdweg) (Philipps University Marburg)
* [http://www.informatik.uni-marburg.de/~kos/](Klaus Ostermann) (Philipps University Marburg)
* [http://bis.uni-leipzig.de/ThorstenBerger](Thorsten Berger) (University of Leipzig)
* Andy Kenner (Metop Research Institute, Magdeburg)
* Steffen Haase (Metop Research Institute, Magdeburg)

The variability-aware lexer is implemented on top of jcpp, an implementation of the
C preprocessor in Java: http://www.anarres.org/projects/jcpp/

For reasoning about propositional formulas, we use the SAT solver sat4j: http://www.sat4j.org/

The GNU C parser is based on an ANTLR grammar: http://www.antlr.org/grammar/cgram

The Java parser is based on a grammar that can be traced back to the Java 1.5 grammar in the
JavaCC repository: http://java.net/projects/javacc/downloads/directory/contrib/grammars

We would further more thank for their contributions and discussions
* [http://www.eng.uwaterloo.ca/~shshe/](Steven She) (University of Waterloo)
* [http://gsd.uwaterloo.ca/kczarnec](Krzysztof Czarnecki) (University of Waterloo)
* [http://www.infosun.fim.uni-passau.de/spl/apel/](Sven Apel) (University of Passau)
* [http://www.cs.brown.edu/~sk/](Shriram Krishnamurthi) (Brown University)

This work is supported in part by the European Research Council, grant #203099.

