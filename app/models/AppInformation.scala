package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/22. 
  */
case class AppInformation(
                           var AppId: Int = 0,
                           var ServerId: Int = 0,
                           var Name: Option[String] = None,
                           var AppType: Option[String] = None,
                           var Path: Option[String] = None,
                           var Remark: Option[String] = None,
                           var Creator: Option[Int] = None,
                           var CreateTime: Option[DateTime] = None,
                           var Updater: Option[Int] = None,
                           var UpdateTime: Option[DateTime] = None
                         )

object AppInformation {
  implicit val appInformationFormat: OFormat[AppInformation] = Json.format[AppInformation]
}
