# simple

This is a simple runnable demo of Lucene. It is a prototypical "hello world" example.


## Overview

This demo is vaguely similar to [Lucene's official tutorial](https://lucene.apache.org/core/9_2_0/demo/index.html), but
it's even smaller in scope. Study the code, and experiment with it!


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
     15:05:34 [main] INFO dgroomes.Runner - Indexing all 'subject documents' in the directory: /Users/dave/repos/personal/lucene-playground/simple/short-stories
     15:05:34 [main] INFO dgroomes.Runner - Writing the index files to the directory: MMapDirectory@/Users/dave/repos/personal/lucene-playground/simple/index lockFactory=org.apache.lucene.store.NativeFSLockFactory@15c43bd9
     15:05:34 [main] INFO dgroomes.Runner - Let's do a basic search. Searching for 'explorer' ...
     15:05:34 [main] INFO dgroomes.Runner - Found 3 hits
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:forest.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:sky.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -
     15:05:34 [main] INFO dgroomes.Runner - Now, let's do a leading wildcard search. Searching for '*fish' ...
     15:05:34 [main] INFO dgroomes.Runner - Found 2 hits
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:a fish,>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:and three starfish.>>
     15:05:34 [main] INFO dgroomes.Runner -
     15:05:34 [main] INFO dgroomes.Runner - Now, let's do an English language-oriented search. Searching for 'entity' (this will yield 0 results!) ...
     15:05:34 [main] INFO dgroomes.Runner - Found 0 hits
     15:05:34 [main] INFO dgroomes.Runner -
     15:05:34 [main] INFO dgroomes.Runner - Now, let's do a range search. Searching for lines 2 and earlier ...
     15:05:34 [main] INFO dgroomes.Runner - Found 6 hits
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:forest.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:forest.txt> stored,indexed,tokenized<contents:saw 6 animals in the forest:>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:saw 6 creatures in the ocean:>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:sky.txt> stored,indexed,tokenized<contents:The explorer>>
     15:05:34 [main] INFO dgroomes.Runner -     Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:sky.txt> stored,indexed,tokenized<contents:saw 6 entities in the sky:>>
     15:05:34 [main] INFO dgroomes.Runner -
     ```


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [x] DONE Implement a demo that just delegates to the Lucene demo. I've built the Lucene demo jar file from source for
      version 9.2.0. Now I need to copy it in and have the Gradle depend on it.
* [x] DONE Eject from the official Lucene demo and create a demo of my own. It might be interesting to create an index over
      JSON files. And then maybe even highlight the query match in the console?
* [ ] Make sure unusual characters work. For example, search by emoji.
* [x] DONE What does fuzzy searching look like? I want to search with "fish" and find "starfish" as a result.
* [x] DONE Actually demonstrate the leading wildcard behavior ("*fish" to match "starfish")
* [x] DONE Why aren't line numbers showing up in the search results (i.e. the "Document" hits)? I think it's because the
      `line_number`field is not stored. But now I'm forgetting what a non-stored field even does? You can search on it
      but not retrieve it? So can you search for a specific line number?
* [x] SKIPPED (It turns out Lucene logs very little and doesn't even log info, debug or trace. Only warnings and errors.
  See this [note](https://issues.apache.org/jira/browse/LUCENE-10342). This is interesting and valid, but this means we
  can't use logs to learn and explore like I thought. Skipped) Get logging working. Lucene logs to JUL so we should use the SLF4J JUL bridge. We want to be able to see Lucene
  logs for debugging and operational purposes.
* [x] DONE (no stemming analyzer) Why doesn't `entity` match `entities`?


## Reference

* [Apache Lucene](https://lucene.apache.org)
* [GitHub repo: `apache/lucene`](https://github.com/apache/lucene)
