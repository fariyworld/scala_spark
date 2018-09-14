package scala.common

import org.apache.log4j.Logger

/**
 * 字符串操作工具类
 */
object StringUtils {
  
  /**
   * 判断某字符串是否为空或长度为0或由空白符(whitespace) 构成
   */
  def isBlank(str: String): Boolean = {
    if (str == null || str.length() == 0) {
      return true
    }

    for (i <- 0 to (str.length() - 1)) {
      if (Character.isWhitespace(str(i)) == false) {
        return false
      }
    }
    return true
  }
  
  
  /**
   * 判断某字符串是否为空，为空的标准是 str==null 或 str.length()==0
   */
  def isEmpty(str: String): Boolean = {
    
    return (str == null || str.length() == 0)
  }

  def main(args: Array[String]): Unit = {
    println(isBlank("\b"))
  }
}