# Docker setup

Recipes for deploying and running a full instance of the AI Service Proof of Concept.

There are four environments of the different participants to be setup: 
* Smart Shepherd (AI Service Provider and Data Service Consumer)
* Happy Cattle (AI Service Consumer)
* Real Time Weather (Data Service Provider)
* Marketplace

For each, docker compose files are provided. 


## Instructions

There is a directory for each of the four participants. Each is containing a docker compose file. 
In addition, there is a master [docker compose](./docker-compose.yml) file creating the 
necessary networks. Therefore, this master docker compose file must be always included.

Some components require private key and certificate files being registered with an EORI at the 
iSHARE Satellite. Those need to be obtained before separately for each participant and are added 
as secrets within the docker compose files. 
Before deploying the components, provide the file locations of private key and certificate as well 
as the EORI of each participant in 
the [.env](./.env) file.

Alternatively, the content of the private key and certificate files can be directly provided as an ENV by 
exporting the contents to the 
variables `<PARTICIPANT>_TOKEN_KEY_CONTENT` and `<PARTICIPANT>_TOKEN_CRT_CONTENT`, respectively.

The setup requires a central iSHARE Satellite instance. It's EORI, host and endpoints need to be configured 
in the [.env](./.env) file as well.

The components of each participant reside in a separate network. In addition, there is a 'shared' network, 
which allows containers to communicate across the participants environments. This 'shared' network 
uses fixed IPs for each container within the subnet `10.2.0.0/16`. Therefore make sure 
that no IPs are already being used in this subnet.

After performing the necessary configuration, all components of each participant can be deployed by running 
the following command:
```shell
docker compose -f docker-compose.yml \
	-f marketplace/docker-compose.yml \
	-f happycattle/docker-compose.yml \
	-f smart-shepherd/docker-compose.yml \
	-f real-time-weather/docker-compose.yml \
	up -d
```
Note that all docker compose files use the context of this directory.

In case there is the need of different `.env` files for different setups, one can simply make a copy of 
the [.env](./.env) and add a further parameter to the docker compose command:
```shell
# Make a copy
cp .env my.env
# Edit configuration in ./my.env

# Deploy components with my.env
docker compose -f docker-compose.yml \
	-f marketplace/docker-compose.yml \
	-f happycattle/docker-compose.yml \
	-f smart-shepherd/docker-compose.yml \
	-f real-time-weather/docker-compose.yml \
	--env-file my.env \
	up -d
```

Some components are part of the 'shared' network. These are accessible via the following IPs, also from the 
docker host:
| Participant         | Component          | IP         |
|---------------------|--------------------|------------|
| Marketplace         | Keyrock            | 10.2.0.10  |
| Marketplace         | Logic Proxy        | 10.2.0.12  |
| Happy Cattle        | Keyrock            | 10.2.0.20  |
| Happy Cattle        | API Umbrella       | 10.2.0.21  |
| Smart Shepherd      | Keyrock            | 10.2.0.30  |
| Smart Shepherd      | API Umbrella       | 10.2.0.31  |
| Real Time Weather   | Keyrock            | 10.2.0.40  |
| Real Time Weather   | API Umbrella       | 10.2.0.41  |

You can skip certain environments for, e.g., testing purposes, but make sure that always the master docker compose 
file is included.

In each participant's directory there is a README file containing instructions on how to 
access/login at certain components.

All containers can be stopped by running:
```shell
docker compose -f docker-compose.yml \
	-f marketplace/docker-compose.yml \
	-f happycattle/docker-compose.yml \
	-f smart-shepherd/docker-compose.yml \
	-f real-time-weather/docker-compose.yml \
	down
```



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

