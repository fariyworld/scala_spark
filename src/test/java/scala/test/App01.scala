package scala.test

import org.junit.Test

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
}