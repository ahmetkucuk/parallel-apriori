package main.spark

import org.apache.spark.{SparkConf, SparkContext}

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "README.md"
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("Simple App")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(_.contains("a")).count()
    val numBs = logData.filter(_.contains("b")).count()
    println(new String("Lines with a: %s, Lines with b: %s").format(numAs, numBs))
    println("finished")
  }
}
