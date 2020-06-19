# Car-User-Application
An android application for communicate between car users.
## Github link:
https://github.com/jezzyjames/CarUserApp
## Installation
1.Download lastest version of CarUserApplication.apk file from here https://github.com/jezzyjames/CarUserApp/releases/<br/>
2.Install CarUserApplication.apk in your android device.
## Installation for developer
1.Pull this git to your Android Studio.
2.Add Firebase to your Android project (following this instruction document -> https://firebase.google.com/docs/android/setup) 
3.Go to Sign-in method in Firebase Authentication and enable sign-in by phone.
4.Create Firebase Realtime Database and Firebase Storage.
5.Installation complete.
## Features
- Users can login by phone number.
- Users can add cars to their profile maximum to 3 cars.
- Search others users car by license plate number with province.
- Users can send text message or image message in chat room.
- 3 Car status are show on users car includes "unverified", "verifying" and "verified".
- Users can verify their car with registration car book in user profile page.
- Profanity filter, bad words will be replaced by '*' symbol.
- Message Notification by Firebase Cloud Messaging.
- Users can report an inappropriate users such as user who use badword or spam message.
- Show both users phone number in chatroom when messages in chat room has been sent from both users.
- Always warn alert dialog to users when users car are not verified or users are chatting with other unverifed car.
- 2 Language support includes English and Thai (automically change by device language).
## Direction tree
```bash
C:.
├───.gradle
│   ├───5.6.4
│   │   ├───executionHistory
│   │   ├───fileChanges
│   │   ├───fileContent
│   │   ├───fileHashes
│   │   ├───javaCompile
│   │   └───vcsMetadata-1
│   ├───buildOutputCleanup
│   └───vcs-1
├───.idea
│   ├───caches
│   ├───codeStyles
│   └───libraries
├───app
│   ├───build
│   │   ├───generated
│   │   │   ├───ap_generated_sources
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───res
│   │   │   │   ├───google-services
│   │   │   │   │   └───debug
│   │   │   │   │       └───values
│   │   │   │   ├───pngs
│   │   │   │   │   └───debug
│   │   │   │   └───resValues
│   │   │   │       └───debug
│   │   │   └───source
│   │   │       └───buildConfig
│   │   │           └───debug
│   │   │               └───com
│   │   │                   └───cs
│   │   │                       └───tu
│   │   │                           └───caruserapp
│   │   ├───intermediates
│   │   │   ├───annotation_processor_list
│   │   │   │   └───debug
│   │   │   ├───apk_list
│   │   │   │   └───debug
│   │   │   ├───blame
│   │   │   │   └───res
│   │   │   │       └───debug
│   │   │   │           ├───multi-v2
│   │   │   │           └───single
│   │   │   ├───bundle_manifest
│   │   │   │   └───debug
│   │   │   │       └───bundle-manifest
│   │   │   ├───compatible_screen_manifest
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───compile_and_runtime_not_namespaced_r_class_jar
│   │   │   │   └───debug
│   │   │   ├───data_binding_layout_info_type_merge
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───dex
│   │   │   │   └───debug
│   │   │   │       ├───mergeExtDexDebug
│   │   │   │       │   └───out
│   │   │   │       ├───mergeLibDexDebug
│   │   │   │       │   └───out
│   │   │   │       └───mergeProjectDexDebug
│   │   │   │           └───out
│   │   │   ├───dex_archive_input_jar_hashes
│   │   │   │   └───debug
│   │   │   ├───duplicate_classes_check
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───external_libs_dex_archive
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───incremental
│   │   │   │   ├───debug-mergeJavaRes
│   │   │   │   │   └───zip-cache
│   │   │   │   ├───debug-mergeNativeLibs
│   │   │   │   │   └───zip-cache
│   │   │   │   ├───mergeDebugAssets
│   │   │   │   ├───mergeDebugJniLibFolders
│   │   │   │   ├───mergeDebugResources
│   │   │   │   │   ├───merged.dir
│   │   │   │   │   │   ├───values
│   │   │   │   │   │   ├───values-af
│   │   │   │   │   │   ├───values-am
│   │   │   │   │   │   ├───values-ar
│   │   │   │   │   │   ├───values-as
│   │   │   │   │   │   ├───values-az
│   │   │   │   │   │   ├───values-b+sr+Latn
│   │   │   │   │   │   ├───values-be
│   │   │   │   │   │   ├───values-bg
│   │   │   │   │   │   ├───values-bn
│   │   │   │   │   │   ├───values-bs
│   │   │   │   │   │   ├───values-ca
│   │   │   │   │   │   ├───values-cs
│   │   │   │   │   │   ├───values-da
│   │   │   │   │   │   ├───values-de
│   │   │   │   │   │   ├───values-el
│   │   │   │   │   │   ├───values-en-rAU
│   │   │   │   │   │   ├───values-en-rCA
│   │   │   │   │   │   ├───values-en-rGB
│   │   │   │   │   │   ├───values-en-rIN
│   │   │   │   │   │   ├───values-en-rXC
│   │   │   │   │   │   ├───values-es
│   │   │   │   │   │   ├───values-es-rUS
│   │   │   │   │   │   ├───values-et
│   │   │   │   │   │   ├───values-eu
│   │   │   │   │   │   ├───values-fa
│   │   │   │   │   │   ├───values-fi
│   │   │   │   │   │   ├───values-fr
│   │   │   │   │   │   ├───values-fr-rCA
│   │   │   │   │   │   ├───values-gl
│   │   │   │   │   │   ├───values-gu
│   │   │   │   │   │   ├───values-h720dp-v13
│   │   │   │   │   │   ├───values-hdpi-v4
│   │   │   │   │   │   ├───values-hi
│   │   │   │   │   │   ├───values-hr
│   │   │   │   │   │   ├───values-hu
│   │   │   │   │   │   ├───values-hy
│   │   │   │   │   │   ├───values-in
│   │   │   │   │   │   ├───values-is
│   │   │   │   │   │   ├───values-it
│   │   │   │   │   │   ├───values-iw
│   │   │   │   │   │   ├───values-ja
│   │   │   │   │   │   ├───values-ka
│   │   │   │   │   │   ├───values-kk
│   │   │   │   │   │   ├───values-km
│   │   │   │   │   │   ├───values-kn
│   │   │   │   │   │   ├───values-ko
│   │   │   │   │   │   ├───values-ky
│   │   │   │   │   │   ├───values-land
│   │   │   │   │   │   ├───values-large-v4
│   │   │   │   │   │   ├───values-ldltr-v21
│   │   │   │   │   │   ├───values-lo
│   │   │   │   │   │   ├───values-lt
│   │   │   │   │   │   ├───values-lv
│   │   │   │   │   │   ├───values-mk
│   │   │   │   │   │   ├───values-ml
│   │   │   │   │   │   ├───values-mn
│   │   │   │   │   │   ├───values-mr
│   │   │   │   │   │   ├───values-ms
│   │   │   │   │   │   ├───values-my
│   │   │   │   │   │   ├───values-nb
│   │   │   │   │   │   ├───values-ne
│   │   │   │   │   │   ├───values-night-v8
│   │   │   │   │   │   ├───values-nl
│   │   │   │   │   │   ├───values-or
│   │   │   │   │   │   ├───values-pa
│   │   │   │   │   │   ├───values-pl
│   │   │   │   │   │   ├───values-port
│   │   │   │   │   │   ├───values-pt
│   │   │   │   │   │   ├───values-pt-rBR
│   │   │   │   │   │   ├───values-pt-rPT
│   │   │   │   │   │   ├───values-ro
│   │   │   │   │   │   ├───values-ru
│   │   │   │   │   │   ├───values-si
│   │   │   │   │   │   ├───values-sk
│   │   │   │   │   │   ├───values-sl
│   │   │   │   │   │   ├───values-sq
│   │   │   │   │   │   ├───values-sr
│   │   │   │   │   │   ├───values-sv
│   │   │   │   │   │   ├───values-sw
│   │   │   │   │   │   ├───values-sw600dp-v13
│   │   │   │   │   │   ├───values-ta
│   │   │   │   │   │   ├───values-te
│   │   │   │   │   │   ├───values-th
│   │   │   │   │   │   ├───values-tl
│   │   │   │   │   │   ├───values-tr
│   │   │   │   │   │   ├───values-uk
│   │   │   │   │   │   ├───values-ur
│   │   │   │   │   │   ├───values-uz
│   │   │   │   │   │   ├───values-v16
│   │   │   │   │   │   ├───values-v17
│   │   │   │   │   │   ├───values-v18
│   │   │   │   │   │   ├───values-v21
│   │   │   │   │   │   ├───values-v22
│   │   │   │   │   │   ├───values-v23
│   │   │   │   │   │   ├───values-v24
│   │   │   │   │   │   ├───values-v25
│   │   │   │   │   │   ├───values-v26
│   │   │   │   │   │   ├───values-v28
│   │   │   │   │   │   ├───values-vi
│   │   │   │   │   │   ├───values-watch-v20
│   │   │   │   │   │   ├───values-watch-v21
│   │   │   │   │   │   ├───values-xlarge-v4
│   │   │   │   │   │   ├───values-zh-rCN
│   │   │   │   │   │   ├───values-zh-rHK
│   │   │   │   │   │   ├───values-zh-rTW
│   │   │   │   │   │   └───values-zu
│   │   │   │   │   └───stripped.dir
│   │   │   │   ├───mergeDebugShaders
│   │   │   │   ├───packageDebug
│   │   │   │   │   └───tmp
│   │   │   │   │       └───debug
│   │   │   │   │           └───zip-cache
│   │   │   │   └───processDebugResources
│   │   │   ├───instant_app_manifest
│   │   │   │   └───debug
│   │   │   ├───javac
│   │   │   │   └───debug
│   │   │   │       └───classes
│   │   │   │           └───com
│   │   │   │               └───cs
│   │   │   │                   └───tu
│   │   │   │                       └───caruserapp
│   │   │   │                           ├───Adapter
│   │   │   │                           ├───Dialog
│   │   │   │                           ├───Fragments
│   │   │   │                           ├───Model
│   │   │   │                           └───Notification
│   │   │   ├───manifest_merge_blame_file
│   │   │   │   └───debug
│   │   │   ├───merged_assets
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   │           └───io
│   │   │   │               └───michaelrocks
│   │   │   │                   └───libphonenumber
│   │   │   │                       └───android
│   │   │   │                           └───data
│   │   │   ├───merged_java_res
│   │   │   │   └───debug
│   │   │   ├───merged_jni_libs
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───merged_manifests
│   │   │   │   └───debug
│   │   │   ├───merged_native_libs
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   │           └───lib
│   │   │   │               ├───arm64-v8a
│   │   │   │               ├───armeabi
│   │   │   │               ├───armeabi-v7a
│   │   │   │               ├───mips
│   │   │   │               ├───mips64
│   │   │   │               ├───x86
│   │   │   │               └───x86_64
│   │   │   ├───merged_shaders
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───metadata_feature_manifest
│   │   │   │   └───debug
│   │   │   │       └───metadata-feature
│   │   │   ├───mixed_scope_dex_archive
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───navigation_json
│   │   │   │   └───debug
│   │   │   ├───processed_res
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───project_dex_archive
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   │           └───com
│   │   │   │               └───cs
│   │   │   │                   └───tu
│   │   │   │                       └───caruserapp
│   │   │   │                           ├───Adapter
│   │   │   │                           ├───Dialog
│   │   │   │                           ├───Fragments
│   │   │   │                           ├───Model
│   │   │   │                           └───Notification
│   │   │   ├───res
│   │   │   │   └───merged
│   │   │   │       └───debug
│   │   │   ├───runtime_symbol_list
│   │   │   │   └───debug
│   │   │   ├───shader_assets
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───stripped_native_libs
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   │           └───lib
│   │   │   │               ├───arm64-v8a
│   │   │   │               ├───armeabi
│   │   │   │               ├───armeabi-v7a
│   │   │   │               ├───mips
│   │   │   │               ├───mips64
│   │   │   │               ├───x86
│   │   │   │               └───x86_64
│   │   │   ├───sub_project_dex_archive
│   │   │   │   └───debug
│   │   │   │       └───out
│   │   │   ├───symbol_list_with_package_name
│   │   │   │   └───debug
│   │   │   └───validate_signing_config
│   │   │       └───debug
│   │   │           └───out
│   │   ├───outputs
│   │   │   ├───apk
│   │   │   │   └───debug
│   │   │   └───logs
│   │   └───tmp
│   │       └───compileDebugJavaWithJavac
│   └───src
│       ├───androidTest
│       │   └───java
│       │       └───com
│       │           └───cs
│       │               └───tu
│       │                   └───caruserapp
│       ├───main
│       │   ├───java
│       │   │   └───com
│       │   │       └───cs
│       │   │           └───tu
│       │   │               └───caruserapp
│       │   │                   ├───Adapter
│       │   │                   ├───Dialog
│       │   │                   ├───Fragments
│       │   │                   ├───Model
│       │   │                   └───Notification
│       │   └───res
│       │       ├───anim
│       │       ├───drawable
│       │       ├───drawable-anydpi
│       │       ├───drawable-hdpi
│       │       ├───drawable-mdpi
│       │       ├───drawable-v24
│       │       ├───drawable-xhdpi
│       │       ├───drawable-xxhdpi
│       │       ├───layout
│       │       ├───menu
│       │       ├───mipmap-anydpi-v26
│       │       ├───mipmap-hdpi
│       │       ├───mipmap-mdpi
│       │       ├───mipmap-xhdpi
│       │       ├───mipmap-xxhdpi
│       │       ├───mipmap-xxxhdpi
│       │       ├───values
│       │       ├───values-th
│       │       └───xml
│       └───test
│           └───java
│               └───com
│                   └───cs
│                       └───tu
│                           └───caruserapp
└───gradle
    └───wrapper
```
