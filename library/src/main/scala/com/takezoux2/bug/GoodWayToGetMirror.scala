package com.takezoux2.bug

import scala.reflect.runtime.universe._

/**
 * Created by takezoux2 on 2014/07/28.
 */
object GoodWayToGetMirror {


  def cloneListOf[T : TypeTag]( l : List[T]) = {

    // Get context loader mirror
    // I recommend this way.
    val mirror = runtimeMirror(Thread.currentThread().getContextClassLoader)
    l.map(o => {
      UseScalaReflection.copy(typeOf[T],o)(mirror)
    })

  }

  def cloneListOf2[T : TypeTag]( l : List[T]) = {

    //Get mirror from passed TypeTag
    //But this way is not good, when other library uses this method.
    val mirror = implicitly[TypeTag[T]].mirror
    l.map(o => {
      UseScalaReflection.copy(typeOf[T],o)(mirror)
    })

  }

}
