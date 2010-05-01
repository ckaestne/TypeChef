// $ANTLR 2.7.2a2 (20020112-1): "CSharp.g" -> "CSharpParser.cs"$
#define ConditionalSymbol

#warning A message about the warning
#define ConditionalSymbol
#if Symbol1
using System.Globalization.SomeClass;
#endif
using TokenBuffer              = antlr.TokenBuffer;
#warning A message about the warning
using AST                      = antlr.collections.AST;
using ASTArray                 = antlr.collections.impl.ASTArray;
#warning A message about the warning

#if Symbol1
[assembly: AssemblyTitle("antlr.runtime"), AssemblyDescription("ANTLR Runtime for .NET"), AssemblyCompany("www.antlr.org")]
[assembly: AssemblyTitle("antlr.runtime"), AssemblyDescription("ANTLR Runtime for .NET"), AssemblyCompany("www.antlr.org")]
#elif Symbol1 || Symbol2
[assembly: AssemblyTitle("antlr.runtime"), AssemblyDescription("ANTLR Runtime for .NET"), AssemblyCompany("www.antlr.org")]
#elif Symbol2
[assembly: AssemblyTitle("antlr.runtime"), AssemblyDescription("ANTLR Runtime for .NET"), AssemblyCompany("www.antlr.org")]
#else
[assembly: AssemblyTitle("antlr.runtime"), AssemblyDescription("ANTLR Runtime for .NET"), AssemblyCompany("www.antlr.org")]
#endif
[assembly: AssemblyProduct(true, ( 1 * 25.0 - -12) / (12 ^ 2))]
[assembly: AssemblyCopyright(xyz = (abc = 15*-2-4+7))]

[assembly: AssemblyVersion("2.7.2.*"), AssemblyVersion(true, null)]

namespace Kunle.LanguageProcessors
{
	class CompilerController : IController
	{
		public             const int    jfk = 1L;
		private            const object obj = null, camel = null;
		protected internal const string str = "<constant>";
		
		#region Implementation of ICloneable		
		private string 		str = null;
		private string[] 	sArray = null;
		private string[][] 	arrayOfStrArray = null;
		private char* 	ptr2Str;
		private char*[]	arrayOfStrPtr = null;
		private char*[][] arrayOfArrayOfStrPtr = null;

		// these are invalid in ECMA - csc rejects them.
		//private char[]* 	ptr2sArray = null;
		//private char[][]*	ptr2ArrayOfStrArray = null;
		//private char[]*[]	arrayOfPtr2StrArray = null;
		public static  ASTArray ast;
		#endregion Implementation of ICloneable

int
i
;		
		
		public void SomeMethod()
		{
			//this.pstring = "Hello";
			pstring = "Hello";
		}		
		public void SomeNamespace.ISomeInterface.SomeMethod()
		{
			//this.pstring = "Hello";
			pstring = "Hello";
		}		
	}

	namespace Kunle.NET
	{
		[ClassWithoutBasesAttribute(10L), ClassWithoutBasesAttribute(true, 1.0F), ClassWithoutBasesAttribute(false, "string")]
		[ClassWithoutBasesSection2Attribute(50L)]
		class ClassWithoutBases
		{
			[AbstractMethodAttribute(false)]
			public int AbstractMethod(int param1, [AbstractMethod_param2Attribute(false)]string param2);

			public int AbstractMethod(int param1, string param2, params int[] args);
			
			public int ParameterkindsMethod(int param1, ref int param2, out int param3, params int[] args);
			public int ParameterkindsMethod2(int[] param1, ref int[] param2, out int[] param3, params int[] args);
			
			static private int AbstractMethod2(params int[][][] args);
			
			//int AbstractMethod(params int[] args, int i);
			
			// Nested class
			[ClassWithManyBasesButFirstIsStringAttribute("<suss>")]
			class ClassWithManyBasesButFirstIsString : string, Base1, Base2, Base3, Base4
			{
				public static IntVector operator++(IntVector iv) { 
  					IntVector temp = new IntVector(iv.Length);
  					IntVector temp = new IntVector[i];
    				for (int i = 0; i < iv.Length; ++i)
      					temp[i] = iv[i] + 1;
    				return temp;
  				}
				public static IntVector operator--(IntVector iv) ;
  			}
		
			// Nested Interface
			interface InterfaceWithManyBases : Base1, Base2, Base3, Base4
			{
			}
			// Nested Interface
			interface InterfaceWithNoBases
			{
			}

			// Nested Struct
			[AuthorAttribute(true, 1L)]
			struct StructWithManyBases : Base1, Base2, Base3, Base4
			{
			}
		
			// Nested Struct
			[AuthorAttribute(true, 1L)]
			struct StructWithNoBases
			{
			}
		
			// Nested Enum
			[AuthorAttribute(true, 1L)]
			private enum EnumAsLong : long
			{
				[AuthorAttribute(true, 1L)]
				Enum1,
				Enum2,
			}

			// Nested Enum
			private enum EnumWithNoBase
			{
				Enum1,
				Enum2,
			}
			
			[AuthorAttribute(true, 1L)]
			public delegate string DelegateThatTakesOneIntParam( int myInt );
			public delegate void   DelegateThatTakesOneIntOneStringParam( int myInt, string myString );
			[AuthorAttribute(true, 1L)]
			public delegate object DelegateThatTakesNoParams( );

		}
		class ClassWithManyManyBases : Base1, Base2, Base3, Base4
		{
		}
	}
}

namespace Kunle.Catalania.Parser
{
}

