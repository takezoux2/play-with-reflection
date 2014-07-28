package com.takezoux2.bug

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag


/**
 * Created by takezoux2 on 2014/07/18.
 */
object UseScalaReflection {


  def cloneListOf[T : TypeTag](o : List[T]) : List[T] = clone[List[T]](o)


  def clone[T : TypeTag](o : T) : T = {

    implicit val mirror = implicitly[TypeTag[T]].mirror
    println("&&&&" + mirror.classLoader)
    val t = typeOf[T]

    val v = t match{
      case t if t <:< typeOf[List[_]] => {
        o.asInstanceOf[List[_]].map( e => copy(t.typeArgs(0),e))
      }
      case _ => copy(t,o)
    }
    v.asInstanceOf[T]
  }

  def copy(tpe : Type,o : Any)(implicit mirror : Mirror) = {
    val pc = tpe.members.collectFirst({
      case t : MethodSymbol if t.isPrimaryConstructor => t
    }).get

    val cm = mirror.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(pc)
    val im = mirror.reflect(o)

    def getValue(fieldName : String) = {
      val m = tpe.members.collectFirst({
        case m : MethodSymbol if m.isGetter && m.name.encodedName.toString == fieldName => m
      }).get
      im.reflectMethod(m).apply()
    }

    val args = pc.paramLists.flatMap(p => {
      p.map(v => getValue(v.name.encodedName.toString))
    })

    cm.apply(args :_*)
  }




}

