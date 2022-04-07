package meetup

import zio.*
import zio.stream.*
import meetup.Zymposium.Box

object Zymposium extends ZIOAppDefault:

  trait Foo:
    def bar: UIO[Int]

  case class Box[+R](set: Set[Any]):
    def ++[R1](that: Box[R1]): Box[R with R1] =
      Box(set ++ that.set)

  object Box:
    def make[R](value: R): Box[R] =
      Box[R](Set(value))

  val intBox: Box[Int]       = Box.make(1)
  val stringBox: Box[String] = Box.make("foo")
  val boolBox: Box[Boolean]  = Box.make(true)

  // val bothBoxes: Box[Int & String & Boolean] =
  //    intBox ++ stringBox ++ boolBox

  object Foo:
    val live: URLayer[Ref[Int] & Analytics, Foo] =
      FooLive.apply.toLayer

  case class FooLive(ref: Ref[Int], analytics: Analytics) extends Foo:
    def bar: UIO[Int] = ref.get

  // # Questions / Topics
  // - ZStream question - @ahmad.ragab
  // - Where to put layer definitions (trait companion vs. implementation comnpanion)
  // - Streams (ZStream / ZSink / ZChannel / ZPipeline)
  // - Hubs

  def run =
    ZStream
      .fromFile(java.nio.file.Paths.get("./src/main/resources/numbers.txt"))
      .via(ZPipeline.utf8Decode)
      .via(ZPipeline.splitLines)
      .map(_.toInt)
      .foreach(int => ZIO.debug(s"INT: $int"))
