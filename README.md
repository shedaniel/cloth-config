# Cloth Config [ ![Download](https://api.bintray.com/packages/shedaniel/cloth-config-2/cloth-config-2/images/download.svg) ](https://bintray.com/shedaniel/ClothConfig/ClothConfig2/_latestVersion)
## Maven
```groovy
repositories {
    jcenter()
}
dependencies {
    modCompile "me.shedaniel:cloth-config-2:LATEST"
}
```
## APIs
#### Config Screen v1 API
Start by using `ConfigScreenBuilder.create`, inside it you can do `addCategory` to get the category instance. Do `addOption` with the category instance to add an option.
```java
ConfigScreenBuilder builder = ConfigScreenBuilder.create(parentScreen, screenTitleKey, saveConsumer);
builder.addCategory("text.category.key").addOption(option);
```
There are multiple builtin option types:
- Boolean -> BooleanListEntry
- String -> StringListEntry
- Integer -> IntegerListEntry (Text Field), IntegerSliderEntry (Slider)
- Long -> LongListEntry (Text Field), LongSliderEntry (Slider)
- Float -> FloatListEntry
- Double -> DoubleListEntry
- Enum -> EnumListEntry (Override enumNameProvider for custom names, or make the enum implement Translatable, or override `toString()` in the enum for names)
- Text for Description -> TextListEntry

And you can always build your own entry. Example of a boolean entry:
```java
builder.addCategory("text.category.key").addOption(new BooleanListEntry(fieldKey, value, save));
```
`fieldKey` will be translated automatically using `I18n`, `value` is the `true` or `false`, for `save`, it will only be called when you press save.

Infect, you should do something like this:
```java
AtomicBoolean configBool = new AtomicBoolean(false);
builder.addCategory("text.category.key").addOption(new BooleanListEntry("text.value.key", configBool, bool -> configBool.set(bool)));
builder.setOnSave(savedConfig -> {
    // Save your config data file here
});
```

Lastly, you can open the screen like this:
```java
MinecraftClient.getInstance().openScreen(builder.build());
```

#### Config Screen v2 API
Start by using `ConfigBuilder.create`, inside it you can do `getOrCreateCategory` to get the category instance. Do `addEntry` with the category instance to add an option.
```java
ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parentScreen).setTitle(screenTitleKey).set(setSavingRunnable);
builder.getOrCreateCategory("text.category.key").addEntry(option);
```

To start adding fields, do `ConfigEntryBuilder.create()` to get the entry builder instance.
Example to add a boolean field:
```java
ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
category.addEntry(entryBuilder.startBooleanToggle("path.to.your.key", false).build());
```

All builtin entry builders can be found in ConfigEntryBuilder.

Lastly, you can open the screen like this:
```java
MinecraftClient.getInstance().openScreen(builder.build());
```
