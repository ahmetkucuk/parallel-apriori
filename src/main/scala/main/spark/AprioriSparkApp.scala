package main.spark

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.immutable.TreeSet

object AprioriSparkApp {

  private val minSupport = 20

  def main(args: Array[String]) {

    val filename = "/Users/ahmetkucuk/Documents/Developer/scala/parallel-apriori/src/main/resources/5000i.csv"
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("Simple App")
    val sc = new SparkContext(conf)

    val logData: RDD[String] = sc.textFile(filename, 2)

    val transactions: RDD[TreeSet[String]] = logData.map(line => {
      val array = line.split(',')
      collection.immutable.TreeSet[String]() ++ array.slice(1, array.length).map(_.trim)
    })

    transactions.reduce((a,b) => {
      a | b
    })

    var itemSet = Set[TreeSet[String]]()
    transactions.collect().foreach(t =>
      t.foreach( s => { itemSet = itemSet + TreeSet(s) } )
    )

    var itemSize = 2

    var candidateItemSet = sc.broadcast(itemSet)

    val time1: Long = java.lang.System.currentTimeMillis()

    while(itemSet.nonEmpty) {

      itemSet = filterItemSet(transactions, candidateItemSet, minSupport)
      println(s"${itemSet.size}")

      candidateItemSet = sc.broadcast(createCandidateItemSet(itemSet, itemSize))
      itemSize = itemSize + 1

    }
    val time2: Long = java.lang.System.currentTimeMillis()
    println(s"spark (${(time2 - time1)})")
    println("finished")
  }

  def filterItemSet(transactions: RDD[TreeSet[String]], candidateItemSet: Broadcast[Set[TreeSet[String]]], supportCount: Int): Set[TreeSet[String]] = {

    //Map Reduce step of Spark
    transactions.flatMap(t => {
      candidateItemSet.value.map(cItem => {
        if (cItem.subsetOf(t)) {
          (cItem, 1)
        } else {
          (cItem, 0)
        }
      })
    }).reduceByKey(_+_).filter(_._2>=supportCount).map { case (itemset, _) => itemset }.toArray().toSet
//
//    val time1: Long = java.lang.System.currentTimeMillis()
//    val resultSet = result.flatMap(x=>x)
//    val time2: Long = java.lang.System.currentTimeMillis()
//    println(s"spark (${(time2 - time1)})")
//    resultSet
  }

  //Subset check(if subset frequent is not done yet
  def createCandidateItemSet(itemSet: Set[TreeSet[String]], itemSize: Int): Set[TreeSet[String]] = {

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
    result


  }
}
