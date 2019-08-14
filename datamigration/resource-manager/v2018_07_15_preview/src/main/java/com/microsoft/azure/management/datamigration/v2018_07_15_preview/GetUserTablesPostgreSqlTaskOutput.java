/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.datamigration.v2018_07_15_preview;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Output for the task that gets the list of tables for a provided list of
 * PostgreSQL databases.
 */
public class GetUserTablesPostgreSqlTaskOutput {
    /**
     * The database this result is for.
     */
    @JsonProperty(value = "databaseName", access = JsonProperty.Access.WRITE_ONLY)
    private String databaseName;

    /**
     * List of valid tables found for this database.
     */
    @JsonProperty(value = "tables", access = JsonProperty.Access.WRITE_ONLY)
    private List<DatabaseTable> tables;

    /**
     * Validation errors associated with the task.
     */
    @JsonProperty(value = "validationErrors", access = JsonProperty.Access.WRITE_ONLY)
    private List<ReportableException> validationErrors;

    /**
     * Get the database this result is for.
     *
     * @return the databaseName value
     */
    public String databaseName() {
        return this.databaseName;
    }

    /**
     * Get list of valid tables found for this database.
     *
     * @return the tables value
     */
    public List<DatabaseTable> tables() {
        return this.tables;
    }

    /**
     * Get validation errors associated with the task.
     *
     * @return the validationErrors value
     */
    public List<ReportableException> validationErrors() {
        return this.validationErrors;
    }

}
