@echo off
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -jar target/extractor-1.0.jar -cli
pause