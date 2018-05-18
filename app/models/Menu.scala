package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/17. 
  */
case class Menu(
                 var MenuNo: Int = 0,
                 var Code: String = "",
                 var PCode: String = "",
                 var Path: String = "",
                 var Name: Option[String] = None,
                 var Icon: Option[String] = None,
                 var Order: Option[Int] = None,
                 var IsIconVisible: Option[Boolean] = None,
                 var Creator: Option[Int] = None,
                 var CreateTime: Option[DateTime] = None,
                 var Updater: Option[Int] = None,
                 var UpdateTime: Option[DateTime] = None
               )

object Menu {
  implicit val menuFormat: OFormat[Menu] = Json.format[Menu]

}
