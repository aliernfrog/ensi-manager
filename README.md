# Ensi Manager
Universal Android app for managing your HTTP(s) APIs.

## 💡 Features
- **🎛️ Dashboard:** Show information about your API and add your own action buttons
- **📃 Strings:** Manage string arrays in your API, with categories
- **📔 Logs:** View logs of your API and filter them based on type (debug, warning and error)
- **🪪 SSL verification:** Save your endpoint's certificate details to prevent malicious actors from stealing your authorization details
- **🎨 Material You & Adaptive design:** Ensi Manager provides an adaptive UI with Material You components to make you feel home!

## 🔗 API configuration
Ensi Manager requires an endpoint which returns a JSON of [APIEndpoints](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt). All fields are nullable.

## 🔧 Building
<details>
  <summary>Using GitHub Actions</summary>

  - Fork the repository
  - Add environment variables required for signing from **Repository settings > Secrets and variables > Actions > Repository secrets**:
    - `KEYSTORE_ALIAS`
    - `KEYSTORE_BASE64` this can be obtained using `openssl base64 -in keystore.jks`
    - `KEYSTORE_PASSWORD`
    - `KEY_PASSWORD`
  - Enable workflows
  - Trigger a build workflow and wait for it to build a release variant APK
</details>
<details>
  <summary>Locally</summary>

  - Clone the repository
  - Add a signing config (unless you only want to build debug variant or sign manually)
  - Build APK:
    - Release variant: `./gradlew assembleRelease`
    - Debug variant: `./gradlew assembleDebug`
</details>
