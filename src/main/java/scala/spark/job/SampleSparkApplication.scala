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
      
      val rdd = sc.textFile(mrInputPath).mapPartitions(iter => {
        val resultList = new ListBuffer[Tuple2[String, Employee]]()
        while (iter.hasNext) {
          val employee = Employee.stringTo(iter.next())
          resultList +=  (Tuple2(employee.getCareer.toString(),employee))
        }
        resultList.iterator
      })
      .union(sc.textFile(xdrInputPath).mapPartitions(iter => {
        val resultList = new ListBuffer[Tuple2[String, Employee]]()
        while (iter.hasNext) {
          val employee = Employee.stringTo(iter.next())
          resultList +=  (Tuple2(employee.getCareer.toString(),employee))
        }
        resultList.iterator
      }))
      .partitionBy(new HashPartitioner(reduceNum))
      .groupByKey(reduceNum)
      .mapValues(iterable => {
        iterable.toList.sortBy(sortRule)
      })
      .flatMap(groupSortValues => {
        val values = groupSortValues._2
        val resultList = new ListBuffer[String]
        val detailsMap = broadcastDetails.value
        val keySet = detailsMap.keySet
        for(employee <- values){
          val employeeName = employee.getName
          var flag = true
          var company: String = ""
          for(key <- keySet if flag){
            if(detailsMap.get(key).get.contains(employeeName)){
              flag = false
              company = key
            }
          }
          resultList += company + "|" + employee.toString()
        }
        resultList.iterator
      })
      .saveAsTextFile(resultPath)
      
//      .flatMapValues(sortList =>{
//        val resultList = new ListBuffer[String]
//        val detailsMap = broadcastDetails.value
//        val keySet = detailsMap.keySet
//        for(employee <- sortList){
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
//      .foreachPartition(partition => {
//        while(partition.hasNext){
//          val group = partition.next()
//          val key = group._1
//          val values = group._2
//          LOGGER.warn(key)
//          for(value <- values){
//            LOGGER.warn(value)
//          }
//        }
//      })
      
//      sc.textFile(mrInputPath).mapPartitions(iter => {
//        val resultList = new ListBuffer[Employee]()
//        while (iter.hasNext) {
//          val employee = Employee.stringTo(iter.next())
//          resultList +=  (employee)
//        }
//        resultList.iterator
//      })
//      .union(sc.textFile(xdrInputPath).mapPartitions(iter => {
//        val resultList = new ListBuffer[Employee]()
//        while (iter.hasNext) {
//          val employee = Employee.stringTo(iter.next())
//          resultList += (employee)
//        }
//        resultList.iterator
//      }))
//      .keyBy(employee => employee.toString().split(",", -1)(2))
//      .partitionBy(new HashPartitioner(reduceNum))
//      .groupByKey(reduceNum)
//      .sortBy(_._1, true, reduceNum)//升序
//      .foreachPartition(partition => {
//        while(partition.hasNext){
//          val group = partition.next()
//          val key = group._1
//          val values = group._2
//          LOGGER.warn(key)
//          for(value <- values){
//            LOGGER.warn(value)
//          }
//        }
//      })
      
//      .saveAsTextFile(resultPath)
//      .mapPartitions(iter => {
//        val resultList = new ListBuffer[String]()
//        while(iter.hasNext){
//          val sameCareer = iter.next()
//          LOGGER.warn(sameCareer._1)
//          val employeeIterable = sameCareer._2
//          LOGGER.warn(employeeIterable.size)
//          for(employee <- employeeIterable){
//            resultList + employee.toString()
//          }
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