package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.Project
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ProjectComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class ProjectTable(tag: Tag) extends Table[Project](tag, "Project") {

    def prono = column[Int]("PRONO", O.PrimaryKey, O.AutoInc)

    def customerno = column[Int]("CUSTOMERNO")

    def proname = column[String]("PRONAME")

    def empno = column[Option[Int]]("EMPNO")

    def protype = column[String]("PROTYPE")

    def stime = column[Option[DateTime]]("STIME")

    def prostatus = column[String]("PROSTATUS")

    def etime = column[Option[DateTime]]("ETIME")

    def lentime = column[Option[Double]]("LENTIME")

    def cost = column[Option[Double]]("COST")

    def maintimes = column[Option[Double]]("MAINTIMES")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (prono, customerno, proname, empno, protype, stime, prostatus, etime, lentime, cost, maintimes, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (prono, customerno, proname, empno, protype, stime, prostatus, etime, lentime, cost, maintimes, creator, createtime, updater, updatetime) =>
          Project(prono, customerno, proname, empno, protype, stime, prostatus, etime, lentime, cost, maintimes, creator, createtime, updater, updatetime)
      }, { p: Project =>
        Some((p.ProNo, p.CustomerNo, p.ProName, p.EmpNo, p.ProType, p.Stime, p.ProStatus, p.Etime, p.LenTime, p.Cost, p.MainTimes, p.Creator, p.CreateTime, p.Updater, p.UpdateTime))
      })
  }

}

class ProjectDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                           implicit val ec: ExecutionContext) extends ProjectComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val projects = TableQuery[ProjectTable]

  def createTable(): Unit = {
    db.run(
      projects.schema.create
    )
  }

  def add(p: Project): Future[Int] = {
    db.run(
      projects += p
    )
  }

  def addInBatch(ps: Seq[Project]): Future[Int] = {
    db.run(
      projects ++= ps
    ).map(_ => ps.length)
  }

  def update(p: Project): Future[Int] = {
    db.run(
      projects.filter(_.prono === p.ProNo).update(p)
    )
  }

  def delete(pNo: Int): Future[Int] = {
    db.run(
      projects.filter(_.prono === pNo).delete
    )
  }

  def get(pNo: Int): Future[Option[Project]] = {
    db.run(
      projects.filter(_.prono === pNo).result
    ) map (_.headOption)
  }

  def gets(cNo: Int): Future[Seq[Project]] = {
    db.run(
      projects.filter(_.customerno === cNo).result
    )
  }
}
