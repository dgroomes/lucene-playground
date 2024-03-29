# in-memory

A demonstration of Lucene using indexes that reside only in-memory and not on disk.


## Overview

Most usages of Lucene are through "search engine on search engine" software systems like Elasticsearch and Solr. By contrast,
many Lucene use-cases are small and can't afford the complexity of these systems. For example, consider a program that
embeds the Lucene library directly in the Java program source code and searches over a small dataset of 10 megabytes.
Such a dataset can fit in-memory, and conveniently, Lucene offers support for in-memory indexes.

An in-memory index design has the added benefit of fewer failure modes: it can't fail on file I/O operations to and from
the index. When you have the choice to use a "smaller architecture", please consider it. 

The Lucene docs describe two different use-cases that use in-memory indexes. The first uses the Lucene class aptly named
`MemoryIndex`. The  [JavaDoc for `MemoryIndex`](https://lucene.apache.org/core/9_2_0/memory/org/apache/lucene/index/memory/MemoryIndex.html)
describes the use-case called *prospective search*. It reads:

> Rather than targeting fulltext search of infrequent queries over huge persistent data archives (historic search) this
> class targets fulltext search of huge numbers of queries over comparatively small transient realtime data (prospective search).

The second use-case uses the class named `ByteBuffersDirectory`. The [JavaDoc for `ByteBuffersDirectory`](https://lucene.apache.org/core/9_2_0/core/org/apache/lucene/store/ByteBuffersDirectory.html)
describes how this class can be used for short-lived indexes. It reads:

> A heap-based directory like this one can have the advantage in case of ephemeral, small, short-lived indexes when disk
> syncs provide an additional overhead.

This program demonstrates usage of `ByteBuffersDirectory`. The index is stored on the Java heap (in-memory). Please
note that Lucene generally warns you away from this class and instead asks you to read the [docs for `MMapDirectory`](https://lucene.apache.org/core/9_2_0/core/org/apache/lucene/store/MMapDirectory.html).


## Instructions

Follow these instructions to build and run a Lucene demo program:

1. Use Java 21
2. Build and run the program:
   * ```shell
     ./gradlew run
     ```
   * It should look something like this:
     ```text
     $ ./gradlew run
     
     > Task :run
     11:26:17 [main] INFO dgroomes.Runner - Indexing all Java classes on the classpath
     11:26:17 [main] INFO dgroomes.Runner - Found 2052 classes. Indexing them...
     
     11:26:17 [main] INFO dgroomes.Runner - Indexing done.
     11:26:17 [main] INFO dgroomes.Runner - Let's search for Java classes using the keyword: 'Parser'
     11:26:17 [main] INFO dgroomes.Runner - Found 2 hits
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:Parser> stored,indexed,tokenized<package_name:nonapi.io.github.classgraph.types>>
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:Parser> stored,indexed,tokenized<package_name:org.apache.lucene.analysis.synonym>>
     11:26:17 [main] INFO dgroomes.Runner -
     11:26:17 [main] INFO dgroomes.Runner - Let's search for Java classes using the keyword: 'ClassGraph'
     11:26:17 [main] INFO dgroomes.Runner - Found 1 hits
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:ClassGraph> stored,indexed,tokenized<package_name:io.github.classgraph>>
     11:26:17 [main] INFO dgroomes.Runner -
     11:26:17 [main] INFO dgroomes.Runner - Let's search for Java classes using the keyword: 'nonapi.io.github.classgraph.types'
     11:26:17 [main] INFO dgroomes.Runner - Found 4 hits
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:ParseException> stored,indexed,tokenized<package_name:nonapi.io.github.classgraph.types>>
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:Parser> stored,indexed,tokenized<package_name:nonapi.io.github.classgraph.types>>
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:TypeUtils> stored,indexed,tokenized<package_name:nonapi.io.github.classgraph.types>>
     11:26:17 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized<class_name:ModifierType> stored,indexed,tokenized<package_name:nonapi.io.github.classgraph.types>>
     11:26:17 [main] INFO dgroomes.Runner -
     ```


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [x] SKIP (This would be better to do in my `http-api` subproject because it's interactive and also I encapsulated a nice `TimeZoneSearchSystem` abstraction there) Facet search. I'm new to facets. Still trying to get a feel for them. What's a good candidate for facet search with
  the Java classes domain? I think maybe modules? While there are probably too many unique packages to be used effectively
  as a (dimension of?) a facet, I think the number of unique modules make modules a good candidate.
  * DONE Index modules.
  * "Search result-side" face implementation. Facets present at the search-side but also at the search result-side (even
    without specifying them in the search). I have to implement the search result-side first
  * "Search-side" facet implementation.
  * Related document changes / finishing touches.


## Reference

* [JavaDoc for `ByteBuffersDirectory`](https://lucene.apache.org/core/9_2_0/core/org/apache/lucene/store/ByteBuffersDirectory.html)
