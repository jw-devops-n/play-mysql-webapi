package filters

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by TallSafe on 2018/5/17. 
  */

@Singleton
final class RestSecured @Inject()(val config: Configuration,
                                  implicit val ec: ExecutionContext,
                                  cc: ControllerComponents
                                 ) extends AbstractController(cc) {

  def getUserId(request: RequestHeader): Option[String] = request.session.get(config.get[String]("auth.userid"))

  def withAuthFuture(f: => String => Request[AnyContent] => Future[Result]): EssentialAction = {
    Security.WithAuthentication(getUserId) {
      uid =>
        Action.async(request => f(uid)(request))
    }
  }

  def withAuthFuture[A](p: BodyParser[A])(f: => String => Request[A] => Future[Result]): EssentialAction = {
    Security.WithAuthentication(getUserId) {
      uid =>
        Action.async(p)(request => f(uid)(request))
    }
  }
}

