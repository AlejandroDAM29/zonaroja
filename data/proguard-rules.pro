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

# Keep Kotlin metadata for Moshi fallback/reflection paths.
-keep class kotlin.Metadata { *; }

# Keep classes annotated for Moshi codegen and their generated adapters so
# minified app builds can still resolve them at runtime.
-if @com.squareup.moshi.JsonClass class *
-keep class <1> { *; }

-if @com.squareup.moshi.JsonClass class *
-keep class <1>JsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
