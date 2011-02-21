##################################################################
# Location of the Linux kernel.
##################################################################
#srcPath=linux-2.6.33.3
# XXX:$PWD/ makes the path absolute, it is needed for some stupid bug!
srcPath=$PWD/linux-2.6.33.3

listFile=linuxKernelSrcList.txt
##################################################################
# List of files to preprocess
##################################################################
filesToProcess=""
while read i; do
  filesToProcess="$filesToProcess $i"
done < $listFile

unset listFile

# vim: set ft=sh:
