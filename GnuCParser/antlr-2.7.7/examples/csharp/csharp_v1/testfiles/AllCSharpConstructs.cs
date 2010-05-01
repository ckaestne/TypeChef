using System;
using File = System.IO.File;

[assembly: AssemblyTitle("antlr.runtime")]
[assembly: AssemblyDescription("ANTLR Runtime for .NET")]
[assembly: AssemblyCompany("www.antlr.org")]
[assembly: AssemblyVersion("2.7.2.4")]
[assembly: CLSCompliantAttribute(true)]


[Serializable]
public class PublicTopLevelClass
{
	public PublicTopLevelClass()	
	{
		for (int i = 0, j = 0; i <= 20; i++, j++)
		{
			j = j * i;
		}
		StringVar1 = s;
	}
	
	public PublicTopLevelClass()	{}
	
	public ~PublicTopLevelClass()	{}
	
	public ~PublicTopLevelClass()
	{
		for (int i = 0, j = 0; i <= 20; i++, j++)
		{
			j = j * i;
		}
		StringVar1 = s;
	}
	
	public PublicTopLevelClass(string s) : base(s)
	{
		StringVar1 = s;
	}

	public static PublicTopLevelClass operator++(PublicTopLevelClass iv) { 
		PublicTopLevelClass temp = new PublicTopLevelClass(iv.Length);
		PublicTopLevelClass temp = new PublicTopLevelClass[i];
		for (int i = 0; i < iv.Length; ++i)
			temp[i] = iv[i] + 1;
		return temp;
	}
	
	public static PublicTopLevelClass operator--(PublicTopLevelClass iv) ;
	
	public static PublicTopLevelClass operator+(PublicTopLevelClass x1, PublicTopLevelClass x2) 
	{ 
		return x1; 
	}
	public static PublicTopLevelClass operator-(PublicTopLevelClass x1, PublicTopLevelClass x2) ;

	public static explicit operator int(PublicTopLevelClass x) { /* . */ } 
  	public static implicit operator long(PublicTopLevelClass x) { /* . */ }
  	
	public void Method1()
	{
		try
		{
			
			for (int i = 0, j = 0; i <= 20; i++, j++)
			{
				j = j * i;
			}
		}
		catch(Exception e)
		{
			Console.Error.WriteLine(e.StackTrace);
		}
		catch(Exception)
		{
			Console.Error.WriteLine(e.StackTrace);
		}
		catch
		{
			Console.Error.WriteLine(Environment.StackTrace);
		}
		finally
		{
			Console.Error.WriteLine(Environment.StackTrace);
		}
	}

	public void Method1(string s)
	{
		for (int i = 0, j = 0; i <= 20; i++, j++)
		{
			j = j * i;
		}
		StringVar1 = s;

		try
		{
			
			for (int i = 0, j = 0; i <= 20; i++, j++)
			{
				j = j * i;
			}
		}
		finally
		{
			Console.Error.WriteLine(Environment.StackTrace);
		}
	}

	public    const int 	TopLevelIntConst = 1;
	internal  const uint 	TopLevelUIntConst = 1u, TopLevelUIntConst2 = 2U;
	[Serializable]
	private   const byte	TopLevelByteConst = 1;
	protected const sbyte	TopLevelSByteConst = 1;
	public    const long	TopLevelLongConst = 1L;
	public    const long	TopLevelLongConst = 1l;
	public    const ulong	TopLevelULongConst = 1UL;
	public    const ulong	TopLevelULongConst = 1LU;
	public    const ulong	TopLevelULongConst = 1ul;
	public    const ulong	TopLevelULongConst = 1lu;
	
	public    int    IntVariable;
	[Serializable]
	public    int    IntVariableWithInit = 1;
	[Serializable]
	private   string 	StringVar1, StringVar2 = "", StringVar3, StringVar4 = "init";
	//private   string[] 	StringVar1, StringVar2 = { "" }, StringVar4 = new string[3];
	private   string[] 	StringVar1, StringVar2, StringVar4;
	private   string[][][] 	StringVar1, StringVar2, StringVar4;
	private   string[,,,,] 	StringVar1, StringVar2, StringVar4;
	private   string[] 	StringVar1;
	private   string[] 	StringVar2 = { "" };
	private   string[] 	StringVar4 = new string[3] { "1", "2", "3" };

	private   string**[,,] 	StringVar4;
	private   string** 	ptrPtrString;
	private   int* 		ptrToInt;
	
	[Serializable]
	public event EventHandler ClickHandler1, ClickHandler;
	
	[Serializable]
	private static readonly object ClickEvent = new object();
      
      // Defines the Click event using the event property syntax.
      // The Events property stores all the event delegates of
      // a control as name/value pairs. 
      [Serializable]
      public event EventHandler Click 
      {
         // When a user attaches an event handler to the Click event 
         // (Click += myHandler;), the add method 
         // adds the handler to the 
         // delegate for the Click event (keyed by ClickEvent 
         // in the Events list).
         [Serializable]
         add 
         {
            Events.AddHandler(ClickEvent, value);
         }
         // When a user removes an event handler from the Click event 
         // (Click -= myHandler;), the remove method 
         // removes the handler from the 
         // delegate for the Click event (keyed by ClickEvent 
         // in the Events list).
         remove 
         {
            Events.RemoveHandler(ClickEvent, value);
         }
      }
      
      
      [Serializable]
		public int IntProperty1
		{
			[Serializable]
			get { return IntVariable;  }
			[Serializable]
			set { IntVariable = value; }
		}
		public int IntProperty2
		{
			get { return IntVariable;  }
			set { IntVariable = value; }
		}
		public string ReadOnlyStringProperty
		{
			[Serializable]
			get { return StringVar2;  }
		}
		[Serializable]
		public string WriteOnlyStringProperty
		{
			set { StringVar3 = value; }
		}

		public int this[int i]
		{
			[Serializable]
			get { return IntVariable;  }
			[Serializable]
			set { IntVariable = value; }
		}
		[Serializable]
		public int this[string s, int i]
		{
			[Serializable]
			get { return IntVariable;  }
			[Serializable]
			set { IntVariable = value; }
		}
		[Serializable]
		public int IConstructorInfo.this[string s, int i]
		{
			[Serializable]
			get { return IntVariable;  }
			[Serializable]
			set { IntVariable = value; }
		}
}

[Serializable]
internal class InternalTopLevelClass
{
	private InternalTopLevelClass()	{}
	
	public int MethodThatReturnsVoid()		{}

	public string MethodThatReturnsVoid(int i, uint ui, short s, ushort us) 
	{
		switch(Type)
		{
			case INT:
				DoIntCase();
				break;
			case REAL:
			case FLOAT:
			case DOUBLE:
				DoRealCase();
				break;
			default:
				DoDefaultThing();
				break;
		}
		
		do
		{
			CallSomeMethod();
		} while (anObj.SomeAccessor());
		
		while (aPointer != null)
			try
			{
				CallAFunction();
				lock(this)
				{
					DoSomeStuff();
				}
				CallAnotherFunction();
			}
			catch {}
			
		while (aPointer != null)
		{
			CallAFunction();
			lock(this)
			{
				DoSomeStuff();
			}
			CallAnotherFunction();
		}
	}

	public string[] MethodThatReturnsVoid(long i, ulong ul) 
	{
		if (SomeCondition)
			DoSomething();
		else
			DoSomethingElse();
			
		if (SomeValue == IF)
		{
			DoSomethingIf();
		}
		else if (SomeValue == ELSE_IF_1)
		{
			DoSomethingElseIf_1();
		}
		else if (SomeValue == ELSE_IF_2)
		{
			if (SomeCondition)
				DoSomething();
			else
				DoSomethingElse();
			DoSomethingElseIf_2();
		}
		else
		{
			DoSomethingElse();
		}
	}

	public void MethodThatReturnsVoid(string s, char c) 
	{
		foreach (Assembly assem in AppDomain.CurrentDomain.GetAssemblies())
		{
			CallSomeMethod();
		}
		foreach (Assembly assem in AppDomain.CurrentDomain.GetAssemblies())
			checked
			{
				SomeCodeGoesHere();
			}
	}

	public void MethodThatReturnsVoid(byte b, sbyte sb) {}

	public void MethodThatReturnsVoid(int[] iarray, string[] sarray) {}

}


namespace TopLevel
{
	namespace SecondLevel
	{
		using System;
		using System.Collections;
	}
}

namespace Server.Extensions
{
}

namespace TopLevel.SecondLevel
{
	using System.Text;
	using Debug = System.Diagnostics.Debug;
}

namespace Client.Core
{
}

namespace Server.Persistence
{
}

namespace TopLevel.SecondLevel.ThirdLevel
{
}
