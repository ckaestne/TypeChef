#!/bin/bash -vxe
# Hack to add an option just for the partial preprocessor.
. jcpp.conf

partialPreprocFlags='-x FEAT_'
# Compilation options used for Vim. FEAT_TINY makes sure most features are left
# variable.
flags="-I. -Iproto -DHAVE_CONFIG_H $(pkg-config --cflags gtk+-2.0) -D_FORTIFY_SOURCE=1"
# flags="$flags -D FEAT_TINY"

list='buffer blowfish charset diff digraph edit eval ex_cmds ex_cmds2 ex_docmd ex_eval ex_getln fileio fold getchar hardcopy hashtab if_cscope if_xcmdsrv main mark memfile memline menu message misc1 misc2 move mbyte normal ops option os_unix popupmnu quickfix regexp screen search sha256 spell syntax tag term ui undo window gui gui_gtk gui_gtk_x11 pty gui_gtk_f gui_beval netbeans version xxd'

srcPath=vim73/src

for i in $list; do
  ./jcpp.sh $srcPath/$i.c $flags
done
for i in $list; do
  ./postProcess.sh $srcPath/$i.c $flags
done
# I commented out these other ones:
# -DFEAT_GUI_GTK  # Should be a variable feature!
# -O2 # This influences the _OPTIMIZE_ macro (check the exact spelling)
# -fno-strength-reduce -Wall 
