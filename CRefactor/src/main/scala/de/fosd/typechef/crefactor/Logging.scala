package de.fosd.typechef.crefactor

import org.apache.logging.log4j.LogManager

/**
 * Trait for an easy include of log4j into scala code.
 */
trait Logging {

  val loggerName = this.getClass.getName
  lazy val logger = LogManager.getLogger(loggerName)

}