### Experiments on Cats with Eff, Effect, Monocle, Featherbed,...




#### Effect(IO) with featherbed:

[AsyncHttpClient](./src/main/scala/effect/AsyncHttpClient.scala)


```scala
  def getUUID(baseurl: String, service: String): IO[String] =
    IO async { cb=>
      val client = new featherbed.Client(new URL(baseurl))
      val simpleGetRequest = client.get(service).send[Response]()
      simpleGetRequest.map(_.contentString).map(Right.apply).map(cb(_)).handle {
        case err@ErrorResponse(rep, reason) => cb(Left(err))
      }
    }
```
Either response from featherbed transformed into a IO async.

**Can be executed using:**
```scala
getUUID("http://httpbin.org/", "uuid").unsafeToFuture
```


#### Eff - state with case class lens:

[StateEffLens](./src/main/scala/atnos/eff/StateEffLens.scala)

Allow us to change/update a field of the state represented as case class.
In our case the case class is:

```scala
  case class personContact( name: String, phoneNumber: String)
```

definition how to read/set the field name.

```scala
  val getterName = ( personcontact: personContact) => personcontact.name
  val setterName = ( personcontact: personContact, newName: String) => personcontact.copy( name= newName)
```

definition of a simple function to modify the name (uppercase)
```scala
  val nameUpperCase: Eff[ TSP, String] = for {
    _ <- modify[TSP, String ]( _.toUpperCase )
    result <- get[TSP, String]
  }yield result;
  val updateName = lensState(nameUpperCase, getterName, setterName)
```
then we can use it with:

```scala
updateName.runState( personContact("lowercasename", "0238")).run
```

------------------------

#### Eff - simple use case implicit lens and functional compose of two functions with different signature.

[simpleUseCase](./src/main/scala/atnos/eff/simpleUseCase.scala)


**data:**
```scala
  case class Order(price: Int, currentProfit: Int)
  case class UserOrders(orders: List[Order], userName: String)
```


**basic function:**
```scala
  def updateOrdersProfit[E : _writerString : _stateOrder ](currentprice: Int): Eff[ E, Unit] = for {
      _ <- modify[E, List[Order]] { orders: List[Order] =>
        orders.map { order => order.copy(currentProfit = currentprice - order.price) }
      }
    } yield ();


  def printUserOrders[E: _writerString : _stateUserOrders]:Eff[E,Unit] = for {
     v1 <- get[E, UserOrders]
     _ <- tell(  s"Username: ${v1.userName}, total profit: ${v1.orders.map(_.currentProfit).sum}")
  }yield ();
```


**implicit setter/getter from UserOrders to List(Order)**
```scala
  //lens
  implicit val getterOrders : UserOrders => List[Order] = (userorders: UserOrders) => userorders.orders
  implicit val setterOrders : List[Order] => UserOrders => UserOrders = (orders: List[Order]) => (userorders: UserOrders)  => userorders.copy(orders = orders)
```

**the program based on the two functions:**
```scala
  def programUpdateProfitShowResult[E: _writerString : _stateUserOrders](newPrice: Int):Eff[E,Unit] = for {
    _ <- updateOrdersProfit(newPrice)
    _ <- printUserOrders
  }yield();
```

------------------------
#### Library / Sources

[Cats](https://typelevel.org/cats/)


[Eff for cats](http://atnos-org.github.io/eff/index.html)


[Reference book by underscore](https://underscore.io/books/scala-with-cats/)

