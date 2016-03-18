package main

import scala.collection.immutable.TreeSet

class Transaction(items: TreeSet[String]) {

  def getItems(): TreeSet[String] = {
    items
  }

}