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
package io.github.photowey.mongoplus.examples.spring.boot.b3x;

import java.util.concurrent.atomic.AtomicLong;

import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import io.github.photowey.mongoplus.core.util.Strings;

/**
 * AbstractMongoTest - Base class for MongoDB integration tests backed by Testcontainers.
 *
 * <p>The Spring application context is intentionally shared so different test
 * classes and methods can execute concurrently. Database isolation is achieved
 * by routing every test thread to a unique MongoDB database name.</p>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@Import(AbstractMongoTest.ParallelMongoTestConfiguration.class)
@ExtendWith(AbstractMongoTest.ParallelMongoDatabaseExtension.class)
public abstract class AbstractMongoTest {

    private static final String DEFAULT_DATABASE_NAME = "mongoplus_test_bootstrap";
    private static final String DEFAULT_MONGO_IMAGE_NAME = "mongo:5.0.0";
    private static final String MONGO_IMAGE_ENVIRONMENT_KEY = "MONGO_PLUS_TEST_MONGO_IMAGE";
    private static final String MONGO_IMAGE_PROPERTY_KEY = "mongo.plus.test.mongo.image";
    private static final AtomicLong DATABASE_SEQUENCE = new AtomicLong(0L);
    private static final ThreadLocal<String> CURRENT_DATABASE = new ThreadLocal<>();

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.data.mongodb.uri",
            SingletonMongoContainer::replicaSetUrl
        );
    }

    static String currentDatabaseName() {
        return CURRENT_DATABASE.get();
    }

    static void bindDatabaseName(String databaseName) {
        CURRENT_DATABASE.set(databaseName);
    }

    static void clearDatabaseName() {
        CURRENT_DATABASE.remove();
    }

    /**
     * JUnit extension that binds one unique MongoDB database name to each test method.
     *
     * @author photowey
     * @version 2026.1.0.0
     * @since 2026/03/11
     */
    public static class ParallelMongoDatabaseExtension implements BeforeEachCallback, AfterEachCallback {

        @Override
        public void beforeEach(ExtensionContext context) {
            AbstractMongoTest.bindDatabaseName(this.nextDatabaseName());
        }

        @Override
        public void afterEach(ExtensionContext context) {
            String databaseName = AbstractMongoTest.currentDatabaseName();
            if (Strings.isBlank(databaseName)) {
                AbstractMongoTest.clearDatabaseName();
                return;
            }

            try (MongoClient mongoClient = MongoClients.create(SingletonMongoContainer.replicaSetUrl())) {
                mongoClient.getDatabase(databaseName).drop();
            } finally {
                AbstractMongoTest.clearDatabaseName();
            }
        }

        private String nextDatabaseName() {
            long sequence = AbstractMongoTest.DATABASE_SEQUENCE.incrementAndGet();
            return "mongoplus_test_" + sequence;
        }
    }

    private static final class SingletonMongoContainer {

        private static final MongoDBContainer MONGO = startMongoContainer();
        private static final String REPLICA_SET_URL = MONGO.getReplicaSetUrl(DEFAULT_DATABASE_NAME);

        private SingletonMongoContainer() {
        }

        private static String replicaSetUrl() {
            return REPLICA_SET_URL;
        }

        private static MongoDBContainer startMongoContainer() {
            String mongoImageName = resolveMongoImageName();
            MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse(mongoImageName));
            mongoContainer.start();
            return mongoContainer;
        }

        private static String resolveMongoImageName() {
            String environmentImageName = System.getenv(MONGO_IMAGE_ENVIRONMENT_KEY);
            if (Strings.isNotBlank(environmentImageName)) {
                return environmentImageName;
            }

            String propertyImageName = System.getProperty(MONGO_IMAGE_PROPERTY_KEY);
            if (Strings.isNotBlank(propertyImageName)) {
                return propertyImageName;
            }

            return DEFAULT_MONGO_IMAGE_NAME;
        }
    }

    /**
     * Test configuration that exposes a MongoTemplate bound to the current test database.
     *
     * @author photowey
     * @version 2026.1.0.0
     * @since 2026/03/11
     */
    @TestConfiguration(proxyBeanMethods = false)
    public static class ParallelMongoTestConfiguration {

        @Bean
        @Primary
        public MongoTemplate parallelMongoTemplate(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoConverter mongoConverter
        ) {
            MongoDatabaseFactory routingFactory = new ThreadLocalMongoDatabaseFactory(mongoDatabaseFactory);
            return new MongoTemplate(routingFactory, mongoConverter);
        }
    }

    /**
     * MongoDatabaseFactory that routes calls to the database assigned to the current test thread.
     *
     * @author photowey
     * @version 2026.1.0.0
     * @since 2026/03/11
     */
    public static final class ThreadLocalMongoDatabaseFactory implements MongoDatabaseFactory {

        private final MongoDatabaseFactory delegate;

        public ThreadLocalMongoDatabaseFactory(MongoDatabaseFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public MongoDatabase getMongoDatabase() throws DataAccessException {
            return this.delegate.getMongoDatabase(this.resolveDatabaseName(DEFAULT_DATABASE_NAME));
        }

        @Override
        public MongoDatabase getMongoDatabase(String databaseName) throws DataAccessException {
            return this.delegate.getMongoDatabase(this.resolveDatabaseName(databaseName));
        }

        @Override
        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.delegate.getExceptionTranslator();
        }

        @Override
        public CodecRegistry getCodecRegistry() {
            return this.delegate.getCodecRegistry();
        }

        @Override
        public ClientSession getSession(ClientSessionOptions options) {
            return this.delegate.getSession(options);
        }

        @Override
        public MongoDatabaseFactory withSession(ClientSessionOptions options) {
            MongoDatabaseFactory factory = this.delegate.withSession(options);
            return new ThreadLocalMongoDatabaseFactory(factory);
        }

        @Override
        public MongoDatabaseFactory withSession(ClientSession session) {
            MongoDatabaseFactory factory = this.delegate.withSession(session);
            return new ThreadLocalMongoDatabaseFactory(factory);
        }

        @Override
        public boolean isTransactionActive() {
            return this.delegate.isTransactionActive();
        }

        private String resolveDatabaseName(String databaseName) {
            String currentDatabaseName = AbstractMongoTest.currentDatabaseName();
            return Strings.isNotBlank(currentDatabaseName)
                ? currentDatabaseName
                : databaseName;
        }
    }
}
