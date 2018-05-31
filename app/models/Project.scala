package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10. 
  */
case class Project(
                    var ProNo: String,
                    var CustomerNo: Int,
                    var ProName: String,
                    var EmpNo: Option[Int],
                    var ProType: String,
                    var Stime: Option[DateTime],
                    var ProStatus: String,
                    var Etime: Option[DateTime],
                    var LenTime: Option[Double],
                    var Cost: Option[Double],
                    var MainTimes: Option[Double],
                    var Creator: Option[Int] = None,
                    var CreateTime: Option[DateTime] = None,
                    var Updater: Option[Int] = None,
                    var UpdateTime: Option[DateTime] = None,
                    var CustomerName: Option[String] = None,
                    var EmpName: Option[String]= None,
                  )

object Project {
  implicit val projectFormat: OFormat[Project] = Json.format[Project]
}
