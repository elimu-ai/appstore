# elimu.ai Appstore

Android application which downloads educational apps stored on the [elimu.ai](http://elimu.ai) platform.

## Download application ⬇️

Download APK at https://github.com/elimu-ai/appstore/releases

## Software architecture

See software architecture diagram at https://github.com/elimu-ai/model

## Development

Note that the `REST_URL` depends on the build type you choose when installing the app:
  * `debug`: http://`<language>`.**test**.elimu.ai/rest/
  * `release`: http://`<language>`.elimu.ai/rest/

### Supported languages

A list of the currently supported languages is available at https://github.com/elimu-ai/model/blob/master/src/main/java/ai/elimu/model/enums/Language.java

The first time you launch the Appstore application, it will ask you to select the language that you want to use from a drop-down menu.
