package service

import java.time.Instant

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import ch.becompany.social.{Status, User}
import org.scalatest.FlatSpec

import scala.util._
import scalatags.Text.all._

/**
  * Scala tests for SocialFeedService.
  */
class SocialFeedServiceSpec extends FlatSpec {

  /**
    * We initialised the Akka system for the name "Test".
    */
  implicit val system = ActorSystem("Test")

  /**
    * Create a default materializer for Akka.
    */
  implicit val materializer = ActorMaterializer()

  val socialFlow: Flow[(Instant, Try[Status]), Boolean, NotUsed] = Flow[(Instant, Try[Status])]
    .map(t => t._2 match {
      case Success(status) => true
      case Failure(e) => false
    })

  behavior of "SocialFeed service"

  it should "transform the latest social feeds events in JSON" in {
    val socialFeedService = new SocialFeedService()
    val testFlow = socialFeedService.socialFeedToJSONFlow
    val mockUser = new User("John Doe", "test")
    val (pub, sub) =
        TestSource.probe[(String, Instant, Try[Status])].via(testFlow).toMat(TestSink.probe[String])(Keep.both).run()
    sub.request(2)
    pub.sendNext(("Test", Instant.MIN, Success(new Status(mockUser, div("test")))))
    pub.sendNext(("Test2", Instant.MIN, Failure(new Exception("Error 404"))))
    sub.expectNext(
      "{\"feed\":\"Test\",\"date\":\"-1000000000-01-01T00:00:00Z\",\"user\":\"John Doe\",\"message\":\"<div>test</div>\"}",
      "Error 404"
    )
  }

  it should "rise the exceptions found while processing the stream" in {
    val socialFeedService = new SocialFeedService()
    val testFlow = socialFeedService.socialFeedToJSONFlow
    val (pub, sub) =
        TestSource.probe[(String, Instant, Try[Status])].via(testFlow).toMat(TestSink.probe[String])(Keep.both).run()
    sub.request(1)
    val error = new Exception("Test")
    pub.sendError(error)
    sub.expectError(error)
  }

}
