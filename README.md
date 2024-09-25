# SafetyNet
Projet 5 de la formation Développement d'applications Java proposé par OpenClassrooms.
## Description
Le but de l’application à développer, SafetyNet Alerts, est d'envoyer des informations aux systèmes de services de secours.
Dans ce projet j'ai dû développer le back-end de l'application, c'est à dire une API REST qui renvoit différentes informations en fonction de la demande.
Elle expose plusieurs endpoints REST pour consulter les informations relatives aux personnes couvertes par des stations de pompiers et pour générer des alertes spécifiques (ex: alerte inondation, alerte enfants, etc.).

L'application est développée en Java avec Spring Boot et persiste les données via un fichier JSON.

## Technologies utilisées
- Java 17
- Spring Boot
- Maven pour la gestion des dépendances
- JUnit 5 pour les tests unitaires et d'intégration
- Mockito pour la simulation des services et repositories
- SLF4J avec Logback pour la gestion des logs
- Jackson pour la manipulation des fichiers JSON

## Installation et lancement
### Cloner le projet
- git clone https://github.com/votre-utilisateur/votre-repository.git
- cd votre-repository
### Compilation et exécution du projet
- mvn clean install
- Lancez l'application : mvn spring-boot:run
### Fichier de configuration
Un fichier JSON contenant les données de test se trouve dans le répertoire /src/main/resources. Vous pouvez le modifier pour personnaliser les données de l'application.
## Endpoints de l'API
Tous les endpoints REST décrits ci-dessous sont accessibles à partir de l'URL suivante : http://localhost:8080
### Firestation
L'objet Firestation est une combinaison entre une addresse et un numéro de station de pompier.
- **Toutes les firestations**
  - GET /firestations
- **Une liste de personnes couvertes par une station donnée**
  - GET /firestation?station_number={station}
- **Liste de téléphones d'addresses couvertes par une station donnée**
  - GET /phoneAlert?fire_station={station}
- **Liste de personne en cas d'incendie**
    - GET /fire?address={address}
- **Liste de personne en cas d'innondation**
    - GET /flood/stations?stations={stations}
- **Créer une nouvelle firestation**
  - POST /firestation
  - Body: {"address": "{address}", "station": "{station}"}
- **Mettre à jour une firestation existante**
    - PUT /firestation
    - Body: {"address": "{address}", "station": "{station}"}
- **Supprimer une firestation existante**
  - DELETE /firestation?address={address}
### Person
L'objet Person rassemble les informations d'une personne.
- **Toutes les personnes**
    - GET /persons
- **Une personne selon ses nom et prénom**
    - GET /person/{first_name}/{last_name}
- **Une liste de personnes ayant le nom donné**
    - GET /personInfo?last_name={last_name}
- **Alerte enfant (liste d'enfants et d'adultes pour une addresse donnée**
    - GET /childAlert?address={address}
- **Liste d'emails'**
    - GET /communityEmail?city={city}
- **Créer une nouvelle personne**
    - POST /person
    - Body (exemple) : {"firstName": "Anne", "lastName": "Shirley", "address": "Green Gables", "city": "Avonlea", "zip": "12345", "phone": "0123456789", "email": "anne.shirley@avonlea.com"}
- **Mettre à jour une personne existante**
    - PUT /person
    - Body (exemple) : {"firstName": "Anne", "lastName": "Shirley", "address": "Patty's House", "city": "Redmond", "zip": "54321", "phone": "0123456789", "email": "anne.shirley@redmond.com"}
- **Supprimer une personne existante**
    - DELETE /person?first_name={first_name}&last_name={last_name}
### MedicalRecord
L'objet MedicalRecord rassemble les informations médicales d'une personne.
- **Tous les dossiers médicaux**
    - GET /medicalrecords
- **Créer un nouveau dossier médical**
    - POST /medicalrecord
    - Body (exemple) : {"firstName" : "Jane", "lastName" : "Eyre", "birthdate" : "03/06/1984", "medications" : [ "vitamins" ], "allergies" : [ "dogs" ]}
- **Mettre à jour un dossier médical existant**
    - PUT /medicalrecord
    - Body (exemple) : {"firstName" : "Jane", "lastName" : "Eyre", "birthdate" : "03/06/1984", "medications" : [ "doliprane:500gr" ], "allergies" : [ "cats" ]}
- **Supprimer un dossier médical existant**
    - DELETE /medicalrecord?first_name={first_name}&last_name={last_name}