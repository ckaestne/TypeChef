#!/bin/bash -e
#!/bin/bash -vxe
if [ -z "$jcppConfLoaded" ]; then
  source jcpp.conf
fi

javaOpts='$javaOpts -Xmx2G -Xms128m -Xss8M -XX:MaxPermSize=128m'

# For Java compiled stuff!
basePath=.

#mainClass="org.anarres.cpp.Main"
mainClass="de.fosd.typechef.typesystem.Main"
sbtPath=~/opt/sbt/sbt-launch-0.7.4.jar

# Brute argument parsing
# The right thing to do would be to be a gcc replacement, parse its flags and
# select the ones we care about.
if [ $# -lt 1 ]; then
  echo "Not enough arguments!" >&2
  exit 1
fi
inp=$1
shift

. setupOutPaths.sh.inc

#bash -c "time java -ea $javaOpts -jar $sbtPath 'project CTypeChecker' \
#  'run $inp' \
bash -c "time java -ea $javaOpts -cp \
$basePath/project/boot/scala-2.8.1/lib/scala-library.jar:\
$basePath/org.sat4j.core/target/scala_2.8.1/classes:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar:\
$basePath/PartialPreprocessor/lib/junit.jar:\
$basePath/FeatureExprLib/target/scala_2.8.1/classes:\
$basePath/PartialPreprocessor/target/scala_2.8.1/classes:\
$basePath/ParserFramework/target/scala_2.8.1/classes:\
$basePath/CParser/target/scala_2.8.1/classes:\
$basePath/CTypeChecker/target/scala_2.8.1/classes \
  $mainClass '$inp' \
  2> '$outErrT'|tee '$outDbgT'" \
  2> "$outTimeT" || true

cat "$outErrT" 1>&2
