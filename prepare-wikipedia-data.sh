#!/bin/bash

set -eu

cd $(dirname "$0")
mkdir -p data
cd data

curl -L -o simplewiki.xml.bz2 https://dumps.wikimedia.org/simplewiki/20230201/simplewiki-20230201-pages-articles-multistream.xml.bz2
bunzip2 simplewiki.xml.bz2
