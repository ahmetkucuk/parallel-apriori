package main.akka

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import main.Transaction

import scala.collection.immutable.TreeSet

/**
  * Created by ahmetkucuk on 17/03/16.
  */

case class StartApriori(transaction: Seq[Transaction], support: Int)

class AprioriActor extends Actor {

  private var support = 20

  private var lastFrequentItems = Set[TreeSet[String]]()
  private var allFrequentItemSets = Set[TreeSet[String]]()
  var transactions: Seq[Transaction] = Seq()
  override def receive: Receive =  {
    case StartApriori(transactions: Seq[Transaction], support: Int) => {
      val itemMap = scala.collection.mutable.HashMap[String, Int]()
      this.transactions = transactions
      this.support = support

      var itemSet = Seq[TreeSet[String]]()
      transactions.foreach(t =>
        t.getItems().foreach( s => { itemSet = itemSet :+ TreeSet(s) } )
      )

      lastFrequentItems = itemSet.toSet

      context.actorOf(Props[FilterItemSetActor]) ! FilterItemSet(transactions, lastFrequentItems, support)

    }
    case FilteredItemSet(itemSet: Set[TreeSet[String]]) => {

      println(s"filtered-item-set ${itemSet.size}")
      if(itemSet.nonEmpty) {
        allFrequentItemSets = allFrequentItemSets ++ itemSet
        lastFrequentItems = createCandidateItemSet(itemSet)

        if(lastFrequentItems.nonEmpty)
          context.actorOf(Props[FilterItemSetActor]) ! FilterItemSet(transactions, lastFrequentItems, support)
        else {
          println("self-stop")
          context.stop(self)

        }
      } else {
        println(s"all frequent sets: (${allFrequentItemSets.size})")
        context.stop(self)
      }
    }
    case _ => print("Message not recognized")
  }
  def createCandidateItemSet(itemSet: Set[TreeSet[String]]): Set[TreeSet[String]] = {
    var candidateItemSeq = Seq[TreeSet[String]]()
    itemSet.foreach(s1 =>
      itemSet.foreach(s2 => {
        val unionOf = s1 | s2
        if (s1.size + 1 == unionOf.size)
          candidateItemSeq = candidateItemSeq :+ unionOf
      }
      )
    )
    candidateItemSeq.toSet
  }

}
