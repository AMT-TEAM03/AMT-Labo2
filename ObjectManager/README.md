# ObjectManager
Gestion du stockage d'objets dans un bucket S3 d'AWS.

## Collaborateurs
- Melly Jérémie
- Seem Thibault

## Débuter sur le projet
[Docker](https://www.docker.com) permet de faire tourner les images de ce projet facilement(voir le chapitre Lancement d'un conteneur Docker)

## Travailler sur le projet en local
Ce projet utilisant maven, il est nécessaire de l'avoir installer au préalable. La marche à suivre se trouve [ici](https://maven.apache.org/install.html)

Une fois maven installé, il faut installer les dépendances avec la commande :

```mvn clean install -DskipTests```

### Mise en place des settings AWS nécessaires
Voir la description du [README](../README.md) général du projet pour les configurations d'AWS

### Changer les settings AWS
Voir la description du [README](../README.md) général du projet pour les configurations d'AWS

### Test et compilation

Les tests sont fait avec JUnit, et lancé avec maven grâce à la commande  
```mvn test```  
Pour la CI de github, nous utilisons des variables d'environnements tirées des githubs secrets afin de ne pas avoir
besoin d'upload des fichiers contenants des informations sensibles.

Les test utilise des variables d'environnement au lieu d'un profile AWS afin que les tests puissent être effectués
par github.

Fermeture d'un client
La fermeture d'un client AWS ne nécessite aucune gestion d'exception, comme indiqué dans la
doc de l'interface [SdkAutoCloseable](https://sdk.amazonaws.com/java/api/2.0.0/software/amazon/awssdk/utils/SdkAutoCloseable.html)
utilisée pour la fermeture des clients.

Compiler le projet  
```mvn package```  
Si la compilation s'est effectuée avec succès, on doit trouver 2 fichier jar dans le dossier target. un dossier ayant
pour nom `<nomDuProjet>.jar` et `original-<nomDuProjet>.jar`. Il faut run le fichier sans l'extension `original-`.


## Utiliser les jar en local
Pour utiliser le projet en local, il faut run la commande  
```java -jar AMT2-DataObject-1.0-SNAPSHOT.jar <image path> <image key for AWS>```  
depuis le dossier ou se trouve le jar. Le chemin vers l'image peut être un chemin relatif ou absolu.  
On reçoit alors une série d'informations:
- La liste des objets présents dans le bucket
- Un lien URL permettant d'accéder à l'objet depuis l'extérieur
- La liste des objets présents dans le bucket après création d'un nouvel objet
- Une liste des patterns détecté
- La liste des objets présents dans le bucket après destruction d'un nouvel objet.
- Un affichage des logs de toutes les actions effectuée sur ce bucket depuis le dernier reset.


## Lancement d'un conteneur Docker
Commencer par pull l'image générée par le pipeline CI/CD
```
docker pull ghcr.io/amt-team03/object-manager:latest
```
Si vous voulez build votre propre image en local, Pour construire l'image, il faut fournir les identifiants AWS lors du build
```
docker build -t ghcr.io/amt-team03/object-manager:latest --build-arg aws_access_key_id={yourAwsAccessKey} --build-arg aws_secret_access_key={yourAwsSecretAccessKey} .
```

Puis nous pouvons la lancer. Le projet tourne sur le port interne 9090, il faut donc penser à le mapper
```
docker run -d -p 9090:9090 ghcr.io/amt-team03/label-detector:latest
```

# API
## GET /v1/object
Permet de download un objet depuis le stockage.

### Paramètres
- name (obligatoire) : nom du fichier à télécharger

### Réponses
- OK (HTTP 200)
```
{
    success : true
    data : {data downloaded}
}
```
- Problème (HTTP 500) avec une réponse expliquant l'erreur


## GET /v1/objects
Permet d'obtenir une liste des différents objets présents dans le bucket

### Paramètres
none

### Réponses
- OK (HTTP 200)
```
{
    "success": true,
    "data": [
        {listOfBucket}
    ]
}
```
- Expectation failed (HTTP 417) : La requête n'a pas pu obtenir de réponse
- Internal server error (HTTP 500) : Erreur pendant le processing de la requête 

## GET /v1/object/url
Obtient un lien vers l'objet dans le bucket

### Paramètres
- name (obligatoire) : nom du fichier dont on veut l'url

### Réponses
- OK (HTTP 200)
```
{
    "success": true,
    "data": "URL"
}
```
- Problème (HTTP 500) avec une réponse expliquant l'erreur

## GET /v1/object/exists
Vérifie l'existence d'un objet dans le bucket

### Paramètres
- name (obligatoire) : nom du fichier dont on veut vérifier l'existance

### Réponses
- OK (HTTP 200)
```
{
    "success": true,
    "data" : true/false
}
```
- Problème (HTTP 500) avec une réponse expliquant l'erreur

## POST /v1/object
Permet d'ajouter un fichier sous format de tableau de byte au bucket

### Header
"Content-Type", "application/json"

### Body
```
{
    "name" : "nameOfTheFile",
    "image" : tableau de byte du fichier à sauvegarder
}
```

### Réponses
- OK (HTTP 200)
```
{   "success":true,
    "data":"Image Created."
}
```

- Problème (HTTP 500) avec une réponse expliquant l'erreur


## DELETE /v1/object
Permet de supprimer un objet du bucket

### Header
"Content-Type", "application/json"

### Body
```
{
    "name" : "nameOfTheFile"
}
```

### Réponses
- OK (HTTP 200)
```
{
    "success":true,
    "data":"delete success"
}
```

- Problème (HTTP 500) avec une réponse expliquant l'erreur