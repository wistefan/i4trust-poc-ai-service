/*
  Setup for Real Time Weather MongoDB databases and users
*/

// API Umbrella
db = db.getSiblingDB('api_umbrella');
db.createUser(
    {
	user: "api_umbrella",
	pwd: "api_umbrella",
	roles: [
	    {
		role: "readWrite",
		db: "api_umbrella"
	    }
	]
    }
);

// Orion
db = db.getSiblingDB('orion');
db.createUser(
    {
	user: "orion",
	pwd: "orion",
	roles: [
	    {
		role: "readWrite",
		db: "orion"
	    }
	]
    }
);
