package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/17. 
  */
case class ProStatus(
                      var Status: Boolean = false,
                      var RowAffected: Option[Int] = None,
                      var CheckResult: Option[Boolean] = None,
                      var EMsg: Option[String] = None,
                    )

object ProStatus {
  implicit val proStatusFormat: OFormat[ProStatus] = Json.format[ProStatus]
}
