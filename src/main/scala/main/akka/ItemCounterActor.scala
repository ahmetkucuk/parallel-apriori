package main.akka

import akka.actor.Actor
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 15/03/16.
  */

case class ItemCounter(transaction: Seq[Transaction], itemSet: TreeSet[String])

case class ItemCount(itemSet: TreeSet[String], numberOfItem: Int)

class ItemCounterActor extends Actor {
  var greeting = ""

  def receive = {
    case ItemCounter(transaction: Seq[Transaction], itemSet: TreeSet[String]) => {
      sender ! ItemCount(itemSet, countItemSetInTransaction(transaction, itemSet))
    }

    case _ => sender ! ItemCount(null, -1) // Send the current greeting back to the sender
  }


  def countItemSetInTransaction(transaction: Seq[Transaction], itemSet: TreeSet[String]): Int = {

    var counter = 0
    transaction.foreach(t => {
      if(itemSet.subsetOf(t.getItems()))
        counter += 1
    })
    counter
  }
}

