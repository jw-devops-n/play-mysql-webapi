package dao

import java.sql.Timestamp

import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.jdbc.OracleProfile.api._
import slick.jdbc.{JdbcType, SetParameter}
import slick.lifted.MappedTo

object CustomColumnTypes {
  implicit val dateTimeType: JdbcType[DateTime] with BaseTypedType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime)
    )

  implicit val SetDateTime: AnyRef with SetParameter[DateTime] = SetParameter[DateTime](
    (dt, pp) => pp.setTimestamp(new Timestamp(dt.getMillis))
  )

  final case class Id[A](value: Long) extends AnyVal with MappedTo[Long]

}
