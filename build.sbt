import Dependencies._

name := """akka-social-stream-angular"""
organization := "ch.becompany"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= backendDepts

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ch.becompany.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ch.becompany.binders._"
