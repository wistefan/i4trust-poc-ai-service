# Docker setup

Recipes for deploying and running a full instance of the AI Service Proof of Concept.

There are four environments to be setup: 
* Smart Shepherd (AI Service Provider and Data Service Consumer)
* Happy Cattle (AI Service Consumer)
* Real Time Weather (Data Service Provider)
* Marketplace

For each, docker compose files are provided.


## Instructions

There is a directory for each of the four participants. Each is containing a docker compose file.

The setups use fixed IPs for each container within different subnets. Therefore make sure 
that no IPs are already being used in these subnets. Otherwise change the docker compose files accordingly. 
The components of each participant are deployed in a separate subnet:
* Marketplace: 10.2.0.0/16
* Happy Cattle: 10.2.1.0/16
* Smart Shepherd: 10.2.2.0/16
* Real Time Weather: 10.2.3.0/16

Some components require private key and certificate files being registered with an EORI at the 
iSHARE Satellite. Those need to be obtained before and configured in the different environments of the 
participants.

For deployment of all components, enter each of the participants directories and deploy the components 
using the docker compose files.


### MacOS

Some containers need to be accessed by their IP from the host machine. On Docker for Mac this 
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

