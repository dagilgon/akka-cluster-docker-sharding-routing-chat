package es.dgg

import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import es.dgg.Domain._
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}

object RandomUserRouting {
  case object Tick
  val phrases = Vector(
    "Creativity is allowing yourself to make mistakes. Art is knowing which ones to keep.",
    "The best way to compile inaccurate information that no one wants is to make it up.",
    "Decisions are made by people who have time, not people who have talent.",
    "Frankly, I'm suspicious of anyone who has a strong opinion on a complicated issue.",
    "Nothing inspires forgiveness quite like revenge.",
    "Free will is an illusion. People always choose the perceived path of greatest pleasure.",
    "The best things in life are silly.",
    "Remind people that profit is the difference between revenue and expense. This makes you look smart.",
    "Engineers like to solve problems. If there are no problems handily available, they will create their own problems.")

  //def name = "RandomUserSharding"

  //def props = Props[RandomUserSharding]
  
  //val numberOfShards = 3

    /*
  def extractShardId: ExtractShardId = {
    case Message(from,_,_) =>
      (from.hashCode() % numberOfShards).toString
    case ShardRegion.StartEntity(from) ⇒
    (from.hashCode() % numberOfShards).toString   
  }

  def extractEntityId: ExtractEntityId = {
    case Message(from,to,payload) ⇒ (from, Message(from,to,payload))     
  }
  * 
  */

}


class RandomUserRouting() extends Actor {
  import RandomUser._
  import context.dispatcher
  val client = context.actorOf(ChatClient.props(self.path.name), "client")

  def scheduler = context.system.scheduler
  def rnd = ThreadLocalRandom.current

  //override def preStart(): Unit =
    //scheduler.scheduleOnce(5.seconds, self, Tick)

  // override postRestart so we don't call preStart and schedule a new Tick
  //override def postRestart(reason: Throwable): Unit = ()

  def receive = {
    case Tick =>
      scheduler.scheduleOnce(rnd.nextInt(5, 20).seconds, self, Tick)
      val msg = phrases(rnd.nextInt(phrases.size))
      client ! ChatClient.Publish(msg)
    case Message(from,to,payload) => client ! ChatClient.Publish(payload) 
  }

}