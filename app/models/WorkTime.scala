package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10. 
  */
case class WorkTime(
                     var EmpNo: Int,
                     var ProNo: String,
                     var WorkDate: DateTime,
                     var WorkTime: Double,
                     var OverTime: Option[Double],
                     var Remark: Option[String],
                     var Creator: Option[Int] = None,
                     var CreateTime: Option[DateTime] = None,
                     var Updater: Option[Int] = None,
                     var UpdateTime: Option[DateTime] = None
                   )

object WorkTime {
  implicit val workTimeFormat: OFormat[WorkTime] = Json.format[WorkTime]
}
