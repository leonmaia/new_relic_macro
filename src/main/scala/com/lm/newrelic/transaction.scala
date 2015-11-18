package com.lm.newrelic

import com.newrelic.api.agent.{NewRelic, Trace}

import scala.reflect.macros.whitebox
import language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

class transaction(category: String, name: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Unit = macro traceMacro.transaction_impl
}

object traceMacro {
  def transaction(annottees: Any*): Unit = macro traceMacro.transaction_impl

  def transaction_impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val category = annottees.head
    val transaction = annottees.last

    c.Expr[Unit](
      q"""
        {
          import com.lm.newrelic.StatsTracing.set
          set($category, $transaction)
        }
      """)
  }
}

object StatsTracing {
  @Trace(dispatcher = true)
  def set(category: String, name: String): Unit = {
    println(s".............Foooo! with $category and $name")
    NewRelic.setTransactionName(category, name)
  }
}
