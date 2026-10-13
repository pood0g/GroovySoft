// Functional test: call MessageBoxW from User32.dll and show "Hello, World!".
// Uses the JDK's built-in Foreign Function & Memory API, so no extra jars are needed.
// Run from groovysh with:  /load HelloMessageBox.groovy

import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.nio.charset.StandardCharsets

arena = Arena.ofConfined()
user32 = SymbolLookup.libraryLookup('user32.dll', arena)

// int MessageBoxW(HWND hWnd, LPCWSTR lpText, LPCWSTR lpCaption, UINT uType)
messageBox = Linker.nativeLinker().downcallHandle(
        user32.find('MessageBoxW').orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT))

text = arena.allocateFrom('Hello, World!', StandardCharsets.UTF_16LE)
caption = arena.allocateFrom('groovysh', StandardCharsets.UTF_16LE)

MB_OK = 0
result = messageBox.invokeWithArguments(MemorySegment.NULL, text, caption, MB_OK)
arena.close()

println "MessageBoxW returned $result"
