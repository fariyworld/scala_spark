package scala.spark.job

import scala.common.AssistUtil
import scala.spark.common.CustomHadoopConfiguration
import scala.spark.tools.BsParamUtil
import scala.spark.tools.ReadConfigFile

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

import bsparam.Cell
import bsparam.EarfcnPciUnitedKey
import scala.collection.mutable.ListBuffer

object AiMrLocateApplication {
  @transient lazy val LOGGER = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val START_TIME = System.currentTimeMillis()
    var configFile = ""

    if (args.length != 1) {
      LOGGER.info("args must be 1, please input the config param xml file.")
      System.exit(1)
    } else {
      LOGGER.warn("you input one param, this param is used as config param")
      configFile = args(0)
    }
    //TODO 读取配置
    val conf = new CustomHadoopConfiguration()
    ReadConfigFile.readXmlConfigFile2HadoopConfiguration(configFile, conf)

    //TODO 参数检查
    val mrInputPath = conf.get("mr.inputPath")
    val xdrInputPath = conf.get("xdr.inputPath")
    val site = conf.get("site")
    val resultPath = conf.get("resultPath")
    if (StringUtils.isBlank(mrInputPath) || StringUtils.isBlank(xdrInputPath) || StringUtils.isBlank(site) || StringUtils.isBlank(resultPath)) {
      LOGGER.warn("please check inputPath and inputPath in " + configFile)
      System.exit(1)
    }

    //TODO 初始化spark环境
    val sparkSession = SparkSession
      .builder()
      .appName(this.getClass.getName)
      .getOrCreate();

    val sc = sparkSession.sparkContext
    val sparkConf = sparkSession.conf

    //TODO 设置spark运行时参数
    sparkConf.set("spark.executor.memoryOverhead", "2048")
    val queuename = conf.get("queuename")
    if (StringUtils.isNotBlank(queuename)) sparkConf.set("spark.yarn.queue", queuename)

    LOGGER.warn("并发数： " + sc.defaultParallelism)
    LOGGER.warn("最小分区数： " + sc.defaultMinPartitions)

    //TODO 保证输出路径不存在
    val hdfs = FileSystem.get(sc.hadoopConfiguration)
    val outPath = new Path(resultPath)
    if (hdfs.exists(outPath)) {
      hdfs.delete(outPath, true)
      LOGGER.warn("输出路径存在, 已删除...")
    }

    try {
      //TODO 加载工参至广播变量
      LOGGER.warn("*" * 20 + "开始加载工参至广播变量" + "*" * 20)
      val cellMap = scala.collection.mutable.Map[Long, Cell]()
      val eciMaps = scala.collection.mutable.Map[Int, scala.collection.immutable.Map[EarfcnPciUnitedKey, Long]]()
      BsParamUtil.readBs2Cache(hdfs, new Path(site), cellMap, eciMaps)
      val broadcastCellMap = sc.broadcast(cellMap)
      val broadcastEciMaps = sc.broadcast(eciMaps)
      LOGGER.warn("cellMap size: " + broadcastCellMap.value.size)
      LOGGER.warn("eciMaps size: " + broadcastEciMaps.value.size)
      LOGGER.warn("*" * 20 + "加载工参至广播变量结束" + "*" * 20)

      //TODO 读入XDR数据
      val xdrRDD = sc.textFile(xdrInputPath)
        .mapPartitions(iter => {
          val resultList = new ListBuffer[Tuple2[String, String]]()
          while (iter.hasNext) {
            val xdrStr = iter.next()
            //TODO 解析XDR

            //TODO 生成LocatorCombinedKey

            //TODO 放入ListBuffer
          }
          resultList.iterator
        })

      //TODO 读入MR数据
      val mrRDD = sc.textFile(mrInputPath)
        .mapPartitions(iter => {
          val resultList = new ListBuffer[Tuple2[String, String]]()
          while (iter.hasNext) {
            val xdrStr = iter.next()
            //TODO 解析MR

            //TODO 生成LocatorCombinedKey

            //TODO 放入ListBuffer
          }
          resultList.iterator
        })

      //TODO 合并XDR和MR数据
      val rdd = xdrRDD.union(mrRDD)

      //TODO 分组

      //TODO 排序

      //TODO 定位

    } catch {
      // TODO handle error
      case ex: Exception => {
        ex.printStackTrace()
      }
    } finally {
      // TODO stop spark
      sparkSession.stop()
      val END_TIME = System.currentTimeMillis()
      LOGGER.warn(AssistUtil.getElapsedTime(END_TIME - START_TIME))
    }
  }
}