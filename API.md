# ⭐ Getting started - `APIEndpoints` setup
To use your API with Ensi Manager, you will first need to create an endpoint which returns a JSON of [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt). You can configure the following with this step:

### ℹ️ Metadata
Use the optional `metadata` field to provide an [`APIMetadata`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIMetadata.kt):
```json
{
  "metadata": {
    "name": "My wonderful API",
    "summary": "API is up and running!",
    "iconURL": "https://myapi.com/favicon.png"
  }
}
```
This will be shown in the API Profile list.

### 🔌 Endpoints
Example to provide an [`APIEndpoint`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoint.kt) for [`APIDashboard`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIDashboard.kt):
```json
{
  "getDashboard": {
    "url": "https://myapi.com/dashboard",
    "method": "GET",
    "requiresAuth": true
  }
}
```

### ➕ Adding the profile to Ensi Manager
After you're done setting up endpoints, you can add the API profile to Ensi Manager.
- **Endpoints URL**: URL to your `APIEndpoints` JSON
- **Authorization (optional)**: Used in `Authorization` header when making requests

# 🎛️ Dashboard
To use dashboard, you must provide the `getDashboard` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) as shown above.

This endpoint must return a JSON of [`APIDashboard`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIDashboard.kt):
```json
{
  "name": "My wonderful API",
  "avatar": "https://myapi.com/favicon.png",
  "status": "Up and running",
  "info": [
    {
      "title": "Info example",
      "value": "JSON of APIDashboardInfo"
    }
  ],
  "actions": [
    {
      "label": "Unclickable action example",
      "description": "JSON of APIDashboardAction",
      "icon": "<svg />"
    },
    {
      "label": "Clickable action example",
      "description": "This will perform a request to the endpoint field (accepts json of APIEndpoint) below",
      "endpoint": {
        "url": "https://myapi.com/do-something",
        "method": "POST",
        "requiresAuth": true
      }
    }
  ]
}
```

# 📃 Strings
To display strings and make the strings tab visible, you must provide the `getStrings` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) as shown above.

## Displaying strings
`getStrings` endpoint must return a JSON array of [`APIChatCategory`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIChatCategory.kt):
```json
[
  {
    "title": "Animals",
    "id": "animals",
    "data": [ "Cat", "Giraffe", "Sheep" ]
  },
  {
    "title": "Fruits",
    "id": "fruits",
    "data": [ "Apple", "Peach" ]
  }
]
```
The `id` field will be used for `addString` and `deleteString` requests.

## Adding strings
`addString` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) must listen to requests in the following format:

Example for adding a string to the `animals` category shown in the example above:
```json
{
  "category": "animals",
  "string": "Cow"
}
```

> `category`: The id of the category to add the string to.
> 
> `string`: The string to add.

## Deleting strings
`deleteString` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) must listen to requests in the following format:

Example for deleting a string from the `fruits` category shown in the example above:
```json
{
  "category": "fruits",
  "string": "Apple"
}
```

> `category`: The id of the category to delete the string from.
>
> `string`: The string to delete.

# 📔 Logs
To display logs, you must provide `getLogs` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) as shown above.

This endpoint must return a JSON array of [`APILog`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APILog.kt):
```json
[
  {
    "date": 1747585568126,
    "type": "LOG",
    "str": "Program started"
  },
  {
    "date": 1747585568127,
    "type": "WARN",
    "str": "Program started with invalid arugments, ignoring!"
  },
  {
    "date": 1747585568128,
    "type": "ERROR",
    "str": "Something went wrong!"
  }
]
```
> **Warning:** This will be displayed in a reversed order, meaning that "Something went wrong!" message will display at top and others at the bottom.

# 🔑 Authorization & security
If you want to secure your endpoints, you can make them require a specific `Authorization` header content. You can then enter this information in your API profile configuration in Ensi Manager.

Ensi Manager can protect your authorization header in following ways:
- **🪪 SSL verification:** This is enabled for all HTTPS APIs. You can either provide the key yourself when adding the API profile or leave the field empty to automatically pull the current one. Ensi Manager will abort the requests if saved SSL key does not match.
- **🔒 Encryption (optional):** Password protected encryption can be enabled in Ensi Manager settings. Additionally, biometric authentication can also be used.

# 🚣 Migrating to a different endpoint
You can provide `migration` field for [`APIEndpoints`](./app/src/main/java/com/aliernfrog/ensimanager/data/api/APIEndpoints.kt) to prompt users to migrate to the given endpoint:
```json
{
  "migration": {
    "url": "https://mynewapi.com/manager-endpoints"
  }
}
```

# 🙍‍♂️ User agent
Requests made by Ensi manager will have the following user-agent header:
```
EnsiManager/<APP VERSION CODE> (com.aliernfrog.ensimanager), Android <Build.VERSION.SDK_INT>
```