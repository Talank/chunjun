/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.flinkx.websocket.reader;

import com.dtstack.flinkx.constants.ConstantValue;
import com.dtstack.flinkx.inputformat.BaseRichInputFormatBuilder;
import com.dtstack.flinkx.util.ExceptionUtil;
import com.dtstack.flinkx.util.TelnetUtil;
import com.dtstack.flinkx.websocket.format.WebSocketInputFormat;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** 构建 WebSocketInputFormat
 * @Company: www.dtstack.com
 * @author kunni@dtstack.com
 */

public class WebSocketInputFormatBuilder extends BaseRichInputFormatBuilder {

    private WebSocketInputFormat format;

    private String serverUrl;

    /**
     * webSocket url前缀
     */
    private static final String WEB_SOCKET_PREFIX = "ws";

    public WebSocketInputFormatBuilder(){
        super.format = format = new WebSocketInputFormat();
    }


    protected void setServerUrl(String serverUrl, Map<String, String> params){
        // 在url的基础上加上授权认证
        if(MapUtils.isNotEmpty(params)){
            StringBuilder stringBuilder = new StringBuilder(30);
            stringBuilder.append('?');
            Set<Map.Entry<String, String>> set = params.entrySet();
            Iterator<Map.Entry<String, String>> iterator = set.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> entry = iterator.next();
                stringBuilder.append(entry.getKey())
                        .append(ConstantValue.EQUAL_SYMBOL)
                        .append(entry.getValue());
                if(iterator.hasNext()){
                    stringBuilder.append('&');
                }
            }
            serverUrl += stringBuilder.toString();
        }
        this.serverUrl = serverUrl;
        format.setServerUrl(serverUrl);
    }

    protected void setRetryTime(int retryTime){
        format.setRetryTime(retryTime);
    }

    protected void setRetryInterval(int retryInterval){
        format.setRetryInterval(retryInterval);
    }

    protected void setMessage(String message) {
        format.setMessage(message);
    }

    protected void setCodec(String codec){
        format.setCodec(codec);
    }

    @Override
    protected void checkFormat() {
        if(StringUtils.isBlank(serverUrl)){
            throw new IllegalArgumentException("config error:[serverUrl] cannot be blank");
        }
        try{
            URI uri = new URI(serverUrl);
            if(!StringUtils.equals(uri.getScheme(), WEB_SOCKET_PREFIX)){
                throw new IllegalArgumentException("config error:[serverUrl] must start with [ws], current serverUrl is " + serverUrl);
            }
            TelnetUtil.telnet(uri.getHost(), uri.getPort());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("could not connect to [serverUrl], [serverUrl] = %s, e = %s", serverUrl, ExceptionUtil.getErrorMessage(e)));
        }
    }
}
