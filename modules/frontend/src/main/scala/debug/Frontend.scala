package $package$

import com.raquo.laminar.api.L._
import zio._
import zio.ZIO._


object Frontend {
  val runtime = Runtime.default
  val client  = debugView

  def view: Div =
    div(
      h3("IMPORTANT WEBSITE")
      
    )

  private def debugView[A](name: String, effect: => UIO[A]): Div = {
    val output = Var(List.empty[String])
    div(
      h3(name),
      children <-- output.signal.map { strings =>
        strings.map(div(_))
      },
      onClick --> { _ => ()
        
      }
    )
  }
}
