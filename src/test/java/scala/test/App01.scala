package scala.test

import org.junit.Test
import scala.collection.JavaConversions._

class App01 {
  
  /**
   * 测试for循环 跳出循环 => 基于Boolean类型的控制变量
   */
  @Test
  def testFor {
    val str = "abcdefg"
    for(ch <- 0 until str.length()){
      print(str(ch)) 
      if(ch < str.length()-1) print("\b") else println()
    }
    var flag = true
    for(i <- 0 until str.length() if flag){
      println("i = " + i)
      if(i == 5) flag = false
    }
  }
  
  
  /**
   * 高阶for循环实现九九乘法表
   */
  @Test
  def printMultiplicationTable {
    
    for(i <- 1 to 9; j <- 1 to i) {
//      print(String.format("%d*%d=%d\t", new Integer(j), new Integer(i), new Integer(i*j)))
//      printf("%d*%d=%d\t", j, i, (i*j))
      print(s"$j*$i=${i*j}\t")
//      print(f"$j%d*$i%d=${i*j}%d\t")
      if(i==j) println()
    }
  }
  
  @Test
  def testPrintBox(){
    printBox("hello,scala!")
  }
  
  def printBox(content :String) {
    var border = "+--" + "-" * content.length() + "--+\n"
    println(border + "|\b\b" + content + "\b\b|\n" + border)
  }
  
  
  /**
   * java hashmap 转 scala map
   * 1. import scala.collection.JavaConversions._
   * 2. .mapValues(_.toLong).toMap
   * scala.collection.immutable.Map$Map3  不可变没有 += 操作符
   */
  @Test
  def testMapConvert() {
    
    val javaScores = new java.util.HashMap[String, java.lang.Long]()
    javaScores.put("scala", 90)
    javaScores.put("python", 95)
    javaScores.put("java", 99)
    println(javaScores.getClass.getName)
    javaScores.+= (("shell", 93))
    val immutable_scalaScores = javaScores.mapValues(_.toLong).toMap
    println(immutable_scalaScores.getClass.getName)
  }
  
  
  /**
   * 
   * 1. import scala.collection.JavaConversions._
   * 2. val scalaMap = javaScores.mapResult(map =>{ })
   * 
   * scala.collection.mutable.Builder$$anon$1
   * 添加元素	scalaMap.+= (("shell", 93))
   */
  @Test
  def testMuMapConvert() {
    
    val javaScores = new java.util.HashMap[String, java.lang.Long]()
    javaScores.put("scala", 90)
    javaScores.put("python", 95)
    javaScores.put("java", 99)
    println(javaScores.size()) 
    println(javaScores.getClass.getName)    
    val scalaMap = javaScores.mapResult(map =>{ })
    println(scalaMap.getClass.getName)
    println(scalaMap)
    scalaMap.+= (("shell", 93))
    println(scalaMap)
  }
  
}