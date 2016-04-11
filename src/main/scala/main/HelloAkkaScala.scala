package main

import _root_.akka.actor
import _root_.akka.actor.{ActorRef, Inbox, Props, ActorSystem}
import _root_.akka.util.Timeout
import main.akka.{StartApriori, AprioriActor}
import main.regular.AprioriAlgorithm
import scala.concurrent.duration._

import scala.collection.immutable.TreeSet
import scala.concurrent.{Future, Await}

/**
  * Created by ahmetkucuk on 17/03/16.
  */
object HelloAkkaScala extends App {

  val minSupport = 3

  val test = new AprioriAlgorithm(minSupport = minSupport)

  val t1 = new Transaction(TreeSet("M", "O", "N", "K", "E", "Y"))
  val t2 = new Transaction(TreeSet("D", "O", "N", "K", "E", "Y"))
  val t3 = new Transaction(TreeSet("M", "A", "K", "E"))
  val t4 = new Transaction(TreeSet("M", "U", "C", "K","Y"))
  val t5 = new Transaction(TreeSet("C", "O", "O", "K", "I", "E"))

  //test.analyze(scala.collection.mutable.Seq(t1, t2, t3, t4, t5));
  val transactions = new DataReader().getTransactions()
  val time1: Long = java.lang.System.currentTimeMillis()
  test.analyze(transactions)


  val system = ActorSystem("aprioriakka")
  val apriori = system.actorOf(Props[AprioriActor], "apriori")
  val inbox = Inbox.create(system)


  val time2: Long = java.lang.System.currentTimeMillis()
  inbox.send(apriori, StartApriori(transactions, minSupport))
  val message1 = inbox.receive(15000.seconds)
  println(s"Greeting: $message1")
  val time3: Long = java.lang.System.currentTimeMillis()

  println(s"regular (${(time2 - time1)}) parallel ${(time3 - time2)}")

  system.stop(apriori)
  system.shutdown

}
