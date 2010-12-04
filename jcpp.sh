#!/bin/bash -e
#!/bin/bash -vxe
if [ -z "$jcppConfLoaded" ]; then
  source jcpp.conf
fi

# What you should configure
javaOpts='$javaOpts -Xmx2G -Xms128m'

macro_stats_path=macroDebug.txt
debugsource_path=debugsource.txt

# For Java compiled stuff!
basePath=.

#mainClass="org.anarres.cpp.Main"
mainClass="PreprocessorFrontend"

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

#time scala -cp BoaCaseStudy/target/scala_2.8.0/classes:FeatureExprLib/lib/org.sat4j.core.jar:FeatureExprLib/target/scala_2.8.0/classes:\
#  PartialPreprocessor/target/scala_2.8.0/classes:PartialPreprocessor/lib/gnu.getopt.jar \
#  <(echo -e '#define b ciao\nb')

echo "=="
echo "==Preprocess source"
echo "=="
gcc -U __weak $gccOpts -E "$inp" "$@" > "$outPreproc" || true

# Beware: the embedded for loop requotes the passed argument. That's dark magic,
# don't ever try to touch it. It simplifies your life as a user of this program
# though!
echo "==Partially preprocessing and typechecking $inp"

bash -c "time java -ea $javaOpts -cp \
$basePath/project/boot/scala-2.8.0/lib/scala-library.jar:\
$basePath/FeatureExprLib/lib/org.sat4j.core.jar:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar:\
$basePath/PartialPreprocessor/lib/junit.jar:\
$basePath/FeatureExprLib/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/target/scala_2.8.0/classes:\
$basePath/ParserFramework/target/scala_2.8.0/classes:\
$basePath/CParser/target/scala_2.8.0/classes:\
$basePath/CTypeChecker/target/scala_2.8.0/classes:\
$basePath/BoaCaseStudy/target/scala_2.8.0/classes \
  $mainClass \
  $(for arg in $partialPreprocFlags "$@"; do echo -n "\"$arg\" "; done) \
  '$inp' -o '$outPartialPreproc' 2> '$outErr' >'$outDbg'" \
  2> "$outTime" || true

cat "$outErr" 1>&2
mv $macro_stats_path "$outMacroDebug" # || true
mv $debugsource_path "$outDebugSource" # || true
