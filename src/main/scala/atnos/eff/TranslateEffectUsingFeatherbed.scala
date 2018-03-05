package atnos.eff


import atnos.eff.appTranslateEffectUsingFeatherbed.{R1, S1, authenticate, runAuth}
import org.atnos.eff._
import org.atnos.eff.syntax.eff._
import org.atnos.eff.future._
import org.atnos.eff.interpret._

import scala.concurrent.{Await, Future}

/*
use featherbed to generate a call to a webservice.
make the effect translated into Future or either

http://atnos-org.github.io/eff/org.atnos.site.TransformStack.html
 */
trait TranslateEffectUsingFeatherbed {

  import org.atnos.eff._
  import org.atnos.eff.syntax.eff._
  import org.atnos.eff.future._
  import org.atnos.eff.interpret._
  import scala.concurrent.Future

  // list of access rights for a valid token
  case class AccessRights(rights: List[String])

  // authentication error
  case class AuthError(message: String)

  // DSL for authenticating users
  sealed trait Authenticated[A]
  case class Authenticate(token: String) extends Authenticated[AccessRights]
  type _authenticate[U] = Authenticated |= U

  type AuthErroEither[A] = AuthError Either A
  type _error[U] = AuthErroEither |= U

  /**
    * The order of implicit parameters is really important for type inference!
    * see below
    */

  def runAuth[R, U, A](e: Eff[R, A])(implicit
                                     authenticated: Member.Aux[Authenticated, R, U],
                                     future:        _future[U],
                                     either:        _error[U]): Eff[U, A] =
    translate(e)(new Translate[Authenticated, U] {
      def apply[X](ax: Authenticated[X]): Eff[U, X] =
        ax match {
          case Authenticate(token) =>
            // send the TimedFuture effect in the stack U
            fromFuture(authenticateImpl(token)).collapse
        }
    })

  // call to a service to authenticate tokens
  def authenticateImpl(token: String): Future[AuthError Either AccessRights] = {
    Future.successful[AuthError Either AccessRights] {
      //Left(AuthError("token invalid!"))
      Right(AccessRights(List("right_one")))
    }
  }

  def authenticate[S :_authenticate](token: String) = Authenticate(token).send

  type S1 = Fx.fx3[Authenticated, TimedFuture ,AuthError Either ? ]
  type R1 = Fx.fx2[TimedFuture, AuthError Either ?]




  val result: Eff[R1, AccessRights] = runAuth(authenticate[S1]("faketoken"))
}


object appTranslateEffectUsingFeatherbed extends TranslateEffectUsingFeatherbed with App {


  implicit val scheduler = ExecutorServices.schedulerFromGlobalExecutionContext
  import org.atnos.eff.syntax.future._
  import scala.concurrent._, duration._
  import scala.concurrent.ExecutionContext.Implicits.global

   Await.result(result.runSequential , 1 second)

}