package example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import enumeratum._
import com.raquo.airstream.core
import com.raquo.domtypes.jsdom.defs.events.TypedTargetEvent
import enumeratum.values.IntEnumEntry
import enumeratum.values.IntEnum

object LiveDashboard {

  sealed abstract class HedgingMode(val value: Int) extends IntEnumEntry

  object HedgingMode extends IntEnum[HedgingMode] {
    override val values = findValues

    final case object SQUARE extends HedgingMode(0)
    final case object AUTO extends HedgingMode(1)

    implicit val ord: Ordering[HedgingMode] = Ordering.by(_.value)

    def options(current: HedgingMode) =
      HedgingMode.values.map(o =>
        option(o.toString(), selected := o == current)
      )
  }

  final case class Position(
      pair: String,
      base: Double,
      term: Double,
      rate: Double,
      hedgingMode: HedgingMode
  ) {

    def render: HtmlElement = tr(
      List(
        td(pair),
        td(base),
        td(term),
        td(rate),
        td(
          select(
            HedgingMode.options(hedgingMode),
            onChange --> { el =>
              el.target match {
                case tgt: dom.HTMLSelectElement =>
                  val value = HedgingMode.withValue(tgt.selectedIndex)
                  dom.window.alert(s"changed to ${value}")
              }
            }
          )
        )
      )
    )
  }

  def apply(
      initialPortfolios: List[Portfolio],
      rows: Signal[List[Position]]
  ): HtmlElement = {

    div(
      p("Dashboard"),
      table(
        thead(
          tr(List("Pair", "Base", "Term", "Rate", "HedgingMode").map(td(_)))
        ),
        tbody(
          children <-- rows.map(_.map(_.render))
        )
      )
    )
  }
}
