package main


import scala.collection.immutable.TreeSet
import scala.io.Source
/**
  * Created by ahmetkucuk on 13/03/16.
  */
class DataReader {

  def getTransactions(filename: String):Set[Transaction] = {
    var s = Seq[Transaction]()
    for (line <- Source.fromFile(filename).getLines()) {
      val t = new Transaction(line.split(',').drop(1).to[TreeSet])
      s = s :+ t
    }
    s.toSet
  }

  def getSmallTestData():Set[Transaction] = {

    val t1 = new Transaction(TreeSet("M", "O", "N", "K", "E", "Y"))
    val t2 = new Transaction(TreeSet("D", "O", "N", "K", "E", "Y"))
    val t3 = new Transaction(TreeSet("M", "A", "K", "E"))
    val t4 = new Transaction(TreeSet("M", "U", "C", "K","Y"))
    val t5 = new Transaction(TreeSet("C", "O", "O", "K", "I", "E"))
    scala.collection.mutable.Seq(t1, t2, t3, t4, t5).toSet
  }
}
