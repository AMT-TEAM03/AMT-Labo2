# LabelDetector
Permet l'utilisation de AWS rekognition pour analiser des images.

## Collaborateurs
- Melly Jérémie
- Seem Thibault

## Débuter sur le projet
[Docker](https://www.docker.com) permet de faire tourner les images de ce projet facilement(voir [Lancement d'un conteneur Docker](#Lancement d'un conteneur Docker)).  
Pour développer sur ce projet, voir ci-dessous.

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
docker pull ghcr.io/amt-team03/label-detector:latest
```
Si vous voulez build votre propre image en local, il faut fournir les identifiants AWS lors du build
```
docker build -t ghcr.io/amt-team03/label-detector:latest --build-arg aws_access_key_id={yourAwsAccessKey} --build-arg aws_secret_access_key={yourAwsSecretAccessKey} .
```

Puis nous pouvons la lancer. Le projet tourne sur le port interne 8787, il faut donc penser à le mapper pour pouvoir communiquer avec depuis l'extérieur
```
docker run -d -p 8787:8787 ghcr.io/amt-team03/label-detector:latest
```

# API
## GET /v1/analyze
Permet d'envoyer un URL a faire analyzer par AWS rekognition

### Paramètres
- imageUrl (obligatoire) : Url vers l'image à analyser, doit être atteignable par rekognition
- maxPattern (optionnel) : Nombre de pattern retourné maximum. Par défaut est à 10
- minConfidence (optionnel) : Confiance minimum que doit avoir le label pour être donné par rekognition. Par défaut 90%

### Réponses
- OK (HTTP 200)
```
 "success": true,
    "data": {
        "time": temps mis pour la détection de pattern (en ms),
        "patternDetected": [
            {
                "name": "nom du tag détecté",
                "confidence": pourcentage de certitude du tag 
            }, 
            ...
        ]
    }
}
```
- URL mal formé (HTTP 400) 
```
{
    "error": "Malformed URLno protocol: URL_Mal_Formé"
}
```
- Paramètre non reconnu par rekognition (HTTP 400) : Un json est reçu avec l'erreur qui s'est produite.