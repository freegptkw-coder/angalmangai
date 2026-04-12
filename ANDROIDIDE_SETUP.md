# AndroidIDE Build Notes

If you are building this project inside AndroidIDE (mobile), set the SDK path manually.

```bash
echo "sdk.dir=/data/user/0/com.tom.rv2ide/files/home/android-sdk" > local.properties
```

Then run:

```bash
./gradlew :app:assembleDebug
```

> Do **not** commit `local.properties`.