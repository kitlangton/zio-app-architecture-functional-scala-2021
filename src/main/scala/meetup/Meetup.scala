package meetup

import zio.*
import java.time.LocalDate
import zio.stream.*
import zio.json.*

// Events, Users, Rsvps, Meetups!
// Good Luck, Kit!

case class Event(id: Int, name: String, date: LocalDate)

object Event:
  given JsonCodec[Event] = DeriveJsonCodec.gen

trait Events:
  def get(id: Int): Task[Option[Event]]

  def all: Task[List[Event]]

  def create(name: String, date: LocalDate): Task[Event]

object Events extends Accessible[Events]:
  val live =
    EventsLive.apply.toLayer

case class EventsLive(analytics: Analytics, random: Random) extends Events:
  val jsonPath = "src/main/resources/events.json"

  def all: Task[List[Event]] =
    for
      content <- ZIO.readFileString(jsonPath)
      events <- ZIO
                  .from(content.fromJson[List[Event]])
                  .orElseFail(new RuntimeException("Could not parse events"))
      _ <- analytics.track("EVENTS ALL")
    yield events

  def get(id: Int): Task[Option[Event]] =
    all.map(_.find(_.id == id))

  def create(name: String, date: LocalDate): Task[Event] =
    for
      id       <- random.nextInt
      event     = Event(id, name, date)
      events   <- all
      newEvents = events :+ event
      _        <- ZIO.writeFileString(jsonPath, newEvents.toJsonPretty)
    yield event

case class User(id: Int, email: String)

object User:
  given JsonCodec[User] = DeriveJsonCodec.gen

trait Users:
  def get(id: Int): Task[Option[User]]

  def all: Task[List[User]]

object Users extends Accessible[Users]:
  val live =
    UsersLive.apply.toLayer

case class UsersLive(random: Random) extends Users:
  val jsonPath = "src/main/resources/users.json"

  def all: Task[List[User]] =
    for
      content <- ZIO.readFileString(jsonPath)
      users <- ZIO
                 .from(content.fromJson[List[User]])
                 .orElseFail(new RuntimeException("Could not parse users"))
    yield users

  def get(id: Int): Task[Option[User]] =
    all.map(_.find(_.id == id))

case class Rsvp(userId: Int, eventId: Int)

object Rsvp:
  given JsonCodec[Rsvp] = DeriveJsonCodec.gen

trait Rsvps:
  def get(userId: Int, eventId: Int): Task[Option[Rsvp]]

  def all: Task[List[Rsvp]]

  def create(userId: Int, eventId: Int): Task[Rsvp]

object Rsvps:
  val live =
    RsvpsLive.apply.toLayer

case class RsvpsLive(random: Random) extends Rsvps:
  val jsonPath = "src/main/resources/rsvps.json"

  def all: Task[List[Rsvp]] =
    for
      content <- ZIO.readFileString(jsonPath)
      rsvps <- ZIO
                 .from(content.fromJson[List[Rsvp]])
                 .orElseFail(new RuntimeException("Could not parse rsvps"))
    yield rsvps

  def get(userId: Int, eventId: Int): Task[Option[Rsvp]] =
    all.map(_.find(r => r.userId == userId && r.eventId == eventId))

  def create(userId: Int, eventId: Int): Task[Rsvp] =
    for
      rsvp    <- ZIO.succeed(Rsvp(userId, eventId))
      rsvps   <- all
      newRsvps = rsvps :+ rsvp
      _       <- ZIO.writeFileString(jsonPath, newRsvps.toJsonPretty)
    yield rsvp

trait Meetup:
  def rsvp(userId: Int, eventId: Int): Task[FullRsvp]

object Meetup extends Accessible[Meetup]:
  val live =
    MeetupLive.apply.toLayer

case class FullRsvp(user: User, event: Event)

trait Analytics:
  def track(message: String): Task[Unit]

object Analytics:
  val live =
    AnalyticsLive.apply.toLayer
      .tap(_ => ZIO.debug("ACCHOEUCCHOSECNUHSONh"))

case class AnalyticsLive(console: Console) extends Analytics:
  def track(message: String): Task[Unit] =
    console.printLine(message)

case class MeetupLive(analytics: Analytics, users: Users, events: Events, rsvps: Rsvps) extends Meetup:
  def rsvp(userId: Int, eventId: Int): Task[FullRsvp] =
    for
      user  <- users.get(userId).someOrFail(new RuntimeException("User not found"))
      event <- events.get(eventId).someOrFail(new RuntimeException("Event not found"))
      _     <- rsvps.create(userId, eventId)
      _     <- analytics.track(s"${user.email} is attending ${event.name}")
    yield FullRsvp(user, event)
