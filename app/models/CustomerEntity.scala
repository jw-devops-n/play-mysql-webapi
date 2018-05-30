package models

import play.api.libs.json.{Json, OFormat}


/**
  * Created by TallSafe on 2018/5/28. 
  */
case class CustomerEntity(
                           var Customer: Option[Customer] = None,
                           var Link: Option[Link] = None
                         )

object CustomerEntity {
  implicit val customerEntityFormat: OFormat[CustomerEntity] = Json.format[CustomerEntity]
}
