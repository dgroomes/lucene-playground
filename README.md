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


## Standalone subprojects

This repository illustrates different concepts, patterns and examples via standalone subprojects. Each subproject is
completely independent of the others and do not depend on the root project. This _standalone subproject constraint_
forces the subprojects to be complete and maximizes the reader's chances of successfully running, understanding, and
re-using the code.

The subprojects include:


### `simple/`

This is a simple runnable demo of Lucene. It is a prototypical "hello world" example.

See the README in [simple/](simple/).


### `in-memory/`

A demonstration of Lucene using indexes that reside only in-memory and not on disk.

See the README in [in-memory/](in-memory/).


### `http-api/`

A demonstration that exposes Lucene search as an HTTP API.

See the README in [http-api/](http-api/).


## Notes

Here are some miscellaneous notes jotted down during my Lucene learning journey.

As are most things Java, the technology far eclipses the marketing. The Lucene project's developer experience is quite
nice. I had a quick experience cloning and assembling the project, thanks to some diligent and expansive work in Lucene's
Gradle setup. They even incorporated a guided workflow into Gradle with a [`./gradlew helpWorkflow` command](https://github.com/apache/lucene/blob/9b185b99c429290c80bac5be0bcc2398f58b58db/CONTRIBUTING.md).
I'm surprised to find such an attentive Gradle setup in an old library. JMeter is another good example of an old library
with a modern Gradle setup. This is often not the case.


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [x] DONE Use an in-memory index. This is convenient for demos so we don't have to clean up files, but also I'm
      interested in the feature.
* [x] DONE Split into into independent sub-projects
* [ ] What's the difference between StringField and TextField?
* [x] DONE What's the difference between the class query parser and the other one?
* [ ] Explore the suggesters feature (mentioned on the [Features page](https://lucene.apache.org/core/features.html))
* [ ] Explore the highlighting feature (mentioned on the [Features page](https://lucene.apache.org/core/features.html))


## Reference

* [Apache Lucene](https://lucene.apache.org)
* [GitHub repo: `apache/lucene`](https://github.com/apache/lucene)
* [Apache Lucene *Facet Userguide and Demo*](https://lucene.apache.org/core/9_7_0/demo/org/apache/lucene/demo/facet/package-summary.html)
  * JavaDocs can be so rich in content. It's a shame how difficult they are to discover, read (font size too small, at least)
    and visually scan (in my opinion). This one is a good one and I wish I had found it sooner. Below is a quote.
  * > In faceted search, in addition to the standard set of search results, we also get facet results, which are lists
      of subcategories for certain categories.
