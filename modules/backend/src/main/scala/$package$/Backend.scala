package $package$

import zio._ 
import zio.Duration._
import zio.stream.ZStream
import ZIO._

import zhttp.http._
import zhttp.service.Server
import zhttp.socket.{Socket, WebSocketFrame}

object Backend extends ZIOAppDefault:

  private val socket =
    Socket.collect[WebSocketFrame] {
      case WebSocketFrame.Text("FOO")  => ZStream.succeed(WebSocketFrame.text("BAR"))
      case WebSocketFrame.Text("BAR")  => ZStream.succeed(WebSocketFrame.text("FOO"))
      case WebSocketFrame.Ping         => ZStream.succeed(WebSocketFrame.pong)
      case WebSocketFrame.Pong         => ZStream.succeed(WebSocketFrame.ping)
      case fr @ WebSocketFrame.Text(_) => ZStream.repeat(fr).schedule(Schedule.spaced(1.second)).take(10)
    }

  private val httpApp =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => succeed(Response.text(s"Greetings ${name}!"))
      case Method.GET -> !! / "subscriptions" => socket.toResponse
    }
  def run =
   for {
    fiber <- Console.printLine("Press enter to exit...") *> Console.readLine.fork
    port <- systemWith(_.envOrElse("PORT", "8088").map(_.toInt).orElseSucceed(8088))
    _    <- (zhttp.service.Server.start(port, httpApp) raceFirst fiber.await)
   } yield ()


