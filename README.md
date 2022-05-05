[![Build Status](https://app.travis-ci.com/mtumilowicz/scala-zio2-fs2-refined-newtype-workshop.svg?branch=master)](https://app.travis-ci.com/mtumilowicz/scala-zio2-fs2-refined-newtype-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
# scala-zio2-fs2-refined-newtype-workshop
* references
    * https://fs2.io
    * [Klarna Tech Talks: Compose your program flow with Stream - Fabio Labella](https://www.youtube.com/watch?v=x3GLwl1FxcA)
    * https://leanpub.com/pfp-scala
    * https://www.manning.com/books/get-programming-with-haskell
    * https://github.com/fthomas/refined
    * https://github.com/estatico/scala-newtype
    * https://wiki.haskell.org/Newtype

## preface
* goals of this workshop
    * introduction to purely functional effectful streams
    * @newtype: zero-cost wrappers
    * refined types: compile and runtime type validation
* workshop task: implement `PurchaseRepository` and integrate with file
    * answer: `PurchaseFileRepository`

## introduction
* for basics info about effect please refer first: https://github.com/mtumilowicz/scala-http4s-zio-doobie-workshop

## fs2
* `Stream[F,O]` represents a stream of `O` values which may request evaluation of `F` effects
    * `F` - the effect type
    * `O` - the output type
    * example: an effectful stream may produce data of type: `O` by reading it from a network socket: `IO`
* example
    ```
    object Main extends scala.App {
      val stream: fs2.Stream[cats.effect.IO, Int] = fs2.Stream.eval {
        cats.effect.IO { println("BEING RUN!!"); 1 + 1 }
      }

      val asEffect: Stream.CompileOps[cats.effect.IO, cats.effect.IO, Int] = stream.compile

      val resultWithEffect: cats.effect.IO[List[Int]] = asEffect.toList // retain elements into list
      val onlyEffect: effect.IO[Unit] = asEffect.drain // remove all elements from the stream

      val result: List[Int] = resultWithEffect.unsafeRunSync()
      println(result)
      val unit: Unit = onlyEffect.unsafeRunSync()
    }
    ```
* effectful streams need to be compiled to the effect
  * `compile` is just a namespace for methods
    * `fold` is accessible on the stream itself, but return a stream - not an effect
    * drain: IO[Unit] // remove all elements from the stream
        * similar to: `fold((), (acc, o) => acc)`
        * motivation for the program is to only execute their effects
            * outputs are meaningless
        * vs directly on the stream: Stream.sleep(1.seconds).drain // Stream[IO, Nothing] - stream doesn't emit anything
* errors
    * stream can raise errors
        * explicitly: `Stream.raiseError`
        * implicitly: via an exception in pure code or inside an effect
    * `handleErrorWith` method lets us catch errors
        * the stream will be terminated after the error and no more values will be pulled
* `evalMap` vs `map`
    * evalMap = alias for flatMap(o => Stream.eval(f(o)))
    * map - should not perform side effects

## newtype
* value classes context
    * example: `case class Username(val value: String) extends AnyVal`
    * extend the `AnyVal` to avoid some runtime costs
        * limitations and performance issues
            * https://docs.scala-lang.org/overviews/core/value-classes.html
            * A value class is actually instantiated when:
              • a value class is treated as another type.
              • a value class is assigned to an array.
              • doing runtime type tests, such as pattern matching.
    * problem with validation
        * suppose we want `Username` to be non-empty string
        * solution: make constructors private and only expose smart constructors
        * example
            ```
            def mkUsername(value: String): Option[Username] =
                (value.nonEmpty).guard[Option].as(Username(value))
            ```
        * but case classes have copy method, so we can bypass validation
            * example: `username.copy(value = "")`
        * solution: `sealed abstract case class Username(value: String)`
* conclusion: avoid sealed abstract, use the Newtype library
    * example: `@newtype case class Username(value: String)`
* `@newtype` gives us zero-cost wrappers with no runtime overhead
    * uses macros so we need an extra compiler flag `-Ymacro-annotations`
* remark: @newtype do not solve validation; they are just zero-cost wrappers
    * we need refined types - discussed in the next chapter
* haskell digression
    * problem
        ```
        type Name = (String,String) // type synonym

        names :: [Name] // list definition
        names = [ ("Emil","Cioran"), ("Eugene","Thacker"), ("Friedrich","Nietzsche") ]
        ```
        * attempt to implement `Ord` for a type synonym produces compile time error
            ```
            instance Ord Name where
                      compare (f1,l1) (f2,l2) = compare (l1,f1) (l2,f2)
            ```
            * to Haskell, Name is identical to (String, String) and Haskell already knows how to sort tuples
    * solution: you need create a new data type
        ```
        data Name = Name (String, String)

        instance Ord Name where
                   compare (Name (f1,l1)) (Name (f2,l2)) = compare (l1,f1) (l2,f2)
        ```
    * but we want only type synonym, but with custom instances of a type classes
        * Haskell has a preferred method of doing this: using the `newtype` keyword
            ```
            newtype Name = Name (String, String)
            ```
        * types defined with `newtype` can have only one type constructor and one type
            * new type and the type of the field are in direct correspondence
                * in mathematical terms: are isomorphic
            * after the type is checked at compile time, at run time the two types can be treated essentially the same
                * without the overhead or indirection normally associated with a data constructor
        * `data` context
            * everything you define with `newtype`, you can also define using `data`
                * opposite isn’t true

## refined types
* refinement types allow us to validate data at compile time as well as at runtime
* example
    * compile time validation
        ```
        import eu.timepit.refined.types.string.NonEmptyString

        type Username = NonEmptyString
        def lookup(username: Username): F[Option[User]]
        ```
        and then
        ```
        import eu.timepit.refined.auto._
        lookup("") // compilation error
        ```
    * runtime validation: refinement types provide `from` method
        ```
        val res: Either[String, NonEmptyString] = NonEmptyString.from(str)
        ```
* custom refinement type
    ```
    import eu.timepit.refined.api.RefinedTypeOps
    import eu.timepit.refined.numeric.Greater

    type GTFive = Int Refined Greater[5]
    object GTFive extends RefinedTypeOps[GTFive, Int] // adds `from` method

    val number: Int = 33
    val res: Either[String, GTFive] = GTFive.from(number)
    ```