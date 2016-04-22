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
object AprioriAkkaApp {


  def main(args: Array[String]): Unit = {

    if(args.length < 2){
      println("There is no enough input args 0 -> filename, args 1 -> minSupport")
    }

    val filename = args(0)
    val minSupport = Integer.parseInt(args(1))

    val transactions = new DataReader().getTransactions(filename)

    val system = ActorSystem("aprioriakka")
    val apriori = system.actorOf(Props[AprioriActor], "apriori")
    val inbox = Inbox.create(system)


    val time2: Long = java.lang.System.currentTimeMillis()
    inbox.send(apriori, StartApriori(transactions, minSupport))
    val message1 = inbox.receive(15000.seconds)
    println(s"Greeting: $message1")
    val time3: Long = java.lang.System.currentTimeMillis()

    println(s"akka ${(time3 - time2)}")

    system.stop(apriori)
    system.shutdown
  }

}
