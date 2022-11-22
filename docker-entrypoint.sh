#!/bin/bash

echo "entrypoint"

: ${NETEX_DATA_URL="https://storage.googleapis.com/marduk-production/tiamat/CurrentwithServiceFrame_latest.zip"}

echo "Downloading $NETEX_DATA_URL"
wget -O /tmp/netex_data.zip $NETEX_DATA_URL

exec "$@"
