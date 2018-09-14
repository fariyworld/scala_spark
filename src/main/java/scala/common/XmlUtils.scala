package scala.common

import scala.xml._

/**
 * xml 操作工具类
 */
object XmlUtils {

  /**
   * 获取所有没有子节点的节点 标签名称（包括其所有父节点的标签名称; 用 _ 连接） 节点文本内容
   * ocate_test_child（三级节点）
   * @param filePath
   */
  def readXml(filePath: String): Unit = {
    val xmlFile = XML.loadFile(filePath) // 根节点
    val childNodes = xmlFile.child // 所有一级节点
    for (childNode <- childNodes) {
      readXml(childNode, childNode.label)
    }
  }


  def readXml(parentNode: Node, parentLabel: String): Unit = {
    if (!"#PCDATA".equals(parentLabel)) {
      if (!hasChildNodes(parentNode)) {
        println(String.format("%-30s%s", parentLabel, parentNode.text))
      } else {
        for (node <- parentNode.\("_")) {
          readXml(node, parentLabel + "_" + node.label)
        }
      }
    }
  }

  /**
   * 判断当前节点是否有子节点
   * @param node
   * @return true: 有子节点
   */
  def hasChildNodes(node: Node): Boolean = {
    return node.\("_").length != 0
  }

  
  /**
   * 读取xml 至map
   */
  def readXml2Map(filePath: String): scala.collection.mutable.Map[String, String] = {
    val xmlFile = XML.loadFile(filePath) // 根节点
    val childNodes = xmlFile.child // 所有一级节点
    val xmlContentMap: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()
    for (childNode <- childNodes) {
      readXml(childNode, childNode.label, xmlContentMap)
    }
    return xmlContentMap
  }

  def readXml(parentNode: Node, parentLabel: String, xmlContentMap: scala.collection.mutable.Map[String, String]): Unit = {
    if (!"#PCDATA".equals(parentLabel)) {
      if (!hasChildNodes(parentNode)) {
        println(String.format("%-30s%s", parentLabel, parentNode.text))
        xmlContentMap += (parentLabel -> parentNode.text)
      } else {
        for (node <- parentNode.\("_")) {
          readXml(node, parentLabel + "_" + node.label, xmlContentMap)
        }
      }
    }
  }

  
    def main(args: Array[String]): Unit = {
//    文件放到resource文件夹下
    val filePath1 = Thread.currentThread().getContextClassLoader.getResource("AiMrLocate.xml").getPath
    val filePath2 = "conf/AiMrLocate.xml"
    readXml(filePath1)
    Console.err.println("\n")
    Console.err.println("--------------------------------------------------")
    Console.err.println("\n")
    val xmlContentMap = readXml2Map(filePath2)
    xmlContentMap.foreach{case (key,value) => println(key,value)}

//    Console.err.println("Please enter filename")

  }
}