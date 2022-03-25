# Real Time Weather

Recipes for running the necessary components of the Data Service Provider 
Real Time Weather Ltd.

Open tasks:
* Pre-configuration (via mongodump) of API Umbrella for: weather service endpoint (Orion)


TODO:
* Context Broker (orionld)
* API Umbrella
* Keyrock (incl. AR functionality) + Activation Service
* Databases


## Instructions

A docker compose file is provided for deploying all the necessary components. 
Note that the networks are created in the master [docker compose](../docker-compose.yml) file, therefore 
this must be included when deploying the components.


### Configuration

This setup contains a default configuration via environment variables stored in env files in the 
[envs](./envs) directory. No further configuration is required, but changes can be made when the 
setup differs.

Keys, certificates, EORI and the iSHARE Satellite configuration should be added to the 
[.env](../env) file of the parent directory.


### Deployment

Deployment must be performed from the parent directory. For running just the components of Real Time Weather, 
change to the parent directory and run:

```shell
docker-compose -f docker-compose.yml -f real-time-weather/docker-compose.yml up -d
```

For stopping all containers, simply run
```shell
docker-compose -f docker-compose.yml -f real-time-weather/docker-compose.yml down
```



## Usage

As soon as the components are healthy, you can open the Keyrock IDP and the API Umbrella PEP/PDP components.


### Keyrock

For Keyrock, open the page [http://10.2.0.40:8080](http://10.2.0.40:8080) 
within your browser and use the following admin credentials: 
```
Username: admin@test.com
Password: admin
```
Within the Admin UI, you can add further users belonging to the Happy Cattle organisation.  
There is also a pre-registered Real Time Weather employee user with the following credentials:
```
Username: bob.employee@real-time-weather.com
Password: bob.employee
```
This account can be used, e.g., for creating offerings on the marketplace on behalf of the 
organisation of Real Time Weather.


### API Umbrella

For API Umbrella, open the page [http://10.2.0.41/admin](http://10.2.0.41/admin) 
within your browser and use the following admin credentials: 
```
Username: admin@test.com
Password: admin
```
This account can be used to add API Backends and perform any administrative tasks.



