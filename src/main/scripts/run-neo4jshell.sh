#!/bin/sh

# Set this to your own project directory
#
function findBaseProjectDir {
  SCRIPT=$0

  cd "`dirname "$SCRIPT"`"
  SCRIPT=`basename "$SCRIPT"`

  while [ -L "$SCRIPT" ]
  do
    SCRIPT=$( readlink "$SCRIPT" )
    cd "$(dirname "$SCRIPT")"
    SCRIPT=`basename "$SCRIPT"`
  done
  PROJECT_DIR=`cd $( dirname "$SCRIPT" ) && dirs -l +0`
  PROJECT_DIR=${PROJECT_DIR%%\/src\/main\/scripts}
  #PROJECT_DIR=`cd $( dirname "$SCRIPT" )/.. && dirs -l +0`

}

findBaseProjectDir
#PROJECT_DIR=/Users/jim/neo/development/neo4j-tutorial
LIB_DIR=$PROJECT_DIR/lib

#echo project at $PROJECT_DIR
#echo libs at $LIB_DIR

java -cp $LIB_DIR/neo4j-jmx.jar:$LIB_DIR/neo4j-community.jar:$LIB_DIR/neo4j-server.jar:$LIB_DIR/neo4j-cypher.jar:$LIB_DIR/neo4j-kernel.jar:$LIB_DIR/neo4j-shell.jar:$LIB_DIR/neo4j-graph-algo.jar:$LIB_DIR/neo4j-lucene-index.jar:$LIB_DIR/lucene-core.jar:$LIB_DIR/neo4j-graph-matching.jar:$LIB_DIR/neo4j-server-static-web.jar:$LIB_DIR/scala-library.jar:$LIB_DIR/geronimo-jta_1.1_spec.jar org.neo4j.shell.StartClient -path $1
