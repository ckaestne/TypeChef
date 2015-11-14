f (char *p)
{
  char c;

  c = *++p;
  if (c != ' ')
    return 0;
  for (;1;)
    {
      c = *p;
      if (g (c))
	p++;
      else
	{
	  if (c == ' ')
	    break;
	  else
	    return 0;
	}
    }
  return 1;
}
