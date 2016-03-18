package main

import _root_.akka.actor.Actor

/**
  * Created by ahmetkucuk on 17/03/16.
  */
class Greeter extends Actor {
  var greeting = ""

  def receive = {
    case WhoToGreet(who) => greeting = s"hello, $who"
    case Greet           => sender ! Greeting(greeting) // Send the current greeting back to the sender
  }
}
