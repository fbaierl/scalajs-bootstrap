package com.karasiq.bootstrap.table

import com.karasiq.bootstrap.BootstrapImplicits._
import rx._

import scalatags.JsDom.all._

trait PagedTable extends Table {
  def currentPage: Var[Int]

  def pages: Rx[Int]

  private def pagination = new Pagination(pages, currentPage)

  override def renderTag(md: Modifier*): RenderedTag = {
    div(div(textAlign.center, pagination), super.renderTag(md))
  }
}

object PagedTable {
  final class StaticPagedTable(val heading: Rx[Seq[Modifier]], contentProvider: Rx[Seq[TableRow]], perPage: Int) extends PagedTable {
    override val currentPage: Var[Int] = Var(1)

    override val content: Rx[Seq[TableRow]] = Rx {
      val data = contentProvider()
      val page = currentPage()
      data.slice(perPage * (page - 1), perPage * (page - 1) + perPage)
    }

    override val pages: Rx[Int] = Rx {
      val data = contentProvider()
      if (data.isEmpty) {
        1
      } else if (data.length % perPage == 0) {
        data.length / perPage
      } else {
        data.length / perPage + 1
      }
    }
  }

  def apply(heading: Rx[Seq[Modifier]], content: Rx[Seq[TableRow]], perPage: Int = 20): StaticPagedTable = {
    new StaticPagedTable(heading, content, perPage)
  }

  def static(heading: Seq[Modifier], content: Seq[TableRow], perPage: Int = 20): StaticPagedTable = {
    this.apply(Rx(heading), Rx(content), perPage)
  }
}