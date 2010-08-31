all:   boa.html boa_toc.html boa.dvi boa.info boa.txt

boa.info:	boa.texi
	makeinfo --number-sections boa.texi

boa.dvi:	boa.texi
	texi2dvi --clean boa.texi

boa.pdf:	boa.texi
	texi2dvi --pdf --clean boa.texi

boa.ps:	boa.dvi
	dvips -o boa.ps boa.dvi

boa_toc.html:	boa.texi
	texi2html -split_chapter -menu boa.texi

boa.html:	boa.texi
	makeinfo --html --number-sections --no-split -o - boa.texi | \
        sed -e 's/Node:.*//' | sed -e 's/Next:.*//' | \
        sed -e 's/Previous:.*//' | sed -e 's/Up:.*//' > boa.html

boa.txt:	boa.texi
	makeinfo --no-headers --no-split -o boa.txt boa.texi


cleanup:
	rm -f boa.{cp,fn,fns,ky,log,pg,toc,tp,vr,vrs,aux} *~

clean: cleanup
	rm -f boa.{html,txt,dvi,ps,pdf,info} boa_*.html
