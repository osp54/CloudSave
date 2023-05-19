<h1 align="center">
  <a href="https://github.com/osp54/CloudSave">
    <img src="icon.svg" alt="Logo" width="125" height="125">
  </a>
</h1>

<div align="center">
  CloudSave: A Mindustry mod for cloud-based game saves
  <br />
  <br />
</div>

## Building for Desktop Testing

1. Install JDK 17.
2. Run `gradlew jar` [1].
3. Find your mod jar in the `build/libs` directory. Please note that this version is only for desktop testing and will
   not work on Android.
   To build an Android-compatible version, you need the Android SDK. You can either let Github Actions handle this for
   you or
   set it up yourself. See the steps below.

## Building with Github Actions

This repository is set up with the Github Actions CI to automatically build the mod for you on every commit. To get a
jar file that works on any platform, follow these steps:

1. Create a Github repository with your desired mod name and upload the contents of that repository to it. Make any
   necessary changes, then commit and push.
2. Go to the Actions tab on your repository page. Select the latest commit from the list. If the build completed
   successfully, you will find a download link in the Artifacts section.
3. Click on the download link (should be the name of your repository). This will download a **zipped jar file** - **not
   ** the jar file itself [2]! Extract the contents of the zip file and import the jar file into Mindustry. This version
   should work on both Android and Desktop.

## Build locally

Building locally requires more setup time, but should not be a problem if you have experience with Android development.

1. Download the Android SDK, unzip it and set the `ANDROID_HOME` environment variable to your location.
2. Make sure you have API level 30 and the latest version of the build tools installed (e.g. 30.0.1).
3. Add the build-tools folder to your PATH. For example, if you have `30.0.1` installed, the path would
   be `$ANDROID_HOME/build-tools/30.0.1`.
4. Run `gradlew deploy`. If everything is set up correctly, this command will create a jar file in the `build/libs`
   directory that can be used on both
   which can be used on both Android and desktop platforms.

---

*[1] On Linux/Mac, use `./gradlew`. However, if you're using Linux, I assume you already know how to execute files
properly.  
[2]: Yes, I understand that this limitation is inconvenient. It's a limitation of the Github UI - although the jar file
itself is uploaded uncompressed, there is currently no direct way to download it as a single file.*