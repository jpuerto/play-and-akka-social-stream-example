package service

import java.time.Instant

import ch.becompany.social.Status
import ch.becompany.social.github.GithubFeed
import org.scalatest.AsyncFlatSpec

/**
  * Example tests for SocialFeeds
  */
class SocialFeedSpec extends AsyncFlatSpec {

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
}
