package zio

import zio.*
import java.io.IOException

extension (self: ZIO.type)
  def howdy(msg: => Any): ZIO[Random & Console, IOException, Unit] =
    val randomNumberOfHowdy =
      Random.nextIntBounded(10).map(_ + 1)
    randomNumberOfHowdy.flatMap { howdyn =>
      val howdies = "Howdy " * howdyn
      val bowdies = "Bowdy " * howdyn
      val header  = scala.Console.BLUE_B + scala.Console.WHITE + " " + howdies + scala.Console.RESET
      val ender   = scala.Console.BLUE_B + scala.Console.WHITE + " " + bowdies + scala.Console.RESET
      Console.printLine(
        s"\n👋🤠$header\n" + scala.Console.GREEN + scala.Console.BOLD + zio.test.PrettyPrint(
          msg
        ) + scala.Console.RESET + s"\n👋💀$ender\n"
      )
    }

  def readFileString(path: String) =
    ZIO.bracket(ZIO(scala.io.Source.fromFile(path))) { source =>
      UIO(source.close)
    } { source =>
      ZIO(source.mkString)
    }

  def writeFileString(path: String, content: String) =
    ZIO.bracket(ZIO(new java.io.FileWriter(path))) { writer =>
      UIO(writer.close)
    } { writer =>
      ZIO(writer.write(content))
    }
