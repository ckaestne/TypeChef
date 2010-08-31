#! /usr/bin/perl

# Remember that CGI programs have to close out the HTTP header
# (with a pair of newlines), after giving the Content-type:
# and any other relevant or available header information.

# Unlike CGI programs running under Apache, CGI programs under Boa
# should understand some simple HTTP options.  The header (and the
# double-newline) should not be printed if the incoming request was
# in HTTP/0.9.  Also, we should stop after the header if
# REQUEST_METHOD == "HEAD".  Under Apache, nph- programs also have
# to worry about such stuff.

# Feb 3, 2000 -- updated to support POST, and avoid passing
# Malicious HTML Tags as described in CERT's CA-2000-02 advisory.

if ($ENV{"SERVER_PROTOCOL"} ne "HTTP/0.9") {
 print "Content-type: text/html; charset=ISO-8859-1\r\n\r\n";
}

exit 0 if ($ENV{"REQUEST_METHOD"} eq "HEAD");

print "<html><head><title>Boa CGI test</title></head><body>\n";
print "<H2>Boa CGI test</H2>\n\n";

$now=`date`;
chomp($now);

print "Date: $now\n";
print "<p>\n";

print "Method: $ENV{\"REQUEST_METHOD\"}\n";
print "<p>\n";

print "<table border=1>\n";
print "<tr><td>Basic GET Form:<br>";
print " <form method=\"get\">\n\
  <input type=\"text\" name=\"parameter_1\" size=5 maxlength=5>\
    <select name=\"select_1\">\
      <option>foo</option>\
      <option>bar</option>\
    </select>\
  <input type=\"submit\" NAME=SUBMIT VALUE=\"Submit\">\
 </form>";
print "</td>";
print "<td>Basic POST Form:<br>";
print "<form method=\"post\">\n\
  <input type=\"text\" name=\"parameter_1\" size=5 maxlength=5>\
    <select name=\"select_1\">\
      <option>foo</option>\
      <option>bar</option>\
    </select>\
  <input type=\"submit\" NAME=SUBMIT VALUE=\"Submit\">\
  </form>";
print "</td>";
print "</tr>\n";
print "<tr><td colspan=2>Sample ISINDEX form:<br>\n";
print "<a href=\"$ENV{\"SCRIPT_NAME\"}?param1+param2+param3\">$ENV{\"SCRIPT_NAME\"}?param1+param2+param3</a>\n";
print "</td></tr>";
print "</table>\n";

print "<p>Query String: $ENV{\"QUERY_STRING\"}\n";

# arguments list
print "<p>\nArguments:\n<ol>\n";
if ($#ARGV >= 0) {
       while ($a=shift(@ARGV)) {
        $a=~s/&/&amp;/g;
        $a=~s/</&lt;/g;
        $a=~s/>/&gt;/g;
        print "<li>$a\n";
       }
}
print "</ol>\n";

# environment list
print "<P>\nEnvironment:\n<UL>\n";
foreach $i (keys %ENV) {
        $a=$ENV{$i};
        $a=~s/&/&amp;/g;
        $a=~s/</&lt;/g;
        $a=~s/>/&gt;/g;
        $i=~s/&/&amp;/g;
        $i=~s/</&lt;/g;
        $i=~s/>/&gt;/g;        
        print "<li>$i = $a\n";
}
print "</UL>\n";

if ($ENV{REQUEST_METHOD} eq "POST") {
    print "Input stream:<br><hr><pre>\n";
    while (<stdin>) {
	s/&/&amp;/g;
	s/</&lt;/g;
	s/>/&gt;/g;
        print "$_";
    }
    print "</pre><hr>\n";
} else {
    print "No input stream: (not POST)<p>";
}

print "id: ", `id`, "\n<p>\n";

if ($ENV{"QUERY_STRING"}=~/ident/ && $ENV{"REMOTE_PORT"} ne "") {

# Uses idlookup-1.2 from Peter Eriksson  <pen@lysator.liu.se>
# ftp://coast.cs.purdue.edu/pub/tools/unix/ident/tools/idlookup-1.2.tar.gz
# Could use modification to timeout and trap stderr messages
	$a="idlookup ".
	   $ENV{"REMOTE_ADDR"}." ".$ENV{"REMOTE_PORT"}." ".$ENV{"SERVER_PORT"};
	$b=qx/$a/;
	print "ident output:<br><pre>\n$b</pre>\n";
}

print "\n<EM>Boa http server</EM>\n";
print "</body></html>\n";

exit 0;

