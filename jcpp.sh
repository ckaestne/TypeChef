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
inp=$1
shift

. setupOutPaths.sh.inc

#time scala -cp BoaCaseStudy/target/scala_2.8.0/classes:FeatureExprLib/lib/org.sat4j.core.jar:FeatureExprLib/target/scala_2.8.0/classes:\
#  PartialPreprocessor/target/scala_2.8.0/classes:PartialPreprocessor/lib/gnu.getopt.jar \
#  <(echo -e '#define b ciao\nb')
bash -c "time scala -cp $basePath/BoaCaseStudy/target/scala_2.8.0/classes:\
$basePath/FeatureExprLib/lib/org.sat4j.core.jar:\
$basePath/FeatureExprLib/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar \
  $mainClass $partialPreprocFlags $@ '$inp' -o '$outPartialPreproc' > '$outDbg' 2> '$outErr'" \
  2> "$outTime"

mv $macro_stats_path "$outStats" || true

