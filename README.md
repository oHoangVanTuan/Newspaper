Newspaper
=========

[![Release](https://img.shields.io/github/release/ayltai/Newspaper.svg?label=release&maxAge=1800)](https://669-77390316-gh.circle-artifacts.com/0/apk/mobile-release.apk) [![Build Status](https://circleci.com/gh/ayltai/Newspaper.svg?style=shield)](https://circleci.com/gh/ayltai/Newspaper) [![Code Quality](https://api.codacy.com/project/badge/Grade/89d745ac9331474e9cf9f3203782a72f)](https://www.codacy.com/app/ayltai/Newspaper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ayltai/Newspaper&amp;utm_campaign=Badge_Grade) [![Code Coverage](https://api.codacy.com/project/badge/Coverage/89d745ac9331474e9cf9f3203782a72f)](https://www.codacy.com/app/ayltai/Newspaper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ayltai/Newspaper&amp;utm_campaign=Badge_Coverage) [![Code Coverage](https://codecov.io/gh/ayltai/Newspaper/branch/master/graph/badge.svg)](https://codecov.io/gh/ayltai/Newspaper) [![Dependency Status](https://www.versioneye.com/user/projects/59d118a0368b0865338c52b0/badge.svg?style=shield)](https://www.versioneye.com/user/projects/59d118a0368b0865338c52b0) [![Android API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat&label=API&maxAge=300)](https://www.android.com/history/) [![License](https://img.shields.io/badge/License-apache%202.0-blue.svg?label=license&maxAge=1800)](https://github.com/ayltai/Newspaper/blob/master/LICENSE)

An aggregated news app containing news from 10+ local news publishers in Hong Kong. Made with ❤

![Screenshot (Compact)](design/screenshot_cozy_framed.png "Screenshot (Cozy)")  ![Screenshot (Dark)](design/screenshot_dark_framed.png "Screenshot (Dark)")

## Features
* Fast. Just fast.
* Support video news
* Auto face centering image cropping
* Optional panoramic images
* Read news from 10 local news publishers
* Bookmarks and reading history
* No ads. We hate ads as much as you do

## Aggregated news publishers
* [Apple Daily (蘋果日報)](http://hk.apple.nextmedia.com)
* [Oriental Daily (東方日報)](http://orientaldaily.on.cc)
* [Sing Tao (星島日報)](http://std.stheadline.com)
* [Hong Kong Economic Times (經濟日報)](http://www.hket.com)
* [Sing Pao (成報)](https://www.singpao.com.hk)
* [Ming Pao (明報)](http://www.mingpao.com)
* [Headline (頭條日報)](http://hd.stheadline.com)
* [Sky Post (晴報)](skypost.ulifestyle.com.hk)
* [Hong Kong Economic Journal (信報)](http://www.hkej.com)
* [RTHK (香港電台)](http://news.rthk.hk)

## Contribution Guidelines

### Avoid Android Fragments and Activities
With [Flow](https://github.com/square/flow), the app achieves the maximum possible speed performance by using only Views. Avoid using Fragments whenever possible as explained [here](https://medium.com/square-corner-blog/advocating-against-android-fragments-81fd0b462c97) and [here](https://github.com/futurice/android-best-practices#activities-and-fragments). Views are preferred over Activities, as a complete Activity lifecycle is not needed in most use cases, and it is much slower to start an Activity using Intent than to attach a View.

### Stay within the dreadful DEX limit
The library dependencies used in the app are chosen carefully to avoid going beyond the DEX 65K limit. App cold-start time is thus reduced by as much as 50%.

### Be empathetic with fellow developers
Use [FindBugs](http://findbugs.sourceforge.net/) and [Checkstyle](http://checkstyle.sourceforge.net/) to help writing clean code and concise methods. Write as many tests as we can.

### Be open and/or free
Use open sourced technologies whenever possible. If not, use free services.

## Requirements
This app supports Android 4.1 Jelly Bean (API 16) or later.

## Acknowledgements
This app is made with the support of open source software:

* [Flow](https://github.com/square/flow)
* [Realm](https://realm.io/news/realm-for-android)
* [Facebook Fresco](https://github.com/facebook/fresco)
* [FrescoImageViewer](https://github.com/stfalcon-studio/FrescoImageViewer)
* [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view)
* [BigImageViewer](https://github.com/Piasy/BigImageViewer)
* [RecyclerView Animators](https://github.com/wasabeef/recyclerview-animators)
* [KenBurnsView](https://github.com/flavioarfaria/KenBurnsView)
* [PanoramaImageView](https://github.com/gjiazhe/PanoramaImageView)
* [ExoPlayer](https://github.com/google/ExoPlayer)
* [SmallBang](https://github.com/hanks-zyh/SmallBang)
* [Sequent](https://github.com/fujiyuu75/Sequent)
* [OkHttp](https://github.com/square/okhttp)
* [BottomBar](https://github.com/roughike/BottomBar)
* [FlowLayout](https://github.com/nex3z/FlowLayout)
* [Retrolambda](https://github.com/orfjackal/retrolambda)
* [Gradle Retrolambda Plugin](https://github.com/evant/gradle-retrolambda)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxBinding](https://github.com/JakeWharton/RxBinding)
* [Dagger 2](https://google.github.io/dagger)
* [Calligraphy](https://github.com/InflationX/Calligraphy)
* [AutoValue](https://github.com/google/auto/tree/master/value)
* [Gson](https://github.com/google/gson)
* [Espresso](https://google.github.io/android-testing-support-library)
* [JUnit 4](https://github.com/junit-team/junit4)
* [Mockito](https://github.com/mockito/mockito)
* [PowerMock](https://github.com/powermock/powermock)
* [Robolectric](http://robolectric.org)
* [LeakCanary](https://github.com/square/leakcanary)

… and closed source software:

* [Google Mobile Vision](https://developers.google.com/vision/face-detection-concepts)
* [Firebase Remote Config](https://firebase.google.com/docs/remote-config)

… and free cloud services:

* [CircleCI](https://circleci.com)
* [Firebase Test Lab](https://firebase.google.com/docs/test-lab)
* [Firebase Crash Reporting](https://firebase.google.com/docs/crash)
* [Firebase Analytics](https://firebase.google.com/docs/analytics)
* [Flurry Analytics](https://developer.yahoo.com/analytics)
* [Fabric Answers](https://answers.io)
* [Fabric Crashlytics](https://fabric.io/kits/android/crashlytics)
* [Instabug](https://instabug.com)
