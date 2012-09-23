package de.fosd.typechef.crefactor.backend.refactor

/**
 * Helper object providing some useful functions for refactorings.
 */
object Helper {

  val languageKeywords = List(
    "auto",
    "break",
    "case",
    "char",
    "const",
    "continue",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "extern",
    "float",
    "for",
    "goto",
    "if",
    "inline",
    "int",
    "long",
    "register",
    "restrict",
    "return",
    "short",
    "signed",
    "sizeof",
    "static",
    "struct",
    "switch",
    "typedef",
    "union",
    "unsigned",
    "void",
    "volatile",
    "while",
    "_Alignas",
    "_Alignof",
    "_Atomic",
    "_Bool",
    "_Complex",
    "_Generic",
    "_Imaginary",
    "_Noreturn",
    "_Static_assert",
    "_Thread_local"
  )

  /**
   * Checks if the name of a variable is compatible to the iso c standard.
   *
   * @param name name to check
   * @return <code>true</code> if valid, <code>false</code> if not
   */
  def isValidName(name: String): Boolean = {
    if (!name.matches("[a-zA-Z_][a-zA-Z0-9]*[a-zA-Z0-9_]*")) {
      return false
    }
    !isReservedLanguageKeyword(name)
  }

  /**
   * Checks if the name is a language keyword.
   *
   * @param name the name to check
   * @return <code>true</code> if language keyword
   */
  def isReservedLanguageKeyword(name: String): Boolean = {
    languageKeywords.contains(name)
  }
}
