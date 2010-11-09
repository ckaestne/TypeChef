if [ -z "$inp" ]; then
  echo "inp not defined!" >&2
  exit 1
fi

#outBase=$1
#shift
#outPartialPreproc=$1

outBase="$(dirname $inp)/$(basename $inp .c)"

# Setup derived output paths
outDbg="$outBase.dbg"
outPartialPreproc="$outBase.pi"
outPreproc="$outBase.i"
outPartialPreprocThenPreproc="$outBase.pi.i"
# Interesting outputs
outErr="$outBase.err"
outTime="$outBase.time"
#outStats="$outBase.stats"
outDiff="$outBase.diffs"
outMacroDebug="$outBase.macroDbg"
outDebugSource="$outBase.dbgSrc"
#Now it's per-testcase, not per file!
#outCSV="$outBase.csv"

# vim: set ft=sh:
