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
#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
#-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
#-optimizations !code/simplification/cast,!field/*,!class/merging/*
#!code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
#-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
#-allowaccessmodification

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
#-dontpreverify

