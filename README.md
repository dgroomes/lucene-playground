# lucene-playground

📚 Learning exploring Apache Lucene: the most widely-used open source search engine.

> Apache Lucene™ is a high-performance, full-featured search engine library written entirely in Java. It is a technology
> suitable for nearly any application that requires structured search, full-text search, faceting, nearest-neighbor
> search across high-dimensionality vectors, spell correction or query suggestions.
>
> -- <cite>https://lucene.apache.org</cite>


## Description

**NOTE**: This project was developed on macOS. It is for my own personal use.

If you omit Google, does Lucene power the majority of search in the world? Lucene and the "search engine on a search engine"
software like Elasticsearch and Solr are the go-to technology to enable search in products like [Wikipedia (Elasticsearch)](https://en.wikipedia.org/wiki/Elastic_NV), [Netflix (Elasticsearch)](https://netflixtechblog.com/how-netflix-content-engineering-makes-a-federated-graph-searchable-5c0c1c7d7eaf), and [Slack (Lucene)](https://slack.engineering/search-at-slack/).


## Instructions

Follow these instructions to build and run a Lucene demo program:

1. Use Java 17
2. Build the program distribution:
   * ```shell
     ./gradlew installDist
     ```
3. Create a convenient alias to run the program:
   * ```shell
     alias run="./build/install/lucene-playground/bin/lucene-playground"
     ```
4. Run the program:
   * ```shell
     run
     ```
   * It should look something like this:
     ```text
     $ run
     11:42:28 [main] INFO dgroomes.Runner - Indexing all 'subject documents' in the directory: /Users/davidgroomes/repos/personal/lucene-playground/short-stories
     11:42:28 [main] INFO dgroomes.Runner - Writing the index files to the directory: MMapDirectory@/Users/davidgroomes/repos/personal/lucene-playground/index lockFactory=org.apache.lucene.store.NativeFSLockFactory@e45f292
     11:42:28 [main] INFO dgroomes.Runner - Let's search for the text content: 'explorer'
     11:42:28 [main] INFO dgroomes.Runner - Found 3 hits
     11:42:28 [main] INFO dgroomes.Runner - 	Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:forest.txt> stored,indexed,tokenized<contents:The explorer>>
     11:42:28 [main] INFO dgroomes.Runner - 	Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:The explorer>>
     11:42:28 [main] INFO dgroomes.Runner - 	Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:sky.txt> stored,indexed,tokenized<contents:The explorer>>
     11:42:28 [main] INFO dgroomes.Runner -
     11:42:28 [main] INFO dgroomes.Runner - Let's search for the text content: 'fish'
     11:42:28 [main] INFO dgroomes.Runner - Found 1 hits
     11:42:28 [main] INFO dgroomes.Runner - 	Hit: Document<stored,indexed,tokenized,omitNorms,indexOptions=DOCS<file_name:ocean.txt> stored,indexed,tokenized<contents:a fish,>>
     11:42:28 [main] INFO dgroomes.Runner -
     11:42:28 [main] INFO dgroomes.Runner - Let's search for the text content: 'entity'
     11:42:28 [main] INFO dgroomes.Runner - Found 0 hits
     11:42:28 [main] INFO dgroomes.Runner -
     ```

## Notes

Here are some miscellaneous notes jotted down during my Lucene learning journey.

As are most things Java, the technology far eclipses the marketing. The Lucene project's developer experience is quite
nice. I had a quick experience cloning and assembling the project, thanks to some diligent and expansive work in Lucene's
Gradle setup. They even incorporated a guided workflow into Gradle with a [`./gradlew helpWorkflow` command](https://github.com/apache/lucene/blob/9b185b99c429290c80bac5be0bcc2398f58b58db/CONTRIBUTING.md).
I'm surprised to find such an attentive Gradle setup in an old library. JMeter is another good example of an old library
with a modern Gradle setup. This is often not the case.

At first I followed [Lucene's official tutorial](https://lucene.apache.org/core/9_2_0/demo/index.html) and then created
my own.


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
