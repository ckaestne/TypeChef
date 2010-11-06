#!/bin/bash -vxe
#!/bin/bash -e
if [ -z "$jcppConfLoaded" ]; then
  source jcpp.conf
fi
# What you should configure
macro_stats_path=macroDebug.txt

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

# Beware: the embedded for loop requotes the passed argument. That's dark magic,
# don't ever try to touch it. It simplifies your life as a user of this program
# though!
bash -c "time java -cp $basePath/project/boot/scala-2.8.0/lib/scala-library.jar:$basePath/BoaCaseStudy/target/scala_2.8.0/classes:\
$basePath/FeatureExprLib/lib/org.sat4j.core.jar:\
$basePath/FeatureExprLib/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar \
  $mainClass $partialPreprocFlags \
  $(for arg in "$@"; do echo -n "\"$arg\" "; done) \
  '$inp' -o '$outPartialPreproc' > '$outDbg' 2> '$outErr'" \
  2> "$outTime" || true

mv $macro_stats_path "$outStats" || true
