package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.mvc.WebSocket
import play.mvc.Controller
import service.SocialFeedService

/**
  * Controller for websocket requests.
  */
@Singleton
class WebSocketController @Inject()(val socialFeedService: SocialFeedService) extends Controller {

  /**
    * Creates a social feed web socket connection.
    * The web socket is attached directly to the social feed stream.
    * @return A web socket connection.
    */
  def socialFeed = WebSocket.accept[String, String] {
    _ => Flow.fromSinkAndSource(Sink.ignore, socialFeedService.subscribe())
  }

}
