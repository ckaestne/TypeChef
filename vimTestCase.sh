#!/bin/bash -e
#!/bin/bash -vxe

srcPath=vim73/src

export partialPreprocFlags="-x FEAT_ -P _H -U FEAT_GUI_MOTIF -U FEAT_SUN_WORKSHOP -U FEAT_GUI_MAC -U RISCOS -U FEAT_GUI_PHOTON -U FEAT_GUI_MSWIN -U FEAT_GUI_NEXTAW -U FEAT_MZSCHEME -U FEAT_GUI_GNOME -U FEAT_MOUSE_GPM -U FEAT_SYSMOUSE"
# Compilation options used for Vim. FEAT_TINY makes sure most features are left
# variable.
flags="-I. -I$PWD/$srcPath/proto -DHAVE_CONFIG_H $(pkg-config --cflags gtk+-2.0)" # -D_FORTIFY_SOURCE=1
# From python-config --cflags:
flags="$flags -Isystems/redhat/usr/include/python2.6 -D_FORTIFY_SOURCE=2 -D_GNU_SOURCE"
# flags="$flags -D FEAT_TINY"

fileList='if_xcmdsrv buffer blowfish charset diff digraph edit eval ex_cmds ex_cmds2 ex_docmd ex_eval ex_getln fileio fold getchar hardcopy hashtab if_cscope if_python main mark memfile memline menu message misc1 misc2 move mbyte normal ops option os_unix popupmnu quickfix regexp screen search sha256 spell syntax tag term ui undo window gui gui_gtk gui_gtk_x11 pty gui_gtk_f gui_beval netbeans version xxd/xxd'

export outCSV=vim73.csv
## Reset output
#echo -n > "$outCSV"

for i in $fileList; do
  ./jcpp.sh $srcPath/$i.c $flags
done
for i in $fileList; do
  ./postProcess.sh $srcPath/$i.c $flags
done
for i in $fileList; do
  ./parseTypecheck.sh $srcPath/$i.pi
done
# I commented out these other ones:
# -DFEAT_GUI_GTK  # Should be a variable feature!
# -O2 # This influences the _OPTIMIZE_ macro (check the exact spelling)
# -fno-strength-reduce -Wall 
