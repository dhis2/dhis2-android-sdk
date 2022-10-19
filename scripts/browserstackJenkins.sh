#!/bin/bash
#
#  Copyright (c) 2004-2022, University of Oslo
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

bs_automate_url="https://api-cloud.browserstack.com/app-automate"
bs_auth="$BROWSERSTACK_USR:$BROWSERSTACK_PSW"

coverage_result_path="$(dirname $0)/../core/build/outputs/code_coverage"

function get_build_info() {
  local response
  local build_id=$1
  response="$(curl -u "$bs_auth" -X GET "$bs_automate_url/espresso/builds/$build_id")"
  echo "$response"
}

function get_build_status() {
  local response_json=$1
  echo "$response_json" | jq -r .status
}

function get_build_session() {
  local response_json=$1
  echo "$response_json" | jq -r ".devices.$browserstack_device_list.session_id"
}

function findApkPath() {
  local project=$1
  find "$(dirname $0)"/../"$project"/build/outputs/apk/ -iname "*.apk"
}

app_apk_path=$(findApkPath "core")
test_apk_path=$(findApkPath "instrumented-test-app")

# Build apks
./gradlew :core:assembleDebug
./gradlew :core:assembleDebugAndroidTest -Pcoverage
./gradlew :instrumented-test-app:assembleDebug

# Upload app and testing apk
echo "Uploading app APK to Browserstack..."
upload_app_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/upload -F file=@$app_apk_path)"
app_url=$(echo "$upload_app_response" | jq .app_url)

echo "Uploading test APK to Browserstack..."
upload_test_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/espresso/test-suite -F file=@$test_apk_path)"
test_url=$(echo "$upload_test_response" | jq .test_url)

# Prepare json and run tests
echo "Starting execution of espresso tests..."

json=$(jq -n \
                --argjson app_url $app_url \
                --argjson test_url $test_url \
                --argjson devices ["$browserstack_device_list"] \
                --argjson package ["$browserstack_package"] \
                --arg logs "$browserstack_device_logs" \
                --arg video "$browserstack_video" \
                --arg loc "$browserstack_local" \
                --arg deviceLogs "$browserstack_deviceLogs" \
                --arg allowDeviceMockServer "$browserstack_mock_server" \
                --arg singleRunnerInvocation "$browserstack_singleRunnerInvocation" \
                --arg coverage "$browserstack_coverage" \
                '{devices: $devices, app: $app_url, testSuite: $test_url, package: $package, logs: $logs, video: $video, local: $loc, deviceLogs: $deviceLogs, allowDeviceMockServer: $allowDeviceMockServer, singleRunnerInvocation: $singleRunnerInvocation, coverage: $coverage}')

test_execution_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/espresso/v2/build -d \ "$json" -H "Content-Type: application/json")"

# Get build
build_id=$(echo "$test_execution_response" | jq -r .build_id)

build_status_response="$(get_build_info "$build_id")"
build_status="$(get_build_status "$build_status_response")"
build_session_id="$(get_build_session "$build_status_response")"

echo "build id running: $build_id"
echo "session id: $build_session_id"

# Monitor build status
sleep $build_time_average
echo "Monitoring build status started...."

while [[ $build_status = "running" ]];
do
  # Get build status
  build_status_response="$(get_build_info "$build_id")"
  build_status="$(get_build_status "$build_status_response")"
  echo "current build status: $build_status"

  # Sleep until next poll
  sleep $polling_interval
done

# Export test reports to bitrise
test_reports_url="https://app-automate.browserstack.com/dashboard/v2/builds/$build_id"

# Download coverage report
mkdir -p "$coverage_result_path"
curl -u "$bs_auth" -X GET "$bs_automate_url/espresso/v2/builds/$build_id/sessions/$build_session_id/coverage" --output "$coverage_result_path/coverage.ec"

# weird behavior from Browserstack api, you can have "done" status with failed tests
# "devices" only show one device result which is inconsistance
# then "device_status" is checked
if [[ $build_status = "failed" || $build_status = "error" ]];
then
	echo "Browserstack build failed, please check the execution of your tests $test_reports_url"
  exit 1
else
  device_status=$(echo "$build_status_response" | jq -r '.device_statuses.error | to_entries[].value')
  if [[ $device_status = "Failed" ]]; # for this Failed Browserstack used bloq mayus
  then
	  echo "Browserstack build failed, please check the execution of your tests $test_reports_url"
    exit 1
  else
  	echo "Browserstack build passed, please check the execution of your tests $test_reports_url"
    exit 0
  fi
fi