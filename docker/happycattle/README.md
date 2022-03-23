# Happy Cattle

Recipes for running the necessary components of the AI service consumer Happy Cattle.

Open tasks:
* Add Sidecar-Proxy and configure for usage with orion instance and using the configured iSHARE key/certificate
* Pre-configuration (via mongodump) of API Umbrella for: notification endpoint



## Instructions

A docker compose file is provided for deploying all the necessary components. 



### Configuration

The Docker compose file uses fixed IPs for each container within the subnet `10.2.1.0`. Therefore make sure 
that no IPs are already being used in this subnet.

Some components require private key and certificate files being registered with the EORI of Happy Cattle at the 
iSHARE Satellite. These are added as secrets within the docker compose file. 
Before deploying the components, provide the file locations of private key and certificate as well as the EORI in 
the [.env](./.env) file.

Alternatively, the content of the private key and certificate files can be directly provided as an ENV by 
exporting the contents to the variables `HAPPYCATTLE_TOKEN_KEY_CONTENT` and `HAPPYCATTLE_TOKEN_CRT_CONTENT`, respectively.


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

For Keyrock, open the page [http://10.2.1.20:8080](http://10.2.1.20:8080) 
within your browser and use the following admin credentials: 
```
Username: admin@test.com
Password: admin
```
Within the Admin UI, you can add further users belonging to the Happy Cattle organisation.  
There is also a pre-registered Happy Cattle employee user with the following credentials:
```
Username: sandra.employee@happycattle.com
Password: sandra.employee
```
This account can be used, e.g., for acquiring offerings on the marketplace on behalf of the 
organisation Happy Cattle.


### API Umbrella

For API Umbrella, open the page [http://10.2.1.30/admin](http://10.2.1.30/admin) 
within your browser and use the following admin credentials: 
```
Username: admin@test.com
Password: admin
```
This account can be used to add API Backends and perform any administrative tasks.


### Orion

Check that Orion is running by executing the following command:
```shell
curl 10.2.1.40:1026/version
```
Any NGSI-LD requests can be sent to the same IP/port.


