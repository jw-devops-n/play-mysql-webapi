package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/17. 
  */
case class Login(
                  var UserName: String = "",
                  var Password: String = "",
                )

object Login {
  implicit val loginFormat: OFormat[Login] = Json.format[Login]
}

