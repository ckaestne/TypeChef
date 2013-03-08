package de.fosd.typechef.crefactor

import org.apache.logging.log4j.LogManager

trait Logging {

  val loggerName = this.getClass.getName
  lazy val logger = LogManager.getLogger(loggerName)

}
