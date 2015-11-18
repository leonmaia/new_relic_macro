package com.lm.newrelic

import com.newrelic.api.agent.{NewRelic, Trace}

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("set transaction name macro")
class transaction(category: String, name: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro traceMacro.impl
}

object traceMacro {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val category = annottees.head
    val transaction = annottees.last

    c.Expr[Any] {
      q"""
        import com.lm.newrelic.StatsTracing.set
        println("Hey You!")
        set($category, $transaction)
      """
    }
  }
}

object StatsTracing {
  @Trace(dispatcher = true)
  def set(category: String, name: String): Unit = {
    NewRelic.setTransactionName(category, name)
  }
}
