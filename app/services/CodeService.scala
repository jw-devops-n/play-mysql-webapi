package services

import com.google.inject.{Inject, Singleton}
import dao.CodeDAO
import models.Code
import org.joda.time.DateTime
import play.api.Logger
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by TallSafe on 2018/5/31. 
  */
@Singleton
class CodeService @Inject()(val codeDAO: CodeDAO, implicit val ec: ExecutionContext) {

  val codeList: ArrayBuffer[Code] = new ArrayBuffer[Code]()

  var lastSyncTime: DateTime = DateTime.now

  def getCode(sys: String, mod: String, key: String): Option[Code] = {
    var cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Key == key)
    if (cod.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Key == key)
    }
    cod
  }

  def getCodes(sys: String, mod: String): Seq[Code] = {
    var codes = codeList.filter(c => c.SysCode == sys && c.ModCode == mod)
    if (codes.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      codes = codeList.filter(c => c.SysCode == sys && c.ModCode == mod)
    }
    codes
  }

  def getCodeByDescription(sys: String, mod: String, description: String): Option[Code] = {
    var cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Description == description)
    if (cod.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Description == description)
    }
    cod
  }

  def getKeyByDescription(sys: String, mod: String, description: String): Option[String] = {
    getCodeByDescription(sys, mod, description) match {
      case Some(c) => Some(c.Key)
      case None => None
    }
  }

  def getDescription(sys: String, mod: String, code: String): String = {
    getCode(sys, mod, code) match {
      case Some(cod) ⇒ cod.Description
      case None ⇒ ""
    }
  }

  def getKey(sys: String, mod: String, desc: String): String = {
    getCodeByDesc(sys, mod, desc) match {
      case Some(cod) ⇒ cod.Key
      case None ⇒ ""
    }
  }

  def getCodeByDesc(sys: String, mod: String, desc: String): Option[Code] = {
    var cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Description == desc)
    if (cod.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      cod = codeList.find(c ⇒ c.SysCode == sys && c.ModCode == mod && c.Description == desc)
    }
    cod
  }

  def listCodesByDesc(sys: String, mod: String, desc: String): Seq[String] = {
    var codes = codeList.filter(c => c.SysCode == sys && c.ModCode == mod && c.Description.contains(desc)).map(_.Key)
    if (codes.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      codes = codeList.filter(c => c.SysCode == sys && c.ModCode == mod && c.Description.contains(desc)).map(_.Key)
    }
    codes
  }

  def reverseCodeByDesc(sys: String, mod: String, desc: String): String = {
    var cod = codeList.find(c => c.SysCode == sys && c.ModCode == mod && c.Description.contains(desc))
    if (cod.isEmpty) {
      Await.ready(refreshAllCodes(), 2 seconds)
      cod = codeList.find(c => c.SysCode == sys && c.ModCode == mod && c.Description.contains(desc))
    }
    cod match {
      case Some(c) => c.Key
      case None => "No code"
    }
  }

  private def refreshAllCodes(): Future[Any] = {
    val gap = DateTime.now.getMillis - lastSyncTime.getMillis
    if (gap > 20000 || codeList.isEmpty) {
      lastSyncTime = DateTime.now()

      codeDAO.findAll() map {
        codes =>
          codeList.clear()
          codeList ++= codes
      } recoverWith {
        case ex: Exception ⇒
          Logger.error(ex.getMessage)
          Future.failed(null)
      }
    } else {
      Future.successful((): Unit)
    }
  }
}

