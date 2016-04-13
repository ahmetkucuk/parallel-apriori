package main.spark

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.immutable.TreeSet

object SimpleApp {
  def main(args: Array[String]) {

    val filename = "/Users/ahmetkucuk/Documents/Developer/scala/parallel-apriori/src/main/resources/75000i.csv"
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("Simple App")
    val sc = new SparkContext(conf)

    val logData: RDD[String] = sc.textFile(filename, 2)

    val transactions: RDD[TreeSet[String]] = logData.map(line => {
      val array = line.split(',')
      collection.immutable.TreeSet[String]() ++ array.slice(1, array.length).map(_.trim)
    })


    var itemSetSeq = Seq[TreeSet[String]]()
    transactions.foreach(t =>
      t.foreach( s => { itemSetSeq = itemSetSeq :+ TreeSet(s) } )
    )

    var itemSet = Set[TreeSet[String]]()
    itemSet = itemSetSeq.toSet

    var itemSize = 2

    var candidateItemSet = sc.broadcast(itemSet)

    while(!itemSet.isEmpty) {

      itemSet = filterItemSet(transactions, candidateItemSet, 10000)
      println(itemSet)

      candidateItemSet = sc.broadcast(createCandidateItemSet(itemSet, itemSize))
      itemSize = itemSize + 1

    }

    println(s"Count 2 ${logData.count()}")

    val numAs = logData.filter(_.contains("1")).count()
    val numBs = logData.filter(_.contains("2")).count()
    println(new String("Lines with a: %s, Lines with b: %s").format(numAs, numBs))
    println("finished")
  }

  def filterItemSet(transactions: RDD[TreeSet[String]], candidateItemSet: Broadcast[Set[TreeSet[String]]], supportCount: Int): Set[TreeSet[String]] = {


    var result = transactions.flatMap(t => {
      candidateItemSet.value.map(cItem => {
        if (t.subsetOf(cItem)) {
          (cItem, 1)
        } else {
          (cItem, 0)
        }
      })
    }).reduceByKey(_+_).filter(_._2>supportCount).map { case (itemset, _) => itemset }

    result.collect().toSet
  }

  def createCandidateItemSet(itemSet: Set[TreeSet[String]], itemSize: Int): Set[TreeSet[String]] = {

    val t1 = System.currentTimeMillis()
    //    def crossProduct(set: Set[TreeSet[String]]): Set[TreeSet[String]] = for { x <- set; y <- set} yield  { x | y }
    //    val result = crossProduct(itemSet).filter(s => s.size == itemSize)

    var result = Set[TreeSet[String]]()
    itemSet.foreach(s1 =>
      itemSet.foreach(s2 => {
        val unionOf = s1 | s2
        if (itemSize == unionOf.size)
          result = result + unionOf
      }
      )
    )

    val t2 = System.currentTimeMillis()
    println(s"in Actor ${t2 - t1}")
    result
  }
}
