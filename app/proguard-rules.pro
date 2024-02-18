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

# Obfuscate all class names
-keepnames class * {
    <methods>;
}

# Obfuscate all method names
-keepclassmembers class * {
    <fields>;
    <methods>;
}

# Obfuscate all resource names (e.g., layout files, drawable files)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Remove debug information
-optimizations !debug

# Keep entry points (e.g., activities, services) and their methods
-keep class com.fahad.mybills.** {
    public *;
}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions