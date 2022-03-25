# Marketplace

Recipes for running an instance of the Business API Ecosystem (BAE) as marketplace for provisioning of the AI Service.

Open topics:

* Integration of asset plugin in file system and DB (when plugin available)
* Adding external IDPs (Keyrock instances of Happy Cattle, Smart Shepherd, Real Time Weather) to DB for 
  login (when EORIs of other organisations are available and registered at the 
  satellite and other orgs environments are setup)


## Instructions

A docker compose file is provided for running a full setup of the BAE including the Keyrock IDP. 
Note that the networks are created in the master [docker compose](../docker-compose.yml) file, therefore 
this must be included when deplyoing the components.


### Configuration

This setup contains a default configuration via environment variables stored in env files in the 
[envs](./envs) directory. No further configuration is required.

Keys, certificates, EORI and the iSHARE Satellite configuration should be added to the 
[.env](../env) file of the parent directory.


### Deployment

Deployment must be performed from the parent directory. For running just an instance of the BAE, 
change to the parent directory and run:

```shell
docker-compose -f docker-compose.yml -f marketplace/docker-compose.yml up -d
```

For stopping all containers, simply run
```shell
docker-compose -f docker-compose.yml -f marketplace/docker-compose.yml down
```





## Usage

### BAE

As soon as the Logic Proxy component of the BAE is healthy, you can open the marketplace start page 
on your host's browser by opening the URL: [http://10.2.0.12:8004](http://10.2.0.12:8004). 
Login can be performed using the pre-configured Keyrock IDP with the name "Local IDP". 
For a first test, hit the Login button and
enter the admin credentials:
```
Username: admin@test.com
Password: admin
```
You are now logged in as a user with admin priviliges on the BAE. 

E.g., an admin is able to add 
further external Identity Providers (IDPs) from the other participants to be used for logging in. After 
adding those external IDPs, these appear as well in the login dialog. This allows users from other participants 
to use their own IDP for login.



### Keyrock

You can also login directly at the Keyrock IDP by opening [http://10.2.0.10:8080](http://10.2.0.10:8080) 
within your browser and using the same admin credentials. When being logged in, you will find a pre-configured 
Application for the BAE (Marketplace). Within the Admin UI, you can add further users and authorize them for 
the BAE.

