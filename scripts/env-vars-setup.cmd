:: Prepare the environment variables used by the application.

:: The port where this project's web service is listening
:: See https://github.com/Azure/azure-iot-pcs-team/wiki/Architecture-draft
SET PCS_STREAMANALYTICS_WEBSERVICE_PORT = "9023"

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ "Event Hub-compatible name"
:: e.g. "my-test-hub"
SET PCS_IOTHUBREACT_HUB_NAME = "..."

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ "Event Hub-compatible endpoint"
v e.g. "iothub-ns-my-test-185521-d1bf252916.servicebus.windows.net"
SET PCS_IOTHUBREACT_HUB_ENDPOINT = "..."

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ Partitions
:: e.g. 4
SET PCS_IOTHUBREACT_HUB_PARTITIONS = "..."

:: see: "IoT Hub" ⇒ your hub ⇒ "Shared access policies"
:: e.g. you should use the predefined "service" policy
SET PCS_IOTHUBREACT_ACCESS_POLICY = "..."

:: see: Shared access policies ⇒ key name ⇒ Primary key
SET PCS_IOTHUBREACT_ACCESS_KEY = "..."

:: see: Shared access policies ⇒ key name ⇒ Connection string ⇒ "HostName"
:: e.g. "my-test-hub..azure-devices.net"
SET PCS_IOTHUBREACT_ACCESS_HOSTNAME = "..."
