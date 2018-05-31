package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/31. 
  */
case class Code(var SysCode: String = "",
                var ModCode: String = "",
                var Key: String = "",
                var Description: String = "",
                var Point01: Option[Double] = None,
                var Point02: Option[Double] = None,
                var String01: Option[String] = None,
                var String02: Option[String] = None,
                var Date01: Option[DateTime] = None,
                var Date02: Option[DateTime] = None,
                var Creator: Option[Int] = None,
                var CreateTime: Option[DateTime] = None,
                var Updater: Option[Int] = None,
                var UpdateTime: Option[DateTime] = None
               )
object Code {
  implicit val codeFormat: OFormat[Code] = Json.format[Code]

}
