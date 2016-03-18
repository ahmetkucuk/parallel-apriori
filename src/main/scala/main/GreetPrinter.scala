package main

import _root_.akka.actor.Actor

/**
  * Created by ahmetkucuk on 17/03/16.
  */
// prints a greeting
class GreetPrinter extends Actor {
  def receive = {
    case Greeting(message) => println(message)
  }
}
