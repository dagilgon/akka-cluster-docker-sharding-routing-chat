package es.dgg


import akka.actor.{ActorLogging, Actor, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import es.dgg.Domain._
import scala.util.Random



object SortingDecider {
  def name = "sortingDecider"

  def props = Props[SortingDecider]
  
  val numberOfShards = 2

  def extractShardId: ExtractShardId = {
    case Message(from,_,_) =>
      (from.toInt % numberOfShards).toString
    case ShardRegion.StartEntity(from) ⇒
    (from.toInt % numberOfShards).toString   
  }

  def extractEntityId: ExtractEntityId = {
    case Message(from,to,payload) ⇒ (from, Message(from,to,payload))     
  }
}

class SortingDecider extends Actor with ActorLogging {
  def receive = {
    case Message(from, to, payload) =>
      val decision = new Random()
      log.info("Decision on to {} for payload {}: {}", to, payload, decision)
      //sender ! Go(decision)
  }
}

