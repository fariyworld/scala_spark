package scala.study

import scala.study.Career._

class Employee extends Serializable {
  private var name: String = ""
  private var employeeID: String = ""
  private var career: Career = null
  private var timestamp: Long = 0

  def getName = name
  def setName(newName: String) { name = newName }
  def getEmployeeID = employeeID
  def setEmployeeID(newEmployeeID: String) { employeeID = newEmployeeID }
  def getCareer = career
  def setCareer(newCareer: Career) { career = newCareer }
  def getTimeStamp = timestamp
  def setTimeStamp(newTimeStamp: Long) { timestamp = newTimeStamp }
  
  
  override def toString(): String = { this.name + "," + this.employeeID + "," + this.career.toString() + "," + this.timestamp }
}

object Employee {
  
  def stringTo(employeeStr: String): Employee = {
    val employee = new Employee
    val paramArray = employeeStr.split(",", -1)
    employee.name=paramArray(0)
    employee.employeeID=paramArray(1)
    employee.career=Career.withName(paramArray(2))
    employee.timestamp=paramArray(3).toLong
    employee
  }
  
  def getCustomKey(employee: Employee): CustomKey = {
    val customKey = new CustomKey
    customKey.careerName_=(employee.career)
    customKey.employeeId_=(employee.employeeID)
    customKey.timeStamp_=(employee.timestamp)
    customKey
  }
}