package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10. 
  */
case class Employee(
                     var EmpNo: Int,
                     var UserName: String,
                     var PassWord: String,
                     var EmpName: Option[String] = None,
                     var HeadPortrait: Option[String] = None,
                     var Openid: Option[String] = None,
                     var Role: Option[String] = None,
                     var Email: Option[String] = None,
                     var Department: Option[String] = None,
                     var EmpTel: Option[String] = None,
                     var Creator: Option[Int] = None,
                     var CreateTime: Option[DateTime] = None,
                     var Updater: Option[Int] = None,
                     var UpdateTime: Option[DateTime] = None
                   )

object Employee {
  implicit val employeeFormat: OFormat[Employee] = Json.format[Employee]
}
