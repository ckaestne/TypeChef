#!/bin/sh
java -ea -Xmx4096m -Xms128m -Xss10m -classpath "/local/joliebig/TypeChef/Sampling/target/scala-2.10/classes:/local/joliebig/TypeChef/CParser/target/scala-2.10/classes:/local/joliebig/TypeChef/FeatureExprLib/target/scala-2.10/classes:/local/joliebig/TypeChef/PartialPreprocessor/target/scala-2.10/classes:/local/joliebig/TypeChef/ParserFramework/target/scala-2.10/classes:/local/joliebig/TypeChef/ConditionalLib/target/scala-2.10/classes:/local/joliebig/TypeChef/CTypeChecker/target/scala-2.10/classes:/local/joliebig/TypeChef/CRewrite/target/scala-2.10/classes:/local/joliebig/TypeChef/Frontend/target/scala-2.10/classes:/local/joliebig/TypeChef/JavaParser/target/scala-2.10/classes:/local/joliebig/TypeChef/FeatureExprLib/lib/javabdd-1.0b2.jar:/local/joliebig/TypeChef/PartialPreprocessor/lib/xtc_2.9.1-2.3.1.jar:/home/joliebig/.sbt/boot/scala-2.10.1/lib/scala-library.jar:/home/joliebig/.ivy2/cache/org.sat4j/org.sat4j.core/jars/org.sat4j.core-2.3.1.jar:/home/joliebig/.ivy2/cache/gnu.getopt/java-getopt/jars/java-getopt-1.0.13.jar:/home/joliebig/.ivy2/cache/org.apache.ant/ant/jars/ant-1.8.2.jar:/home/joliebig/.ivy2/cache/org.apache.ant/ant-launcher/jars/ant-launcher-1.8.2.jar:/home/joliebig/.ivy2/cache/com.googlecode.kiama/kiama_2.10/jars/kiama_2.10-1.4.0.jar:/home/joliebig/.ivy2/cache/jline/jline/jars/jline-1.0.jar" de.fosd.typechef.Sampling "$@"