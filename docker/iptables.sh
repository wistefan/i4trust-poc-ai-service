#!/bin/bash

while getopts ":h:help:" option; do
   case $option in
      h) # display Help
         echo "Small script for adding and removing the PoC related iptables. Be aware that this functionality requires sudo-permissions."
         echo "                                                                                                                          "
         echo "               add            add the iptables to forward requests targeting api-umbrella to envoy                        "
         echo "               delete         delete the iptables create via the add command                                              "
         exit;;
   esac
done

mode=$1

if [[ "$mode" != "add" && "$mode" != "delete" ]]; then
  echo "Received invalid mode."
fi

if [ "$mode" = "add" ]; then
  echo "Setting iptables to forward traffic to envoy."

  # let through everything from root, the proxy is using that user anyways
  iptables -t nat -A OUTPUT -m owner --uid-owner 0 -j RETURN

  # forward everything sent to 8080(smart shepard umbrella) to 15001(port of happy cattle envoy).
  # we do not send all(as we do in real-world scenarios) to keep the dev/test system mostly untouched.
  iptables -t nat -A OUTPUT -p tcp --dport 8080 -j REDIRECT --to-port 15001

  # forward everything sent to 8081(happy cattle umbrella) to 15002(port of smart shepard envoy).
  iptables -t nat -A OUTPUT -p tcp --dport 8081 -j REDIRECT --to-port 15002
fi

if [ "$mode" = "delete" ]; then
  echo "Removing iptables."

  iptables -t nat -D OUTPUT -m owner --uid-owner 0 -j RETURN

  iptables -t nat -D OUTPUT -p tcp --dport 8080 -j REDIRECT --to-port 15001
  iptables -t nat -D OUTPUT -p tcp --dport 8081 -j REDIRECT --to-port 15002
fi

