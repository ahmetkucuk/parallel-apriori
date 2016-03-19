package main.akka

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 17/03/16.
  */

case class FilterItemSet(transactions: Set[Transaction], itemSet: Set[TreeSet[String]], support: Int)
case class FilteredItemSet(itemSet: Set[TreeSet[String]])

class FilterItemSetActor extends Actor{

  private var numberOfItemSet = 0
  private var numberOfProcessed = 0

  private var aprioriSender: Option[ActorRef] = None
  var support = 500
  var frequentItems = Seq[TreeSet[String]]()//scala.collection.mutable.Set[TreeSet[String]]()
  override def receive: Receive =  {

    case FilterItemSet(transactions: Set[Transaction], itemSet: Set[TreeSet[String]], support: Int) => {
      this.aprioriSender = Some(sender())
      this.numberOfItemSet = itemSet.size
      this.support = support

      doForEach(transactions, itemSet)

    }
    case ItemCount(itemSet: TreeSet[String], numberOfItem: Int) => {

      numberOfProcessed += 1
      if(numberOfItem >= support) {
        frequentItems = frequentItems :+ itemSet
      }
      if (numberOfProcessed == numberOfItemSet) {
        aprioriSender.get ! FilteredItemSet(frequentItems.toSet)
      }
    }
    case _ => print("filter-item-set-actor-Message not recognized")
  }
  def doForEach(transactions: Set[Transaction], frequentItems: Set[TreeSet[String]]): Unit = {
    frequentItems.foreach( i =>
      context.actorOf(Props(new ItemCounterActor(transactions))) ! ItemCounter(i)
    )
  }
}
