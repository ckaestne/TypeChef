#!/bin/bash -e
#!/bin/bash -vxe
if [ -z "$jcppConfLoaded" ]; then
  source jcpp.conf
fi

# What you should configure
javaOpts='$javaOpts -Xmx2G -Xms128m -Xss10m'

macro_stats_path=macroDebug.txt
debugsource_path=debugsource.txt

# For Java compiled stuff!
basePath=.

#mainClass="org.anarres.cpp.Main"
mainClass="de.fosd.typechef.linux.LinuxPreprocessorFrontend"

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

#time scala -cp BoaCaseStudy/target/scala_2.8.1/classes:FeatureExprLib/lib/org.sat4j.core.jar:FeatureExprLib/target/scala_2.8.1/classes:\
#  PartialPreprocessor/target/scala_2.8.1/classes:PartialPreprocessor/lib/gnu.getopt.jar \
#  <(echo -e '#define b ciao\nb')

if [ ! -f "$outPreproc" ]; then
  echo "=="
  echo "==Preprocess source"
  echo "=="
  gcc -Wp,-P -U __weak $gccOpts -E "$inp" "$@" > "$outPreproc" || true
fi

# Beware: the embedded for loop requotes the passed argument. That's dark magic,
# don't ever try to touch it. It simplifies your life as a user of this program
# though!
echo "==Partially preprocessing $inp"
echo $partialPreprocFlags

bash -c "time java -ea $javaOpts -cp \
$basePath/project/boot/scala-2.9.0/lib/scala-library.jar:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar:\
$basePath/PartialPreprocessor/lib/junit.jar:\
$basePath/lib/junit-4.8.1.jar:\
$basePath/FeatureExprLib/lib_managed/scala_2.9.0/compile/org.sat4j.core-2.3.1.jar:\
$basePath/lib/scalacheck_2.8.1-1.8.jar:\
$basePath/lib/scalatest-1.2.jar:\
$basePath/FeatureExprLib/target/scala_2.9.0/classes:\
$basePath/ConditionalLib/target/scala_2.9.0/classes:\
$basePath/PartialPreprocessor/target/scala_2.9.0/classes:\
$basePath/ParserFramework/target/scala_2.9.0/classes:\
$basePath/CParser/target/scala_2.9.0/classes:\
$basePath/CTypeChecker/target/scala_2.9.0/classes:\
$basePath/LinuxAnalysis/target/scala_2.9.0/classes:\
$basePath/PreprocessorFrontend/target/scala_2.9.0/classes:\
$basePath/ParserFramework/lib_managed/scala_2.9.0/compile/kiama_2.9.0-1.1.0.jar\
  $mainClass -t -i \
  $(for arg in $partialPreprocFlags "$@"; do echo -n "\"$arg\" "; done) \
  '$inp' -o '$outPartialPreproc' 2> '$outErr' |tee '$outDbg'" \
  2> "$outTime" || true
#bash -c "time java -ea $javaOpts -jar $sbtPath 'project PreprocessorFrontend' \
#  \"run $(for arg in $partialPreprocFlags "$@"; do echo -n "\"$arg\" "; done) \
#  '$inp' -o '$outPartialPreproc'\"  \
#  2> '$outErr'|tee '$outDbg'" \
#  2> "$outTime" || true


cat "$outErr" 1>&2
