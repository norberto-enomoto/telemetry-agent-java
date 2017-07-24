[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

Azure IoT Stream Analytics
==========================

This service allows to stream, analyze and store messages from Azure IoT Hub
to DocumentDb.
The IoT stream is analyzed using a set of rules defined in the
[Device Telemetry service](https://github.com/Azure/device-telemetry-java),
and generating "alarms" when a message matches some of these rules. The alarms
are also stored in DocumentDb.

* [IoT Streaming Analytics Wiki](https://github.com/Azure/iot-stream-analytics-java/wiki)
* [Development setup, scripts and tools](DEVELOPMENT.md)
* [How to contribute to the project](CONTRIBUTING.md)

How to use the microservice
===========================

## Quick demo using the public Docker image

After cloning the repository, follow these steps:

1. Install Docker Compose: https://docs.docker.com/compose/install
1. Create an instance of [Azure IoT Hub](https://azure.microsoft.com/services/iot-hub)
1. Store the "IoT Hub" details in the [env-vars-setup](scripts)
   script.  The service internally uses [IoT Hub React](https://github.com/Azure/toketi-iothubreact) so you will notice some
   variables with an "IOTHUBREACT" prefix. For more information about
   environment variables, see the
   [development notes](DEVELOPMENT.md#configuration-and-environment-variables).
1. Open a terminal console into the project folder, and run these command to start
   the [IoT Hub Manager](https://github.com/Azure/iothub-manager-dotnet)
   and the Simulation services:
   ```
   cd scripts
   env-vars-setup      // (Bash users: ./env-vars-setup)
   cd docker
   docker-compose up
   ```

You should now start seeing the stream content in the console.

## Working with IntelliJ IDEA

After cloning the repository, follow these steps:

1. Install Docker: https://docs.docker.com/engine/installation
1. Create an instance of [Azure IoT Hub](https://azure.microsoft.com/services/iot-hub)
1. Open the solution in IntelliJ IDEA
1. Either in IntelliJ Run Configuration or in your system, define the following
   environment variables:
    1. `PCS_IOTHUBREACT_HUB_NAME` = {your Azure IoT Hub - Event Hub compatible name}
    1. `PCS_IOTHUBREACT_HUB_ENDPOINT` = {your Azure IoT Hub endpoint}
    1. `PCS_IOTHUBREACT_HUB_PARTITIONS` = {your Azure IoT Hub partition count}
    1. `PCS_IOTHUBREACT_ACCESS_CONNSTRING` = {your Azure IoT Hub access connection string}
    1. `PCS_STREAMANALYTICS_WEBSERVICE_PORT` = 9023

   For more information about environment variables, see the
   [development notes](DEVELOPMENT.md#configuration-and-environment-variables).
1. Start the StreamingAgent project

You should now start seeing the stream content in the IDE console.


[build-badge]: https://img.shields.io/travis/Azure/iot-stream-analytics-java.svg
[build-url]: https://travis-ci.org/Azure/iot-stream-analytics-java
[issues-badge]: https://img.shields.io/github/issues/azure/iot-stream-analytics-java.svg
[issues-url]: https://github.com/azure/iot-stream-analytics-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-pcs.js.svg
[gitter-url]: https://gitter.im/azure/iot-pcs
