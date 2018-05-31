package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/30. 
  */
case class EmployeeComBox(
                           var EmpNo: Int,
                           var EmpName: Option[String] = None
                         )

object EmployeeComBox {
  implicit val employeeComBoxFormat: OFormat[EmployeeComBox] = Json.format[EmployeeComBox]
}
