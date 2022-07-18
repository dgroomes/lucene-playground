# in-memory

NOT YET IMPLEMENTED

A demonstration of Lucene's in-memory index.


## Description

Most usages of Lucene are through "search engine on search engine" software systems like Elasticsearch and Solr. By contrast,
many Lucene use-cases are small and can't afford the complexity of these systems. For example, consider a program that
embeds the Lucene library directly in the Java program source code and searches over a small dataset of 10 megabytes.
Such a dataset can fit in-memory, and conveniently, Lucene offers support for in-memory indexes.

An in-memory index design has the added benefit of fewer failure modes: it can't fail on file I/O operations to and from
the index. When you can opt for a "smaller architecture", please consider it. 

The Lucene docs describe a "small data use-case" called *prospective search*. Specifically, the [JavaDoc for `MemoryIndex`](https://lucene.apache.org/core/9_2_0/memory/org/apache/lucene/index/memory/MemoryIndex.html)
reads: 

> Rather than targeting fulltext search of infrequent queries over huge persistent data archives (historic search) this
> class targets fulltext search of huge numbers of queries over comparatively small transient realtime data (prospective search).

This program demonstrates usage of the `MemoryIndex` Lucene index type. 


## Instructions

Follow these instructions to build and run a Lucene demo program:

1. Use Java 17
2. Build and run the program:
   * ```shell
     ./gradlew run
     ```
   * It should look something like this:
     ```text
     $ ./gradlew run
     
     todo
     ```


## Reference

* [Lucene JavaDoc: `MemoryIndex`](https://lucene.apache.org/core/9_2_0/memory/org/apache/lucene/index/memory/MemoryIndex.html)
  > Rather than targeting fulltext search of infrequent queries over huge persistent data archives (historic search),
  > this class targets fulltext search of huge numbers of queries over comparatively small transient realtime data (prospective search).
