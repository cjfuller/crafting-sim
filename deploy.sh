#!/bin/bash
# Deploy the webserver application.
# Does not automatically re-copy the service file, so if that was edited, it
# needs to be done manually.
# usage: deploy.sh <user@ip_addr>
# where user@ip_addr can be used for ssh access

scp target/scala-2.13/crafting-sim-server.jar "$1:/app/"

ssh -tt "$1" "sudo service optimizer restart"