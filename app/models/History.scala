package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10. 
  */
case class History(
                    var EmpNo: Int,
                    var ProNo: Int,
                    var Year: Int,
                    var Month: Int,
                    var Week: Int,
                    var WorkTime: Double,
                    var OverTime: Option[Double],
                    var Remark: Option[String],
                    var Creator: Option[Int] = None,
                    var CreateTime: Option[DateTime] = None,
                    var Updater: Option[Int] = None,
                    var UpdateTime: Option[DateTime] = None
                  )
object History {
  implicit val historyFormat: OFormat[History] = Json.format[History]
}
