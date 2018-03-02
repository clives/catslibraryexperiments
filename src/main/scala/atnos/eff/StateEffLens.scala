package atnos.eff

import cats.data._
import org.atnos.eff._, all._, syntax.all._
import cats.data._
import cats.implicits._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._

trait StateEffLens {


  type StateInt[A] = State[Int, A]
  type StateIntPair[A] = State[(Int, Int), A]
  type SS = Fx.fx2[StateIntPair, Option]
  type TS = Fx.fx2[StateInt, Option]

  val action: Eff[TS, String] =
    for {
      _ <- put[TS, Int](10)
      h <- OptionEffect.some[TS, String]("hello")
      _ <- modify[TS, Int](_ + 2)
    } yield h

  //getter / setter on the second member of the paire.
  val getter = (pair: (Int, Int)) => pair._2
  val setter = (pair: (Int, Int), j: Int) => (pair._1, j)
  val lensed = lensState(action, getter, setter)

}

trait StateEffLensWithCaseClass{
  case class personContact( name: String, phoneNumber: String)
  val getterName = ( personcontact: personContact) => personcontact.name
  val setterName = ( personcontact: personContact, newName: String) => personcontact.copy( name= newName)

  val getterPhone = ( personcontact: personContact) => personcontact.phoneNumber
  val setterPhone = ( personcontact: personContact, newphoneNumber: String) => personcontact.copy( phoneNumber= newphoneNumber)


  type StatePersonContact[A] = State[personContact,A]
  type StateString[A] = State[String,A]
  type TSP = Fx.fx1[StateString]



  val nameUpperCase: Eff[ TSP, String] = for {
    _ <- modify[TSP, String ]( _.toUpperCase )
    result <- get[TSP, String]
  }yield result;

  val addCountryCode: Eff[ TSP, String] = for {
    _ <- modify[TSP, String ]( "+54" + _ )
    result <- get[TSP, String]
  }yield result;

  val updateName = lensState(nameUpperCase, getterName, setterName)

  val updatePhoneNumber = lensState(addCountryCode, getterPhone, setterPhone)
}


object appStateEffLens extends StateEffLens with App with StateEffLensWithCaseClass {
  println("lens to know how to read/write  the paire state")
  println("result:" + lensed.runOption.runState((20, 30)).run )


  println("lens on a case class")
  println("result:" + updateName.runState( personContact("lowercasename", "0238")).run )

  println("Composition:")
  println("result:" + (updateName >> updatePhoneNumber).runState( personContact("lowercasename", "0238")).run )
}