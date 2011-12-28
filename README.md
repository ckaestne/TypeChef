TypeChef
========

TypeChef is a research project with the goal of type checking ifdef variability in C code with the target of
type checking the entire Linux kernel with several thousand features (or configuration options).

Instead of type checking each variant for each feature combination in isolation, TypeChef parses the
entire source code containing all variability in a variability-aware fashion without preprocessing.
The resulting abstract syntax tree contains the variability in form of choice nodes. Eventually, a
variability-aware type system performs type checking on these trees.

TypeChef detects syntax and type errors in all possible feature combinations. TypeChef was originally short for *Type* *Ch*ecking Ifd*ef* Variability.


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

For simple experimentation, try our online version at [http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/online/](http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/online/)

To install TypeChef, build it from scratch using [sbt](http://code.google.com/p/simple-build-tool/). Install
*git* and download and compile the code as follows

    git clone git://github.com/ckaestne/TypeChef.git
    cd TypeChef
    java -Xmx512M -Xss10m -jar sbt-launch.jar clean update compile

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

IntelliJ Idea users may run `sbt idea` to create corresponding project and classpath information for the IDE. Similar sbt plugins for Eclipse are available, but we have not tried or integrated them yet. In general avoid to set the classpath in IDEs manually, but let sbt generate corresponding files for you.

Evaluation
----------

Details on our Linux analysis have been published in an OOPSLA paper (see below).
You find additional information on that evaluation at [http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/](http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/)


Credits
-------

The project was only possible with fruitful collaboration of many researchers.

* [Christian Kaestner](http://www.uni-marburg.de/fb12/ps/team/kaestner) (Philipps University Marburg, project leader)
* [Paolo G. Giarrusso](http://www.informatik.uni-marburg.de/~pgiarrusso/) (Philipps University Marburg)
* [Tillmann Rendel](http://www.informatik.uni-marburg.de/~rendel/) (Philipps University Marburg)
* [Sebastian Erdweg](http://www.informatik.uni-marburg.de/~seba/) (Philipps University Marburg)
* [Klaus Ostermann](http://www.informatik.uni-marburg.de/~kos/) (Philipps University Marburg)
* [Thorsten Berger](http://bis.uni-leipzig.de/ThorstenBerger) (University of Leipzig)
* Alex Eifler (Philipps University Marburg)
* David Kraus (Philipps University Marburg)
* Andy Kenner (Metop Research Institute, Magdeburg)
* Steffen Haase (Metop Research Institute, Magdeburg)

The variability-aware lexer is implemented on top of [jcpp](http://www.anarres.org/projects/jcpp/), an implementation of the
C preprocessor in Java.

For reasoning about propositional formulas, we use the SAT solver [sat4j](http://www.sat4j.org/).

The GNU C parser is based on an [ANTLR grammar for GNU C](http://www.antlr.org/grammar/cgram).

The Java parser is based on a grammar that can be traced back to the Java 1.5 grammar in the
[JavaCC repository](http://java.net/projects/javacc/downloads/directory/contrib/grammars).

For convenience we include [sbt](http://code.google.com/p/simple-build-tool/) in the repository.

We would further more thank for their contributions and discussions
* [Steven She](http://www.eng.uwaterloo.ca/~shshe/) (University of Waterloo)
* [Krzysztof Czarnecki](http://gsd.uwaterloo.ca/kczarnec) (University of Waterloo)
* [Sven Apel](http://www.infosun.fim.uni-passau.de/spl/apel/) (University of Passau)
* [Shriram Krishnamurthi](http://www.cs.brown.edu/~sk/) (Brown University)
* Thomas Leich (Metop Research Institute, Magdeburg)

This work is supported in part by the European Research Council, grant #203099.

Contributing
------------

Fork the project, write bug reports, contact us, .... We are open for collaborations and extensions and other scenarios.


Publications
------------

An indepth discussion of the parsing approach and our experience with parsing Linux was published at OOPSLA:

> Christian Kaestner, Paolo G. Giarrusso, Tillmann Rendel, Sebastian Erdweg, Klaus Ostermann, and Thorsten Berger. [Variability-Aware Parsing in the Presence of Lexical Macros and Conditional Compilation](http://www.informatik.uni-marburg.de/~kaestner/oopsla11_typechef.pdf). In Proceedings of the 26th Annual ACM SIGPLAN Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA) (Portland, OR), New York, NY, October 2011. ACM Press.

An early overview of the project with a very preliminary implementation was published at

> Andy Kenner, Christian Kästner, Steffen Haase, and Thomas Leich. [TypeChef: Toward Type Checking #ifdef Variability in C](http://www.informatik.uni-marburg.de/~kaestner/FOSD10-typechef.pdf). In Proceedings of the Second Workshop on Feature-Oriented Software Development (FOSD) (Eindhoven, The Netherlands), pages 25-32, New York, NY, USA, October 2010. ACM Press.

A more detailed discussion of the variability-aware lexer (or partial preprocessor) was presented at

> Christian Kästner, Paolo G. Giarrusso, and Klaus Ostermann. [Partial Preprocessing C Code for Variability Analysis](http://www.informatik.uni-marburg.de/~kaestner/vamos11.pdf). In Proceedings of the Fifth International Workshop on Variability Modelling of Software-intensive Systems (VaMoS) (Namur, Belgium), pages 137-140, New York, NY, USA, January 2011. ACM Press.



License
-------

TypeChef is published as open source under GPL 3.0. See [LICENSE](TypeChef/blob/master/LICENSE).
