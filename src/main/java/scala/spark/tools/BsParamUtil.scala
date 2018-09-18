package scala.spark.tools



import scala.collection.mutable.Map
import scala.io.BufferedSource
import scala.io.Source

import org.apache.hadoop.fs.FSDataInputStream
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.log4j.Logger

import bsparam.BsParam
import bsparam.Cell
import bsparam.EarfcnPciUnitedKey
import scala.collection.JavaConversions._

/**
 * 加载工参至Map工具
 */
object BsParamUtil {

  @transient lazy val LOGGER = Logger.getLogger(this.getClass)

  def readBs2Cache(hdfs: FileSystem, bsPath: Path, cellMap: Map[Long, Cell], eciMaps: Map[Int, scala.collection.immutable.Map[EarfcnPciUnitedKey, Long]]): Unit = {

    var inputStream: FSDataInputStream = null
    var bufferedSource: BufferedSource = null
    try {
      inputStream = hdfs.open(bsPath)
      bufferedSource = Source.fromInputStream(inputStream)
      for (strBsParam <- bufferedSource.getLines()) {
        val bsParam = new BsParam()
        // 字符串数组转换为bsParam对象
        bsParam.stringTo(strBsParam)
        // 装载到map
        cellMap += (bsParam.getServerCell().getEci() -> bsParam.getServerCell())
        eciMaps += (bsParam.getServerCell().getEnodebId() -> bsParam.getEciMap.mapValues(_.toLong).toMap)
      }

    } catch {
      // TODO: handle error
      case ex: Exception => ex.printStackTrace()
    } finally {
      // TODO: handle finally clause
      inputStream.close()
      bufferedSource.close()
    }
  }

  def readContent(hdfs: FileSystem, bsPath: Path, contentMap: Map[Int, String]): Unit = {

    var inputStream: FSDataInputStream = null
    var bufferedSource: BufferedSource = null
    var lineNum: Int = 0
    try {
      inputStream = hdfs.open(bsPath)
      bufferedSource = Source.fromInputStream(inputStream)
      for (lineTxt <- bufferedSource.getLines()) {
        lineNum += 1
        LOGGER.warn(lineNum)
        LOGGER.warn(lineTxt)
        contentMap += (lineNum -> lineTxt)
      }
    } catch {
      // TODO: handle error
      case ex: Exception => ex.printStackTrace()
    } finally {
      inputStream.close()
      bufferedSource.close()
    }
  }
}