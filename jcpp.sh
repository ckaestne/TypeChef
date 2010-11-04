#!/bin/bash -e
# What you should configure
macro_stats_path=macroDebug.txt
basePath=.
#Flags which should not be passed to the standard preprocessor.
# partialPreprocFlags="-p _"
partialPreprocFlags="-c darwin.properties"
gccOpts="-x c -std=gnu99"
#mainClass="org.anarres.cpp.Main"
mainClass="PreprocessorFrontend" 

# Brute argument parsing
# The right thing to do would be to be a gcc replacement, parse its flags and
# select the ones we care about.
inp=$1
shift
outBase="$(dirname $inp)/$(basename $inp .c)"
#outBase=$1
#shift
#outPartialPreproc=$1

# Setup derived output paths
outDbg="$outBase.dbg"
outPartialPreproc="$outBase.pi"
outPreproc="$outBase.i"
outPartialPreprocThenPreproc="$outBase.pi.i"
# Interesting outputs
stdErrWithTimings="$outBase.err"
outStats="$outBase.stats"
outDiff="$outBase.diffs"

#time scala -cp BoaCaseStudy/target/scala_2.8.0/classes:FeatureExprLib/lib/org.sat4j.core.jar:FeatureExprLib/target/scala_2.8.0/classes:\
#  PartialPreprocessor/target/scala_2.8.0/classes:PartialPreprocessor/lib/gnu.getopt.jar \
#  <(echo -e '#define b ciao\nb')
time scala -cp $basePath/BoaCaseStudy/target/scala_2.8.0/classes:\
$basePath/FeatureExprLib/lib/org.sat4j.core.jar:\
$basePath/FeatureExprLib/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/target/scala_2.8.0/classes:\
$basePath/PartialPreprocessor/lib/gnu.getopt.jar \
  $mainClass $partialPreprocFlags "$inp" "$@" -o "$outPartialPreproc" #> $outDbg # 2> "$stdErrWithTimings"

echo "Output size stats - partial preprocessor:"
wc "$outPartialPreproc"
#Commented out - where is macroDebug?
#mv $macro_stats_path "$outStats"

gcc -E -x c "$inp" "$@" > "$outPreproc"
echo "Output size stats - preprocessor:"
wc "$outPreproc"

gcc -E -x c "$outPartialPreproc" "$@" > "$outPartialPreprocThenPreproc"
echo "Output size stats - partial preprocessor then preprocessor:"
wc "$outPartialPreprocThenPreproc"

# -w ignores white space, -B blank line, -u helps readability.
if ! diff -uBw <(grep -v '^#' "$outPartialPreprocThenPreproc") <(grep -v '^#' "$outPreproc") > "$outDiff"; then
  echo "*** WARNING! - $outDiff not empty, inconsistency detected ***"
  echo "*** The output of the preprocessor is different if it is run on the original input or on the partially preprocessed file"
else
  echo "Output matches"
fi
