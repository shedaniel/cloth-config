# Cloth [ ![Download](https://api.bintray.com/packages/shedaniel/cloth-config-2/config-1/images/download.svg) ](https://bintray.com/shedaniel/cloth-config-2/config-1/_latestVersion)
### Maven
```groovy
repositories {
    jcenter()
}
dependencies {
    'me.shedaniel.cloth:config-1:LATEST'
}
```
### APIs
###### Config Screen API
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
