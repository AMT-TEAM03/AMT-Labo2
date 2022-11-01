# AMT-Labo2
## Collaborateurs
- Melly Jeremy  
- Seem Thibault

## Débuter sur le projet
Le projet utilise maven pour la gestion des dépendance, il faut donc utiliser  
```mvn clean install ```

### Mise en place des settings AWS nécessaires
Actuellement, le projet est fait pour tourner avec AWS ete utilise [AWS S3](https://aws.amazon.com/s3/) pour le storge
et [AWS Rekognition](https://aws.amazon.com/rekognition/) et plus spécifiquement [AWS Rekognition labels](https://aws.amazon.com/rekognition/)

Le projet utilise les fichiers d'identification et de settings d'AWS. [Ce lien](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where) permet de savoir où sont stocké les identifiants et configurations.
Si les fichiers n'existent pas, le CLI d'AWS permet de les créer à l'aide de la commande `aws configure`. Il est également possible
de créer soit-même ses identifiants et ses configuration en respectant l'exemple de configuration du lien précédent et l'[AWS Credentials File Format](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format)

### Test et compilation

Les tests sont fait avec JUnit, et lancé avec maven grâce à la commande  
```mvn test```

Compiler le projet  
```mvn package```  
Si la compilation s'est effectuée avec succès, le fichier jar résultant est trouvable dans le dossier target à la racine
du projet.


## Changer les settings
Les settings se trouvent [de cette manière](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where).
Le fichier `config` permet de renseigner la région et le format des réponses.
Le fichier `credentials` permet de renseigner ses identifiants AWS.