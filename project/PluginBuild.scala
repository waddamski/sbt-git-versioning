/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.SbtGit.git
import sbt.Keys._
import sbt._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.SbtAutoBuildPlugin


object PluginBuild extends Build {

  val pluginName = "sbt-git-versioning"

  lazy val project = Project(pluginName, file("."))
    .enablePlugins(SbtAutoBuildPlugin, GitVersioning)
    .settings(
      sbtPlugin := true,
      targetJvm := "jvm-1.7",
      scalaVersion := "2.10.4",
      resolvers += Resolver.url(
        "bintray-sbt-plugin-releases",
        url("https://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
      addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.4"),
      BuildDescriptionSettings(),
      git.useGitDescribe := true,
      git.versionProperty := "NONE",
      git.gitTagToVersionNumber := { tag => Some(updateTag(tag)) },
      git.gitDescribedVersion <<= {
        git.gitDescribedVersion((vO) => {
          val deNulledVersion: Option[String] = vO.flatMap{ vOO => Option(vOO) }
          deNulledVersion map updateTag
        })
      },
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        "org.pegdown" % "pegdown" % "1.5.0" % "test"
      )
    )

  def updateTag(tag:String):String={
    val removedV = if (tag.startsWith("v")) tag.drop(1) else tag

    removedV.matches("^.*-.*-g.*$") match {
      case true  => removedV
      case false => removedV + "-0-g0000000"
    }
  }
}

object BuildDescriptionSettings {

  def apply() = 
    pomExtra := (<url>https://www.gov.uk/government/organisations/hm-revenue-customs</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git@github.com:hmrc/sbt-bintray-publish.git</connection>
        <developerConnection>scm:git@github.com:hmrc/sbt-bintray-publish.git</developerConnection>
        <url>git@github.com:hmrc/sbt-bintray-publish.git</url>
      </scm>
      <developers>
        <developer>
          <id>charleskubicek</id>
          <name>Charles Kubicek</name>
          <url>http://www.equalexperts.com</url>
        </developer>
        <developer>
          <id>duncancrawford</id>
          <name>Duncan Crawford</name>
          <url>http://www.equalexperts.com</url>
        </developer>
      </developers>)
  
}