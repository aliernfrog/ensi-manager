# Ensi Manager
Universal Android app for managing your HTTP(s) APIs.

## 💡 Features
- **🎛️ Dashboard:** Show information about your API and add your own action buttons
- **📃 Strings:** Manage string arrays in your API, with categories
- **📔 Logs:** View logs of your API and filter them based on type (debug, warning and error)
- **🪪 SSL verification:** Save your endpoint's certificate details to prevent malicious actors from stealing your authorization details
- **🎨 Material You & Adaptive design:** Ensi Manager provides an adaptive UI with Material You components to make you feel home!

## 🔗 API configuration
Ensi Manager requires an endpoint which returns a JSON of [APIData](https://github.com/aliernfrog/ensi-manager/blob/main/app/src/main/java/com/aliernfrog/ensimanager/data/api/APIData.kt). A request will be done to this endpoint every time you launch the app.

## 🔧 Building
- Clone the repository
- Do your changes
- Run `./gradlew assembleRelease`
