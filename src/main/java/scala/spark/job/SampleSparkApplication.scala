package scala.spark.job

import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource
import scala.io.Source
import scala.spark.common.CustomHadoopConfiguration
import scala.spark.tools.ReadConfigFile

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.fs.FSDataInputStream
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.study.Employee
import scala.study.CustomKey
import org.apache.spark.HashPartitioner
import scala.study.CustomKey
import scala.study.CustomKey

object SampleSparkApplication {

  @transient lazy val LOGGER = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    var configFile = ""

    if (args.length != 1) {
      LOGGER.info("args must be 1, please input the config param xml file.")
      System.exit(1)
    } else {
      LOGGER.info("you input one param, this param is used as config param")
      configFile = args(0)
    }

    //TODO 读取配置
    val conf = new CustomHadoopConfiguration()
    ReadConfigFile.readXmlConfigFile2HadoopConfiguration(configFile, conf)

    val sparkConf = new SparkConf()
      .setAppName("wordcount")
    /*.setMaster("local[1]")*/

    val queuename = conf.get("queuename")
    if (StringUtils.isNotBlank(queuename)) sparkConf.set("spark.yarn.queue", queuename)

    val sc = new SparkContext(sparkConf)

    LOGGER.warn("并发数： " + sc.defaultParallelism)
    LOGGER.warn("最小分区数： " + sc.defaultMinPartitions)

    try {

      val mrInputPath = conf.get("mr.inputPath")
      val xdrInputPath = conf.get("xdr.inputPath")
      val site = conf.get("site")
      val resultPath = conf.get("resultPath")
      val reduceNum = conf.getInt("reduceNum", 1)

      val hdfs = FileSystem.get(sc.hadoopConfiguration)
      val outPath = new Path(resultPath)
      if (hdfs.exists(outPath)) {
        hdfs.delete(outPath, true)
        LOGGER.warn("输出路径存在, 已删除...")
      }

      //TODO 加载工参至广播变量
      LOGGER.warn("*" * 20 + "开始加载工参至广播变量" + "*" * 20)
      val details = scala.collection.mutable.Map[String, ListBuffer[String]]()
      readDetails(hdfs, new Path(site), details)
      val broadcastDetails = sc.broadcast(details)
      LOGGER.warn("broadcast details size: " + broadcastDetails.value.size)
      LOGGER.warn("*" * 20 + "加载工参至广播变量结束" + "*" * 20)
      
      val rdd = sc.textFile(mrInputPath,1).mapPartitions(iter => {
        val resultList = new ListBuffer[Tuple2[CustomKey, Employee]]()
        while (iter.hasNext) {
          val employee = Employee.stringTo(iter.next())
          resultList +=  (Tuple2(Employee.getCustomKey(employee), employee))
        }
        resultList.iterator
      })
      .union(sc.textFile(xdrInputPath,1).mapPartitions(iter => {
        val resultList = new ListBuffer[Tuple2[CustomKey, Employee]]()
        while (iter.hasNext) {
          val employee = Employee.stringTo(iter.next())
          resultList +=  (Tuple2(Employee.getCustomKey(employee), employee))
        }
        resultList.iterator
      }))
      .partitionBy(new HashPartitioner(reduceNum))
      .groupByKey(reduceNum)
      /*.sortByKey(true, reduceNum)*///key extends Ordered[CustomKey] 重写 override def compare(other: CustomKey): Int
      .mapValues(iterable => {
        iterable.toList.sortBy(sortRule)
      })
      .flatMap(kvs => {
        println(kvs._1)
        println(kvs._2.toString())
        "ok"
      }).count()
//      .mapValues(iterable => {
//        iterable.toList.sortBy(sortRule)
//      })
//      .flatMap(groupSortValues => {
//        val values = groupSortValues._2
//        LOGGER.warn(groupSortValues._1.toString())
//        LOGGER.warn(values.size)
//        val resultList = new ListBuffer[String]
//        val detailsMap = broadcastDetails.value
//        val keySet = detailsMap.keySet
//        for(employee <- values){
//          val employeeName = employee.getName
//          var flag = true
//          var company: String = ""
//          for(key <- keySet if flag){
//            if(detailsMap.get(key).get.contains(employeeName)){
//              flag = false
//              company = key
//            }
//          }
//          resultList += company + "|" + employee.toString()
//        }
//        resultList.iterator
//      })
//      .saveAsTextFile(resultPath)
      
    } catch {
      // TODO handle error
      case ex: Exception => {
        ex.printStackTrace()
      }
    } finally {
      // TODO stop spark
      sc.stop()
    }
  }
  
  def sortRule(employee: Employee): (Long, String) = {
    (employee.getTimeStamp, employee.getEmployeeID)
  }

  def readDetails(hdfs: FileSystem, bsPath: Path, details: scala.collection.mutable.Map[String, ListBuffer[String]]) {

    var inputStream: FSDataInputStream = null
    var bufferedSource: BufferedSource = null
    try {
      inputStream = hdfs.open(bsPath)
      bufferedSource = Source.fromInputStream(inputStream)
      for (lineTxt <- bufferedSource.getLines()) {
        val lineArray = lineTxt.split("\\|", -1)
        val persons = lineArray(1).split(",", -1)
        val listBuffer = details.get(lineArray(0)).getOrElse(ListBuffer[String]())
        for(person <- persons){
          listBuffer += person
        }
        details += (lineArray(0) -> listBuffer)
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
}