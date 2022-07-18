# simple

This is a simple runnable demo of Lucene. It is a prototypical "hello world" example.

---

This demo is vaguely similar [Lucene's official tutorial](https://lucene.apache.org/core/9_2_0/demo/index.html) but it's
even smaller in scope. Study the code, and experiment with it!


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
     
     > Task :run
     13:59:59 [main] INFO dgroomes.Runner - Indexing all 'subject documents' in the directory: /Users/davidgroomes/repos/personal/lucene-playground/simple/short-stories
     13:59:59 [main] INFO dgroomes.Runner - Writing the index files to the directory: MMapDirectory@/Users/davidgroomes/repos/personal/lucene-playground/simple/index lockFactory=org.apache.lucene.store.NativeFSLockFactory@26be92ad
     14:00:00 [main] INFO dgroomes.Runner - Let's search for the text content: 'explorer'
     14:00:00 [main] INFO dgroomes.Runner - Found 3 hits
     14:00:00 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:forest.txt> stored,indexed,tokenized<contents:The explorer>>
     14:00:00 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:The explorer>>
     14:00:00 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:sky.txt> stored,indexed,tokenized<contents:The explorer>>
     14:00:00 [main] INFO dgroomes.Runner -
     14:00:00 [main] INFO dgroomes.Runner - Let's search for the text content: 'fish'
     14:00:00 [main] INFO dgroomes.Runner - Found 1 hits
     14:00:00 [main] INFO dgroomes.Runner -  Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:a fish,>>
     14:00:00 [main] INFO dgroomes.Runner -
     14:00:00 [main] INFO dgroomes.Runner - Let's search for the text content: 'entity'
     14:00:00 [main] INFO dgroomes.Runner - Found 0 hits
     14:00:00 [main] INFO dgroomes.Runner -
     ```


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [x] DONE Implement a demo that just delegates to the Lucene demo. I've built the Lucene demo jar file from source for
      version 9.2.0. Now I need to copy it in and have the Gradle depend on it.
* [x] DONE Eject from the official Lucene demo and create a demo of my own. It might be interesting to create an index over
      JSON files. And then maybe even highlight the query match in the console?
* [ ] Make sure unusual characters work. For example, search by emoji.
* [ ] What does fuzzy searching look like? I want to search with "fish" and find "starfish" as a result.


## Reference

* [Apache Lucene](https://lucene.apache.org)
* [GitHub repo: `apache/lucene`](https://github.com/apache/lucene)
