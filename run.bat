@echo off
set TARGET=%~dp0
%TARGET%\proj.android\build_native.py run
rem set TARGET=%COCOS_CONSOLE_ROOT%
rem %TARGET%\cocos.py compile -p android
