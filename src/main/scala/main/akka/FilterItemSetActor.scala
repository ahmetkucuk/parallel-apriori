package main.akka

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 17/03/16.
  */

case class FilterItemSet(transactions: Seq[Transaction], itemSet: Set[TreeSet[String]], support: Int)
case class FilteredItemSet(itemSet: Set[TreeSet[String]])

class FilterItemSetActor extends Actor{

  private var numberOfItemSet = 0
  private var numberOfProcessed = 0

  private var aprioriSender: Option[ActorRef] = None
  var support = 500
  val frequentItems = scala.collection.mutable.Set[TreeSet[String]]()
  override def receive: Receive =  {

    case FilterItemSet(transactions: Seq[Transaction], itemSet: Set[TreeSet[String]], support: Int) => {
      this.aprioriSender = Some(sender())
      this.numberOfItemSet = itemSet.size
      this.support = support

      doForEach(transactions, itemSet)

    }
    case ItemCount(itemSet: TreeSet[String], numberOfItem: Int) => {
      numberOfProcessed += 1
      if(numberOfItem >= support) {
        frequentItems += itemSet
      }
      if (numberOfProcessed == numberOfItemSet) {
        println("filter-item-set-actor-finished")
        aprioriSender.get ! FilteredItemSet(frequentItems.map(m => m).toSet)
      }
    }
    case _ => print("filter-item-set-actor-Message not recognized")
  }
  def doForEach(transaction: Seq[Transaction], frequentItems: Set[TreeSet[String]]): Unit = {
    frequentItems.foreach( i =>
      context.actorOf(Props[ItemCounterActor]) ! ItemCounter(transaction, i)
    )
  }
}
