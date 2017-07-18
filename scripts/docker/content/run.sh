#!/usr/bin/env bash

cd /app/

bin/iot-stream-analytics & \
    bin/iot-stream-analytics -main com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.Main && \
    fg
