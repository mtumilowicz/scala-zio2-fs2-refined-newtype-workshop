# scala-zio2-fs2-refined-newtype-workshop
* references
    * https://fs2.io
    * [Klarna Tech Talks: Compose your program flow with Stream - Fabio Labella](https://www.youtube.com/watch?v=x3GLwl1FxcA)
    * https://leanpub.com/pfp-scala

## fs2
* `Stream[F,O]` represents a stream of `O` values which may request evaluation of `F` effects
    * `F` - the effect type
    * `O` - the output type
    * example: an effectful stream may produce data of type: `O` by reading it from a network socket: `IO`
* example
    ```
    object Main2 extends scala.App {
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

## newtype
* value classes
    * In vanilla Scala, we can wrap a single field and extend the AnyVal abstract class to avoid
      some runtime costs
    * example: `case class Username(val value: String) extends AnyVal`
    * suppose we want value to be not empty
        * A way
          to communicate our intentions to the compiler is to make the case class constructors
          private only expose smart constructors.
        * example
            ```
            def mkUsername(value: String): Option[Username] =
            (value.nonEmpty).guard[Option].as(Username(value))
            ```
    * We can still do wrong...
        * example: username.copy(value = "")
        * we are still using case classes, which means the copy method is still there
        * proper way to finally get around this issue is to use sealed abstract case class es
            * sealed abstract case class Username(value: String)
    * limitations and performance issues
        * https://docs.scala-lang.org/overviews/core/value-classes.html
        * A value class is actually instantiated when:
          • a value class is treated as another type.
          • a value class is assigned to an array.
          • doing runtime type tests, such as pattern matching.
    * solution
        *  avoid value classes and sealed abstract classes completely
          and instead use the Newtype 2 library, which gives us zero-cost wrappers with no runtime
          overhead
* @newtype case class Username(value: String)
    * It uses macros so we need  an extra compiler flag -Ymacro-annotations in versions 2.13.0 and above
* Newtypes do not solve validation; they are just zero-cost wrappers

## refined types