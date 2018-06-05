package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/6/5. 
  */
case class WorkTimeEntity(
                           var ProName: String = "",
                           var EmpName: Option[String] = None,
                           var WorkTime: WorkTime
                         )

object WorkTimeEntity {
  implicit val workTimeEntityFormat: OFormat[WorkTimeEntity] = Json.format[WorkTimeEntity]
}
