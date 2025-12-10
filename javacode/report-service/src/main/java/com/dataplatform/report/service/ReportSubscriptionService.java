package com.dataplatform.report.service;

public interface ReportSubscriptionService {
    
    void subscribeReport(Long reportId, String subscriber, String subscriptionType, String subscriptionConfig);
    
    void unsubscribeReport(Long reportId, String subscriber);
    
    void notifySubscribers(Long reportId, String reportFile);
}