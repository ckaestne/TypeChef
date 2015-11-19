TypeChef
========

[![Build Status](https://travis-ci.org/ckaestne/TypeChef.svg?branch=master)](https://travis-ci.org/ckaestne/TypeChef)
[![Coverage](https://coveralls.io/repos/ckaestne/TypeChef/badge.png?branch=master)](https://coveralls.io/github/ckaestne/TypeChef)


TypeChef is a research project with the goal of analyzing 
ifdef variability in C code with the goal of finding
variability-induced bugs in large-scale real-world systems,
such as the Linux kernel with several thousand 
features (or configuration options).

Instead of analyzing each variant for each feature 
combination in isolation, TypeChef parses the
entire source code containing all variability in a 
variability-aware fashion without preprocessing.
The resulting abstract syntax tree contains the 
variability in form of choice nodes. Eventually, a
variability-aware type system performs type checking 
on these trees, variability-aware data-flow analysis
performs data-flow analysis and so forth.

TypeChef was started with the goal of building a type
system for C code with compile-time configurations.
TypeChef was originally short for
*Type Checking Ifdef Variability*.
Over time it has grown into an infrastructure of all
kinds of analyses.
In all cases, the goal is to detect errors in all possible 
feature combinations, without resorting to a brute-force approach. 
It also evolved into a basis for a sound transformation and refactoring engine
([Hercules](https://github.com/joliebig/Hercules) and
[Morpheus](https://github.com/joliebig/Morpheus)) and as 
an import mechanism for [mbeddr](https://github.com/mbeddr).


<a href="http://ckaestne.github.com/TypeChef/typechef-poster.png"><img alt="TypeChef Poster" src="http://ckaestne.github.com/TypeChef/typechef-poster-small.png" /></a>

Architecture and Subprojects
----------------------------

The TypeChef project contains of four main components 
and several helper libraries.

* A library for reasoning about **feature expressions** (subproject *FeatureExprLib*). 
  The library allows to easily express and reason about expressions
  in propositional logic. It supports also parsing feature expressions
  and loading entire feature models (in textual format or a .dimacs file).
  For reasoning, internally both BDDs and SAT solvers are used which
  allows to scale reasoning even to feature models the size of the Linux kernel.
  The library is stable, has a simple and convenient syntax, and can be (and is) reused in 
  very different contexts. It also works well with Java code.

* A library of **conditional data structures** for variational programming 
  (subproject *ConditionalLib*) with several useful operations on
  conditional structures. Used heavily in all other subprojects.
  For a short introduction see [VariationalProgramming.md](https://github.com/ckaestne/TypeChef/blob/master/VariationalProgramming.md).

* A **variability-aware lexer** (also called partial preprocessor;
  subproject *PartialPreprocessor*) that
  reads unpreprocessed code and produces a conditional 
  token stream. The variability-aware lexer is responsible
  for resolving macros and file inclusions and for 
  normalizing `#ifdef` conditions. 
  There are two possible internal implementations to chose from,
  our own based on a heavily modified version of [jcpp](http://www.anarres.org/projects/jcpp/) and 
  the independently developed on from [xtc](http://cs.nyu.edu/xtc/).

* A **variability-aware parser framework** provides 
  parser combinators to build variability-aware parsers
  (subproject *ParserFramework*).

* The **variability-aware parsers** for GNU C and Java 
  (subprojects *CParser* and *JavaParser*) use the parser
  framework to build parsers for the corresponding languages. 
  The parsers read a conditional token stream and
  produce abstract syntax trees with corresponding choice nodes.
  Variability-aware parsers for HTML and JavaScript exist in
  forks.

* A **variability-aware type system** (subproject *CTypeChecker*)
  checks types considering variability in the abstract syntax tree. 
  As a normal type system, it walks over the AST, collects an environment
  of known types, and issues (conditional) type errors when problems
  are found. In addition, it extracts (conditional) symbol tables 
  needed for linker checks.

* A **variability-aware control-flow and data-flow analysis**
  (subproject *CRewrite*) provides implementations for
  successor/predecessor determination of abstract syntax tree
  elements in the presence of choice nodes and on top of it
  a variable liveness implementation. 

* A **call graph** analysis with a corresponding **pointer analysis**
  is currently developed in a [fork](https://github.com/gabrielcsf/TypeChef).

* A **rewrite and refactoring engine** built on TypeChef is
  available as separate projects [Hercules](https://github.com/joliebig/Hercules) and
  [Morpheus](https://github.com/joliebig/Morpheus).

* Setups for analyzing individual systems together with useful tooling  are available as
  separate github projects (see section evaluation below).



Installation and Usage
----------------------

You can download a .jar file including all necessary libraries [TypeChef.jar](http://ckaestne.github.com/TypeChef/deploy/TypeChef-0.3.7.jar). Run as usual

    java -jar TypeChef.jar ...

TypeChef is also available as a [maven repository](http://search.maven.org/#search%7Cga%7C1%7Ctypechef).
With [sbt](http://code.google.com/p/simple-build-tool/) you can include TypeChef with the following line:

```scala
libraryDependencies += "de.fosd.typechef" %% "frontend" % "0.3.7"
```

There is an Eclipse plugin [Colligens](https://sites.google.com/a/ic.ufal.br/colligens/) available, developed and maintained at Universidade Federal de Alagoas and Universidade Federal de Campina Grande. The plugin is also integrated in [FeatureIDE](http://fosd.net/fide).


To build TypeChef from source, we use 
[sbt](http://code.google.com/p/simple-build-tool/). 
Install *git* and download and compile the code as follows

    git clone git://github.com/ckaestne/TypeChef.git
    cd TypeChef
    java -Xmx512M -Xss10m -jar sbt-launch.jar clean update compile

Due to library dependencies, setting up the TypeChef classpath
can be difficult. There are two convenient mechanisms:
Use `sbt assembly` to build a single jar file.
Alternatively, call `sbt mkrun` to create a script 
`typechef.sh` that sets a correct classpath. 

Most functionality of TypeChef is accessible through parameters of
the main `de.fosd.typechef.Frontend` class. Call TypeChef with `--help` to see a 
list of configuration parameters. See also 
[Parameter.txt](https://github.com/ckaestne/TypeChef/blob/master/Parameter.txt).
You will need to set up system include paths with `-I` and
a header file with the compiler's macro definitions with `-h` (generate, 
for example, with `echo - | gcc -dM - -E -std=gnu99` for gcc).
Have a look at existing projects using TypeChef or contact us in
case of questions.

For development, we use IntelliJ Idea. In never versions, you can
simply import the project as an sbt project. In general 
avoid to set the classpath in IDEs manually, but automate
this step through sbt.

Evaluation
----------

Details on our syntax analysis of Linux have been published 
in an OOPSLA paper (see below). Details of the type checking and linker checking of Busybox were
publishing in an subsequent OOPSLA paper (see below).

The implementation of the data-flow analysis has been evaluated in two case studies (BusyBox and Linux) and the result of this evaluation is available from http://fosd.net/vaa

The setups for running TypeChef on these projects are available
as separate github projects, for example:

 * https://github.com/ckaestne/TypeChef-LinuxAnalysis
 * https://github.com/ckaestne/TypeChef-BusyboxAnalysis

Credits
-------

The project was only possible with fruitful collaboration of many researchers, included, but not limited to:

* [Christian Kaestner](http://www.cs.cmu.edu/~ckaestne/) (Carnegie Mellon University, project lead)
* [Joerg Liebig](http://www.infosun.fim.uni-passau.de/cl/staff/liebig/) (University of Passau)
* [Paolo G. Giarrusso](http://www.informatik.uni-marburg.de/~pgiarrusso/) (Philipps University Marburg)
* [Tillmann Rendel](http://www.informatik.uni-marburg.de/~rendel/) (Philipps University Marburg)
* [Sebastian Erdweg](http://erdweg.org/) (Technical University Darmstadt)
* [Klaus Ostermann](http://www.informatik.uni-marburg.de/~kos/) (Philipps University Marburg)
* [Thorsten Berger](http://bis.uni-leipzig.de/ThorstenBerger) (University of Leipzig)
* [Alexander von Rhein](http://www.infosun.fim.uni-passau.de/spl/people-rhein.php) (University of Passau)
* [Sarah Nadi](http://swag.uwaterloo.ca/~snadi/index.html) (University of Waterloo)
* Alex Eifler (Philipps University Marburg)
* David Kraus (Philipps University Marburg)
* Andy Kenner (Metop Research Institute, Magdeburg)
* Steffen Haase (Metop Research Institute, Magdeburg)
* Andreas Janker (University of Passau)
* Florian Garbe (University of Passau)
* Flavio Medeiros (Universidade Federal de Campina Grande, UFCG)
* [Sven Apel](http://www.infosun.fim.uni-passau.de/spl/apel/) (University of Passau)

The variability-aware lexer is implemented on top of [jcpp](http://www.anarres.org/projects/jcpp/), an implementation of the
C preprocessor in Java. An alternative based on [xtc/superc](http://cs.nyu.edu/xtc/) is included experimentally as well.

For reasoning about propositional formulas, we use the SAT solver [sat4j](http://www.sat4j.org/).

The GNU C parser is based on an [ANTLR grammar for GNU C](http://www.antlr.org/grammar/cgram).

The Java parser is based on a grammar that can be traced back to the Java 1.5 grammar in the
[JavaCC repository](http://java.net/projects/javacc/downloads/directory/contrib/grammars).

For convenience, we include a binary of [sbt](http://code.google.com/p/simple-build-tool/) in the repository.

This work was supported in part by the European Research Council, grant #203099 and the National Science Foundation #.

Contributing
------------

Fork the project, write bug reports, contact us, .... We are open for collaborations and extensions and other scenarios. 


Publications
------------

An in-depth discussion of the **parsing** approach and our experience with parsing Linux was published at OOPSLA 2011:

> Christian Kaestner, Paolo G. Giarrusso, Tillmann Rendel, Sebastian Erdweg, Klaus Ostermann, and Thorsten Berger. [Variability-Aware Parsing in the Presence of Lexical Macros and Conditional Compilation](http://www.cs.cmu.edu/~ckaestne/pdf/oopsla11_typechef.pdf). In Proceedings of the 26th Annual ACM SIGPLAN Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA) (Portland, OR), New York, NY, October 2011. ACM Press.

In the context of a variability-aware **module system**, we discussed **type checking** and **linker checks** on the example of Busybox, published at OOPSLA 2012:

> Christian Kästner, Klaus Ostermann, and Sebastian Erdweg. [A Variability-Aware Module System](http://www.cs.cmu.edu/~ckaestne/pdf/oopsla12.pdf). In Proceedings of the 27th Annual ACM SIGPLAN Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA), pages 773--792, New York, NY: ACM Press, October 2012.

A description of how we scaled **type system** and **data-flow analysis** and a performance comparison with sampling strategies can be found here:

> J. Liebig, A. von Rhein, C. Kästner, S. Apel, J. Dörre, and C. Lengauer. [Scalable Analysis of Variable Software](http://www.cs.cmu.edu/~ckaestne/pdf/fse13.pdf). In Proceedings of the European Software Engineering Conference and ACM SIGSOFT Symposium on the Foundations of Software Engineering (ESEC/FSE), New York, NY: ACM Press, August 2013.

A large scale **analysis of configuration constraints** in Linux, Busybox, eCOS, and uClibc using TypeChef was presented at ICSE 2014 and subsequently published in TSE:

> S. Nadi, T. Berger, C. Kästner, and K. Czarnecki. [Mining Configuration Constraints: Static Analyses and Empirical Results](https://www.cs.cmu.edu/~ckaestne/pdf/icse14_mining.pdf). In Proceedings of the 36th International Conference on Software Engineering (ICSE), pages 140--151, June 2014.

> S. Nadi, T. Berger, C. Kästner, and K. Czarnecki. [Where do Configuration Constraints Stem From? An Extraction Approach and an Empirical Study](https://www.cs.cmu.edu/~ckaestne/pdf/tse15.pdf). IEEE Transactions on Software Engineering (TSE), 2015.

The underlying concepts of building **variational data structures** and programming with variational lists, maps, and sets were discussed in an Onward 2014 paper:

> E. Walkingshaw, C. Kästner, M. Erwig, S. Apel, and E. Bodden. [Variational Data Structures: Exploring Tradeoffs in Computing with Variability](https://www.cs.cmu.edu/~ckaestne/pdf/onward14.pdf). In Proceedings of the 13rd SIGPLAN Symposium on New Ideas in Programming and Reflections on Software at SPLASH (Onward!), pages 213--226, New York, NY: ACM Press, 2014.

A sound **refactoring engine** based on TypeChef was presented at ICSE 2015:

> J. Liebig, A. Janker, F. Garbe, S. Apel, and C. Lengauer. [Morpheus: Variability-Aware Refactoring in the Wild](http://www.infosun.fim.uni-passau.de/publications/docs/LiJaGa+15.pdf). In Proceedings of the IEEE/ACM International Conference on Software Engineering (ICSE), pages 380–391. IEEE Computer Society, May 2015. 

A more detailed discussion of the variability-aware **lexer** (or partial preprocessor) was presented at VaMoS 2011:

> Christian Kästner, Paolo G. Giarrusso, and Klaus Ostermann. [Partial Preprocessing C Code for Variability Analysis](http://www.cs.cmu.edu/~ckaestne/pdf/vamos11.pdf). In Proceedings of the Fifth International Workshop on Variability Modelling of Software-intensive Systems (VaMoS) (Namur, Belgium), pages 137-140, New York, NY, USA, January 2011. ACM Press.

A discussion of potential **integer vulnerabilities** across compile-time configurations was discussed in a technical report:

> Z. Coker, S. Hasan, J. Overbey, M. Hafiz, and C. Kästner. [Integers In C: An Open Invitation to Security Attacks?](https://www.cs.cmu.edu/~ckaestne/pdf/csse14-01.pdf) Technical Report CSSE14-01, Auburn, AL: College of Engineering, Auburn University, February 2014. 

A simple variability-aware **interpreter** for executing test cases was build on top of TypeChef and published at FOSD 2012. A subsequent extension of this work for PHP was published at ICSE 2014:

> C. Kästner, A. von Rhein, S. Erdweg, J. Pusch, S. Apel, T. Rendel, and K. Ostermann. [Toward Variability-Aware Testing](http://www.cs.cmu.edu/~ckaestne/pdf/FOSD12_testing.pdf). In Proceedings of the 4th International Workshop on Feature-Oriented Software Development (FOSD), pages 1--8, New York, NY: ACM Press, September 2012. 

> H. Nguyen, C. Kästner, and T. Nguyen. [Exploring Variability-Aware Execution for Testing Plugin-Based Web Applications](https://www.cs.cmu.edu/~ckaestne/pdf/icse14_varex.pdf). In Proceedings of the 36th International Conference on Software Engineering (ICSE), pages 907--918, June 2014.

An approach to **simplify presence conditions**, integrated into TypeChef, was presented at ICSE 2015:

> A. von Rhein, A. Grebhahn, S. Apel, N. Siegmund, D. Beyer, and T. Berger. [Presence-Condition Simplification in Highly Configurable Systems](http://www.infosun.fim.uni-passau.de/publications/docs/RhGrAp+15.pdf). In Proceedings of the IEEE/ACM International Conference on Software Engineering (ICSE), pages 178–188. IEEE Computer Society, May 2015.

TypeChef supported the infrastructure for work on **analyzing HTML and JavaScript within PHP code**, published at FSE 2014, FSE 2015 and a tool demo:

> H. Nguyen, C. Kästner, and T. Nguyen. [Building Call Graphs for Embedded Client-Side Code in Dynamic Web Applications](https://www.cs.cmu.edu/~ckaestne/pdf/fse14.pdf). In Proceedings of the ACM SIGSOFT Symposium on the Foundations of Software Engineering (FSE), New York, NY: ACM Press, November 2014. 

> H. Nguyen, C. Kästner, and T. Nguyen. [Varis: IDE Support for Embedded Client Code in PHP Web Applications](https://www.cs.cmu.edu/~ckaestne/pdf/icse15_varis_demo.pdf). In Proceedings of the 37th International Conference on Software Engineering (ICSE), May 2015. Formal Demonstration paper. 

> H. Nguyen, C. Kästner, and T. Nguyen. Cross-language Program Slicing for Dynamic Web Applications. In Proceedings of the European Software Engineering Conference and ACM SIGSOFT Symposium on the Foundations of Software Engineering (ESEC/FSE), New York, NY: ACM Press, August 2015. 

For an overview of **variability-aware analysis** in general, please refer to the
a technical report on the following webpage http://fosd.net/spl-strategies and the following reports:

> T. Thüm, S. Apel, C. Kästner, M. Kuhlemann, I. Schaefer, and G. Saake. [Analysis Strategies for Software Product Lines](http://www.cs.cmu.edu/~ckaestne/pdf/tr_analysis12.pdf). Technical Report FIN-2012-04, Magdeburg, Germany: University of Magdeburg, April 2012.

> A. von Rhein, S. Apel, C. Kästner, T. Thüm, and I. Schaefer. [The PLA Model: On the Combination of Product-Line Analyses](). In Proceedings of the 7th Int'l Workshop on Variability Modelling of Software-Intensive Systems (VaMoS), pages 14:1--14:8, New York, NY: ACM Press, January 2013.

Finally, an early overview of the project with a very preliminary implementation (now terribly outdated and superseeded by the papers above) was published at

> Andy Kenner, Christian Kästner, Steffen Haase, and Thomas Leich. [TypeChef: Toward Type Checking #ifdef Variability in C](http://www.cs.cmu.edu/~ckaestne/pdf/FOSD10-typechef.pdf). In Proceedings of the Second Workshop on Feature-Oriented Software Development (FOSD) (Eindhoven, The Netherlands), pages 25-32, New York, NY, USA, October 2010. ACM Press.

Change Log
-----------

 * v0.3.7 (November 2014)
   * Updated to Scala 2.11.4
   * Lots of minor changes to type system and control flow graph
   * Preparations to run on newer version of Linux
 * v0.3.4 (March 2013)
   * Updated to Scala 2.10.1
   * Various fixes and extensions to the type system (e.g., support for both styles of parameter declarations in C, better handling of ignored types, correct type for sizeOf)
   * Fixed bug in parser that caused duplicated subtrees and subtrees with unsatisfiable conditions in some cases of undisciplined ifdefs
   * Report #error and #warning tags again during lexing
   * Experimental integration of Xtc/SuperC lexer
   * Changed defaults of several command line parameters
   * Supports now the entire X86 setup for the Linux kernel
   * And several more
 * v0.3.3 (June 2012)
 * v0.3.2 (May 2012)


License
-------

TypeChef is published as open source under LGPL 3.0. See [LICENSE](TypeChef/blob/master/LICENSE).
