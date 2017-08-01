package es.dgg

object Domain {

  case class Message(from: String, to: String , payload: String)
}