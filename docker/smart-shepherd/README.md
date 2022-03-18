# Smart Shepherd

Recipes for running the necessary components of the AI service provider Smart Shepherd Inc.

* Context Broker (orionld) + Sidecar-Proxy
* API Umbrella
* Keyrock (incl. AR functionality) + Activation Service
* Databases
* AI Service

TODO:
* CB+Proxy
* API Umbrella + initial setup
* AI Service


## Instructions

A docker compose file is provided for deploying all the necessary components. 



### Configuration

The Docker compose file uses fixed IPs for each container within the subnet `10.2.2.0`. Therefore make sure 
that no IPs are already being used in this subnet.

Some components require private key and certificate files being registered with the EORI of Smart Shepherd at the 
iSHARE Satellite. These are added as secrets within the docker compose file. 
Before deploying the components, provide the file locations of private key and certificate as well as the EORI in 
the [.env](./.env) file.

Alternatively, the content of the private key and certificate files can be directly provided as an ENV by 
exporting the contents to the variables `SHEPHERD_TOKEN_KEY_CONTENT` and `SHEPHERD_TOKEN_CRT_CONTENT`, respectively.


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

For Keyrock, open the page [http://10.2.2.20:8080](http://10.2.2.20:8080) 
within your browser and use the following admin credentials: 
```
Username: admin@test.com
Password: admin
```
Within the Admin UI, you can add further users belonging to the Smart Shepherd organisation.  
There is also a pre-registered Smart Shepherd employee user with the following credentials:
```
Username: peter.employee@smartshepherd.com
Password: peter.employee
```
This account can be used, e.g., for creating offerings on the marketplace on behalf of the 
organisation Smart Shepherd.

