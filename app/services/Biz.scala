package services

import com.google.inject.Inject
import dao.MenuDAO
import models.{Menu, MenuTree}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by TallSafe on 2018/5/18. 
  */
class Biz@Inject()(
                    val menuDAO: MenuDAO,
                    implicit val ec: ExecutionContext
                  ) {
  private def buildMenuTree(pMenuCode: String, menus: Seq[Menu]): Seq[MenuTree] = {
    menus.filter(_.PCode == pMenuCode).sortBy(_.Order) map {
      menu ⇒
        val entity = MenuTree(Option(menu), Seq())
        entity.Children = buildMenuTree(menu.Code, menus)
        entity
    }
  }

  def listAllMenuTrees(): Future[Seq[MenuTree]] = {
    menuDAO.findAll() map {
      menus =>
        buildMenuTree("root", menus.distinct)
    }
  }


}
