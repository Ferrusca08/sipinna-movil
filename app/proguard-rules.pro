# Retrofit conserva las firmas genéricas de las interfaces suspend.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Moshi genera adaptadores por KSP; los DTO deben conservar sus nombres de campo.
-keep class com.sipinna.atizapan.reportes.data.remote.dto.** { *; }
-keep @com.squareup.moshi.JsonQualifier @interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# OkHttp declara clases opcionales de plataformas que no usamos.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
