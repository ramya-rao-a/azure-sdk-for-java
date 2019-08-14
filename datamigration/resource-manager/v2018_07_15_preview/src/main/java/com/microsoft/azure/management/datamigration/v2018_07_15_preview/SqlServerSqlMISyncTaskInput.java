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
 * Input for task that migrates SQL Server databases to Azure SQL Database
 * Managed Instance online scenario.
 */
public class SqlServerSqlMISyncTaskInput {
    /**
     * Databases to migrate.
     */
    @JsonProperty(value = "selectedDatabases", required = true)
    private List<MigrateSqlServerSqlMIDatabaseInput> selectedDatabases;

    /**
     * Backup file share information for all selected databases.
     */
    @JsonProperty(value = "backupFileShare")
    private FileShare backupFileShare;

    /**
     * Fully qualified resourceId of storage.
     */
    @JsonProperty(value = "storageResourceId", required = true)
    private String storageResourceId;

    /**
     * Connection information for source SQL Server.
     */
    @JsonProperty(value = "sourceConnectionInfo", required = true)
    private SqlConnectionInfo sourceConnectionInfo;

    /**
     * Connection information for Azure SQL Database Managed Instance.
     */
    @JsonProperty(value = "targetConnectionInfo", required = true)
    private MiSqlConnectionInfo targetConnectionInfo;

    /**
     * Azure Active Directory Application the DMS instance will use to connect
     * to the target instance of Azure SQL Database Managed Instance and the
     * Azure Storage Account.
     */
    @JsonProperty(value = "azureApp", required = true)
    private AzureActiveDirectoryApp azureApp;

    /**
     * Get databases to migrate.
     *
     * @return the selectedDatabases value
     */
    public List<MigrateSqlServerSqlMIDatabaseInput> selectedDatabases() {
        return this.selectedDatabases;
    }

    /**
     * Set databases to migrate.
     *
     * @param selectedDatabases the selectedDatabases value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withSelectedDatabases(List<MigrateSqlServerSqlMIDatabaseInput> selectedDatabases) {
        this.selectedDatabases = selectedDatabases;
        return this;
    }

    /**
     * Get backup file share information for all selected databases.
     *
     * @return the backupFileShare value
     */
    public FileShare backupFileShare() {
        return this.backupFileShare;
    }

    /**
     * Set backup file share information for all selected databases.
     *
     * @param backupFileShare the backupFileShare value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withBackupFileShare(FileShare backupFileShare) {
        this.backupFileShare = backupFileShare;
        return this;
    }

    /**
     * Get fully qualified resourceId of storage.
     *
     * @return the storageResourceId value
     */
    public String storageResourceId() {
        return this.storageResourceId;
    }

    /**
     * Set fully qualified resourceId of storage.
     *
     * @param storageResourceId the storageResourceId value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withStorageResourceId(String storageResourceId) {
        this.storageResourceId = storageResourceId;
        return this;
    }

    /**
     * Get connection information for source SQL Server.
     *
     * @return the sourceConnectionInfo value
     */
    public SqlConnectionInfo sourceConnectionInfo() {
        return this.sourceConnectionInfo;
    }

    /**
     * Set connection information for source SQL Server.
     *
     * @param sourceConnectionInfo the sourceConnectionInfo value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withSourceConnectionInfo(SqlConnectionInfo sourceConnectionInfo) {
        this.sourceConnectionInfo = sourceConnectionInfo;
        return this;
    }

    /**
     * Get connection information for Azure SQL Database Managed Instance.
     *
     * @return the targetConnectionInfo value
     */
    public MiSqlConnectionInfo targetConnectionInfo() {
        return this.targetConnectionInfo;
    }

    /**
     * Set connection information for Azure SQL Database Managed Instance.
     *
     * @param targetConnectionInfo the targetConnectionInfo value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withTargetConnectionInfo(MiSqlConnectionInfo targetConnectionInfo) {
        this.targetConnectionInfo = targetConnectionInfo;
        return this;
    }

    /**
     * Get azure Active Directory Application the DMS instance will use to connect to the target instance of Azure SQL Database Managed Instance and the Azure Storage Account.
     *
     * @return the azureApp value
     */
    public AzureActiveDirectoryApp azureApp() {
        return this.azureApp;
    }

    /**
     * Set azure Active Directory Application the DMS instance will use to connect to the target instance of Azure SQL Database Managed Instance and the Azure Storage Account.
     *
     * @param azureApp the azureApp value to set
     * @return the SqlServerSqlMISyncTaskInput object itself.
     */
    public SqlServerSqlMISyncTaskInput withAzureApp(AzureActiveDirectoryApp azureApp) {
        this.azureApp = azureApp;
        return this;
    }

}
