/* Copyright 2009-2018 EPFL, Lausanne */

package inox

import inox.solvers.smtlib.DebugSectionSMT

object TestContext {

  def apply(options: Options) = {
    val reporter = new TestSilentReporter
    Context(reporter, new utils.InterruptManager(reporter), options)
  }

  def empty = apply(Options.empty)
}
