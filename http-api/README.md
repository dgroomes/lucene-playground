# http-api

NOT YET IMPLEMENTED

A demonstration that exposes Lucene search as an HTTP API.


## Description

A common use case for Lucene is supporting search on websites. A user types in a search phrase on a web page, the browser
makes an HTTP request to a server, and the server engages Lucene and a Lucene index. The `lucene-playground/http-api`
project implements a runnable Java program that embeds Lucene embedded into a web server.  


## Instructions

Follow these instructions to build and run a Lucene demo program:

1. Use Java 17
2. Build and run the program:
   * ```shell
     ./gradlew run
     ```
3. Make some search requests
   * ```shell
     curl -X GET http://localhost:8080
     ```
   * It should look something like this:
     ```text
     Found 7 results for the search.
     ```
4. Stop the server
   * Stop the server process with `Ctrl + C`.
