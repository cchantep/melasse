# Melasse API

Melasse *glue* API.

## Overall

Main usage class is `melasse.Binder`, which prepares UI bindings.

## Object path

Source/observed element and as target/endpoint are specified to Melasse using object paths:

```java
import melasse.Binder;

Binder.bind("columnModel.columnCount", aJTable, // observed
            "columnCount", status, // target
            optionMap);
```

In previous example, starting from `aJTable` (not null instance of `JTable`) Melasse will observe change on `columnCount` property of `columnModel`, which is statically equivalent to:

```java
aJTable.getColumnModel().getColumnCount();
```

Using its own object path API allows Melasse to observe 'deep' property, which are not directly on the root (there `aJTable`), even if at some point element of the path is null when binding is set up.

In previous example, even if property `columnModel` of `aJTable` is null when binding is created, as soon as it will be set Melasse will start observing `columnCount` on this newly set property.

## Button/action

UI properties transformed as boolean can be bound to activation of button or action using `enabled` property:

```java
import melasse.Binder;

Binder.bind("sourcePath", sourceRoot,
            "enabled", anAction,
            optionMap);
```

If several UI properties should be bound (e.g. all fields should have value to enable action), aggregation syntax can be used in target path:

```java
Binder.bind("sourcePath", sourceRoot,
            "enabled[]", anAction,
            optionMap);
```

## Text components

```java
import melasse.Binder;

Binder.bind("text", textComponent,
            "targetPath", targetRoot,
            optionMap);
```

- If `TextBindingKey.CONTINUOUSLY_UPDATE_VALUE` is set: listens to each key event.
- If `TextBindingKey.CONTINUOUSLY_UPDATE_VALUE` is not set: listens to action event and focus lost event.

> If text component is editable, programmatical call to `setText` [doesn't trigger binding](http://docs.oracle.com/javase/6/docs/api/javax/swing/text/JTextComponent.html#setText%28java.lang.String%29), only changes from UI do.
> If component is not editable property change will be bound.

Most interesting transformers for text components are:

- StringLengthToBooleanTransformer
- StringToCharArrayTransformer

### Bind button/action to provided value

Enable button/action only if value is provided to a text component:

```java
import melasse.StringLengthToBooleanTransformer;
import melasse.BindingOptionMap;
import melasse.TextBindingKey;
import melasse.Binder;

Binder.bind("text", textComponent,
            "enabled", anAction,
            new BindingOptionMap().
            add(BindingKey.INPUT_TRANSFORMER,
                StringLengthToBooleanTransformer.
                getTrimmingInstance()).
                add(TextBindingKey.CONTINUOUSLY_UPDATE_VALUE));
```

## Bind error display

Visibility of error can be directly bound using `JLabel.visible` as endpoint.

```java
JLabel errorDisplay = new JLabel("Missing value");
JTextField field = new JTextField();

// ... lay out comonents

Binder.bind("visible", errorDisplay, "text", field,
                    new BindingOptionMap().
                    add(BindingKey.INPUT_TRANSFORMER,
                        StringLengthToBooleanTransformer.
                        getTrimmingInstance().negate()).
                        add(TextBindingKey.CONTINUOUSLY_UPDATE_VALUE));
// |errorDisplay| will be visible when |field| is empty
```

## Provided transformers

- NegateBooleanTransformer
- IntegerToBooleanTransformer

## Enhanced property change support

As Melasse can be bound to property change on Java Bean, it also provided an enhanced utility to fire such changes.

This utility allow to declare dependencies between properties, so that if property B is depending on A, when A is changed there are 2 change events which are fired, first for A and second for B.

This is also useful to easily generate change event based on several properties. As dependent property can be a computed one, result of computation is fired as change event each time properties it depends on are changed. 

## Binding options

Way bindings are set up can be configured providing options, as firth argument of the `bind` method.

- targetModeOptions: When using a read-only property as binding source. Avoid warning such as `Target object does not support setting value for property X: myInstance@123456`.
