package main

import _root_.akka.actor.{Props, ActorSystem}
import main.akka.{StartApriori, AprioriActor}
import main.regular.AprioriAlgorithm

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 17/03/16.
  */
object HelloAkkaScala extends App {

  val test = new AprioriAlgorithm()

  val t1 = new Transaction(TreeSet("M", "O", "N", "K", "E", "Y"))
  val t2 = new Transaction(TreeSet("D", "O", "N", "K", "E", "Y"))
  val t3 = new Transaction(TreeSet("M", "A", "K", "E"))
  val t4 = new Transaction(TreeSet("M", "U", "C", "K","Y"))
  val t5 = new Transaction(TreeSet("C", "O", "O", "K", "I", "E"))

  //test.analyze(scala.collection.mutable.Seq(t1, t2, t3, t4, t5));
  test.analyze(new DataReader().getTransactions());

  val system = ActorSystem("aprioriakka")
  val apriori = system.actorOf(Props[AprioriActor], "apriori")
  val dataReader = new DataReader()

  apriori ! StartApriori(dataReader.getTransactions(), 20)

  //
  //  // Create the 'helloakka' actor system
  //  val system = ActorSystem("helloakka")
  //
  //  // Create the 'greeter' actor
  //  val greeter = system.actorOf(Props[Greeter], "greeter")
  //
  //  // Create an "actor-in-a-box"
  //  val inbox = Inbox.create(system)
  //
  //  // Tell the 'greeter' to change its 'greeting' message
  //  greeter.tell(WhoToGreet("akka"), ActorRef.noSender)
  //
  //  // Ask the 'greeter for the latest 'greeting'
  //  // Reply should go to the "actor-in-a-box"
  //  inbox.send(greeter, Greet)
  //
  //  // Wait 5 seconds for the reply with the 'greeting' message
  //  val Greeting(message1) = inbox.receive(5.seconds)
  //  println(s"Greeting: $message1")
  //
  //  // Change the greeting and ask for it again
  //  greeter.tell(WhoToGreet("typesafe"), ActorRef.noSender)
  //  inbox.send(greeter, Greet)
  //  val Greeting(message2) = inbox.receive(5.seconds)
  //  println(s"Greeting: $message2")
  //
  //  val greetPrinter = system.actorOf(Props[GreetPrinter])
  //  // after zero seconds, send a Greet message every second to the greeter with a sender of the greetPrinter
  //  system.scheduler.schedule(0.seconds, 1.second, greeter, Greet)(system.dispatcher, greetPrinter)

}
