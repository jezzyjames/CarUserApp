# Car-User-Application
An Android mobile application for communicate between car users
## Features
- Users can login by phone number.
- Users can add cars to their profile maximum to 3 cars.
- Search others users car by license plate number with province.
- Users can send text message or image message in chat room.
- 3 Car status are show on users car includes "unverified", "verifying" and "verified".
- Users can verified their car with registration car book in user profile page.
- Profanity filter, bad words will be replaced by '*' symbol.
- Message Notification by Firebase Cloud Messaging.
- Users can report an inappropriate users such as user who use badword or spam message.
- Show both users phone number in chatroom when messages in chat room has been sent from both users.
- Always warn alert dialog to users when users car are not verified or users are chatting with other unverifed car.
- 2 Language support includes English and Thai (automically change by device language).

## Installation
1.Download lastest version of CarUserApplication.apk file from here https://github.com/jezzyjames/CarUserApp/releases/
2.Install CarUserApplication.apk in your android device.

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
│   ├───libs
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
│       │       │   └───Chat
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
