#!/usr/bin/perl

#  webindex, a world wide web directory generating program
#  Copyright (C) 1997 Larry Doolittle  <ldoolitt@jlab.org>
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 1, or (at your option)
#  any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# likely future additions:
#   incorporate user-supplied README files
#   use config file to supply icons
#   other formats and sort options, including one giving dates
#   more robust handling of GCOS field
#   nicer date format in footer

# usable as it stands, 11-Feb-1997 lrd
# $Id: webindex.pl,v 1.2 1999/10/12 14:49:08 jon Exp $


# *****configure me******
$index="INDEX.html";
#       ^^^^^^^^^^
# The line above defines the output file name from webindex.
# You should make sure it lines up with the configuration of
# the web server on your system, i.e., the DirectoryIndex
# directive in NCSA, Apache, or Boa.

$magic="This file is unmodified webindex output";

($name,$passwd,$uid,$gid,$quota,$comment,$gcos,$dir,$shell)
  =getpwuid($<);

# Title of the page - there is no unique mapping from the unix
# filename to URL space.  Take an easy way out and just give
# the name of the directory.
# Even this can be confused, look what happens if user joe
# makes a public_html/foo directory, and runs webindex on it.
# Now user bill makes a symbolic link from his public_html/bar
# ~joe/public_html/foo.  This directory now has two URLs,
# http://some.server/~joe/foo/ and http://some.server/~bill/bar/ .
# The name that we generate is foo, which is only half right :-(

@A=split("/",`pwd`);
$here=pop(@A);

# We will happily create a new $index file, or overwrite
# one if it has the magic comment in it.  We won't overwrite
# a carefully hand-crafted one, though.

if (open CHK,"<$index") {
  while (<CHK>) {
     last if ($found = / $magic /);
  }
  close(CHK);
  die "existing $index not overwritten" if (!$found);
}

opendir DIR, "." || die "opendir";
open OUT,">$index" || die "fopen";

print OUT "<html><head>\n<title>Index of $here</title>\n</head>\n\n";

# This comment syntax should be compatible with all HTML versions
print OUT "<!-- $magic -->\n";

print OUT "<body>\n\n<h2>Index of $here</h2>\n\n";

@A=sort(readdir(DIR));

print OUT "<h3>Directories</h3>\n<ul>\n";
print OUT  "<li><A HREF=\"../\">Parent Directory</a>\n";
for $a (@A) {
   next if ($a eq ".");
   next if ($a eq "..");
   ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
              $atime,$mtime,$ctime,$blksize,$blocks) = stat($a);
   if ( -d _) {
      print OUT  "<li><A HREF=\"$a/\">$a</A>\n"; 
   }
}
print OUT "</ul>\n";

$found=0;
for $a (@A) {
   next if ($a eq ".");
   next if ($a eq "..");
   next if ($a eq $index);
   ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
              $atime,$mtime,$ctime,$blksize,$blocks) = stat($a);
   if (! -d _) {
      if (!$found) {
         $found=1;
         print OUT "<h3>Files</h3><ul>\n";
      }
      print OUT  "<li><A HREF=\"$a\">$a</A> ($size bytes)\n"; 
   }
}
print OUT "</ul>\n" if $found;

closedir(DIR);
$now=localtime();
($who)=split(/,/,$gcos);
print OUT  "<hr>Index created by <a href=\"/~$name/\">$who</a>
 with <a href=\"http://recycle.jlab.org/webindex/\">webindex</a> at $now<br>\n";

print OUT "</body>\n</html>\n";

close OUT;
