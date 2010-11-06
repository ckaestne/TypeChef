#!/bin/bash -e
#!/bin/bash -vxe
if [ -z "$jcppConfLoaded" ]; then
  source jcpp.conf
fi
inp=$1
shift

. setupOutPaths.sh.inc

echo "=="
echo "==Preprocess source"
echo "=="
gcc $gccOpts -E "$inp" "$@" > "$outPreproc" || true

echo "=="
echo "==Preprocess output of partial preprocessor"
echo "=="
gcc $gccOpts -E "$outPartialPreproc" "$@" > "$outPartialPreprocThenPreproc" || true

echo "Output size stats - partial preprocessor:"
grep -v '^$' "$outPartialPreproc"|wc

echo "Output size stats - preprocessor:"
grep -v '^$' "$outPreproc"|wc

echo "Output size stats - partial preprocessor then preprocessor:"
grep -v '^$' "$outPartialPreprocThenPreproc"|wc

# Remove dashed and empty lines before diffing.
excludeLines='^(#|$)'
spacesToNewLine='s/[ 	]\+/\n/g'
# -w ignores white space, -B blank line, -u helps readability.
if ! diff -uBw <(egrep -v "$excludeLines" "$outPartialPreprocThenPreproc"| \
  $sed -e "$spacesToNewLine") <(egrep -v "$excludeLines" "$outPreproc"| \
  $sed -e "$spacesToNewLine") > "$outDiff"; then
  echo "*** WARNING! - $outDiff not empty, inconsistency detected ***"
  echo "*** The output of the preprocessor is different if it is run on the original input or on the partially preprocessed file"
else
  echo "Output matches"
fi
