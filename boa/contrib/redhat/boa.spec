Summary: a single-tasking high performance http server
Name: boa
Version: 0.94.9
Release: 1
Group: System Environment/Daemons
Source: http://www.boa.org/boa-%{version}.tar.gz
Copyright: GNU general public license
Requires: /etc/mime.types
Prereq: /sbin/chkconfig, man, gzip
Provides: setup webserver
Buildroot: /usr/tmp/boa

%description
Boa is a single-tasking HTTP server. That means that
unlike traditional web servers, it does not fork for each
incoming connection, nor does it fork many copies of
itself to handle multiple connections. It internally mul­
tiplexes all of the ongoing HTTP connections, and forks
only for CGI programs (which must be separate processes.)
Preliminary tests show Boa is more than twice as fast as
Apache.

Boa was created in 1991 by Paul Phillips <psp@well.com>. It is now being
maintained and enhanced by Larry Doolittle <ldoolitt@boa.org>
and Jon Nelson <jnelson@boa.org>.

For more information (including installation instructions) examine
the file docs/boa.txt or docs/boa.dvi, point your web browser to docs/boa.html,
or visit the Boa homepage at

    http://www.boa.org/ 
    
%changelog
* Thu Aug 6 2000 Jonathon D Nelson <jnelson@boa.org>
- revamp packaging based upon examples provided by 
  Jules Stuifbergen <jules@zjuul.net> and others

%prep
%setup -T -b 0
%build
(cd src && CFLAGS=$RPM_OPT_FLAGS ./configure --prefix=$RPM_BUILD_ROOT)
(cd src && make)
(cd docs && gzip -c boa.8 > boa.8.gz)
(cd docs && make boa.html)

%clean
rm -rf $RPM_BUILD_ROOT

%install
rm -rf $RPM_BUILD_ROOT

mkdir -p $RPM_BUILD_ROOT/etc/{boa,logrotate.d}
mkdir -p $RPM_BUILD_ROOT/usr/sbin
mkdir -p $RPM_BUILD_ROOT/home/httpd/{html,cgi-bin}
mkdir -p $RPM_BUILD_ROOT/var/log/boa
mkdir -p $RPM_BUILD_ROOT/usr/lib/boa
mkdir -p $RPM_BUILD_ROOT/usr/man/man8
mkdir -p $RPM_BUILD_ROOT/etc/rc.d/init.d

install -m755 src/boa $RPM_BUILD_ROOT/usr/sbin/
install -m755 src/boa_indexer $RPM_BUILD_ROOT/usr/lib/boa/
install -m644 redhat/boa.conf $RPM_BUILD_ROOT/etc/boa/
install -m755 redhat/boa.init $RPM_BUILD_ROOT/etc/rc.d/init.d/boa
mv docs/boa.8.gz $RPM_BUILD_ROOT/usr/man/man8/
install -m644 redhat/boa.logrotate $RPM_BUILD_ROOT/etc/logrotate.d/boa

touch $RPM_BUILD_ROOT/var/log/boa/{error,access}_log

%post
/sbin/chkconfig boa reset

%preun
/etc/rc.d/init.d/boa stop
/sbin/chkconfig --del boa

%files
%defattr(-,root,root)
%dir /home/httpd/html
%dir /home/httpd/cgi-bin
%dir /var/log/boa
%doc Gnu_License README docs/* 
%doc /usr/man/man8/*
%config /etc/boa/boa.conf
%config /etc/rc.d/init.d/boa
%config /etc/logrotate.d/boa
%attr(600,nobody,nobody)/var/log/boa/error_log
%attr(600,nobody,nobody)/var/log/boa/access_log
/usr/sbin/boa
/usr/lib/boa/boa_indexer
