@ECHO off & setlocal enableextensions enabledelayedexpansion

IF "%PCS_STREAMANALYTICS_WEBSERVICE_PORT%" == "" (
    echo Error: the PCS_STREAMANALYTICS_WEBSERVICE_PORT environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_HUB_NAME%" == "" (
    echo Error: the PCS_IOTHUBREACT_HUB_NAME environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_HUB_ENDPOINT%" == "" (
    echo Error: the PCS_IOTHUBREACT_HUB_ENDPOINT environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_HUB_PARTITIONS%" == "" (
    echo Error: the PCS_IOTHUBREACT_HUB_PARTITIONS environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_ACCESS_POLICY%" == "" (
    echo Error: the PCS_IOTHUBREACT_ACCESS_POLICY environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_ACCESS_KEY%" == "" (
    echo Error: the PCS_IOTHUBREACT_ACCESS_KEY environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBREACT_ACCESS_HOSTNAME%" == "" (
    echo Error: the PCS_IOTHUBREACT_ACCESS_HOSTNAME environment variable is not defined.
    exit /B 1
)

endlocal
