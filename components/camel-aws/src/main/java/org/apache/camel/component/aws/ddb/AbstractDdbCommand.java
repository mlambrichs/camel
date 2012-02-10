/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.ddb;

import java.util.Collection;
import java.util.Map;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodb.model.Key;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDdbCommand {
    protected DdbConfiguration configuration;
    protected Exchange exchange;
    protected AmazonDynamoDB ddbClient;

    public AbstractDdbCommand(AmazonDynamoDB ddbClient,
                              DdbConfiguration configuration, Exchange exchange) {

        this.ddbClient = ddbClient;
        this.configuration = configuration;
        this.exchange = exchange;
    }


    public abstract void execute();

    protected Message getMessageForResponse(Exchange exchange) {
        if (exchange.getPattern().isOutCapable()) {
            Message out = exchange.getOut();
            out.copyFrom(exchange.getIn());
            return out;
        }
        return exchange.getIn();
    }

    protected String determineTableName() {
        String tableName = exchange.getIn().getHeader(DdbConstants.TABLE_NAME, String.class);
        return tableName != null ? tableName : configuration.getTableName();
    }

    protected Map<String, ExpectedAttributeValue> determineUpdateCondition() {
        return exchange.getIn().getHeader(DdbConstants.UPDATE_CONDITION, Map.class);
    }

    protected Map<String, AttributeValue> determineItem() {
        return exchange.getIn().getHeader(DdbConstants.ITEM, Map.class);
    }

    protected String determineReturnValues() {
        return exchange.getIn().getHeader(DdbConstants.RETURN_VALUES, String.class);
    }

    protected void addAttributesToResult(Map<String, AttributeValue> attributes) {
        Message msg = getMessageForResponse(exchange);
        msg.setHeader(DdbConstants.ATTRIBUTES, attributes);
    }
    
    protected void addToResult(String headerKey, Object value) {
        Message msg = getMessageForResponse(exchange);
        msg.setHeader(headerKey, value);
    }

    protected Key determineKey() {
        return exchange.getIn().getHeader(DdbConstants.KEY, Key.class);
    }

    protected Collection<String> determineAttributeNames() {
        return exchange.getIn().getHeader(DdbConstants.ATTRIBUTE_NAMES, Collection.class);
    }

    protected Boolean determineConsistentRead() {
        Boolean consistentRead = exchange.getIn().getHeader(DdbConstants.CONSISTENT_READ, Boolean.class);
        return consistentRead != null ? consistentRead : configuration.getConsistentRead();
    }
}