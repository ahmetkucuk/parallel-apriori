package main.akka

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 17/03/16.
  */

case class StartApriori(transaction: Set[Transaction], support: Int)

class AprioriActor extends Actor {

  private var support = 20

  private var lastFrequentItems = Set[TreeSet[String]]()
//  private var allFrequentItemSets = Set[TreeSet[String]]()
  var transactions: Set[Transaction] = Set()
  private var initialSender: Option[ActorRef] = None
  var itemSize = 1

  override def receive: Receive =  {
    case StartApriori(transactions: Set[Transaction], support: Int) => {
      this.transactions = transactions
      this.support = support
      initialSender = Some(sender())

      var itemSet = Seq[TreeSet[String]]()
      transactions.foreach(t =>
        t.getItems().foreach( s => { itemSet = itemSet :+ TreeSet(s) } )
      )

      lastFrequentItems = itemSet.toSet

      println(lastFrequentItems.size)
      context.actorOf(Props[FilterItemSetActor]) ! FilterItemSet(transactions, lastFrequentItems, support)

    }
    case FilteredItemSet(itemSet: Set[TreeSet[String]]) => {

      println(s"${itemSet.size}")
      if(itemSet.nonEmpty) {
//        allFrequentItemSets = allFrequentItemSets ++ itemSet
        itemSize += 1
        lastFrequentItems = createCandidateItemSet(itemSet, itemSize)

        if(lastFrequentItems.nonEmpty)
          context.actorOf(Props[FilterItemSetActor]) ! FilterItemSet(transactions, lastFrequentItems, support)
        else {
          initialSender.get ! "finished"
        }
      } else {
//        println(s"all frequent sets: (${allFrequentItemSets.size})")
        initialSender.get ! "finished"
      }
    }
    case _ => print("Message not recognized")
  }
  def createCandidateItemSet(itemSet: Set[TreeSet[String]], itemSize: Int): Set[TreeSet[String]] = {

    val t1 = System.currentTimeMillis()
    def crossProduct(set: Set[TreeSet[String]]): Set[TreeSet[String]] = for { x <- set; y <- set} yield  { x | y }
    val result = crossProduct(itemSet).filter(s => s.size == itemSize)
    val t2 = System.currentTimeMillis()
    println(s"in Actor ${t2 - t1}")
    result
  }

}
