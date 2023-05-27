/*
 * Copyright 2023 Responsive Computing, Inc.
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

package dev.responsive.db;

import com.datastax.oss.driver.api.querybuilder.Literal;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.ColumnRelationBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.function.Function;
import org.apache.kafka.common.utils.Bytes;

public enum ColumnNames {

  // shared partition key column
  PARTITION_KEY("partitionKey", "pk"),

  // columns for the data tables
  DATA_KEY("key", "k", b -> bytes((Bytes) b)),
  DATA_VALUE("value", "v", b -> bytes((byte[]) b)),
  OFFSET("offset", "o"),
  PERMIT("permit", "p"),
  WINDOW_START("windowStart", "ws", ts -> timestamp((long) ts));

  private final String column;
  private final String bind;
  private final Function<Object, Literal> getLiteral;

  private static Literal bytes(final byte[] b) {
    return QueryBuilder.literal(ByteBuffer.wrap(b));
  }

  private static Literal bytes(final Bytes b) {
    return QueryBuilder.literal(ByteBuffer.wrap(b.get()));
  }

  private static Literal timestamp(final long ts) {
    return QueryBuilder.literal(Instant.ofEpochMilli(ts));
  }

  ColumnNames(final String column, final String bind) {
    this(column, bind, QueryBuilder::literal);
  }

  ColumnNames(final String column, final String bind, final Function<Object, Literal> getLiteral) {
    this.column = column;
    this.bind = bind;
    this.getLiteral = getLiteral;
  }

  public String column() {
    return column;
  }

  public String bind() {
    return bind;
  }

  public Literal literal(final Object value) {
    return getLiteral.apply(value);
  }

  public ColumnRelationBuilder<Relation> relation() {
    return Relation.column(column);
  }
}