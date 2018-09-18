package scala.study

import scala.beans.BeanProperty

class Person extends Serializable{
  
  @BeanProperty var name = ""//默认公有才能使用JavaBean规范
  @BeanProperty var age  = 0
  private var idNumber: Long = 0//私有
  def idcard = idNumber//自定义getter
  def idcard_=(newIdNumber: Long){//自定义setter
    idNumber = newIdNumber
  }
  
  //只读属性
  private var value = 0 
  def increment() { value += 1 }
  def current = value
  
  //对象私有的属性
  private[this] var phone = "185****8251"
//  private/*[this]*/ var phone = "185****8251"
//  def isSame(other: Person) = phone.equals(other.phone)
  
  //辅助构造器
  def this(name: String, age: Int) {
    this()
    this.name = name
    this.age = age
  }
  
  //主构造器 默认 ()
  // class Person(private name: String, private age: Int){  }
  
  //重写 toString()
  override def toString(): String = { "Person[name="+ this.name + ", age="+ this.age +"]" }
}


object Persion{
  
  def main(args: Array[String]): Unit = {
    
    val person = new Person//主构造器
    println(person.name.equals(""))
    person.name="mace"//setter
    println(person.name)//getter
    person.setAge(18)
    println(person.getAge())
    println(person.idcard)
    person.idcard_=(612722)
    println(person.idcard)
    println(new Person("mace", 19))//辅助构造器/toString
  }
}