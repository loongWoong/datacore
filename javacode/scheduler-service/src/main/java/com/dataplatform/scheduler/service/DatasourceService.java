package com.dataplatform.scheduler.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DatasourceService {
    
    private final RestTemplate restTemplate;
    
    public DatasourceService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 根据数据源ID获取数据源详细信息
     * 
     * @param datasourceId 数据源ID
     * @return 数据源信息Map
     */
    public Map<String, Object> getDatasourceById(Long datasourceId) {
        try {
            String url = "http://localhost:8086/api/datasources/" + datasourceId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            System.err.println("获取数据源信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}