package com.dataplatform.lineage.entity.nodes;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Node("JobNode")
public class JobNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("jobName")
    private String jobName;
    
    @Property("jobType")
    private String jobType;
    
    @Property("description")
    private String description;
    
    @Property("schedule")
    private String schedule;
}