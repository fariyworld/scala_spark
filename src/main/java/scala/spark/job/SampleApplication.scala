package scala.spark.job

import org.apache.log4j.Logger
import scala.spark.common.CustomHadoopConfiguration
import scala.spark.tools.ReadConfigFile
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FileSystem
import org.apache.commons.lang3.StringUtils

/**
 * 数据倾斜
 */
object SampleApplication {

  @transient lazy val LOGGER = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

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
    val inputPath = conf.get("inputPath")
    val resultPath = conf.get("resultPath")
    if(StringUtils.isBlank(inputPath) || StringUtils.isBlank(resultPath)){
      LOGGER.warn("please check inputPath and inputPath in " + configFile)
      System.exit(1)
    }
    
    //TODO 初始化spark环境
    val sparkSession = SparkSession
      .builder()
      .appName(this.getClass.getName)
      .getOrCreate();
    val sc = sparkSession.sparkContext
    
    //TODO 保证输出路径不存在
    val hdfs = FileSystem.get(sc.hadoopConfiguration)
    val outPath = new Path(resultPath)
    if (hdfs.exists(outPath)) {
      hdfs.delete(outPath, true)
      LOGGER.warn("输出路径存在, 已删除...")
    }

    try {

      //TODO job
      val mostWord = sc.textFile(inputPath, 1)
//        .sample(false, 0.9, System.currentTimeMillis())
        .flatMap(_.split(","))
        .map((_,1))
        .reduceByKey(_+_, 1)
        .sortBy(_._2, true, 1)
        .take(10).toList.toString()
//        .first()
//        .toString()
        
     LOGGER.warn(mostWord)

    } catch {
      // TODO handle error
      case ex: Exception => {
        ex.printStackTrace()
      }
    } finally {
      // TODO stop spark
      sparkSession.stop()
    }

  }
}