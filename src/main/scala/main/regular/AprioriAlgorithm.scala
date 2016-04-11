package main.regular


import main.Transaction

import scala.collection.immutable.TreeSet
import scala.math.Ordering.String

class AprioriAlgorithm(
    minSupport: Int = 50,
    minConfidence: Double = 0.6,
    maxItemSetSize: Int = 5,
    isQuickRun: Boolean = true,
    maxJoinedSetsSizeWhenQuickRun: Int = 2000,
    timeoutMillis: Int = 60000) {
  var itemSize = 1

  def analyze(transactions: Set[Transaction]): Unit = {
    val itemSet = scala.collection.mutable.Set[TreeSet[String]]()
    transactions.foreach(t =>

      t.getItems().foreach( s =>
        itemSet += TreeSet(s)
      )
    )

    var frequentItemSet = filterBySupportCount(transactions, itemSet, minSupport)

    while(frequentItemSet.nonEmpty) {

      itemSize += 1
      val candidates = createCandidateItemSet(frequentItemSet, itemSize)

      println(frequentItemSet.size)
      frequentItemSet = filterBySupportCount(transactions, candidates, minSupport)

    }
  }

//  def getFrequentItemSet(): Set[FrequentItemSet] = {
//    (new FrequentItemSet(, 0))
//  }

  def createCandidateItemSet(itemSet: scala.collection.mutable.Set[TreeSet[String]], itemSize: Int): scala.collection.mutable.Set[TreeSet[String]] = {


    val t1 = System.currentTimeMillis()
    val candidateItemSet = scala.collection.mutable.Set[TreeSet[String]]()
    itemSet.foreach(s1 =>
      itemSet.foreach(s2 => {
        val unionOf = s1 | s2
        if (itemSize == unionOf.size)
          candidateItemSet += unionOf
      }
      )
    )

    val t2 = System.currentTimeMillis()
    println(s"in Regular ${t2 - t1}")
    candidateItemSet
  }

  def filterBySupportCount(transaction: Set[Transaction], itemSet: scala.collection.mutable.Set[TreeSet[String]], support: Int): scala.collection.mutable.Set[TreeSet[String]] = {
    val candidateItemSet = scala.collection.mutable.Set[TreeSet[String]]()

    itemSet.foreach(tSet => {
      var counter = 0
      transaction.foreach(t => {
        if(tSet.subsetOf(t.getItems()))
          counter += 1
      })
      if(counter >= support) {
        candidateItemSet += tSet
      }
    }
    )
    candidateItemSet
  }

}