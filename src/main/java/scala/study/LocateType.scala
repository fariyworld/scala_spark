package scala.study

import org.apache.log4j.Logger

object LocateType extends Enumeration {
  
  @transient lazy val log = Logger.getLogger(this.getClass)
  
  //声明枚举对外暴露的变量类型
  type LocateType = Value
  val V1, V2 = Value
  
  def main(args: Array[String]): Unit = {
    log.info(LocateType.V1 == LocateType.withName("V1"));
    LocateType.values.foreach{value => print(value, value.id)}
    
  }
}