package main.regular


import main.{DataReader, Transaction}

import scala.collection.immutable.TreeSet
import scala.math.Ordering.String

class AprioriAlgorithm(minSupport: Int = 20) {
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


    val candidateItemSet = scala.collection.mutable.Set[TreeSet[String]]()
    itemSet.foreach(s1 =>
      itemSet.foreach(s2 => {
        val unionOf = s1 | s2
        if (itemSize == unionOf.size)
          candidateItemSet += unionOf
      }
      )
    )

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

object AprioriRegularApp {
  def main(args: Array[String]): Unit = {

    if(args.length < 2){
      println("There is no enough input args 0 -> filename, args 1 -> minSupport")
    }

    val filename = args(0)
    val minSupport = Integer.parseInt(args(1))

    val test = new AprioriAlgorithm(minSupport = minSupport)

    //test.analyze(scala.collection.mutable.Seq(t1, t2, t3, t4, t5));
    val transactions = new DataReader().getTransactions(filename)
    val time1: Long = java.lang.System.currentTimeMillis()
    test.analyze(transactions)
    val time2: Long = java.lang.System.currentTimeMillis()
    println(s"regular (${(time2 - time1)})")
  }
}