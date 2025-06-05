echo off
javac -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test.java
java -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test
pause