# elimu.ai Appstore 📲

Android application which downloads and installs educational apps stored on the [elimu.ai](http://elimu.ai) platform.

<img width="320" alt="device-2020-06-15-155802" src="https://user-images.githubusercontent.com/15718174/84632262-39fec180-af21-11ea-8a8a-215120744f05.png">

## Download Application ⬇️

Download APK (`ai.elimu.appstore-<versionCode>.apk`) at https://github.com/elimu-ai/appstore/releases

### Install the APK

When opening the APK, you might see a prompt saying "Install unknown apps". If so, select "Allow from this source", and return to the installation.

<img width="320" alt="install-unknown-apps" src="https://user-images.githubusercontent.com/15718174/84587915-c93ea300-ae55-11ea-9116-448fc76ebede.png">

For step-by-step instructions on how to download and install the software, see [Wiki: elimu.ai Software Installation](https://github.com/elimu-ai/wiki/blob/master/SOFTWARE_INSTALLATION.md).

## What Devices are Being Used?

We are building our software for Android devices with **6"-10" displays** installed with Android API **version 24 (7.0)** or higher.

## Development 👩🏽‍💻

### Software Architecture

[
  <img width="320" alt="Software Architecture" src="https://user-images.githubusercontent.com/15718174/83595568-fb6a1e00-a594-11ea-990a-10c0bd62ed11.png">
](https://github.com/elimu-ai/wiki/blob/master/SOFTWARE_ARCHITECTURE.md)

### REST API

Note that the `REST_URL` depends on the build type you choose when installing the app:
  * `debug`: http://`<language>`.**test**.elimu.ai/rest/
  * `qa_test`: http://`<language>`.**test**.elimu.ai/rest/
  * `release`: http://`<language>`.elimu.ai/rest/

### Supported Languages

A list of the currently supported languages is available at https://github.com/elimu-ai/model/blob/master/src/main/java/ai/elimu/model/enums/Language.java

The first time you launch the Appstore application, it will ask you to select the language that you want to use:

<img width="320" alt="device-2020-06-10-152910" src="https://user-images.githubusercontent.com/15718174/84239611-58367d00-ab2f-11ea-9fb0-f119de951cef.png">

For information on how to add support for a new language, see https://github.com/elimu-ai/wiki/blob/master/LOCALIZATION.md.

---

## About the elimu.ai Community

![elimu ai-tagline](https://user-images.githubusercontent.com/15718174/54360503-e8e88980-465c-11e9-9792-32b513105cf3.png)

 * For a high-level description of the project, see https://github.com/elimu-ai/wiki/blob/master/README.md.
 * For project milestones, see https://github.com/elimu-ai/wiki/projects.
