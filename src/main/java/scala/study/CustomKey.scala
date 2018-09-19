package scala.study

import scala.study.Career._

class CustomKey extends Ordered[CustomKey] with Serializable {
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
    career.hashCode()
  }

  override def compare(other: CustomKey): Int = {
    if (this.timeStamp.compare(other.timeStamp) == 0) {
      this.employeeId.compare(other.employeeId)
    } else {
      0
    }
  }
  
  override def toString(): String = {
    "CustomKey[".concat("career=").concat(career.toString())
    .concat(", timestamp=").concat(timestamp.toString())
    .concat(", employeeID=").concat(employeeId).concat("]") 
    }
}