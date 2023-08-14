# http-api

A demonstration that exposes Lucene search as an HTTP API.


## Overview

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
   * It should look something like the following.
   * ```text
     $ ./gradlew run
     
     ... omitted ...
     
     11:19:47 [main] INFO dgroomes.TimeZoneSearchSystem - Indexing 165 known time zones.
     11:19:47 [main] INFO dgroomes.TimeZoneSearchSystem - Indexing done.
     11:19:47 [main] INFO dgroomes.Runner - The Lucene search server is serving traffic on port 8080
     ```
3. Make some search requests
   * ```shell
     curl -X GET http://localhost:8080?keyword=Central
     ```
   * Try some of your own searches. Use Lucene syntax.
   * Altogether, it might look something like this:
     ```text
     $ curl -X GET http://localhost:8080?keyword=Central
     Search found 7 results for keyword 'Central':
     	Australian Central Standard Time
     	Australian Central Western Standard Time
     	Central Africa Time
     	Central European Standard Time
     	Central European Time
     	Central Indonesia Time
     	Central Standard Time
     	
     $ curl -X GET http://localhost:8080?keyword=Isl
     No search results found for keyword 'Isl'
     
     $ curl -X GET http://localhost:8080?keyword=Isl*
     Search found 12 results for keyword 'Isl*':
     	Christmas Island Time
     	Cocos Islands Time
     	Cook Islands Standard Time
     	Easter Island Standard Time
     	Falkland Islands Standard Time
     	Gilbert Islands Time
     	Line Islands Time
     	Marshall Islands Time
     	Norfolk Island Standard Time
     	Phoenix Islands Time
     	Solomon Islands Time
     	Wake Island Time
     ```
4. Stop the server
   * Stop the server process with `Ctrl + C`.


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [ ] Facet search. Define facets on maybe "observes daylight savings time" and one other dimension?
   * Index (and "facet" index?) "observes daylight savings time".
   * "Search result-side" face implementation. Facets present at the search-side but also at the search result-side (even
     without specifying them in the search). I have to implement the search result-side first
   * "Search-side" facet implementation. Maybe a query param?
   * Related document changes / finishing touches.
