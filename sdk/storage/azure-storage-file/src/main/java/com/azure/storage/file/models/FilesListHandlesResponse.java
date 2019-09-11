// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.storage.file.models;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.rest.ResponseBase;
import java.util.Map;

/**
 * Contains all response data for the listHandles operation.
 */
public final class FilesListHandlesResponse extends ResponseBase<FileListHandlesHeaders, ListHandlesResponse> {
    /**
     * Creates an instance of FilesListHandlesResponse.
     *
     * @param request the request which resulted in this FilesListHandlesResponse.
     * @param statusCode the status code of the HTTP response.
     * @param rawHeaders the raw headers of the HTTP response.
     * @param value the deserialized value of the HTTP response.
     * @param headers the deserialized headers of the HTTP response.
     */
    public FilesListHandlesResponse(HttpRequest request, int statusCode, HttpHeaders rawHeaders, ListHandlesResponse value, FileListHandlesHeaders headers) {
        super(request, statusCode, rawHeaders, value, headers);
    }

    /**
     * @return the deserialized response body.
     */
    @Override
    public ListHandlesResponse value() {
        return super.value();
    }
}
