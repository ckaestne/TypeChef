TypeChef
========

TypeChef is a research project with the goal of type checking 
ifdef variability in C code with the target of
type checking the entire Linux kernel with several thousand 
features (or configuration options).

Instead of type checking each variant for each feature 
combination in isolation, TypeChef parses the
entire source code containing all variability in a 
variability-aware fashion without preprocessing.
The resulting abstract syntax tree contains the 
variability in form of choice nodes. Eventually, a
variability-aware type system performs type checking 
on these trees.

TypeChef detects syntax and type errors in all possible 
feature combinations. TypeChef was originally short for 
*Type Checking Ifdef Variability*.


Architecture and Subprojects
----------------------------

The TypeChef project contains of four main components 
and several helper libraries.

* A **variability-aware lexer** (also called partial preprocessor;
  subproject *PartialPreprocessor*) that
  reads unpreprocessed code and produces a conditional 
  token stream. The variability-aware lexer is responsible
  for resolving macros and file inclusions and for 
  normalizing `#ifdef` conditions

* A **variability-aware parser framework** provides 
  parser combinators to build variability-aware parsers
  (subproject *ParserFramework*).

* The **variability-aware parsers** for GNU C and Java 
  (subprojects *CParser* and *JavaParser*) use the parser
  framework to build parsers for the corresponding languages. 
  The parsers read a conditional token stream and
  produce abstract syntax trees with corresponding choice nodes.

* A **variability-aware type system** (subproject *CTypeChecker*)
  eventually checks variability in the abstract
  syntax tree. This will be supported by variability-aware 
  linker checks.

* A **variability-aware control and data-flow implementation**
  (subproject *CRewrite*) provides implementations for
  successor/predecessor determination of abstract syntax tree
  elements in the presence of choice nodes and on top of it
  a variable liveness implementation. The implementation has been
  evaluated in two case studies (BusyBox and Linux) and the result
  of this evaluation is available from [http://fosd.net/vaa](http://fosd.net/vaa).
  For an overview of variability-aware analysis, please refer to the
  technical report from Thuem, Apel, Kaestner, Kuhlemann, Schaefer, and
  Saake [http://www.cs.uni-magdeburg.de/inf_media/downloads/forschung/technical_reports_und_preprints/2012/04_2012.pdf](http://www.cs.uni-magdeburg.de/inf_media/downloads/forschung/technical_reports_und_preprints/2012/04_2012.pdf) and the corresponding website [http://fosd.net/spl-strategies](http://fosd.net/spl-strategies).

* All tasks are supported by a library for feature 
  expressions and reasoning about feature expressions
  (subproject *FeatureExprLib*). Internally the library 
  uses the SAT solver *sat4j*.

* Evaluation specific parts, mostly for Linux are provided 
  in the frontend subproject *LinuxAnalysis*.

Installation and Usage
----------------------

For simple experimentation, try our online version at 
[http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/online/](http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/online/).

Alternatively, you can download a .jar file including
all necessary libraries [TypeChef.jar](http://ckaestne.github.com/TypeChef/deploy/TypeChef-0.3.3.jar). Run as usual

    java -jar TypeChef.jar ...

TypeChef is also available as a [maven repository](http://search.maven.org/#search%7Cga%7C1%7Ctypechef).
With [sbt](http://code.google.com/p/simple-build-tool/) you can include TypeChef with the following line:

```scala
libraryDependencies += "de.fosd.typechef" % "frontend_2.9.1" % "0.3.3"
```

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
[Parameter.txt]( https://github.com/ckaestne/TypeChef/blob/master/Parameter.txt).
You will need to set up system include paths with `-I` and
a header file with the compiler's macro definitions with `-h` (generate, 
for example, with `echo - | gcc -dM - -E -std=gnu99` for gcc).
Have a look at existing projects using TypeChef or contact us in
case of questions.

IntelliJ Idea users should run `sbt gen-idea` to create 
corresponding project and classpath information for the 
IDE. Similar sbt plugins for Eclipse are available, 
but we have not tried or integrated them yet. In general 
avoid to set the classpath in IDEs manually, but let sbt 
generate corresponding files for you.

Evaluation
----------

Details on our syntax analysis of Linux have been published 
in an OOPSLA paper (see below).
You find additional information on that evaluation at 
[http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/](http://www.mathematik.uni-marburg.de/~kaestner/TypeChef/)


Credits
-------

The project was only possible with fruitful collaboration of many researchers.

* [Christian Kaestner](http://www.uni-marburg.de/fb12/ps/team/kaestner) (Philipps University Marburg, project leader)
* [Paolo G. Giarrusso](http://www.informatik.uni-marburg.de/~pgiarrusso/) (Philipps University Marburg)
* [Tillmann Rendel](http://www.informatik.uni-marburg.de/~rendel/) (Philipps University Marburg)
* [Sebastian Erdweg](http://www.informatik.uni-marburg.de/~seba/) (Philipps University Marburg)
* [Klaus Ostermann](http://www.informatik.uni-marburg.de/~kos/) (Philipps University Marburg)
* [Thorsten Berger](http://bis.uni-leipzig.de/ThorstenBerger) (University of Leipzig)
* [Joerg Liebig](http://www.infosun.fim.uni-passau.de/cl/staff/liebig/) (University of Passau)
* [Alexander von Rhein](http://www.infosun.fim.uni-passau.de/spl/people-rhein.php) (University of Passau)
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

For convenience, we include [sbt](http://code.google.com/p/simple-build-tool/) in the repository.

Furthermore, we thank for their contributions and discussions:
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

A discussion of a module system including type checks and linker checks built on top of TypeChef was published as technical report:

> Christian Kästner, Klaus Ostermann, and Sebastian Erdweg. [A Variability-Aware Module System](http://www.informatik.uni-marburg.de/~kaestner/tr_modulesys12.pdf). Technical Report 01/2012, Department of Mathematics and Computer Science, Philipps University Marburg, April 2012.

An early overview of the project with a very preliminary implementation was published at

> Andy Kenner, Christian Kästner, Steffen Haase, and Thomas Leich. [TypeChef: Toward Type Checking #ifdef Variability in C](http://www.informatik.uni-marburg.de/~kaestner/FOSD10-typechef.pdf). In Proceedings of the Second Workshop on Feature-Oriented Software Development (FOSD) (Eindhoven, The Netherlands), pages 25-32, New York, NY, USA, October 2010. ACM Press.

A more detailed discussion of the variability-aware lexer (or partial preprocessor) was presented at

> Christian Kästner, Paolo G. Giarrusso, and Klaus Ostermann. [Partial Preprocessing C Code for Variability Analysis](http://www.informatik.uni-marburg.de/~kaestner/vamos11.pdf). In Proceedings of the Fifth International Workshop on Variability Modelling of Software-intensive Systems (VaMoS) (Namur, Belgium), pages 137-140, New York, NY, USA, January 2011. ACM Press.



License
-------

TypeChef is published as open source under GPL 3.0. See [LICENSE](TypeChef/blob/master/LICENSE).
