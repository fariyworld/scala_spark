package scala.common

object AssistUtil {

  /**
   * 获取运行时长
   * @param mss		毫秒
   */
  def getElapsedTime(mss: Long): String = {
    val ms = mss / 1000;
    val hours = (ms % (60 * 60 * 24)) / (60 * 60);
    val minutes = (ms % (60 * 60)) / 60;
    val seconds = ms % 60;
    
    String.format("运行时长\t%s:%s:%s", hours.toString(), minutes.toString(), seconds.toString())
  }
}