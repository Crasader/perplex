cls
@echo off

@if NOT  "%1"=="" set TARGET=editor.%1
@if 	 "%1"=="" set TARGET=editor.MainFrame

@echo on
java -classpath class -Xmx512m %TARGET%
@echo off
