package controllers

import filters.RestSecured
import javax.inject.Inject
import play.api.{Configuration, Environment}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

/**
  * Created by TallSafe on 2018/5/25. 
  */
class FileController @Inject()(val config: Configuration,
                               val env: Environment,
                               implicit val re: RestSecured,
                               implicit val ec: ExecutionContext) extends InjectedController {

  def atImg(id: String): Action[AnyContent] = Action {
    val photoDir = env.getFile(s"${config.getOptional[String]("img").getOrElse("")}")
    if (photoDir.exists()) {
      val tempPhoto = photoDir.listFiles().find(p ⇒ p.getName.startsWith(id))
      tempPhoto match {
        case Some(photo) ⇒
          Ok.sendFile(photo, inline = true)
        case None ⇒
          val photo = env.getFile(s"${config.getOptional[String]("img").getOrElse("")}/default.jpg")
          Ok.sendFile(photo, inline = true)
      }
    } else {
      val photo = env.getFile(s"${config.getOptional[String]("img").getOrElse("")}/default.jpg")
      Ok.sendFile(photo)
    }
  }

  def atVideo(id: String): Action[AnyContent] = Action {
    val videoDir = env.getFile(s"${config.getOptional[String]("video").getOrElse("")}")
    if (videoDir.exists()) {
      val tempVideo = videoDir.listFiles().find(p ⇒ p.getName.startsWith(id))
      tempVideo match {
        case Some(video) ⇒
          Ok.sendFile(video, inline = true)
        case None ⇒
          val video = env.getFile(s"${config.getOptional[String]("video").getOrElse("")}/video1.mp4")
          Ok.sendFile(video, inline = true)
      }
    } else {
      val video = env.getFile(s"${config.getOptional[String]("video").getOrElse("")}/video1.mp4")
      Ok.sendFile(video)
    }
  }
}
