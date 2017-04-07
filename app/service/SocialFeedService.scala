package service

import java.time.Instant
import javax.inject.Singleton

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import ch.becompany.social.{Feed, Status}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import spray.json._
import DefaultJsonProtocol._
import ch.becompany.social.github.GithubFeed

/**
  * Service to provide a stream of social feed events.
  */
@Singleton
class SocialFeedService {

  /**
    * We initialised the Akka system for the name "Tutorial".
    */
  implicit val system = ActorSystem("Tutorial")

  /**
    * Create a default materializer for Akka.
    */
  implicit val materializer = ActorMaterializer()

  /**
    * Initialise the list of feeds.
    */
  lazy val feeds = Feed(
      "github" -> new GithubFeed("becompany", 5 minutes)
    )(10)

  /**
    * Create a Flow that process incoming elements of type (String, Instant, Try[Status]) and produce String.
    * The produced content is a simple JSON format produced with spray-json from a Map.
    */
  val socialFeedToJSONFlow: Flow[(String, Instant, Try[Status]), String, NotUsed] = Flow[(String, Instant, Try[Status])]
    .map {
      case (feed, date, status) => status match {
        case Success(stat) => Map(
          ("feed",feed),
          ("date", date.toString),
          ("user", stat.author.username),
          ("message", stat.html.render))
          .toJson.compactPrint
        case Failure(e) => e.getMessage
      }
    }

  /**
    * Subscribe the list of feeds via the defined Flow.
    * @return A Source that links the Social feeds with the Flow to process to JSON.
    */
  def subscribe(): Source[String, _] = feeds.subscribe.via(socialFeedToJSONFlow)
}
