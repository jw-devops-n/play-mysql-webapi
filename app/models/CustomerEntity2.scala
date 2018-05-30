package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/30. 
  */
case class CustomerEntity2(
                            var Customer: Option[Customer] = None,
                            var Links: Seq[Link] = Nil
                          )

object CustomerEntity2 {
  implicit val customerEntity2Format: OFormat[CustomerEntity2] = Json.format[CustomerEntity2]
}