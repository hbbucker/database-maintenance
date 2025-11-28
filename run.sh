#!/bin/bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion

nvm use v23.11.0
export DATABASE_CONFIG_FILE=$(pwd)/../index-maintenance.conf
mvn quarkus:dev -Dquarkus.http.port=8181
