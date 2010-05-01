/*
[The "BSD licence"]
Copyright (c) 2002-2005 Kunle Odutola
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


// bug(?) in DotGNU 0.6 - "using antlr" will workaround the problem.
#if __CSCC__
using antlr;
#endif

namespace Kunle.CSharpParser
{
	using System;
	using FileInfo						= System.IO.FileInfo;
	using Directory						= System.IO.Directory;
	using FileStream					= System.IO.FileStream;
	using FileMode						= System.IO.FileMode;
	using FileAccess					= System.IO.FileAccess;
	using Stream						= System.IO.Stream;
	using StreamReader					= System.IO.StreamReader;
	using StringWriter 					= System.IO.StringWriter;
	
	using BaseAST						= antlr.BaseAST;
	using CommonAST						= antlr.CommonAST;
	using ASTFactory					= antlr.ASTFactory;
	using RecognitionException			= antlr.RecognitionException;
	using AST							= antlr.collections.AST;
	using ASTFrame						= antlr.debug.misc.ASTFrame;
	using IToken						= antlr.IToken;
	using TokenStream					= antlr.TokenStream;
	using TokenStreamSelector			= antlr.TokenStreamSelector;
	using TokenStreamHiddenTokenFilter	= antlr.TokenStreamHiddenTokenFilter;

	
	class AppMain
	{
		
		internal static bool showTree  = false;
		internal static bool printTree = false;
		internal static bool useFlexLexer = false;
		internal static bool displayTokens = false;
	
		public static void  Main(string[] args)
		{
			long startTime = DateTime.Now.Ticks;
			// Use a try/catch block for parser exceptions
			try
			{
				// if we have at least one command-line argument
				if (args.Length > 0)
				{
					Console.Error.WriteLine("Parsing...");				
					// for each directory/file specified on the command line
					for (int i = 0; i < args.Length; i++)
					{
						if (args[i].ToLower().Equals("-showtree"))
						{
							showTree = true;
						}
						else if (args[i].ToLower().Equals("-prettyprint"))
						{
							printTree = true;
						}
						else if (args[i].ToLower().Equals("-flex"))
						{
							useFlexLexer = true;
						}
						else if (args[i].ToLower().Equals("-tokens"))
						{
							displayTokens = true;
						}
						else
						{
							doFile(new FileInfo(args[i])); // parse it
						}
					}
				}
				else
					Console.Error.WriteLine("Usage: kcsparse [-tokens] [-flex] [-showtree] [-prettyprint] " + "<directory or file name>");
			}
			catch (System.Exception e)
			{
				Console.Error.WriteLine("exception: " + e);
				Console.Error.WriteLine(e.StackTrace); // so we can get stack trace
			}
			double elapsedTime = ((DateTime.Now.Ticks - startTime) / TimeSpan.TicksPerMillisecond) / 1000.0;
			System.Console.Out.WriteLine("");
			System.Console.Out.WriteLine("");
			System.Console.Out.WriteLine("Total run time was {0} seconds.", elapsedTime);
		}
		
		
		// This method decides what action to take based on the type of
		//   file we are looking at
		public static void  doFile(FileInfo f)
		{
			// If this is a directory, walk each file/dir in that directory
			if (Directory.Exists(f.FullName))
			{
				string[] files = Directory.GetFileSystemEntries(f.FullName);
				 for (int i = 0; i < files.Length; i++)
					doFile(new FileInfo(f.FullName + "\\" + files[i]));
			}
			else if ((f.Name.Length > 3) && f.Name.Substring(f.Name.Length - 3).Equals(".cs"))
			{
				Console.Error.WriteLine("   " + f.FullName);
				parseFile(f.Name, new FileStream(f.FullName, FileMode.Open, FileAccess.Read));
			}
		}
		
		// Here's where we do the real work...
		public static void  parseFile(string f, Stream s)
		{
			try
			{
				// Define a selector that can switch from the C# codelexer to the C# preprocessor lexer
				TokenStreamSelector selector = new TokenStreamSelector();

				TokenStream lexer;
				// Create a scanner that reads from the input stream passed to us
				if (useFlexLexer)
				{
					CSharpFlexLexer flexLexer = new CSharpFlexLexer(new StreamReader(s));
					flexLexer.SetFileInfo(new FileInfo(f));
					lexer = flexLexer;
				}
				else
				{
					CSharpLexer antlrLexer = new CSharpLexer(new StreamReader(s));

					antlrLexer.Selector = selector;
					antlrLexer.setFilename(f);
					CSharpPreprocessorLexer preproLexer = new CSharpPreprocessorLexer(antlrLexer.getInputState());
					preproLexer.Selector = selector;
					CSharpPreprocessorHooverLexer hooverLexer = new CSharpPreprocessorHooverLexer(antlrLexer.getInputState());
					hooverLexer.Selector = selector;

					// use the special token object class
					antlrLexer.setTokenCreator(new CustomHiddenStreamToken.CustomHiddenStreamTokenCreator());
					antlrLexer.setTabSize(1);
					preproLexer.setTokenCreator(new CustomHiddenStreamToken.CustomHiddenStreamTokenCreator());
					preproLexer.setTabSize(1);
					hooverLexer.setTokenCreator(new CustomHiddenStreamToken.CustomHiddenStreamTokenCreator());
					hooverLexer.setTabSize(1);
			
					// notify selector about various lexers; name them for convenient reference later
					selector.addInputStream(antlrLexer,       "codeLexer");
					selector.addInputStream(preproLexer, "directivesLexer");
					selector.addInputStream(hooverLexer, "hooverLexer");
					selector.select("codeLexer"); // start with main the CSharp code lexer
					lexer = selector;
				}

				// create the stream filter; hide WS and SL_COMMENT
				TokenStreamHiddenTokenFilter filter; // = new TokenStreamHiddenTokenFilter(lexer);
				if (displayTokens)
					filter = new TokenStreamHiddenTokenFilter(new LoggingTokenStream(lexer));
				else
					filter = new TokenStreamHiddenTokenFilter(lexer);

				filter.hide(CSharpTokenTypes.WHITESPACE);
				filter.hide(CSharpTokenTypes.NEWLINE);
				filter.hide(CSharpTokenTypes.ML_COMMENT);
				filter.hide(CSharpTokenTypes.SL_COMMENT);

				// Create a parser that reads from the scanner
				CSharpParser parser = new CSharpParser(filter);

				// create trees that copy hidden tokens into tree also
				parser.setASTNodeClass(typeof(ASTNode).FullName);
				parser.setASTFactory(new ASTNodeFactory());
				CSharpParser.initializeASTFactory(parser.getASTFactory());
				parser.setFilename(f);
				//parser.getASTFactory().setASTNodeCreator(new ASTNode.ASTNodeCreator());
			
				// start parsing at the compilationUnit rule
				long startTime = DateTime.Now.Ticks;
				parser.compilationUnit();
				double elapsedTime = ((DateTime.Now.Ticks - startTime) / TimeSpan.TicksPerMillisecond) / 1000.0;
				System.Console.Out.WriteLine("Parsed {0} in: {1} seconds.", f, elapsedTime);
			
				// do something with the tree
				Console.Error.WriteLine("       ... calling doTreeAction:");
				doTreeAction(f, (ASTNode)parser.getAST(), parser.getTokenNames());
			}
			catch (System.Exception e)
			{
				Console.Error.WriteLine("parser exception: " + e);
				Console.Error.WriteLine(e.StackTrace); // so we can get stack trace		
			}
		}
		
		public static void  doTreeAction(string f, ASTNode t, string[] tokenNames)
		{
			if (t == null)
				return ;
			if (showTree)
			{
				BaseAST.setVerboseStringConversion(true, tokenNames);
				ASTNode r = (ASTNode) new ASTNodeFactory().create(0, "AST ROOT");
				r.setFirstChild(t);
				ASTFrame frame = new ASTFrame("C# AST for file [" +f+ "]", r);
				frame.ShowDialog();
				//frame.Visible = true;
				// System.out.println(t.toStringList());
			}
			else if (printTree)
			{
				StringWriter writer = new StringWriter();
				CSharpPrettyPrinter printer = new CSharpPrettyPrinter();
				printer.setASTFactory(new ASTNodeFactory());
				CSharpParser.initializeASTFactory(printer.getASTFactory());

				long startTime = DateTime.Now.Ticks;
				printer.Print(writer, t, string.Empty);
				double elapsedTime = ((DateTime.Now.Ticks - startTime) / TimeSpan.TicksPerMillisecond) / 1000.0;
				System.Console.Out.WriteLine(writer.ToString());
				System.Console.Out.WriteLine("");
				System.Console.Out.WriteLine("");
				System.Console.Out.WriteLine("Pretty-printed {0} in: {1} seconds.", f, elapsedTime);
			}
		}
	}

	class LoggingTokenStream : TokenStream
	{
		TokenStream source;

		public LoggingTokenStream(TokenStream source)
		{
			this.source = source;
		}

		public IToken nextToken()
		{
			IToken tok = source.nextToken();
			if (tok != null)
				Console.Out.WriteLine(tok.ToString());

			return tok;
		}
	}
}
