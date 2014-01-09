package de.fosd.typechef.parser.common


import de.fosd.typechef.error.Position


class JPosition(file: String, line: Int, col: Int) extends Position {
    def getFile: String = file

    def getLine: Int = line

    def getColumn: Int = col
    
    override def toString = getFile + ":" + getLine + ":" + getColumn + ":" + positionToLine(file, col)
    
    /**
	 * Returns the line containing the offset position in a file.
	 * Line starts from 1.
	 */
    def positionToLine(file: String, offset: Int): Int = {
        val absFile = "/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12/" + file
        
        if (!new java.io.File(absFile).exists())
          return -1;
        
		var lines = new java.util.ArrayList[Integer]();
		val pattern = java.util.regex.Pattern.compile("^", java.util.regex.Pattern.MULTILINE);
		val matcher = pattern.matcher(readStringFromFile(absFile));
		var line = 0;
		while (matcher.find()) {
			line += 1;
			if (matcher.start() > offset)
				return line - 1;
			lines.add(matcher.start());
		}
		return line;
    }
    
    def readStringFromFile(file: String): String = {
      val s = scala.io.Source.fromFile(file)
      s.mkString
    }
    
}
