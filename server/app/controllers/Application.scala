package controllers

import play.api._
import play.api.mvc._
import com.takezoux2.bug.{GoodWayToGetMirror, Setting, UseScalaReflection}

object Application extends Controller {

  def index = Action( req => {

    Ok(views.html.index( ))
  })


  def okPattern = Action{

    val s = UseScalaReflection.clone[List[User]](List(User(1,"hoge")))
    println(s)
    // Setting is defined in library.
    val s2 = UseScalaReflection.cloneListOf[Setting](List(Setting("localhost",80)))
    println(s2)
    Ok("always ok")
    
  }

  def ngPattern = Action{

    val s = UseScalaReflection.cloneListOf[User](List(User(2,"fuga")))
    println(s)
    Ok("If you start server by 'start' command, it will ok")
  }


  def goodPattern = Action{
    val s = GoodWayToGetMirror.cloneListOf[User](List(User(3,"wahoo")))
    println(s)
    val s2 = GoodWayToGetMirror.cloneListOf2[User](List(User(4,"uguxu")))
    println(s2)
    Ok("always ok")
  }

}

case class User(id : Long, nickname : String)