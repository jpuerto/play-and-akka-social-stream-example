package controllers

import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl._
import ch.becompany.social.Status
import ch.becompany.social.github.GithubFeed
import org.scalatest.AsyncFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util._

/**
  * Created by jpuerto on 5/04/17.
  */
class SocialFeedSpec extends AsyncFlatSpec {


  val socialFlow: Flow[(Instant, Try[Status]), String, NotUsed] = Flow[(Instant, Try[Status])]
    .map(t => t._2 match {
      case Success(status) => status.html.render
      case Failure(e) => e.getMessage()
    })

  behavior of "SocialFeed service"

  it should "print something" in {
    println("Hello Scalatest")
    assert(true)
  }

  it should "provide latest social feeds" in {
    val githubFeed = new GithubFeed("becompany")
    githubFeed.latest(5) map {
      (list: List[(Instant, Status)]) => {
        list.foreach(println(_))
        assert(list.size == 5)
      }
    }
  }

  it should "stream latest social feeds" in {
    val githubFeed = new GithubFeed("becompany")
    val streamGraph: RunnableGraph[Future(String)] =
      githubFeed.stream()
        .via(socialFlow)
        .map(
          case event:String => {
          println(event)
          event
        })
        val event = streamGraph.run()
      assert(true)
  }
}
