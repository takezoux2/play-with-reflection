package controllers

import play.api._
import play.api.mvc._
import com.takezoux2.bug.{Setting, UseScalaReflection}

object Application extends Controller {

  def index = Action( req => {

    Ok(views.html.index( ))
  })


  def okPattern = Action{

    val s = UseScalaReflection.clone[List[User]](List(User(1,"hoge")))
    println(s)
    val s2 = UseScalaReflection.cloneListOf[Setting](List(Setting("localhost",80)))
    println(s2)
    Ok("always ok")
    
  }

  def ngPattern = Action{

    val s = UseScalaReflection.cloneListOf[User](List(User(2,"fuga")))
    println(s)
    Ok("If you start server by 'start' command, it will ok")
  }

}

case class User(id : Long, nickname : String)