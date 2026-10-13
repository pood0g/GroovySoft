# GroovySoft

A self-contained, minimal Windows bundle for running `groovysh` (the Apache Groovy shell).
It needs no installed Java or Groovy: a trimmed Java runtime and only the required Groovy
libraries are included.

## Contents

| Path | What it is |
| --- | --- |
| `groovysh-minimal/groovysh.bat` | Launcher. Points `JAVA_HOME` and `GROOVY_HOME` at this folder and starts the shell. |
| `groovysh-minimal/bin/`, `conf/` | Unmodified `groovysh.bat`, `startGroovy.bat` and `groovy-starter.conf` from Groovy. |
| `groovysh-minimal/lib/` | 29 of the Groovy SDK's jars. |
| `groovysh-minimal/jre/` | Java runtime built with `jlink`, 13 modules. |
| `groovysh-tests/HelloMessageBox.groovy` | Functional test: calls `MessageBoxW` in `User32.dll`. |

The bundle is about 66 MB on disk (about 49 MB zipped).

## Running

```
groovysh-minimal\groovysh.bat
```

To run the functional test, enter this at the `groovy>` prompt (adjust the path):

```
/load C:/path/to/groovysh-tests/HelloMessageBox.groovy
```

A "Hello, World!" message box appears; after OK is clicked the shell prints
`MessageBoxW returned 1`. The script uses the JDK's Foreign Function & Memory API
(`java.lang.foreign`), so it needs no extra jars.

## How the bundle was built

Sources (not tracked in this repository, see `.gitignore`):

- `apache-groovy-sdk-6.0.0.zip`
- `microsoft-jdk-25.0.4.1-windows-x64.zip`

### Java runtime

```
jlink --module-path <jdk>\jmods ^
  --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,java.prefs,java.management,java.compiler,java.datatransfer,jdk.zipfs,jdk.unsupported ^
  --strip-debug --no-header-files --no-man-pages --compress zip-6 ^
  --output groovysh-minimal\jre
```

`java.desktop` is the largest module and cannot be dropped: Groovy's core depends on
`java.beans`, which lives there.

### Libraries

Copied from the SDK's `lib` folder:

- Shell: `groovy`, `groovy-groovysh`, `groovy-json`
- Terminal: `jansi`, `jline-builtins`, `jline-console`, `jline-reader`, `jline-shell`
- Standard extension modules: `groovy-astbuilder`, `groovy-contracts`, `groovy-datetime`,
  `groovy-dateutil`, `groovy-ginq`, `groovy-jsr223`, `groovy-macro`, `groovy-macro-library`,
  `groovy-nio`, `groovy-sql`, `groovy-xml`
- `/console` and `/inspect` (GUI): `groovy-console`, `groovy-swing`, `groovy-templates`,
  `flatlaf`, `flatlaf-extras`, `jsvg`, `javaparser-core`, `org.abego.treelayout.core`
- `/grab`: `groovy-grape-ivy`, `ivy`

The selection came from `jdeps` on `groovy-groovysh-6.0.0.jar` plus the jars actually
loaded during a traced session (`-Xlog:class+load`).

Left out: docs, sources, the other Groovy launchers (`groovy`, `groovyc`, `groovyConsole`),
Ant, Maven resolver, JUnit/TestNG, Jackson, the YAML/TOML/CSV modules, and non-English
locale data. To restore a Groovy module, copy its jar from the SDK into `groovysh-minimal\lib`.

## What was tested

On Windows 11 x64, with `JAVA_HOME` pointing at a non-existent path:

- Evaluating code: closures, class definitions, JSON, XML, `groovy.sql.Sql` loading
- Shell commands: `/vars`, `/types`, `/imports`, `/ls`, `/ttop --help`, `/load`
- Interactive console: colours, syntax highlighting, history, tab completion
- `/console` and `/inspect -g` open their windows
- `/grab` downloads and loads a dependency (needs network access)
- `HelloMessageBox.groovy` shows the message box

## Licences

Apache Groovy is under the Apache License 2.0 (`groovysh-minimal/LICENSE`, `NOTICE`).
The Java runtime's notices are in `groovysh-minimal/jre/legal`.
