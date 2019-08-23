// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.eventprocessorhost;

import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.ProxyConfiguration;
import com.microsoft.azure.eventhubs.TransportType;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CheckpointManagerTest extends TestBase {
    private static final int PARTITION_COUNT = 8;

    private final boolean useAzureStorage;
    private final boolean useProxy;

    private ILeaseManager[] leaseManagers;
    private ICheckpointManager[] checkpointManagers;
    private EventProcessorHost[] hosts;
    private ProxyConfiguration proxyConfiguration;
    private String azureStorageConnectionString;

    public CheckpointManagerTest(boolean useAzureStorage, boolean useProxy) {
        this.useAzureStorage = useAzureStorage;
        this.useProxy = useProxy;
    }

    @Parameterized.Parameters(name = "{index}: useAzureStorage={0};useProxy{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {true, true},
            {true, false},
            {false, true},
            {false, false},
        });
    }

    @Before
    public void setup() {
        proxyConfiguration = TestUtilities.getProxyConfiguration();
        azureStorageConnectionString = TestUtilities.getStorageConnectionString();

        Assume.assumeTrue("Cannot test with proxy. Environment variable not set.",
            !useProxy || proxyConfiguration != null);
        Assume.assumeTrue("Cannot test with Azure storage. Environment variable not set.",
            !useAzureStorage || azureStorageConnectionString != null);
    }

    @Test
    public void singleManagerSmokeTest() throws Exception {
        singleManagerCheckpointSmokeTest(useAzureStorage, PARTITION_COUNT, useProxy);
    }

    @Test
    public void twoCheckpointManagersSmokeTest() throws Exception {
        twoManagerCheckpointSmokeTest(useAzureStorage, PARTITION_COUNT, useProxy);
    }

    private void singleManagerCheckpointSmokeTest(boolean useAzureStorage, int partitionCount, boolean useProxy) throws Exception {
        this.leaseManagers = new ILeaseManager[1];
        this.checkpointManagers = new ICheckpointManager[1];
        this.hosts = new EventProcessorHost[1];
        setupOneManager(useAzureStorage, 0, "0", generateContainerName("0"), useProxy);

        TestBase.logInfo("Check whether checkpoint store exists before create");
        boolean boolret = this.checkpointManagers[0].checkpointStoreExists().get();
        assertFalse("checkpoint store should not exist yet", boolret);

        TestBase.logInfo("Create checkpoint store");
        if (useAzureStorage) {
            // Storage implementation optimizes checkpoint store creation to a no-op. Have to call lease manager
            // to actually create.
            this.leaseManagers[0].createLeaseStoreIfNotExists().get();
        } else {
            this.checkpointManagers[0].createCheckpointStoreIfNotExists().get();
        }

        TestBase.logInfo("Check whether checkpoint store exists after create");
        boolret = this.checkpointManagers[0].checkpointStoreExists().get();
        assertTrue("checkpoint store should exist but does not", boolret);

        ArrayList<String> partitionIds = new ArrayList<String>();
        for (int i = 0; i < partitionCount; i++) {
            partitionIds.add(String.valueOf(i));
        }
        TestBase.logInfo("Create checkpoint holders for all partitions");
        if (useAzureStorage) {
            // Storage implementation optimizes checkpoint creation to a no-op. Have to create the leases instead.
            this.leaseManagers[0].createAllLeasesIfNotExists(partitionIds);
        } else {
            this.checkpointManagers[0].createAllCheckpointsIfNotExists(partitionIds);
        }

        TestBase.logInfo("Trying to get checkpoints for all partitions");
        for (int i = 0; i < partitionCount; i++) {
            Checkpoint blah = this.checkpointManagers[0].getCheckpoint(String.valueOf(i)).get();
            assertNull("unexpectedly successful retrieve checkpoint for " + i, blah);
        }

        // AzureStorageCheckpointLeaseManager tries to pretend that checkpoints and leases are separate, but they really aren't.
        // Because the checkpoint data is stored in the lease, updating the checkpoint means updating the lease, and it is
        // necessary to hold the lease in order to update it.
        HashMap<String, CompleteLease> leases = new HashMap<String, CompleteLease>();
        if (useAzureStorage) {
            for (int i = 0; i < partitionCount; i++) {
                CompleteLease l = this.leaseManagers[0].getLease(partitionIds.get(i)).get();
                assertNotNull("null lease for " + partitionIds.get(i), l);
                leases.put(l.getPartitionId(), l);
                boolret = this.leaseManagers[0].acquireLease(l).get();
                assertTrue("failed to acquire lease for " + l.getPartitionId(), boolret);
            }
        }

        Checkpoint[] checkpoints = new Checkpoint[partitionCount];
        TestBase.logInfo("Creating checkpoints for all partitions");
        for (int i = 0; i < partitionCount; i++) {
            // Arbitrary values, just checking that they are persisted
            checkpoints[i] = new Checkpoint(String.valueOf(i));
            checkpoints[i].setOffset(String.valueOf(i * 234));
            checkpoints[i].setSequenceNumber(i + 77);
            this.checkpointManagers[0].updateCheckpoint(leases.get(String.valueOf(i)), checkpoints[i]).get();
        }

        TestBase.logInfo("Getting checkpoints for all partitions and verifying");
        for (int i = 0; i < partitionCount; i++) {
            Checkpoint blah = this.checkpointManagers[0].getCheckpoint(String.valueOf(i)).get();
            assertNotNull("failed to retrieve checkpoint for " + i, blah);
            assertEquals("retrieved offset does not match written offset", blah.getOffset(), checkpoints[i].getOffset());
            assertEquals("retrieved seqno does not match written seqno", blah.getSequenceNumber(), checkpoints[i].getSequenceNumber());
        }

        // Have to release the leases before we can delete the store.
        if (useAzureStorage) {
            for (CompleteLease l : leases.values()) {
                this.leaseManagers[0].releaseLease(l).get();
            }
        }

        TestBase.logInfo("Cleaning up checkpoint store");
        this.checkpointManagers[0].deleteCheckpointStore().get();
    }

    private void twoManagerCheckpointSmokeTest(boolean useAzureStorage, int partitionCount, boolean withProxy) throws Exception {
        this.leaseManagers = new ILeaseManager[2];
        this.checkpointManagers = new ICheckpointManager[2];
        this.hosts = new EventProcessorHost[2];
        String containerName = generateContainerName(null);
        setupOneManager(useAzureStorage, 0, "twoCheckpoint", containerName, withProxy);
        setupOneManager(useAzureStorage, 1, "twoCheckpoint", containerName, withProxy);

        TestBase.logInfo("Check whether checkpoint store exists before create");
        boolean boolret = this.checkpointManagers[0].checkpointStoreExists().get();
        assertFalse("checkpoint store should not exist yet", boolret);

        TestBase.logInfo("Second manager create checkpoint store");
        if (useAzureStorage) {
            // Storage implementation optimizes checkpoint store creation to a no-op. Have to call lease manager
            // to actually create.
            this.leaseManagers[1].createLeaseStoreIfNotExists().get();
        } else {
            this.checkpointManagers[1].createCheckpointStoreIfNotExists().get();
        }

        TestBase.logInfo("First mananger check whether checkpoint store exists after create");
        boolret = this.checkpointManagers[0].checkpointStoreExists().get();
        assertTrue("checkpoint store should exist but does not", boolret);

        ArrayList<String> partitionIds = new ArrayList<String>();
        for (int i = 0; i < partitionCount; i++) {
            partitionIds.add(String.valueOf(i));
        }
        TestBase.logInfo("Create checkpoint holders for all partitions");
        if (useAzureStorage) {
            // Storage implementation optimizes checkpoint creation to a no-op. Have to create the leases instead.
            this.leaseManagers[0].createAllLeasesIfNotExists(partitionIds);
        } else {
            this.checkpointManagers[0].createAllCheckpointsIfNotExists(partitionIds);
        }

        TestBase.logInfo("Try to get each others checkpoints for all partitions");
        for (int i = 0; i < partitionCount; i++) {
            Checkpoint blah = this.checkpointManagers[(i + 1) % 2].getCheckpoint(String.valueOf(i)).get();
            assertNull("unexpected successful retrieve checkpoint for " + i, blah);
        }

        // AzureStorageCheckpointLeaseManager tries to pretend that checkpoints and leases are separate, but they really aren't.
        // Because the checkpoint data is stored in the lease, updating the checkpoint means updating the lease, and it is
        // necessary to hold the lease in order to update it.
        HashMap<String, CompleteLease> leases = new HashMap<String, CompleteLease>();
        if (useAzureStorage) {
            for (int i = 0; i < partitionCount; i++) {
                CompleteLease l = this.leaseManagers[1].getLease(partitionIds.get(i)).get();
                leases.put(l.getPartitionId(), l);
                boolret = this.leaseManagers[1].acquireLease(l).get();
                assertTrue("failed to acquire lease for " + l.getPartitionId(), boolret);
            }
        }

        Checkpoint[] checkpoints = new Checkpoint[partitionCount];
        TestBase.logInfo("Second manager update checkpoints for all partitions");
        for (int i = 0; i < partitionCount; i++) {
            // Arbitrary values, just checking that they are persisted
            checkpoints[i] = new Checkpoint(String.valueOf(i));
            checkpoints[i].setOffset(String.valueOf(i * 234));
            checkpoints[i].setSequenceNumber(i + 77);
            this.checkpointManagers[1].updateCheckpoint(leases.get(String.valueOf(i)), checkpoints[i]).get();
        }

        TestBase.logInfo("First manager get and verify checkpoints for all partitions");
        for (int i = 0; i < partitionCount; i++) {
            Checkpoint blah = this.checkpointManagers[0].getCheckpoint(String.valueOf(i)).get();
            assertNotNull("failed to retrieve checkpoint for " + i, blah);
            assertEquals("retrieved offset does not match written offset", blah.getOffset(), checkpoints[i].getOffset());
            assertEquals("retrieved seqno does not match written seqno", blah.getSequenceNumber(), checkpoints[i].getSequenceNumber());
        }

        // Have to release the leases before we can delete the store.
        if (useAzureStorage) {
            for (CompleteLease l : leases.values()) {
                assertNotNull("failed to retrieve lease", l);
                this.leaseManagers[1].releaseLease(l).get();
            }
        }

        TestBase.logInfo("Clean up checkpoint store");
        this.checkpointManagers[0].deleteCheckpointStore().get();
    }

    private String generateContainerName(String infix) {
        StringBuilder containerName = new StringBuilder(64);
        containerName.append("ckptmgrtest-");
        if (infix != null) {
            containerName.append(infix);
            containerName.append('-');
        }
        containerName.append(UUID.randomUUID().toString());
        return containerName.toString();
    }

    private void setupOneManager(boolean useAzureStorage, int index, String suffix, String containerName,
                                 boolean useProxy) throws Exception {
        ILeaseManager leaseMgr = null;
        ICheckpointManager checkpointMgr = null;

        if (!useAzureStorage) {
            leaseMgr = new InMemoryLeaseManager();
            checkpointMgr = new InMemoryCheckpointManager();
        } else {
            TestBase.logInfo("Container name: " + containerName);
            AzureStorageCheckpointLeaseManager azMgr = new AzureStorageCheckpointLeaseManager(azureStorageConnectionString, containerName, null);

            if (useProxy) {
                azMgr.setProxyConfiguration(proxyConfiguration);
            }

            leaseMgr = azMgr;
            checkpointMgr = azMgr;
        }

        TransportType transportType = TransportType.AMQP;
        ProxyConfiguration configuration = null;

        if (useProxy) {
            transportType = TransportType.AMQP_WEB_SOCKETS;
            configuration = proxyConfiguration;
        }

        // Host name needs to be unique per host so use index. Event hub should be the same for all hosts in a test, so use the supplied suffix.
        EventProcessorHost host = EventProcessorHost.EventProcessorHostBuilder.newBuilder("dummyHost" + index, EventHubClient.DEFAULT_CONSUMER_GROUP_NAME)
            .useUserCheckpointAndLeaseManagers(checkpointMgr, leaseMgr)
            .useEventHubConnectionString(RealEventHubUtilities.SYNTACTICALLY_CORRECT_DUMMY_CONNECTION_STRING + suffix,
                RealEventHubUtilities.SYNTACTICALLY_CORRECT_DUMMY_EVENT_HUB_PATH + suffix)
            .setProxyConfiguration(configuration)
            .setTransportType(transportType)
            .build();

        try {
            if (!useAzureStorage) {
                ((InMemoryLeaseManager) leaseMgr).initialize(host.getHostContext());
                ((InMemoryCheckpointManager) checkpointMgr).initialize(host.getHostContext());
            } else {
                ((AzureStorageCheckpointLeaseManager) checkpointMgr).initialize(host.getHostContext());
            }
        } catch (Exception e) {
            TestBase.logError("Manager initializion failed");
            throw e;
        }

        this.leaseManagers[index] = leaseMgr;
        this.checkpointManagers[index] = checkpointMgr;
        this.hosts[index] = host;
    }
}
