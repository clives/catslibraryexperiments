package finagle.featherbed

import com.twitter.finagle.http.Response
import com.twitter.util.{Await, Future}
import featherbed.request.{ErrorResponse, InvalidResponse}



trait SimpleHttpClient {
  import java.net.URL

  val client = new featherbed.Client(new URL("http://httpbin.org/"))
  val VALID_SERVICE ="uuid";
  val INVALID_SERVICE ="uuuid"


  def testGetClients() = {
    val simpleGetRequest =  client.get(VALID_SERVICE).send[Response]()
    val simpleErrorGetRequest = client.get(INVALID_SERVICE).send[Response]()

    Await.result {

      val f1:Future[Unit]= simpleGetRequest map {
        response =>
          println("response" + response.contentString)
      }

      val f2= simpleErrorGetRequest.map(_.contentString).map(Right.apply).handle {
        case err @ ErrorResponse(rep, reason) => Left(err)
      }.map{
        case Left( error ) => println(s"Error: $error")
        case Right( content ) => println(s"Sucess: $content")
      }

      // !! TwitterFuture
      Future.collect(Seq(f1,f2))
    }
  }
}

object AppSimpleHttpClient extends SimpleHttpClient with App {
  testGetClients()
}