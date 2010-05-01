namespace Testing
{
	class Tester
	{
	/*
		public string[] sArray = null;

		sbyte[][,] DoSomething()
		{
			return null;
		}
	*/
	}
}


class TopLevel
{
	public string sArrayInTopLevel = null;
/*
	public string[] sArrayInTopLevel = null;

	sbyte[][,] DoSomethingInTopLevel()
	{
		return null;
	}
*/
	~TopLevel()
	{
		return
     new string[,,] { {"help", "me", "please"} }
     ;
	}

	public static PublicTopLevelClass operator+(PublicTopLevelClass x1, PublicTopLevelClass x2) 
	{ 
		return x1; 
	}

	public static explicit operator int(PublicTopLevelClass x) { /* . */ } 

  	public static implicit operator long(PublicTopLevelClass x) { /* . */ }

	[Serializable]
	public event EventHandler ClickHandler1, ClickHandler;
	
	[Serializable]
	private static readonly object ClickEvent = new object();
	object ClickEvent = new object();
	object[] ClickEvent;
      
	[Serializable]
	public event EventHandler Click 
	{
		 [Serializable]
		 add 
		 {
		    Events.AddHandler(ClickEvent, value);
		 }
		 remove 
		 {
		    Events.RemoveHandler(ClickEvent, value);
		 }
	}
}

struct TopLevel
{
	public const int sIntInTopLevel = 0;

	private enum EnumAsLong : long
	{
		[AuthorAttribute(true, 1L)]
		Enum1,
		Enum2,
	}
}

enum TopLevel : long
{
	e1,
	e2,
}
