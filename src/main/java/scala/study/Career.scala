package scala.study

object Career extends Enumeration with Serializable {
  //声明枚举对外暴露的变量类型
  type Career = Value
  val RD, HR, CEO, CTO = Value

}