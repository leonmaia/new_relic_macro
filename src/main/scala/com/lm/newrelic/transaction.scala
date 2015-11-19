package com.lm.newrelic

import com.newrelic.api.agent.{NewRelic, Trace}

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class hello extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro helloMacro.impl
}

object helloMacro {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val list = c.macroApplication.children.head.children.head.children.tail
    val category = list.head
    val transactionName = list.last

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods def $name(...$paramss): $returnType = $expr" :: Nil =>
          q"""
            $mods def $name(...$paramss): $returnType = {
            import com.lm.newrelic.StatsTracing.set
            set($category, $transactionName)
              ..$expr
            }
          """
      }
    }
    c.Expr[Any](result)
  }
}

object StatsTracing {
  @Trace(dispatcher = true)
  def set(category: String = "testCat", name: String = "testName"): Unit = {
    println(s".............Foooo! with $category and $name")
    NewRelic.setTransactionName(category, name)
  }
}
