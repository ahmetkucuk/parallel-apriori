package main


import scala.collection.immutable.TreeSet
import scala.io.Source
/**
  * Created by ahmetkucuk on 13/03/16.
  */
class DataReader {

  def getTransactions():Set[Transaction] = {
    val filename = "/Users/ahmetkucuk/Documents/Developer/scala/parallel-apriori/src/main/resources/75000i.csv"
    var s = Seq[Transaction]()
    for (line <- Source.fromFile(filename).getLines()) {
      val t = new Transaction(line.split(',').drop(1).to[TreeSet])
      s = s :+ t
    }
    s.toSet
  }
}
