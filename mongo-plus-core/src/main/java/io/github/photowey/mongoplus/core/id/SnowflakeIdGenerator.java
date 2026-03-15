/*
 * Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.photowey.mongoplus.core.id;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * SnowflakeIdGenerator - Twitter Snowflake ID implementation.
 *
 * <p>
 * Structure (64 bits):
 * - 1 bit: sign (always 0)
 * - 41 bits: timestamp (milliseconds since epoch)
 * - 10 bits: worker ID + data center ID
 * - 12 bits: sequence number
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class SnowflakeIdGenerator implements IdentifyGenerator {

    /**
     * Start timestamp (2024-01-01)
     */
    private static final long START_TIMESTAMP = 1704067200000L;

    /**
     * Worker ID bits
     */
    private static final long WORKER_ID_BITS = 5L;
    /**
     * Data center ID bits
     */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /**
     * Sequence bits
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * Max worker ID: 2^5 - 1 = 31
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * Max data center ID: 2^5 - 1 = 31
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    /**
     * Sequence mask: 2^12 - 1 = 4095
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * Worker ID shift
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * Data center ID shift
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * Timestamp shift
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * Data center ID
     */
    private final long dataCenterId;
    /**
     * Worker ID
     */
    private final long workerId;

    /**
     * Sequence number
     */
    private final AtomicLong sequence = new AtomicLong(0);
    /**
     * Last timestamp
     */
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);

    /**
     * Default constructor with auto-detected worker info
     */
    public SnowflakeIdGenerator() {
        this(getDataCenterId(), getWorkerId());
    }

    /**
     * Constructor with specified data center ID and worker ID
     *
     * @param dataCenterId data center ID (0-31)
     * @param workerId     worker ID (0-31)
     */
    public SnowflakeIdGenerator(long dataCenterId, long workerId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                "Data center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0"
            );
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                "Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0"
            );
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    @Override
    public Long nextId() {
        return generateId();
    }

    /**
     * Generate next ID
     *
     * @return ID
     */
    private synchronized Long generateId() {
        long timestamp = System.currentTimeMillis();
        long lastTs = this.lastTimestamp.get();

        // Clock moved backwards, throw exception
        if (timestamp < lastTs) {
            throw new RuntimeException(
                "Clock moved backwards. Refusing to generate ID for " + (lastTs - timestamp) + " milliseconds"
            );
        }

        long seq = this.sequence.get();

        // Same millisecond, increment sequence
        if (timestamp == lastTs) {
            seq = (seq + 1) & SEQUENCE_MASK;
            // Sequence overflow, wait for next millisecond
            if (seq == 0) {
                timestamp = tilNextMillis(lastTs);
            }
        } else {
            // Different millisecond, reset sequence
            seq = new Random().nextInt(10);
        }

        this.sequence.set(seq);
        this.lastTimestamp.set(timestamp);

        // Compose ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
            | (this.dataCenterId << DATA_CENTER_ID_SHIFT)
            | (this.workerId << WORKER_ID_SHIFT)
            | seq;
    }

    /**
     * Wait for next millisecond
     *
     * @param lastTimestamp last timestamp
     * @return current timestamp
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * Get data center ID from local IP address
     *
     * @return data center ID
     */
    private static long getDataCenterId() {
        try {
            InetAddress ip = getLocalHostAddress();
            byte[] bytes = ip.getAddress();
            return (bytes[bytes.length - 2] & 0xFF) % (MAX_DATA_CENTER_ID + 1);
        } catch (Exception e) {
            return new Random().nextInt((int) MAX_DATA_CENTER_ID + 1);
        }
    }

    /**
     * Get worker ID from local MAC address
     *
     * @return worker ID
     */
    private static long getWorkerId() {
        try {
            InetAddress ip = getLocalHostAddress();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (Objects.isNull(network)) {
                return new Random().nextInt((int) MAX_WORKER_ID + 1);
            }
            byte[] mac = network.getHardwareAddress();
            if (Arrays.isEmpty(mac)) {
                return new Random().nextInt((int) MAX_WORKER_ID + 1);
            }
            return ((mac[mac.length - 2] & 0xFF) << 8 | (mac[mac.length - 1] & 0xFF)) % (MAX_WORKER_ID + 1);
        } catch (Exception e) {
            return new Random().nextInt((int) MAX_WORKER_ID + 1);
        }
    }

    /**
     * Get local host address
     *
     * @return local InetAddress
     * @throws UnknownHostException if localhost cannot be resolved
     */
    private static InetAddress getLocalHostAddress() throws UnknownHostException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress() && addr.isSiteLocalAddress()) {
                        return addr;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return InetAddress.getLocalHost();
    }

    /**
     * Parse ID to extract timestamp
     *
     * @param id the generated ID
     * @return the timestamp when ID was generated
     */
    public static LocalDateTime parseTimestamp(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}
