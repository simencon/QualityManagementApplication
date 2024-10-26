# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclassmembers public class com.simenko.qmapp.navigation.** { public *;}
-keepclassmembers public class com.simenko.qmapp.data.remote.** { public *;}
-keepclassmembers public class com.simenko.qmapp.data.cache.prefs.model.** { public *;}
-keepclassmembers public class com.simenko.qmapp.data.cache.db.** { public *;}
-keepclassmembers public class com.simenko.qmapp.domain.entities.** { public *;}

-keep public class com.simenko.qmapp.navigation.** { public *;}
-keep public class com.simenko.qmapp.data.remote.** { public *;}
-keep public class com.simenko.qmapp.data.cache.prefs.model.** { public *;}
-keep public class com.simenko.qmapp.data.cache.db.** { public *;}
-keep public class com.simenko.qmapp.domain.entities.** { public *;}

# Keep `INSTANCE.serializer()` of serializable objects.
#-if @kotlinx.serialization.Serializable class ** {
#    public static ** INSTANCE;
#}
#-keepclassmembers class <1> {
#    public static <1> INSTANCE;
#    kotlinx.serialization.KSerializer serializer(...);
#}

#okhttp3
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#
#-dontwarn okhttp3.**