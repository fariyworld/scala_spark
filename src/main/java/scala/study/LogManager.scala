package scala.study

import org.apache.log4j.Logger

object LogManager {
  
  @transient lazy val log = Logger.getLogger(this.getClass)
  
  def main(args: Array[String]): Unit = {
    log.info("hello scala log4j")
  }
}