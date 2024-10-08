/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seatunnel.app.controller;

import org.apache.seatunnel.app.common.Result;
import org.apache.seatunnel.app.common.SeatunnelWebTestingBase;
import org.apache.seatunnel.app.domain.request.datasource.DatasourceCheckReq;
import org.apache.seatunnel.app.domain.request.datasource.DatasourceReq;
import org.apache.seatunnel.app.domain.response.PageInfo;
import org.apache.seatunnel.app.domain.response.datasource.DatasourceDetailRes;
import org.apache.seatunnel.app.domain.response.datasource.DatasourceRes;
import org.apache.seatunnel.app.utils.JSONTestUtils;
import org.apache.seatunnel.app.utils.JSONUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeatunnelDatasourceControllerWrapper extends SeatunnelWebTestingBase {

    public String createFakeSourceDatasource(String datasourceName) {
        DatasourceReq req = getFakeSourceDatasourceReq(datasourceName);
        Result<String> result = createDatasource(req);
        assertTrue(result.isSuccess());
        return result.getData();
    }

    public String createConsoleDatasource(String datasourceName) {
        DatasourceReq req = getConsoleDatasourceReq(datasourceName);
        Result<String> result = createDatasource(req);
        assertTrue(result.isSuccess());
        return result.getData();
    }

    public DatasourceReq getFakeSourceDatasourceReq(String datasourceName) {
        DatasourceReq req = new DatasourceReq();
        req.setDatasourceName(datasourceName);
        req.setPluginName("FakeSource");
        req.setDescription(datasourceName + " desc");
        req.setDatasourceConfig(
                "{\"fields\":\"{\\n      \\\"name\\\": \\\"string\\\",\\n      \\\"age\\\": \\\"int\\\"\\n    }\"}");
        return req;
    }

    private DatasourceReq getConsoleDatasourceReq(String datasourceName) {
        DatasourceReq req = new DatasourceReq();
        req.setDatasourceName(datasourceName);
        req.setPluginName("Console");
        req.setDescription(datasourceName + " description");
        req.setDatasourceConfig("{\"log.print.data\":\"true\",\"log.print.delay.ms\":\"100\"}");
        return req;
    }

    public Result<String> createDatasource(DatasourceReq req) {
        String requestBody = JSONUtils.toPrettyJsonString(req);
        String response = sendRequest(url("datasource/create"), requestBody, "POST");
        return JSONTestUtils.parseObject(response, new TypeReference<Result<String>>() {});
    }

    public Result<Boolean> testConnect(DatasourceCheckReq req) {
        String requestBody = JSONUtils.toPrettyJsonString(req);
        String response = sendRequest(url("datasource/check/connect"), requestBody, "POST");
        return JSONTestUtils.parseObject(response, new TypeReference<Result<Boolean>>() {});
    }

    public Result<Boolean> updateDatasource(String id, DatasourceReq req) {
        String requestBody = JSONUtils.toPrettyJsonString(req);
        String response = sendRequest(url("datasource/" + id), requestBody, "PUT");
        return JSONTestUtils.parseObject(response, new TypeReference<Result<Boolean>>() {});
    }

    public Result<Boolean> deleteDatasource(String id) {
        String response = sendRequest(url("datasource/" + id), null, "DELETE");
        return JSONTestUtils.parseObject(response, new TypeReference<Result<Boolean>>() {});
    }

    public Result<DatasourceDetailRes> getDatasource(String id) {
        String response = sendRequest(url("datasource/" + id));
        return JSONTestUtils.parseObject(
                response, new TypeReference<Result<DatasourceDetailRes>>() {});
    }

    public Result<PageInfo<DatasourceRes>> getDatasourceList(
            String searchVal, String pluginName, Integer pageNo, Integer pageSize) {
        String response =
                sendRequest(
                        String.format(
                                "%s/datasource/list?searchVal=%s&pluginName=%s&pageNo=%d&pageSize=%d",
                                baseUrl, searchVal, pluginName, pageNo, pageSize));
        return JSONTestUtils.parseObject(
                response, new TypeReference<Result<PageInfo<DatasourceRes>>>() {});
    }
}
