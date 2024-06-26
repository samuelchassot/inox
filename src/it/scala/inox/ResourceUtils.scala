/* Copyright 2009-2018 EPFL, Lausanne */

package inox

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.concurrent._

import java.io.File

import utils._

trait ResourceUtils {

  def resourceFiles(dir: String, filter: String => Boolean = (s: String) => true, recursive: Boolean = false): Seq[File] = {
    Option(getClass.getResource(s"/$dir")).toSeq.flatMap { url =>
      val baseDir = new File(url.getPath)

      def rec(f: File): Seq[File] = Option(f.listFiles().toSeq).getOrElse(Seq.empty).flatMap { f =>
        if (f.isDirectory) {
          if (recursive) rec(f)
          else Nil
        } else {
          List(f)
        }
      }

      rec(baseDir).filter(f => filter(f.getPath)).sortBy(_.getPath).reverse
    }
  }
}
