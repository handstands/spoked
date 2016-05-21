#Spokèd

This is a very basic app that, by virtue of the inbuilt Text-to-Speech (TTS) libraries, allows you to, well, speak. Type in whatever you want to say, tap the `Speak` button, and it'll read it out for you.
It's intended audience was originally non-verbal people, or those with a speech impediment, but anyone who would find such a thing an useful addition is more than welcome to use it.
Keep in mind that this is quite unpolished. Especially with regard to the UI there's a substantial chance for shenanigans. So be aware of that. The app should now play nice regardless of device orientation but I can't yet guarantee it's free from weirdness.

##Usage
* For Android 5.0 and up it tries to use the device's default speech engine, or the US version if that isn't available.
* For earlier versions the app tries to use the TTS based on your device's locale. If that's not available, it tries the US version. 
* Enter what you want to say in the box where it says `Enter text here` and tap the `Speak` button. 
* Press the `Stop` button while it's speaking to have it stopped immediately.
* The `Clear` button empties the entry box. Doing so while it's still speaking will have no effect. In other words, you can delete whatever you wrote right after you tapped `Speak` and start writing the next thing. It'll keep on reading out your message.
* Tapping the `+` button will save whatever is currently in the entry box to the saved sentence list at the top of the application. If that exact text is already included there it won't be added again so there's no fear of doubling things up.
* Tapping a saved sentence will put it in the entry box from where you can either play it or tweak it and add the changed version to the saved sentences.
* Tapping and holding, or longtapping, a saved sentence will delete it.

##Requirements
It should run on Android 4.0 and up, but I haven't got the means to actually test that. There shouldn't be any big problems though, it's a very simple app. 
One thing you might need to do is go `into Settings > Language & Input > Text-to-speech input` (or whatever that might be in the language your device is set to) and make sure you've installed the voice pack for your locale. Doing so should enable you to use this without an internet connection.

