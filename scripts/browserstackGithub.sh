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

source "$(dirname $0)/config_github.init"
source "$(dirname $0)/browserstackCommon.sh"

coverage_result_path="$(dirname $0)/../core/build/outputs/code_coverage"

# Build apks (skip if already built in CI)
if [ "$SKIP_BUILD" != "true" ]; then
  echo "Building APKs..."
  ./gradlew :core:assembleDebug
  ./gradlew :core:assembleDebugAndroidTest -Pcoverage
  ./gradlew :instrumented-test-app:assembleDebug
else
  echo "Skipping build - using pre-built APKs from artifacts"
fi

app_apk_path=$(findApkPath "instrumented-test-app" "debug")
test_apk_path=$(findApkPath "core" "androidTest/debug")

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
                --argjson package ["$browserstack_package"] \
                --arg logs "$browserstack_device_logs" \
                --arg video "$browserstack_video" \
                --arg loc "$browserstack_local" \
                --arg deviceLogs "$browserstack_deviceLogs" \
                --arg allowDeviceMockServer "$browserstack_mock_server" \
                --arg singleRunnerInvocation "$browserstack_singleRunnerInvocation" \
                --arg coverage "$browserstack_coverage" \
                '{devices: $devices, app: $app_url, testSuite: $test_url, package: $package, logs: $logs, video: $video, local: $loc, deviceLogs: $deviceLogs, allowDeviceMockServer: $allowDeviceMockServer, singleRunnerInvocation: $singleRunnerInvocation, coverage: $coverage}')

# Get build
build_id="$(execute_build "$json")"
echo "build id running: $build_id"

build_status_response="$(waitForBuildFinish $build_id)"

build_status="$(get_build_status "$build_status_response")"
build_session_id="$(get_build_session "$build_status_response")"

# Export test reports URL
test_reports_url="https://app-automate.browserstack.com/dashboard/v2/builds/$build_id"
echo "BrowserStack Dashboard: $test_reports_url"

# Add to GitHub Actions summary if available
if [ -n "$GITHUB_STEP_SUMMARY" ]; then
  echo "### BrowserStack Test Results" >> $GITHUB_STEP_SUMMARY
  echo "**Build ID:** $build_id" >> $GITHUB_STEP_SUMMARY
  echo "**Dashboard:** [$test_reports_url]($test_reports_url)" >> $GITHUB_STEP_SUMMARY
  echo "**Status:** $build_status" >> $GITHUB_STEP_SUMMARY
fi

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
