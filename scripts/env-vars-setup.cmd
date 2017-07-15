:: Prepare the environment variables used by the application.

:: The port where this project's web service is listening
:: See https://github.com/Azure/azure-iot-pcs-team/wiki/Architecture-draft
SET PCS_STREAMANALYTICS_WEBSERVICE_PORT = "9023"

:: see: Shared access policies => key name => Connection string
SET IOTHUB_CONN_STRING = "..."
