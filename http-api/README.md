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
     curl -X GET http://localhost:8080?keyword=Europe
     ```
   * Try some of your own searches. Use Lucene syntax.
   * Altogether, it might look something like this:
     ```text
     $ curl -X GET http://localhost:8080?keyword=Europe
     Search found 65 results for keyword 'Europe':
        Greenwich Mean Time (Europe/Belfast) offset=PT0S observesDST=true
        Greenwich Mean Time (Europe/Dublin) offset=PT0S observesDST=true
        Greenwich Mean Time (Europe/Guernsey) offset=PT0S observesDST=true
        Greenwich Mean Time (Europe/Isle_of_Man) offset=PT0S observesDST=true
        Greenwich Mean Time (Europe/Jersey) offset=PT0S observesDST=true
        Western European Standard Time (Europe/Lisbon) offset=PT0S observesDST=true
        Greenwich Mean Time (Europe/London) offset=PT0S observesDST=true
        Central European Standard Time (Europe/Amsterdam) offset=PT1H observesDST=true
        ...omitted...

     $ curl -X GET http://localhost:8080?keyword=Isla
     No search results found for keyword 'Isla'
     
     $ curl -X GET http://localhost:8080?keyword=Isla*
     Search found 16 results for keyword 'Isla*':
        Cook Islands Standard Time (Pacific/Rarotonga) offset=PT-10H observesDST=false
        Easter Island Standard Time (Chile/EasterIsland) offset=PT-6H observesDST=true
        Easter Island Standard Time (Pacific/Easter) offset=PT-6H observesDST=true
        Falkland Islands Standard Time (Atlantic/Stanley) offset=PT-3H observesDST=false
        Cocos Islands Time (Indian/Cocos) offset=PT6H30M observesDST=false
        Christmas Island Time (Indian/Christmas) offset=PT7H observesDST=false
        ...omitted...
     ```
4. Stop the server
   * Stop the server process with `Ctrl + C`.


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [ ] IN PROGRESS Facet search. Define facets on maybe "observes daylight savings time" and one other dimension?
   * DONE (that was harder than I thought, and I've made this subproject more complex than I planned but I did learn
     some good stuff) Index (and "facet" index?) "observes daylight savings time".
   * "Search result-side" face implementation. Facets present at the search-side but also at the search result-side (even
     without specifying them in the search). I have to implement the search result-side first
   * "Search-side" facet implementation. Maybe a query param?
   * Related document changes / finishing touches.
