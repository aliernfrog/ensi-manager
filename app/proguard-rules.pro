##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.aliernfrog.ensimanager.data.EnsiAPIChatCategory { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiAPIDashboard { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiAPIDashboardInfo { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiAPIDashboardAction { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiAPIData { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiAPIEndpoint { *; }
-keep class com.aliernfrog.ensimanager.data.EnsiLog { *; }
-keep class com.aliernfrog.ensimanager.enum.EnsiLogType { *; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**