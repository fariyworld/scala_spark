package scala.study

object Gender extends Enumeration with Serializable{
  //声明枚举对外暴露的变量类型
  type Gender = Value
  val male, female = Value
}