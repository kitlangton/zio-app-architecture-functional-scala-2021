// package meetup

// import zio.*
// import java.time.LocalDate

// object Main extends ZIOAppDefault:

//   val program =
//     for
//       events <- Meetup(_.rsvp(3, 3))
//       _      <- ZIO.howdy(events)
//     yield ()

//   def run =
//     program.provide(
//       Meetup.live,
//       Users.live,
//       Events.live,
//       Analytics.live,
//       Rsvps.live,
//       Random.live,
//       Console.live,
//       ZLayer.Debug.mermaid
//     )
