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

object Main extends App {

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

    val route =
      path("chat") {
        get {
          //Soy un nodo o un seed
          if (roles.contains("node")) {
            println("crando actores en los nodos")
            //val system1 = ActorSystem(c.clusterName)
            //Cluster(system1).join(joinAddress)
            system.actorOf(Props[RandomUser], "node-"+ new Random())
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Creado usuario en nodo</h1>"))
          } else {
            println("crando actores en los seeds")
            //val system2 = ActorSystem(c.clusterName)
            //Cluster(system2).join(joinAddress)
            system.actorOf(Props[RandomUser], "seed-"+ new Random())
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Creado usuario en seed</h1>"))
          }    
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8888)

    /*
    println(s"Server online at http://localhost:8888/\nPulsa RETURN para acabar...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done}
      * 
      */

  }
  println("Server online en http://localhost:8888/ ")

}