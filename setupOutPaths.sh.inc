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
outDiff="$outBase.diffs"
outMacroDebug="$outBase.macroDbg"
outDebugSource="$outBase.dbgSrc"

#For parsing + preprocessing
outDbgT="$outBase.dbgT"
outErrT="$outBase.errT"
outTimeT="$outBase.timeT"

# vim: set ft=sh:
