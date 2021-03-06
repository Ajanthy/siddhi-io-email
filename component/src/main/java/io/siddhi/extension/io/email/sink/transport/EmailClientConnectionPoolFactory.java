/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.siddhi.extension.io.email.sink.transport;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.wso2.transport.email.contract.EmailClientConnector;
import org.wso2.transport.email.contract.EmailConnectorFactory;
import org.wso2.transport.email.exception.EmailConnectorException;

import java.util.Map;

/**
 * The abstract class that needs to be implemented when supporting a new non-secure transport
 * to mainly create, validate and terminate  the client to the endpoint.
 */
public class EmailClientConnectionPoolFactory extends BaseKeyedPoolableObjectFactory {
    private EmailClientConnector emailClientConnector;

    public EmailClientConnectionPoolFactory(EmailConnectorFactory emailConnectorFactory,
                                            Map<String, String> clientProperties) throws EmailConnectorException {
        emailClientConnector = emailConnectorFactory.createEmailClientConnector();
        emailClientConnector.init(clientProperties);
    }

    @Override
    public Object makeObject(Object key) throws EmailConnectorException {
        if (!emailClientConnector.isConnected()) {
            emailClientConnector.connect();
        }
        return emailClientConnector;
    }

    @Override
    public boolean validateObject(Object key, Object obj) {
        return obj != null && ((EmailClientConnector) obj).isConnected();
    }

    public void destroyObject(Object key, Object obj) {
        if (obj != null) {
            ((EmailClientConnector) obj).disconnect();
        }
    }
}
