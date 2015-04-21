cls
@echo off

@if NOT  "%1"=="" set TARGET=%1.java
@if 	 "%1"=="" set TARGET=*.java

@echo on
javac -deprecation -classpath class src\editor\%TARGET% -d class 
javac -deprecation -classpath class src\editoradd\%TARGET% -d class
@echo off

pause
