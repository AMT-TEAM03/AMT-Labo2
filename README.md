# AMT-Labo2
## Collaborateurs
- Melly Jérémie  
- Seem Thibault


## Livrable sur la machine linux
Notre livrable est un fichier nommé `AMT2-DataObject-1.0-SNAPSHOT.jar`, situé dans le répertoire `/opt/AMT-Labo2`.  
Il se lance tel qu'expliqué dans la section [Usage](#usage). De plus, une image est présente dans le répoertoire afin 
de pouvoir tester le programme

## Débuter sur le projet
### Installation de maven


Ce projet utilisant maven, il est nécessaire de l'avoir installer au préalable. La marche à suivre se trouve [ici](https://maven.apache.org/install.html)

Une fois maven installé, il faut installer les dépendances avec la commande :  

> TODOR votre commande fait tourner les tests ce qui n'est pas désirable à cette étape.
> RES added -DskipTests
```mvn clean install -DskipTests```

### Mise en place des settings AWS nécessaires
Actuellement, le projet est fait pour tourner avec AWS et utilise [AWS S3](https://aws.amazon.com/s3/) pour le storage
et [AWS Rekognition](https://aws.amazon.com/rekognition/) et plus spécifiquement [AWS Rekognition labels](https://aws.amazon.com/rekognition/) pour la reconnaissance de paterne dans les images

Le projet utilise les fichiers d'identification et de settings d'AWS. [Ce lien](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where) permet de savoir où sont stocké les identifiants et configurations.
Si les fichiers n'existent pas, le [CLI d'AWS](https://aws.amazon.com/cli/) permet de les créer à l'aide de la commande `aws configure`. Il est également possible
de créer soit-même ses identifiants et ses configuration en respectant l'exemple de configuration du lien précédent et l'[AWS Credentials File Format](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format)


### Mise en cache pour la détection de patterne
Le cache est stocké dans le même bucket que l'image traitée en tant qu'objet, avec le nom {imageKey}_result.

Les data sont stockées sous la forme suivante:
```json
[
    {
        "name": "Car",
        "confidence": 98.438
     },
     ...
]
```

Les objets du tableau sont des instances de la classe AwsPatternDetected pour la sérialisation/déserialisation.

### Stockage des logs de transaction pour de futur paiements

Les logs sont stockées dans le bucket actuel en tant qu'objet avec le nom "logs".

Les informations sont stockées sous la forme suivante:

```json
[
    {
        "fileTreatedKey": "ImageKey",
        "duration": 1154 // In ms
     },
     ...
]
```  

Les objets du tableau sont des instances de la classe AwsLogEntry pour la sérialisation/déserialisation.

Une méthode ResetLogging() a été implémentée pour supprimmer les logs de transaction.


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

## Changer les settings
Les settings se trouvent [de cette manière](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where).
Le fichier `config` permet de renseigner la région et le format des réponses.
Le fichier `credentials` permet de renseigner ses identifiants AWS.

Par défaut, nous utilisons le bucket qui nous a été fournis par le professeur. Il est possible de changer le bucket sur 
lequel nous taravaillons avec la méthode SetBucket.  


## Usage
Pour utiliser le projet, il faut run la commande  
```java -jar AMT2-DataObject-1.0-SNAPSHOT.jar <image path> <image key for AWS>```  
depuis le dossier ou se trouve le jar. Le chemin vers l'image peut être un chemin relatif ou absolu.  
On reçoit alors une série d'informations:
- La liste des objets présents dans le bucket
- Un lien URL permettant d'accéder à l'objet depuis l'extérieur
- La liste des objets présents dans le bucket après création d'un nouvel objet
- Une liste des patterns détecté
- La liste des objets présents dans le bucket après destruction d'un nouvel objet.
- Un affichage des logs de toutes les actions effectuée sur ce bucket depuis le dernier reset.

> TODOR pour les choix technologiques précisez  version de JAVA et du SDK java AWS ainsi que pourquoi vous les avez choisit.
> RES Added Java and AWS SDK version to the wiki The reasons for choosing all technologies are listed below every technology.

## Build and run docker container for ObjectManager

Build:

```docker build -t <container_flag> --build-arg aws_access_key_id=<AWS access key id> --build-arg aws_secret_access_key=<AWS secret access key> ./ObjectManager```  

Run:

```docker run -p 9090:9090 <container_flag>```  