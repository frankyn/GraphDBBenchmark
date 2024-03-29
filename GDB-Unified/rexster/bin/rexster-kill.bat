:: Windows launcher script for Rexster
@echo off

set work=%CD%

if [%work:~-3%]==[bin] cd ..

set LIBDIR=lib
set EXTDIR=ext/*
set PUBDIR=public

cd ext

FOR /D /r %%i in (*) do (
    set EXTDIR=%EXTDIR%;%%i/*
)

cd ..

set JAVA_OPTIONS=-Xms32m -Xmx512m

:: Launch the application
java %JAVA_OPTIONS% %JAVA_ARGS% -cp %LIBDIR%/*;%EXTDIR%;  com.tinkerpop.rexster.Application %* -wr %PUBDIR% 1>rexsterkilloutput.txt