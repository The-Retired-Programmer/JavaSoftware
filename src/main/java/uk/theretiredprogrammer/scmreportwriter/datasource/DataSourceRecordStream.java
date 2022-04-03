/*
 * Copyright 2022 Richard Linsdale (richard at theretiredprogrammer.uk).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.scmreportwriter.datasource;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;

public abstract class DataSourceRecordStream {

    private final boolean success;

    public DataSourceRecordStream(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFailure() {
        return this.success == false;
    }

    public abstract Throwable getThrownMessage();

    public abstract Stream<DataSourceRecord> get();

    public abstract DataSourceRecordStream map(Function<DataSourceRecord, DataSourceRecord> mapper);

    public abstract DataSourceRecordStream sort(Comparator<DataSourceRecord> comparator);

    public abstract DataSourceRecordStream filter(Predicate<DataSourceRecord> predicate);

    public abstract DataSourceRecordStream filter(BooleanExpression expression);

    @SuppressWarnings("ThrowableResultIgnored")
    static DataSourceRecordStream failure(Throwable t) {
        Objects.requireNonNull(t);
        return new Failure(t);
    }

    static DataSourceRecordStream success(Stream<DataSourceRecord> value) {
        Objects.requireNonNull(value);
        return new Success(value);
    }

    static DataSourceRecordStream of(Supplier<Stream<DataSourceRecord>> streamsource) {
        Objects.requireNonNull(streamsource);
        try {
            return DataSourceRecordStream.success(streamsource.get());
        } catch (Throwable t) {
            return DataSourceRecordStream.failure(t);
        }
    }

    static DataSourceRecordStream of(Stream<DataSourceRecord> stream) {
        Objects.requireNonNull(stream);
        try {
            return DataSourceRecordStream.success(stream);
        } catch (Throwable t) {
            return DataSourceRecordStream.failure(t);
        }
    }

    static class Failure extends DataSourceRecordStream {

        private final Throwable t;

        public Failure(Throwable t) {
            super(false);
            this.t = t;
        }

        @Override
        public Stream<DataSourceRecord> get() {
            throw new IllegalStateException("Failure never has a value - so can't get()");
        }

        @Override
        public DataSourceRecordStream map(Function<DataSourceRecord, DataSourceRecord> mapper) {
            return DataSourceRecordStream.failure(this.t);
        }

        @Override
        public DataSourceRecordStream sort(Comparator<DataSourceRecord> comparator) {
            return DataSourceRecordStream.failure(this.t);
        }

        @Override
        public DataSourceRecordStream filter(Predicate<DataSourceRecord> predicate) {
            return DataSourceRecordStream.failure(this.t);
        }

        @Override
        public DataSourceRecordStream filter(BooleanExpression predicate) {
            return DataSourceRecordStream.failure(this.t);
        }

        @Override
        public Throwable getThrownMessage() {
            return this.t;
        }
    }

    static class Success extends DataSourceRecordStream {

        private final Stream<DataSourceRecord> value;

        public Success(Stream<DataSourceRecord> value) {
            super(true);
            this.value = value;
        }

        @Override
        public Stream<DataSourceRecord> get() {
            return this.value;
        }

        @Override
        public DataSourceRecordStream map(Function<DataSourceRecord, DataSourceRecord> mapper) {
            if (mapper == null) {
                return this;
            }
            try {
                return DataSourceRecordStream.success(this.value.map(mapper));
            } catch (Throwable t) {
                return DataSourceRecordStream.failure(t);
            }
        }

        @Override
        public DataSourceRecordStream sort(Comparator<DataSourceRecord> comparator) {
            if (comparator == null) {
                return this;
            }
            try {
                return DataSourceRecordStream.success(this.value.sorted(comparator));
            } catch (Throwable t) {
                return DataSourceRecordStream.failure(t);
            }
        }

        @Override
        public DataSourceRecordStream filter(Predicate<DataSourceRecord> predicate) {
            if (predicate == null) {
                return this;
            }
            try {
                return DataSourceRecordStream.success(this.value.filter(predicate));
            } catch (Throwable t) {
                return DataSourceRecordStream.failure(t);
            }
        }

        @Override
        public DataSourceRecordStream filter(BooleanExpression expression) {
            if (expression == null) {
                return this;
            }
            try {
                return DataSourceRecordStream.success(this.value.filter((dr) -> expression.evaluate(dr)));
            } catch (Throwable t) {
                return DataSourceRecordStream.failure(t);
            }
        }

        @Override
        public Throwable getThrownMessage() {
            throw new IllegalStateException("Success never has an exception - getThrownMessage()");
        }
    }
}
