#!/bin/bash

echo "Waiting for SQL Server..."

sleep 20

/opt/mssql-tools18/bin/sqlcmd \
-S sqlserver \
-U sa \
-P "Trungkien132@" \
-C \
-Q "IF DB_ID('sales') IS NULL CREATE DATABASE sales"

echo "Database sales ready"