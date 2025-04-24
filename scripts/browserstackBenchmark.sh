#!/bin/bash

#
#  Copyright (c) 2004-2025, University of Oslo
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#  Redistributions of source code must retain the above copyright notice, this
#  list of conditions and the following disclaimer.
#
#  Redistributions in binary form must reproduce the above copyright notice,
#  this list of conditions and the following disclaimer in the documentation
#  and/or other materials provided with the distribution.
#  Neither the name of the HISP project nor the names of its contributors may
#  be used to endorse or promote products derived from this software without
#  specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
#  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
#  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
#  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
#  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

set -ex
source "$(dirname $0)/config_jenkins.init"
source "$(dirname $0)/browserstackCommon.sh"

log_file=$1
benchmark_config_file="$(dirname $0)/../core/src/androidTest/assets/benchmark.json"

CONFIG="{
  \"serverUrl\": \"${SERVER_URL:-https://play.im.dhis2.org/stable-2-42-1}\",
  \"username\": \"${USERNAME:-android}\",
  \"password\": \"${PASSWORD:-Android123}\"
}
"
echo "$CONFIG" > $benchmark_config_file

# Build apks
./gradlew :core:assembleDebug
./gradlew :core:assembleDebugAndroidTest
./gradlew :instrumented-test-app:assembleDebug -PsdkVersion=$SDK_VERSION

app_apk_path=$(findApkPath "instrumented-test-app")
test_apk_path=$(findApkPath "core")

# Upload app and testing apk
echo "Uploading app APK to Browserstack..."
app_url="$(upload_app_apk $app_apk_path)"

echo "Uploading test APK to Browserstack..."
test_url="$(upload_test_apk $test_apk_path)"

# Prepare json and run tests
echo "Starting execution of espresso tests..."

json=$(jq -n \
                --argjson app_url $app_url \
                --argjson test_url $test_url \
                --argjson devices ["$browserstack_device_list"] \
                --argjson package ["$browserstack_package_benchmark"] \
                --arg logs "$browserstack_device_logs" \
                --arg video "$browserstack_video" \
                --arg loc "$browserstack_local" \
                --arg deviceLogs "$browserstack_deviceLogs" \
                --arg allowDeviceMockServer "$browserstack_mock_server" \
                --arg singleRunnerInvocation "$browserstack_singleRunnerInvocation" \
                '{devices: $devices, app: $app_url, testSuite: $test_url, package: $package, logs: $logs, video: $video, local: $loc, deviceLogs: $deviceLogs, allowDeviceMockServer: $allowDeviceMockServer, singleRunnerInvocation: $singleRunnerInvocation}')

build_id="$(execute_build "$json")"
echo "build id running: $build_id"

build_status_response="$(waitForBuildFinish $build_id)"

build_status="$(get_build_status "$build_status_response")"
build_session_details_url="$(get_build_session_details_url "$build_status_response")"
build_device_logs_url="$(get_device_logs "$build_session_details_url")"

devicelogs="$(curl -u $bs_auth $build_device_logs_url | grep "SDKPerformanceAnalysis")"

# At this point, we have something like:
# 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisTime(20150): Wipe data and dowload again: 14797 ms
# 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisTime(20150): Delete data and push changes: 5150 ms
# 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisMemory(20150): D2 Instantiation: 11 MB
# 2025-07-07 16:19:06.495 +0000 D/SDKPerformanceAnalysisMemory(20150): D2 Login: 4 MB

echo "# Benchmark result" >> $log_file
echo "**SDK version:** ${SDK_VERSION:-current}" >> $log_file
echo "" >> $log_file
echo "**Server url:** $SERVER_URL" >> $log_file

echo -e "$devicelogs" | awk -F': ' '
    /SDKPerformanceAnalysisTime/ {
        time_rows = time_rows sprintf("| %-30s | %s |\n", $2, $3)
    }
    /SDKPerformanceAnalysisMemory/ {
        memory_rows = memory_rows sprintf("| %-30s | %s |\n", $2, $3)
    }
    END {
        if (time_rows != "") {
            print "### Time Metrics\n| Operation                      | Duration |\n|-------------------------------|----------|"
            printf "%s\n", time_rows
        }
        if (memory_rows != "") {
            print "\n### Memory Metrics\n| Operation                      | Usage |\n|-------------------------------|--------|"
            printf "%s\n", memory_rows
        }
    }
    ' >> $log_file