package atnos.eff

import cats.data.State
import org.atnos.eff.{Eff, Fx}
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import cats._
import data._
import org.atnos.eff._

trait simpleUseCase {


  type StateUserOrders[A] = State[UserOrders, A]
  type StateOrder[A] = State[List[Order], A]

  type ReaderInt[A] = Reader[Int, A]
  type WriterString[A] = Writer[String, A]


  // useful type aliases showing that the ReaderInt and the WriterString effects are "members" of R
  // note that R could have more effects
  type _readerInt[E] = ReaderInt |= E
  type _writerString[E] = WriterString |= E
  type _stateOrder[E] = StateOrder |= E
  type _stateUserOrders[E] = StateUserOrders |= E
  type UserOrderS[E] = State[UserOrders, ?] |= E


  case class Order(price: Int, currentProfit: Int)

  case class UserOrders(orders: List[Order], userName: String)


  def updateOrdersProfit[E : _writerString : _stateOrder ](currentprice: Int): Eff[ E, Unit] = for {
      _ <- modify[E, List[Order]] { orders: List[Order] =>
        orders.map { order => order.copy(currentProfit = currentprice - order.price) }
      }
    } yield ();


  def printUserOrders[E: _writerString : _stateUserOrders]:Eff[E,Unit] = for {
     v1 <- get[E, UserOrders]
     _ <- tell(  s"Username: ${v1.userName}, total profit: ${v1.orders.map(_.currentProfit).sum}")
  }yield ();


  import state._

  //lens
  implicit val getterOrders : UserOrders => List[Order] = (userorders: UserOrders) => userorders.orders
  implicit val setterOrders : List[Order] => UserOrders => UserOrders = (orders: List[Order]) => (userorders: UserOrders)  => userorders.copy(orders = orders)


  def programUpdateProfitShowResult[E: _writerString : _stateUserOrders](newPrice: Int):Eff[E,Unit] = for {
    _ <- updateOrdersProfit(newPrice)
    _ <- printUserOrders
  }yield();
}


object appSimpleUseCase extends simpleUseCase with App {
  val listOrders:List[Order]=(1 to 3).map(Order(_, 0)).toList
  val orderJohn = UserOrders( listOrders, "John")

  println("use implicit lens to compose a function from two functions with different signature")
  println("result:"+ programUpdateProfitShowResult[Fx.fx2[WriterString, State[UserOrders, ?]]](23).runWriterUnsafe( (x:String) => println(x)).runState(orderJohn).run);
}