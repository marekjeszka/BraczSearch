# BraczSearch

[![Build Status](https://travis-ci.org/marekjeszka/BraczSearch.svg?branch=master)](https://travis-ci.org/marekjeszka/BraczSearch)
[![Coverage Status](https://coveralls.io/repos/marekjeszka/BraczSearch/badge.svg)](https://coveralls.io/github/marekjeszka/BraczSearch?branch=master)

### What is BraczSearch?

Are you using Raczynski's library to borrow books?
BraczSearch is a decorator of it's catalog search available [here](http://www.bracz.edu.pl).
I found original page quite hard to use and this is why I decided to create this app.

BraczSearch is written in Scala 2.11 and uses Play 2.5.

### Running locally

Using SBT directly:

`sbt run`

Using Docker:

`docker run -it -p 80:9000 marekjeszka/braczsearch`

`docker build -t marekjeszka/braczsearch`

Application will be available at `localhost:9000`.

