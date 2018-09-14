package scala.common

import scala.io.Source
import java.io.File

object FileUtils {

  /**
   * 根据指定编码按行读取文件内容至Iterator迭代器
   */
  def readFile2Iterator(filename: String, encoding: String) = {
    Source.fromFile(filename, encoding).getLines()
  }

  /**
   * 根据平台编码按行读取文件内容至Iterator迭代器
   */
  def readFile2Iterator(filename: String) = {
    Source.fromFile(filename).getLines()
  }

  /**
   * 根据指定编码读取单个字符
   */
  def readFile2Source(filename: String, encoding: String) = {
    Source.fromFile(filename, encoding)
  }

  /**
   * 根据平台编码读取单个字符
   */
  def readFile2Source(filename: String) = {
    Source.fromFile(filename)
  }

  /**
   * 递归获取指定目录下的所有文件名
   */
  def getAllFileName(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(getAllFileName _)
  }

  def main(args: Array[String]) {
    val source = readFile2Source("D:/JavaEE学习笔记/资源/scala/hello1.txt", "GBK")
    for (lineTxt <- source) println(lineTxt)
    source.close()

    val fileIterator = getAllFileName(new File("D:\\WebLogs\\self-study\\redisApp"))
    for (lineTxt <- fileIterator) println(lineTxt)
    
    val prices = List(5.0, 10.0, 20.0)
    val nums = List(5, 2, 10)
    val zip = prices.zip(nums)
    for (list <- zip) println(list)
  }
}