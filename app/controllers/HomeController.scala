package controllers

import dao._
import filters.RestSecured
import models._
import javax.inject._
import org.joda.time.DateTime
import play.api.{Configuration, Logger}
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import services.Biz
import services.StringUtil._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(
                                menuDAO: MenuDAO,
                                customerDAO: CustomerDAO,
                                employeeDAO: EmployeeDAO,
                                historyDAO: HistoryDAO,
                                linkDAO: LinkDAO,
                                projectDAO: ProjectDAO,
                                workTimeDAO: WorkTimeDAO,
                                biz: Biz,
                                cc: ControllerComponents,
                                val config: Configuration,
                                implicit val re: RestSecured,
                                implicit val ec: ExecutionContext) extends InjectedController {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  def index = Action {
    val sTime = DateTime.now().plusDays(2)
    val eTime = sTime.minusDays(sTime.getDayOfWeek - 1)
    Ok("Application started  " + eTime.toString("yyyy/MM/dd"))
  }

  def login: Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Login].fold(
        error => {
          Future {
            BadRequest(Json.toJson(ProStatus(EMsg = Some(error.toString()))))
          }
        },
        l => {
          employeeDAO.login(l.UserName, parseToMD5(l.Password)).map {
            case Some(e) =>
              Ok(Json.toJson(ProStatus(Status = true))).withSession(req.session + (config.get[String]("auth.userid") -> e.EmpNo.toString))
            case _ =>
              BadRequest(Json.toJson(ProStatus(EMsg = Some("用户名或密码错误！"))))
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def loginOut: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        Future(Ok(Json.toJson(ProStatus(Status = true))).withNewSession)
  }

  def addMenu(): EssentialAction = re.withAuthFuture(parse.json) {
    uid =>
      implicit req =>
        req.body.validate[Menu].fold(
          error => {
            Future {
              BadRequest(Json.toJson(ProStatus(EMsg = Some(error.toString()))))
            }
          },
          m => {
            menuDAO.checkIsEx(m.Code) flatMap {
              case true =>
                m.Creator = Some(uid.toInt)
                m.CreateTime = Some(DateTime.now())
                menuDAO.add(m) map {
                  n =>
                    Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n))))
                }
              case false =>
                Future(BadRequest(Json.toJson(ProStatus(EMsg = Option("code is exist")))))
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def updateMenu(): EssentialAction = re.withAuthFuture(parse.json) {
    uid =>
      implicit req =>
        req.body.validate[Menu].fold(
          error => {
            Future {
              BadRequest(Json.toJson(ProStatus(EMsg = Some(error.toString()))))
            }
          },
          m => {
            m.Updater = Some(uid.toInt)
            m.UpdateTime = Some(DateTime.now())
            menuDAO.add(m) map {
              n =>
                Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n))))
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def deleteMenu(mNo: Int): EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        menuDAO.delete(mNo).map {
          n =>
            Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n))))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def getMenu(mNo: Int): EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        menuDAO.get(mNo).map {
          menu =>
            Ok(Json.toJson(menu))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def getMenuByCode: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        val code = req.getQueryString("code").getOrElse("")
        menuDAO.get(code).map {
          menu =>
            Ok(Json.toJson(menu))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def getAllMenuTrees: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        biz.listAllMenuTrees().map {
          trees =>
            Ok(Json.toJson(trees))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def getAllMenuElTrees: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        biz.listAllMenuElTrees().map {
          trees =>
            Ok(Json.toJson(trees))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def addCustomer(): EssentialAction = re.withAuthFuture(parse.json) {
    _ =>
      implicit req =>
        req.body.validate[Customer].fold(
          error => {
            Future {
              BadRequest(Json.toJson(ProStatus(EMsg = Some(error.toString()))))
            }
          },
          c => {
            customerDAO.add(c) map {
              n =>
                Ok(n.toString)
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def updateCustomer(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Customer].fold(
        error => {
          Future {
            BadRequest(Json.toJson(ProStatus(EMsg = Option(error.toString()))))
          }
        },
        c => {
          customerDAO.update(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def deleteCustomer(cNo: Int): EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        customerDAO.delete(cNo).map {
          n =>
            Ok(n.toString)
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def getCustomer(cNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      customerDAO.get(cNo).map {
        c =>
          Ok(Json.toJson(c))
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def listCustomers(): EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        customerDAO.list().map {
          result =>
            Ok(Json.toJson(result))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def addCustomerEntity(): EssentialAction = re.withAuthFuture(parse.json) {
    uid =>
      implicit req =>
        req.body.validate[CustomerEntity2].fold(
          error => {
            Future {
              BadRequest(error.toString)
            }
          },
          c => {
            val customer = c.Customer.getOrElse(Customer())
            customer.Creator = Some(uid.toInt)
            customer.CreateTime = Some(DateTime.now())
            customerDAO.add(customer) flatMap {
              cid =>
                c.Links.foreach(l => l.CustomerNo = cid)
                linkDAO.addInBatch(c.Links).map {
                  n =>
                    Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n + 1))))
                }
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def updateCustomerEntity(): EssentialAction = re.withAuthFuture(parse.json) {
    uid =>
      implicit req =>
        req.body.validate[CustomerEntity2].fold(
          error => {
            Future {
              BadRequest(error.toString)
            }
          },
          c => {
            val customer = c.Customer.getOrElse(Customer())
            customer.Updater = Some(uid.toInt)
            customer.UpdateTime = Some(DateTime.now())
            c.Links.groupBy(_.LinkNo).map {
              s =>
                s._1 match {
                  case 0 =>
                    linkDAO.addInBatch(s._2)
                  case _ =>
                    linkDAO.updateInBatch(s._2)
                }
            }
            customerDAO.update(customer) map {
              n =>
                Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n + 1))))
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def rmCustomerEntity(): EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        val cNo = req.getQueryString("customerNo").getOrElse("").toInt
        linkDAO.deleteByCNo(cNo).flatMap {
          _ =>
            customerDAO.delete(cNo).map {
              _ =>
                Ok(Json.toJson(ProStatus(Status = true)))
            }
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def checkUserName: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        val username = req.getQueryString("username").getOrElse("")
        employeeDAO.check(username).map {
          result =>
            Ok(Json.toJson(ProStatus(Status = result)))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def addEmployee(): EssentialAction = re.withAuthFuture(parse.json) {
    _ =>
      implicit req =>
        req.body.validate[Employee].fold(
          error => {
            Future {
              BadRequest(Json.toJson(ProStatus(EMsg = Some(error.toString()))))
            }
          },
          c => {
            c.PassWord = parseToMD5(c.PassWord)
            employeeDAO.add(c) map {
              n =>
                Ok(Json.toJson(ProStatus(Status = true, RowAffected = Some(n))))
            } recover {
              case ex: Exception ⇒
                InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
            }
          }
        )
  }

  def updateEmployee(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Employee].fold(
        error => {
          Future {
            BadRequest(error toString())
          }
        },
        c => {
          employeeDAO.update(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def deleteEmployee(eNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      employeeDAO.delete(eNo).map {
        n =>
          Ok(n.toString)
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def getEmployee(eNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      employeeDAO.get(eNo).map {
        c =>
          Ok(Json.toJson(c))
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def listEmployees: EssentialAction = re.withAuthFuture {
    _ =>
      implicit req =>
        employeeDAO.list().map {
          result =>
            Ok(Json.toJson(result))
        } recover {
          case ex: Exception ⇒
            InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
        }
  }

  def addProject(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Project].fold(
        error => {
          Future {
            BadRequest(error.toString)
          }
        },
        c => {
          projectDAO.add(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def updateProject(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Project].fold(
        error => {
          Future {
            BadRequest(error toString())
          }
        },
        c => {
          projectDAO.update(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def deleteProject(pNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      projectDAO.delete(pNo).map {
        n =>
          Ok(n.toString)
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def getProject(pNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      projectDAO.get(pNo).map {
        c =>
          Ok(Json.toJson(c))
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def getProjects(cNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      projectDAO.gets(cNo).map {
        c =>
          Ok(Json.toJson(c))
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def addLink(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Link].fold(
        error => {
          Future {
            BadRequest(error.toString)
          }
        },
        c => {
          linkDAO.add(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def updateLink(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[Link].fold(
        error => {
          Future {
            BadRequest(error toString())
          }
        },
        c => {
          linkDAO.update(c) map {
            n =>
              Ok(n.toString)
          } recover {
            case ex: Exception ⇒
              InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
          }
        }
      )
  }

  def deleteLink(lNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      linkDAO.delete(lNo).map {
        n =>
          Ok(n.toString)
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def getLink(lNo: Int): Action[AnyContent] = Action.async {
    implicit req =>
      linkDAO.get(lNo).map {
        c =>
          Ok(Json.toJson(c))
      } recover {
        case ex: Exception ⇒
          InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
      }
  }

  def addWorkTime(): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      req.body.validate[WorkTime].fold(
        error => {
          Future {
            BadRequest(error toString())
          }
        },
        w => {
          val wDate = new DateTime(w.WorkDate.getYear,
            w.WorkDate.getMonthOfYear, w.WorkDate.getDayOfMonth,
            12, 0, 0, 0
          )
          workTimeDAO.get(w.ProNo, w.EmpNo, wDate) flatMap {
            old =>
              if (old.isEmpty) {
                w.WorkDate = wDate
                workTimeDAO.add(w) map {
                  n =>
                    Ok(n.toString)
                } recover {
                  case ex: Exception ⇒
                    Logger.info(ex.getMessage)
                    InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString))))
                }
              } else {
                Future(Ok("0"))
              }
          } recoverWith {
            case ex: Exception ⇒
              Logger.info(ex.getMessage)
              Future(InternalServerError(Json.toJson(ProStatus(EMsg = Option(ex.toString)))))
          }
        }
      )
  }
}
