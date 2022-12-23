# AMT-Microservices
## Collaborateurs
- Melly Jérémie  
- Seem Thibault

## Présentation du 
Ce projet d'inscrit dans le cours d'AMT(application multi-tiers) de la HEIG-VD. Il s'agit de créer deux microservices accessibles au travers d'API.

Contient 3 sous-projet:
- [ObjectManager](./ObjectManager)
- [LabelDetector](./LabelDetector)
- [MainController](./MainController)

Les deux premier sous-projet sont des micro-services servant respectivement gérer des objet pour le premier 
et à détecter ce que contient une image à l'aide d'AWS rekognition. Le dernier projet est un main faisant appel aux deux
microservices cités ci-dessus.

## Prérequis

L'utilisation de [Docker](https://www.docker.com) permet de se faciliter la vie pour les test

### Mise en place des settings AWS nécessaires
Actuellement, le projet est fait pour tourner avec AWS et utilise [AWS S3](https://aws.amazon.com/s3/) pour le storage
et [AWS Rekognition](https://aws.amazon.com/rekognition/) et plus spécifiquement [AWS Rekognition labels](https://aws.amazon.com/rekognition/) pour la reconnaissance de paterne dans les images

Le projet utilise les fichiers d'identification et de settings d'AWS. [Ce lien](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where) permet de savoir où sont stocké les identifiants et configurations.
Si les fichiers n'existent pas, le [CLI d'AWS](https://aws.amazon.com/cli/) permet de les créer à l'aide de la commande `aws configure`. Il est également possible
de créer soit-même ses identifiants et ses configuration en respectant l'exemple de configuration du lien précédent et l'[AWS Credentials File Format](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format)

## Changer les settings
Les settings se trouvent [de cette manière](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where).
Le fichier `config` permet de renseigner la région et le format des réponses.
Le fichier `credentials` permet de renseigner ses identifiants AWS.

Par défaut, nous utilisons le bucket qui nous a été fournis par le professeur. Il est possible de changer le bucket sur 
lequel nous taravaillons avec la méthode SetBucket.  

## Tester le projet avec les images déjà prêtes
Pour lancer le projet le plus simplement possible, ils faut commencer par pull les deux images des microservices, puis les lancer
```
docker pull ghcr.io/amt-team03/object-manager:latest
docker pull ghcr.io/amt-team03/label-detector:latest
docker run -d -p 8787:8787 ghcr.io/amt-team03/label-detector:latest
docker run -d -p 9090:9090 ghcr.io/amt-team03/label-detector:latest
```
Une fois que ces deux containers ont été lancés, on peut tester leur fonctionnement avec le programme de test Main.