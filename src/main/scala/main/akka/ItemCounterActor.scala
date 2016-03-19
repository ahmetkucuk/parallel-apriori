package main.akka

import akka.actor.Actor
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 15/03/16.
  */

case class ItemCounter(itemSet: TreeSet[String])

case class ItemCount(itemSet: TreeSet[String], numberOfItem: Int)

class ItemCounterActor(transactions: Set[Transaction]) extends Actor {
  var greeting = ""

  def receive = {
    case ItemCounter(itemSet: TreeSet[String]) => {
      sender ! ItemCount(itemSet, countItemSetInTransaction(transactions, itemSet))
    }

    case _ => sender ! ItemCount(null, -1) // Send the current greeting back to the sender
  }


  def countItemSetInTransaction(transaction: Set[Transaction], itemSet: TreeSet[String]): Int = {

    var counter = 0
    transaction.foreach(t => {
      if(itemSet.subsetOf(t.getItems()))
        counter += 1
    })
    counter
  }
}

