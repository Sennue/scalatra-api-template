# Sennue API Template #

## Build & Run ##

```sh
$ cd scalatra_api_template
$ ./sbt
> container:start
> ~ ;copy-resources;aux-compile
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Run Unit Tests ##

```sh
$ ./sbt test
```

## Curl Call Examples ##

```sh
#!/bin/sh

export SENNUE_API='http://localhost:8080'
export USER_ID='"curl-'`hostname`'"'
export CONTENT_TYPE="Content-Type: application/json"

export SENNUE_ENDPOINT=""
curl $SENNUE_API/$SENNUE_ENDPOINT | python -m json.tool

export SENNUE_ENDPOINT="error"
curl $SENNUE_API/$SENNUE_ENDPOINT | python -m json.tool

export SENNUE_ENDPOINT="echo"
export MESSAGE='"Echo this message!"'
curl -H $CONTENT_TYPE -d "{\"id\":$USER_ID,\"message\":$MESSAGE}" $SENNUE_API/$SENNUE_ENDPOINT | python -m json.tool
```

