Minimal infrastructure for general error reporting in TypeChef

Can be used by all analysis parts to have a uniform way of writing errors
into the error.xml file

The infrastructure of actually writing error messages to the command line or XML files is part of the frontend.


TypeChefError is the central error class used for all error reporting on the AST:

  * `condition` describes the partial configuration space in which the error occurs
  * `msg` is the error message intended for developers
  * `where` is an AST node that describes the location (position information is presented to the developer)
  * `severity` is a broad classification of errors and may be extended (the first letter should be distinct). Only the first letter is shown to users.
  * `severityExtra` can be any additional machine readable string useful for automatic filtering. Will not be presented to users.
