# Quarkus HOCON Extension

An extension allows loading of HOCON `.conf` and `.hocon` files using the same rules as the YAML configuration loader in Quarkus.

This code is based off the YAML config code directly in Quarkus.

Note that expressions / substitutions must be quoted, otherwise the TypeSafe Config layer will through an error since
the SmallRye config loader does not resolve these correctly.  Therefore, the following works:

```json
{
  "foo": "bar",
  "subFoo": "${foo}"
}
```

yet `subFoo: ${foo}` would not without being quoted.

Releases are available from JitPack: https://jitpack.io/#sort-dev/quarkus-hocon

Add maven repo to your build:
```
"https://jitpack.io"
```

Latest release dependency:
```
com.github.sort-dev:quarkus-hocon:0.1.1
```

!! Work in Progress !!