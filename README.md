# Ensi Manager
An app for interacting with Ensi's API

Unless you have a running instance of [Ensi (closed-source)](https://aliernfrog.github.io/ensibot), this app is most likely useless for you.

## 🔗 API configuration
Ensi Manager requires an endpoint which returns a JSON of [EnsiAPIData](https://github.com/aliernfrog/ensi-manager/blob/main/app/src/main/java/com/aliernfrog/ensimanager/data/EnsiAPIData.kt). A request will be done every time you launch the app.

## 🔧 Building
- Clone the repository
- Do your changes
- Run `./gradlew assembleRelease`
