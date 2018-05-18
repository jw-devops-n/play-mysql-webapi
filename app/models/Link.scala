package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10.
  */
case class Link(
                 var LinkNo: Int,
                 var CustomerNo: Int,
                 var LinkName: String,
                 var LinkTel: Option[String],
                 var Creator: Option[Int] = None,
                 var CreateTime: Option[DateTime] = None,
                 var Updater: Option[Int] = None,
                 var UpdateTime: Option[DateTime] = None
               )

object Link {
  implicit val linkFormat: OFormat[Link] = Json.format[Link]
}
