package scala.study

import scala.study.Career._
import org.apache.log4j.Logger
import org.spark_project.dmg.pmml.False

class CustomKey extends Ordered[CustomKey] with Serializable {
  
  @transient lazy val LOGGER = Logger.getLogger(this.getClass)
  
  private var employeeID: String = ""
  private var career: Career = Career.RD
  private var timestamp: Long = 0
  def employeeId = employeeID
  def employeeId_=(newEmployeeID: String) { employeeID = newEmployeeID }
  def careerName = career
  def careerName_=(newCareerName: Career) { career = newCareerName }
  def timeStamp = timestamp
  def timeStamp_=(newtimeStamp: Long) { timestamp = newtimeStamp }

  override def hashCode(): Int = {
    LOGGER.info("HashPartitioner...")
    career.hashCode()
  }
  
  final override def equals(obj: Any): Boolean = {
    LOGGER.info("groupByKey...")
    val other = obj.asInstanceOf[CustomKey]
    if (other == null)  false
    else career == other.career
  }

  override def compare(other: CustomKey): Int = {
    LOGGER.info("sortByKey...")
    if (this.timeStamp.compare(other.timeStamp) == 0) {
      this.employeeId.compare(other.employeeId)
    } else {
      this.timeStamp.compare(other.timeStamp)
    }
  }
  
  override def toString(): String = {
    "CustomKey[".concat("career=").concat(career.toString())
    .concat(", timestamp=").concat(timestamp.toString())
    .concat(", employeeID=").concat(employeeId).concat("]") 
    }
}