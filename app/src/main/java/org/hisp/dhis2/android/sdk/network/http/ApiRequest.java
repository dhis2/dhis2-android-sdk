package org.hisp.dhis2.android.sdk.network.http;


import org.hisp.dhis2.android.sdk.network.managers.IHttpManager;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.io.IOException;

public final class ApiRequest<T> {
    private final Request request;
    private final IHttpManager networkManager;
    private final ApiRequestCallback<T> taskCallback;

    private ApiRequest(Builder<T> builder) {
        request = builder.getRequest();
        networkManager = builder.getNetworkManager();
        taskCallback = builder.getCallback();
    }

    private static boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    public ApiRequestCallback<T> getTaskCallback() {
        return taskCallback;
    }

    public IHttpManager getNetworkManager() {
        return networkManager;
    }

    public Request getRequest() {
        return request;
    }

    public void request() {
        Response response = null;

        try {
            response = networkManager.request(request);
        } catch (IOException networkException) {
            taskCallback.onFailure(APIException.networkError(request.getUrl(),
                    networkException));
            return;
        } catch (Exception unknownException) {
            taskCallback.onFailure(APIException.unexpectedError(request.getUrl(),
                    unknownException));
            return;
        }

        if (response == null) {
            taskCallback.onFailure(APIException.unexpectedError(request.getUrl(),
                    new RuntimeException("Response cannot be null")));
            return;
        }

        if (!isSuccessful(response.getStatus())) {
            taskCallback.onFailure(APIException.httpError(request.getUrl(),
                    response));
            return;
        }

        try {
            String responseBody = new String(response.getBody());
        } catch (Exception conversionException) {
            taskCallback.onFailure(APIException.conversionError(request.getUrl(),
                    response, conversionException));
            return;
        }

        taskCallback.onSuccess(response);
    }

    public static class Builder<BuilderType> {
        private Request mRequest;
        private IHttpManager mNetworkManager;
        private ApiRequestCallback<BuilderType> mCallback;

        public Builder setRequestCallback(ApiRequestCallback<BuilderType> callback) {
            mCallback = callback;
            return this;
        }

        public Request getRequest() {
            return mRequest;
        }

        public Builder setRequest(Request request) {
            mRequest = request;
            return this;
        }

        public IHttpManager getNetworkManager() {
            return mNetworkManager;
        }

        public Builder setNetworkManager(IHttpManager networkManager) {
            mNetworkManager = networkManager;
            return this;
        }

        public ApiRequestCallback<BuilderType> getCallback() {
            return mCallback;
        }

        public ApiRequest<BuilderType> build() {
            if (mRequest == null) {
                throw new IllegalArgumentException("Request cannot be null");
            }

            if (mCallback == null) {
                throw new IllegalArgumentException(("TaskCallback cannot be null"));
            }

            if (mNetworkManager == null) {
                throw new IllegalArgumentException(("NetworkManager cannot be null"));
            }

            return new ApiRequest<BuilderType>(this);
        }
    }
}
