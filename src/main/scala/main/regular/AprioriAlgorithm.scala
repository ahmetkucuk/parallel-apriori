package main.regular


import main.{AnalysisResult, Transaction}

import scala.collection.immutable.TreeSet
import scala.math.Ordering.String

class AprioriAlgorithm(
    minSupport: Int = 20,
    minConfidence: Double = 0.6,
    maxItemSetSize: Int = 5,
    isQuickRun: Boolean = true,
    maxJoinedSetsSizeWhenQuickRun: Int = 2000,
    timeoutMillis: Int = 60000) {

  def analyze(transactions: Seq[Transaction]): AnalysisResult = {
    val itemMap = scala.collection.mutable.HashMap[String, Int]()
    val itemSet = scala.collection.mutable.Set[TreeSet[String]]()
    transactions.foreach(t =>

      t.getItems().foreach( s =>
        itemSet += TreeSet(s)
      )
    )

    var frequentItemSet = filterBySupportCount(transactions, itemSet, minSupport)

    while(frequentItemSet.nonEmpty) {

      val candidates = createCandidateItemSet(frequentItemSet)

      println(frequentItemSet.size)
      frequentItemSet = filterBySupportCount(transactions, candidates, minSupport)

    }

    new AnalysisResult(null, null)
  }

//  def getFrequentItemSet(): Set[FrequentItemSet] = {
//    (new FrequentItemSet(, 0))
//  }

  def createCandidateItemSet(itemSet: scala.collection.mutable.Set[TreeSet[String]]): scala.collection.mutable.Set[TreeSet[String]] = {
    val candidateItemSet = scala.collection.mutable.Set[TreeSet[String]]()
    itemSet.foreach(s1 =>
      itemSet.foreach(s2 => {
        val unionOf = s1 | s2
        if (s1.size + 1 == unionOf.size)
          candidateItemSet += unionOf
      }
      )
    )
    candidateItemSet
  }

  def filterBySupportCount(transaction: Seq[Transaction], itemSet: scala.collection.mutable.Set[TreeSet[String]], support: Int): scala.collection.mutable.Set[TreeSet[String]] = {
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