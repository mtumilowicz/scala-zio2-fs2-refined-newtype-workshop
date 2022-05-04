# scala-zio2-fs2-refined-newtype-workshop

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
