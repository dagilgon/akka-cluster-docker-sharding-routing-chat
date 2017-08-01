package es.dgg

import akka.actor._
import com.typesafe.config._
import java.net.{ InetAddress, NetworkInterface }
import scala.collection.JavaConversions._
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import scala.util.Random
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import es.dgg.Domain._
import akka.stream.extra.Implicits
import es.dgg.RandomUserRouting
import akka.routing.FromConfig

object MainRouting extends App {

  println("parseando argumentos...")
  val nodeConfig = NodeConfig parse args

  // If a config could be parsed - start the system
  nodeConfig map { c =>
    println("argumentos parseados...")
    implicit val system = ActorSystem(c.clusterName, c.config)
    val joinAddress = Cluster(system).selfAddress

    // Register a monitor actor for demo purposes
    system.actorOf(Props[MonitorActor], "cluster-monitor")

    system.log info s"ActorSystem ${system.name} started successfully"

    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    implicit val node = Cluster(system)
    val roles = node.getSelfRoles
    println("roles->" + roles)

    /*
    ClusterSharding(system).start(
      typeName = RandomUserSharding.name,
      entityProps = RandomUserSharding.props,
      settings = ClusterShardingSettings(system),
      extractShardId = RandomUserSharding.extractShardId,
      extractEntityId = RandomUserSharding.extractEntityId)

    val decider = ClusterSharding(system).shardRegion(RandomUserSharding.name)
		*/

    val randomRouter = system.actorOf(Props[RandomUserRouting].withRouter(FromConfig()), name = "ClusterAwareActor")
    
    if (c.isSeed) {
      val route =
        path("chat") {
          get {
            parameters('from, 'to, 'payload) { (from, to, payload) =>
              //println("creando actores en los nodos")
              val m = new Message(from, to, payload)
              randomRouter ! m
              //decider ! m 
              //val fromActor = system.actorOf(Props(new RandomUserSharding(decider)), from)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Creado usuario en nodo</h1>"))

            }
          }
        }

      val bindingFuture = Http().bindAndHandle(route, "localhost", 8888)
      println("Server online en http://localhost:8888/ ")

    }
  }
}