package example

import org.scalajs.dom
import com.raquo.laminar.api.L._
import scala.concurrent.Future
import scala.util.Random
import com.raquo.airstream.ownership.OneTimeOwner

object Main extends App {
  val containerNode = dom.document.querySelector("#app")

  val positions = Var[List[LiveDashboard.Position]](Nil)
  implicit val owenr = new OneTimeOwner(() => ())

  val currencies = Seq("EUR", "USD", "GBP", "CAD", "CZK")

  val pairs = (for {
    base <- currencies
    term <- currencies if base != term
  } yield base + term).toVector

  EventStream
    .periodic(1)
    .foreach(_ =>
      positions.set(
        pairs
          .map(pair =>
            LiveDashboard.Position(
              pair,
              Random.nextDouble() * 1e6,
              Random.nextDouble() * 1e6,
              Random.nextDouble() * 1e2,
              LiveDashboard.HedgingMode.values(Random.nextInt(2))
            )
          )
          .toList
      )
    )

  val rootElement = div(
    LiveDashboard(List("Insti", "Retail"), positions.signal)
  )

  render(containerNode, rootElement)

}
