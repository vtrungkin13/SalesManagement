#!/bin/bash

echo "Waiting for SQL Server..."

sleep 20

/opt/mssql-tools18/bin/sqlcmd \
-S sqlserver \
-U "${DB_USERNAME:-sa}" \
-P "${SA_PASSWORD}" \
-C \
-Q "IF DB_ID('${DB_NAME:-sales}') IS NULL CREATE DATABASE ${DB_NAME:-sales}"

echo "Database ${DB_NAME:-sales} ready"