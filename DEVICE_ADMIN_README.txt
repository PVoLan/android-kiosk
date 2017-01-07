Steps to make app a device administrator:

1) Do factory reset on your device, or create a new Android emulator

2) Turn device on. IMPORTANT! Do NOT create Google account. If you create google account, these account becames a device administrator. You'll unable to set another administrator before you make another factory reset

3) Install your app onto device

4a) Plug device to your PC in "developer mode" and run

adb shell dpm set-device-owner [your.app.id]/[full.name.of.your.admin.receiver.class]

Example for my sample app is

adb shell dpm set-device-owner ru.pvolan.testkiosk/ru.pvolan.testkiosk.MyDeviceAdminReceiver

You should get output like this:

Success: Device owner set to package ComponentInfo{ru.pvolan.testkiosk/ru.pvolan.testkiosk.MyDeviceAdminReceiver}
Active admin set to component {ru.pvolan.testkiosk/ru.pvolan.testkiosk.MyDeviceAdminReceiver}

4b) You should be also able to see your device in "Settings - Security - Device Administrators" list. In some cases I was also able to manually enable/disable app directly in this list, even without connecting app to PC. Not tested well yet.



Now you can fully lock device screen for your app. Note that you should first install your app and then give it adminitsrator privileges. If you uninstall app and install again, you'll need to make it adminitrator manually again. But if you'll install updates "over" already installed app, you don't need to "re-enable" administrator mode.


