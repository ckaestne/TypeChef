#!/bin/bash

# gnuplot configured with

srcPath=./casestudies/gnuplot-4.2.5/src
prjPath=./casestudies/gnuplot-4.2.5
export partialPreprocFlags="-p false
	-U CGI
	-U COLUMN_HELP
	-U DJSVGA
	-U DOS32
	-U EXTENDED_COLOR_SPECS
	-U EXTERN_ERRNO
	-U GIF_ANIMATION
	-U GNUGRAPH
	-U GRX21
	-U HAVE_3DKIT_H
	-U HAVE_DOPRNT
	-U HAVE_EDITLINE_READLINE_H
	-U HAVE_FORK
	-U HAVE_GD_GIF
	-U HAVE_GD_H
	-U HAVE_GD_JPEG
	-U HAVE_GD_PNG
	-U HAVE_GD_TTF
	-U HAVE_GGI_WMH_H
	-U HAVE_GGI_XMI_H
	-U HAVE_GTK
	-U HAVE_GTK28
	-U HAVE_KPSEXPAND
	-U HAVE_LIBAQUATERM
	-U HAVE_LIBC_H
	-U HAVE_LIBEDITLINE
	-U HAVE_LIBGD
	-U HAVE_LIBPDF
	-U HAVE_LIBPDF_OPEN_FILE
	-U HAVE_LIBREADLINE
	-U HAVE_NODASH_LIBPDF
	-U HAVE_OLD_LIBPDF
	-U HAVE_PDFLIB_H
	-U HAVE_READLINE_HISTORY_H
	-U HAVE_READLINE_READLINE_H
	-U HAVE_READLINE_TILDE_H
	-U HAVE_STRICMP
	-U HAVE_STRNICMP
	-U HAVE_SYS_BSDTYPES_H
	-U HAVE_SYS_SYSTEMINFO_H
	-U HAVE_VFORK
	-U HAVE_VFORK_H
	-U HAVE_VGAGL_H
	-U HAVE_WORKING_FORK
	-U HAVE_WORKING_VFORK
	-U HIDDEN3D_GRIDBOX
	-U IRIS
	-U LINUXVGA
	-U MGR
	-U MSDOS
	-U NOCWDRC
	-U RGIP
	-U STAT_MACROS_BROKEN
	-U SUN
	-U THIN_PLATE_SPLINES_GRID
	-U THREEDKIT
	-U UNIXPLOT
	-U USE_GGI_DRIVER
	-U VGAGL
	-U WXWIDGETS
	-U X_DISPLAY_MISSING
	-U const
	-U gdImageStringFT
	-U inline
	-U pid_t
	-U size_t
	-U vfork
	-U ATARI
	-U MTOS
	-U MSDOS
	-U DOS386
	--openFeat $prjPath/gnuplotfeatures.txt
"
flags='
	-I '$prjPath' -I '$srcPath'
	--include ./host/platform.h
	-DPACKAGE="gnuplot"
	-DPACKAGE_BUGREPORT=""
	-DPACKAGE_NAME="gnuplot"
	-DPACKAGE_STRING="gnuplot 4.2.5"
	-DPACKAGE_TARNAME="gnuplot"
	-DPACKAGE_VERSION="4.2.5"
	-DRETSIGTYPE=void
	-DSELECT_TYPE_ARG1=int
	-DVERSION="4.2.5"
	-I /usr/local/include
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include-fixed
	-I /usr/include/x86_64-linux-gnu
	-I /usr/include
'
#	-D'\''SELECT_TYPE_ARG234=(fd_set'\ '*)'\''
#	-D'\''SELECT_TYPE_ARG5=(struct timeval'\ '*)'\''

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################

for i in `find "$srcPath" -type f -name "*.c"`;
do
	echo './jcpp.sh' $i $flags $partialPreprocFlags
    ./jcpp.sh $i $flags
done

for i in `find "$srcPath" -type f -name "*.h"`;
do
    ./jcpp.sh $i $flags
done

