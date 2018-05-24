package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.History
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait HistoryComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class HistoryTable(tag: Tag) extends Table[History](tag, "History") {

    def empno = column[Int]("EMPNO")

    def prono = column[Int]("PRONO")

    def year = column[Int]("YEAR")

    def month = column[Int]("MONTH")

    def week = column[Int]("WEEK")

    def worktime = column[Double]("WORKTIME")

    def overtime = column[Option[Double]]("OVERTIME")

    def remark = column[Option[String]]("REMARK")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")

    def * = (empno, prono, year, month, week, worktime, overtime, remark, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (empno, prono, year, month, week, worktime, overtime, remark, creator, createtime, updater, updatetime) =>
          History(empno, prono, year, month, week, worktime, overtime, remark, creator, createtime, updater, updatetime)
      }, { h: History =>
        Some((h.EmpNo, h.ProNo, h.Year, h.Month, h.Week, h.WorkTime, h.OverTime, h.Remark, h.Creator, h.CreateTime, h.Updater, h.UpdateTime))
      })
  }

}

class HistoryDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                           implicit val ec: ExecutionContext) extends HistoryComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val historys = TableQuery[HistoryTable]

  def createTable(): Unit = {
    db.run(
      historys.schema.create
    )
  }

  def add(c: History): Future[Int] = {
    db.run(
      historys += c
    )
  }

  def addInBatch(cs: Seq[History]): Future[Int] = {
    db.run(
      historys ++= cs
    ).map(_ => cs.length)
  }

  def empHistorys(empNo: Option[Int]): PartialFunction[Boolean, Future[Seq[History]]] = {
    case true =>
      db.run(
        historys.filter(_.empno === empNo.get).result
      )
  }

  def proHistorys(proNo: Option[Int]): PartialFunction[Boolean, Future[Seq[History]]] = {
    case _ =>
      db.run(
        historys.filter(_.prono === proNo.get).result
      )
  }

  def gets(empNo: Option[Int], proNo: Option[Int]): Future[Seq[History]] = {
    (empHistorys(empNo) orElse proHistorys(proNo)) (empNo.nonEmpty)
  }
}
