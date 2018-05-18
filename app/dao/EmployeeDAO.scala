package dao

import services.StringUtil._
import com.google.inject.Inject
import models.Employee
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import dao.CustomColumnTypes._
import scala.concurrent.{ExecutionContext, Future}

trait EmployeeComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class EmployeeTable(tag: Tag) extends Table[Employee](tag, "Employee") {

    def empno = column[Int]("EMPNO", O.PrimaryKey, O.AutoInc)

    def username = column[String]("UserName")

    def password = column[String]("PassWord")

    def empname = column[Option[String]]("EMPNAME")

    def openid = column[Option[String]]("OPENID")

    def emptel = column[Option[String]]("EMPTEL")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (empno, username, password, empname, openid, emptel, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (empno, username, password, empname, openid, emptel, creator, createtime, updater, updatetime) =>
          Employee(empno, username, password, empname, openid, emptel, creator, createtime, updater, updatetime)
      }, { e: Employee =>
        Some((e.EmpNo, e.UserName, e.PassWord, e.EmpName, e.Openid, e.EmpTel, e.Creator, e.CreateTime, e.Updater, e.UpdateTime))
      })
  }

}

class EmployeeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            implicit val ec: ExecutionContext) extends EmployeeComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val employees = TableQuery[EmployeeTable]

  def createTable(): Unit = {
    db.run(
      employees.schema.create
    )
  }

  def addAdmin(): Future[Future[Int]] = {
    db.run(
      employees.filter(e => e.empno === 1).result
    ) map (
      _.headOption match {
          case None =>
            db.run(
              employees += Employee(EmpNo = 1, UserName = "admin", PassWord = parseToMD5("123456"), EmpName = Some("Admin"))
            )
          case Some(_)=>
            Future(0)
        }
      )
  }

  def add(e: Employee): Future[Int] = {
    db.run(
      employees += e
    )
  }

  def addInBatch(es: Seq[Employee]): Future[Int] = {
    db.run(
      employees ++= es
    ).map(_ => es.length)
  }

  def update(e: Employee): Future[Int] = {
    db.run(
      employees.filter(_.empno === e.EmpNo).update(e)
    )
  }

  def delete(eNo: Int): Future[Int] = {
    db.run(
      employees.filter(_.empno === eNo).delete
    )
  }

  def get(eNo: Int): Future[Option[Employee]] = {
    db.run(
      employees.filter(_.empno === eNo).result
    ) map (_.headOption)
  }

  def login(userName: String, passWd: String): Future[Option[Employee]] = {
    db.run(
      employees.filter(e => e.username === userName && e.password === passWd).result
    ) map (_.headOption)
  }


}
