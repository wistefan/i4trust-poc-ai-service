# Marketplace

Recipes for running an instance of the Business API Ecosystem (BAE) as marketplace for provisioning of the AI Service.


## Instructions

A docker compose file is provided for running a full setup of the BAE including the Keyrock IDP.


### Configuration

The Docker compose file uses fixed IPs for each container within the subnet `10.2.0.0`. Therefore make sure 
that no IPs are already being used in this subnet.

The BAE requires private key and certificate files being registered with the EORI of the marketplace at the 
iSHARE Satellite. These are added as secrets within the docker compose file. 
Before running the BAE, provide the file locations of private key and certificate in 
the [.env](./.env) file and enter the corresponding EORI in the environment variable `BAE_EORI` of the files 
[charging.env](./envs/charging.env) and [proxy.env](./envs/proxy.env).


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

As soon as the Logic Proxy component of the BAE is healthy, you can open the marketplace start page 
on your host's browser by opening the URL: [http://10.2.0.13:8004](http://10.2.0.13:8004). 
Login can be performed using the pre-configured Keyrock IDP. For a first test, hit the Login button and
enter the admin credentials:
```
Username: admin@test.com
Password: admin
```

You can also login directly at the Keyrock IDP by opening [http://10.2.0.20:8080](http://10.2.0.20:8080) 
within your browser and using the same admin credentials. When being logged in, you will find a pre-configured 
Application for the BAE (Marketplace). Within the Admin UI, you can add further users and authorize them for 
the BAE.

