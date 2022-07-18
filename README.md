# lucene-playground

📚 Learning and exploring Apache Lucene: the most widely-used open source search engine.

> Apache Lucene™ is a high-performance, full-featured search engine library written entirely in Java. It is a technology
> suitable for nearly any application that requires structured search, full-text search, faceting, nearest-neighbor
> search across high-dimensionality vectors, spell correction or query suggestions.
>
> -- <cite>https://lucene.apache.org</cite>


## Description

**NOTE**: This project was developed on macOS. It is for my own personal use.

If you omit Google, does Lucene power the majority of search in the world? Lucene and the "search engine on a search engine"
software like Elasticsearch and Solr are the go-to technology to enable search in products like [Wikipedia (Elasticsearch)](https://en.wikipedia.org/wiki/Elastic_NV), [Netflix (Elasticsearch)](https://netflixtechblog.com/how-netflix-content-engineering-makes-a-federated-graph-searchable-5c0c1c7d7eaf), and [Slack (Lucene)](https://slack.engineering/search-at-slack/).


## Standalone sub-projects

This repository illustrates different concepts, patterns and examples via standalone sub-projects. Each sub-project is
completely independent of the others and do not depend on the root project. This _standalone sub-project constraint_
forces the sub-projects to be complete and maximizes the reader's chances of successfully running, understanding, and
re-using the code.

The sub-projects include:

### `simple/`

This is a simple runnable demo of Lucene. It is a prototypical "hello world" example.

See the README in [simple/](simple/).


## Notes

Here are some miscellaneous notes jotted down during my Lucene learning journey.

As are most things Java, the technology far eclipses the marketing. The Lucene project's developer experience is quite
nice. I had a quick experience cloning and assembling the project, thanks to some diligent and expansive work in Lucene's
Gradle setup. They even incorporated a guided workflow into Gradle with a [`./gradlew helpWorkflow` command](https://github.com/apache/lucene/blob/9b185b99c429290c80bac5be0bcc2398f58b58db/CONTRIBUTING.md).
I'm surprised to find such an attentive Gradle setup in an old library. JMeter is another good example of an old library
with a modern Gradle setup. This is often not the case.


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [ ] Use an in-memory index. This is convenient for demos so we don't have to clean up files, but also I'm
      interested in the feature.
* [ ] IN PROGRESS Split into into independent sub-projects

## Reference

* [Apache Lucene](https://lucene.apache.org)
* [GitHub repo: `apache/lucene`](https://github.com/apache/lucene)
* [Lucene JavaDoc: `MemoryIndex`](https://lucene.apache.org/core/9_2_0/memory/org/apache/lucene/index/memory/MemoryIndex.html)
  > Rather than targeting fulltext search of infrequent queries over huge persistent data archives (historic search),
  > this class targets fulltext search of huge numbers of queries over comparatively small transient realtime data (prospective search).
