package com.karasiq.bootstrap.table

import rx._

import scalatags.JsDom.all._

trait PagedTable extends Table {
  def currentPage: Var[Int]

  def pages: Rx[Int]

  private lazy val pagination = new Pagination(pages, currentPage)

  override def withStyles(style: String*): Tag = {
    val table = super.withStyles(style:_*)
    div(div(textAlign.center, pagination), table)
  }
}

object PagedTable {
  final class StaticPagedTable(val heading: Rx[Seq[String]], contentProvider: Rx[Seq[TableRow]], perPage: Int) extends PagedTable {
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

  def apply(heading: Rx[Seq[String]], content: Rx[Seq[TableRow]], perPage: Int = 20): StaticPagedTable = {
    new StaticPagedTable(heading, content, perPage)
  }

  def static(heading: Seq[String], content: Seq[TableRow], perPage: Int = 20): StaticPagedTable = {
    this.apply(Rx(heading), Rx(content), perPage)
  }
}
