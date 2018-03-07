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

#### Library / Sources

[Cats](https://typelevel.org/cats/)


[Eff for cats](http://atnos-org.github.io/eff/index.html)


[Reference book by underscore](https://underscore.io/books/scala-with-cats/)

