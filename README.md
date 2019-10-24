# Cloth Config [ ![Download](https://api.bintray.com/packages/shedaniel/cloth-config-2/config-2/images/download.svg) ](https://bintray.com/shedaniel/cloth-config-2/config-2/_latestVersion)
[Help translate ClothConfig on Crowdin!](https://crowdin.com/project/cloth-config)

## Maven
```groovy
repositories {
    jcenter()
}
dependencies {
    'me.shedaniel.cloth:config-2:LATEST'
}
```
## APIs
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

#### Dropdown Menus
Start by doing `entryBuilder.startDropdownMenu()`, the `SelectionTopCellElement` is the search bar, and `SelectionCellCreator` is the cells below.

Create a `SelectionTopCellElement` with `DropdownMenuBuilder.TopCellElementBuilder.of()`, which takes three parameters:
- `value`: The value of the field
- `toObjectFunction`: The toObject function, turning String into T, returns null if error
- `toStringFunction`: The toString function, which never returns null, affects the displayed text of your value.

You can also use the premade `SelectionTopCellElement` for items and blocks.


Create a `SelectionCellCreator` with `DropdownMenuBuilder.CellCreatorBuilder.of()`, which defines the cell height, the cell width, and how many cells are displayed at most.
- `toStringFunction`: The toString function, which never returns null, affects the displayed text of the cell.

You can also use the premade `SelectionCellCreator` for items and blocks as well.

You should create your own cell creator extending the `DefaultSelectionCellCreator` to create custom cells.

Do `.setSelections()` with your builder to specify the list of suggestions.

This is what you should do if you got a config for items:
```java
entryBuilder.startDropdownMenu("Field Key", 
    DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(configItem), // This should contain your saved item instead of an apple as shown here 
    DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()
)
    .setDefaultValue(Items.APPLE) // You should define a default value here
    .setSelections(Registry.ITEM.stream().collect(Collectors.toSet()))
    .setSaveConsumer(item -> configItem = (Item) item) // You should save it here, cast the item because Java is "smart"
    .build();
```
