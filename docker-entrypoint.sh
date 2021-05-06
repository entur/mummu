#!/bin/bash

echo "entrypoint"

: ${DOWNLOAD="https://storage.googleapis.com/marduk-production/tiamat/CurrentwithServiceFrame_latest.zip"}

echo "Downloading $DOWNLOAD"
wget $DOWNLOAD

exec "$@"
