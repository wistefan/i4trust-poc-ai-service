# Docker setup

Recipes for deploying and running a full instance of the PoC.

There are three environments to be setup. For each, docker compose files are provided.


## Instructions

Instructions to run the full setup


### MacOS

Some containers needs to be accessed by their IP from the host machine. On Docker for Mac this 
is not possible, since containers run in a separate Linux VM.

A solution can be found [here](https://github.com/chipmk/docker-mac-net-connect). This tool will 
create a network tunnel between macOS and the Docker Desktop Linux VM and will allow to access the 
containers by their IP. 

The tool can be installed with `homebrew` and should be run as service, so that it is also launched 
at boot time. No further configuration is required.

```shell
# Install via Homebrew
$ brew install chipmk/tap/docker-mac-net-connect

# Run the service and register it to launch at boot
$ sudo brew services start chipmk/tap/docker-mac-net-connect
```

