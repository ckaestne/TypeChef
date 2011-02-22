##################################################################
# Location of the Linux kernel.
##################################################################
#srcPath=linux-2.6.33.3
# XXX:$PWD/ makes the path absolute, it is needed for some stupid bug!
srcPath=$PWD/linux-2.6.33.3

##################################################################
# List of files to preprocess
##################################################################
filesToProcess() {
  local listFile=linuxKernelSrcList.txt
  #cat $listFile
  awk -F: '{print $1}' < linux_2.6.33.3_pcs.txt
}


# vim: set ft=sh:
