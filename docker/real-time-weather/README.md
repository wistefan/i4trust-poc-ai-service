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



### Configuration

The Docker compose file uses fixed IPs for each container within the subnet `10.5.0.0`. Therefore make sure 
that no IPs are already being used in this subnet.

Some components require private key and certificate files being registered with the EORI of 
Real Time Weather at the 
iSHARE Satellite. These are added as secrets within the docker compose file. 
Before deploying the components, provide the file locations of private key and certificate as well as the EORI in 
the [.env](./.env) file.

Alternatively, the content of the private key and certificate files can be directly provided as an ENV by 
exporting the contents to the variables `RTW_TOKEN_KEY_CONTENT` and `RTW_TOKEN_CRT_CONTENT`, respectively.


### Deployment

For deploying all components, simply run
```shell
docker-compose up -d
```

For stopping all containers, simply run
```shell
docker-compose down
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


### Orion
REMOVE
Check that Orion is running by executing the following command:
```shell
curl 10.5.0.40:1026/version
```
Any NGSI-LD requests can be sent to the same IP/port.


