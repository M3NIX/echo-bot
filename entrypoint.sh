#!/bin/bash
cp echo.yaml config.yaml
sed -i 's/rootPath: \/echo\/\*/rootPath: \//g' config.yaml

if [ ! -f "$SQLITE_FILE" ]; then
  mkdir -p "$(dirname "$SQLITE_FILE")"
  sqlite3 -batch $SQLITE_FILE ".read /opt/echo-bot/script.txt"
fi

/usr/bin/java -Djava.library.path=/opt/wire/lib -jar echo.jar server /opt/echo-bot/config.yaml
