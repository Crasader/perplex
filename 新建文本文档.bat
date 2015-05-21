cls
@echo off
@if "%1"=="" set TARGET=createconfig.jar
@echo on
java -jar %TARGET%
@echo off
pause
