package scala.test

import org.junit.Test
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.study.CustomKey
import scala.study.Career.Career
import scala.study.Career
import scala.study.Employee
import scala.io.Source

class App01 {

  /**
   * 测试for循环 跳出循环 => 基于Boolean类型的控制变量
   */
  @Test
  def testFor {
    val str = "abcdefg"
    for (ch <- 0 until str.length()) {
      print(str(ch))
      if (ch < str.length() - 1) print("\b") else println()
    }
    var flag = true
    for (i <- 0 until str.length() if flag) {
      println("i = " + i)
      if (i == 5) flag = false
    }
  }

  /**
   * 高阶for循环实现九九乘法表
   */
  @Test
  def printMultiplicationTable {

    for (i <- 1 to 9; j <- 1 to i) {
      //      print(String.format("%d*%d=%d\t", new Integer(j), new Integer(i), new Integer(i*j)))
      //      printf("%d*%d=%d\t", j, i, (i*j))
      print(s"$j*$i=${i * j}\t")
      //      print(f"$j%d*$i%d=${i*j}%d\t")
      if (i == j) println()
    }
  }

  @Test
  def testPrintBox() {
    printBox("hello,scala!")
  }

  def printBox(content: String) {
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
    javaScores.+=(("shell", 93))
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
  def testMuBuilderMapConvert() {

    val javaScores = new java.util.HashMap[String, java.lang.Long]()
    javaScores.put("scala", 90)
    javaScores.put("python", 95)
    javaScores.put("java", 99)
    println(javaScores.size())
    println(javaScores.getClass.getName)
    val scalaMap = javaScores.mapResult(map => {})
    println(scalaMap.getClass.getName)
    println(scalaMap)
    scalaMap.+=(("shell", 93))
    println(scalaMap)
  }

  @Test
  def testMap() {

    val details = scala.collection.mutable.Map[String, ListBuffer[String]]()
    val listBuffer = details.get("bonc")
    println(listBuffer)
    println(listBuffer.isEmpty)
  }

  @Test
  def testHashCode() {

    val customKey1 = new CustomKey
    customKey1.careerName_=(Career.HR)

    val customKey2 = new CustomKey
    customKey2.careerName_=(Career.RD)

    println(customKey1.hashCode())
    println(customKey2.hashCode())

  }

  @Test
  def testEnum() {
    val valueSet = Career.values
    for (value <- valueSet) {
      println(value.id + "\t" + value.toString())
    }
  }

  @Test
  def testSortBy() {
    val em3 = Employee.stringTo("james,0110275,CEO,1")
    val em1 = Employee.stringTo("killy,0110099,CEO,2")
    val em2 = Employee.stringTo("james,0110285,CEO,1")

    val listBuffer = new ListBuffer[Employee]
    listBuffer += em1
    listBuffer += em2
    listBuffer += em3
    println(listBuffer)
    println(listBuffer.sortBy(sortRule)(Ordering.Tuple2(Ordering.Long, Ordering.String.reverse)))
  }

  def sortRule(employee: Employee): (Long, String) = {
    (employee.getTimeStamp, employee.getEmployeeID)
  }

  @Test
  def testMapContains() {

    val details = scala.collection.mutable.Map[String, ListBuffer[String]]()
    val bufferedSource = Source.fromFile("D:/Workspaces/eclipse_4.5_workspace/scala.common/data/site")
    for (lineTxt <- bufferedSource.getLines()) {
      val lineArray = lineTxt.split("\\|", -1)
      val persons = lineArray(1).split(",", -1)
      val listBuffer = details.get(lineArray(0)).getOrElse(ListBuffer[String]())
      for (person <- persons) {
        listBuffer += person
      }
      details += (lineArray(0) -> listBuffer)
    }
    bufferedSource.close()
    println(details)
    val keySet = details.keySet
    println(keySet)
    var flag = true
    for (key <- keySet if flag) {
      println(key)
      println(details.get(key).getClass)
      println(details.get(key).get.getClass)
      if (details.get(key).get.contains("mace")) {
        flag = false
        println(key + "|" + "aaaaa")
      }
    }
  }

  @Test
  def testEquals() {
    
    val customKey2 = new CustomKey
    customKey2.careerName_=(Career.RD)
    customKey2.employeeId_=("0110398")
    customKey2.timeStamp_=(1002)

    val customKey1 = new CustomKey
    customKey1.careerName_=(Career.RD)
    customKey1.employeeId_=("0110298")
    customKey1.timeStamp_=(1001)
    
    println(customKey1.equals(customKey2))
  }
}