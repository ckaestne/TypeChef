classfiles = LineObject.class PreprocessorInfoChannel.class StdCParser.class StdCLexer.class GnuCParser.class GnuCLexer.class 
javafiles = CSymbolTable.java TNode.java TNodeFactory.java CToken.java LineObject.java PreprocessorInfoChannel.java StdCParser.java StdCLexer.java GnuCParser.java GnuCLexer.java 


all : $(javafiles) 
	


clean : 
	


StdCParser.java StdCLexer.java : StdCParser.g
	java -cp ../../../bin antlr.Tool StdCParser.g


GnuCParser.java GnuCLexer.java : GnuCParser.g StdCParser.g
	java -cp ../../../bin antlr.Tool -glib "StdCParser.g" GnuCParser.g


GnuCTreeParser.java : GnuCTreeParser.g
	java -cp ../../../bin antlr.Tool GnuCTreeParser.g




.SUFFIXES: .java .class

.java.class : 
	javac $<



