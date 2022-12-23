# MainController
Projet permettant de tester le bon fonctionnement des microservices

## Collaborateurs
- Melly Jérémie
- Seem Thibault

## Faire fonctionner le projet
### Maven
Ce projet utilisant maven, il est nécessaire de l'avoir installer au préalable. La marche à suivre se trouve [ici](https://maven.apache.org/install.html)

Une fois maven installé, il faut installer les dépendances avec la commande :

```mvn clean install -DskipTests```

### Microservices
Ce main ayant été fait pour fonctionner avec les microservices LabelDetector et ObjectManager, il est nécessaire que 
ces deux services soient accessible par ce programme. Ils doivent donc être lancé les deux, que ce soit dans 
un container ou en tant qu'application locale. Pour lancer les microservices, se référer aux README de [LabelDetector](../LabelDetector/README.md)
et [ObjectManager](../ObjectManager/README.md).

## Compiler le projet  
```mvn package```  
Si la compilation s'est effectuée avec succès, on doit trouver 2 fichier jar dans le dossier target. un dossier ayant
pour nom `<nomDuProjet>.jar` et `original-<nomDuProjet>.jar`. Il faut run le fichier sans l'extension `original-`.

## Usage

Pour utiliser le projet, il faut run la commande  
```java -jar AMT2-DataObject-1.0-SNAPSHOT.jar <URL_LabelDetector> <URL_ObjectManager>```  
depuis le dossier ou se trouve le jar. Le chemin vers l'image peut être un chemin relatif ou absolu. 
Si les deux microservices sont atteignable avec les adresse "http://localhost:8787/v1" pour le LabelDetector et 
"http://localhost:8787/v1" pour l'ObjectManager, les paramètres sont optionnels.

```ATTENTION!! Les microservices doivent être lancé AVANT de lancer ce projet```