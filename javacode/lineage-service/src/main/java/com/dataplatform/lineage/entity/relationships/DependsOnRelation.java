package com.dataplatform.lineage.entity.relationships;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class DependsOnRelation {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String transformType;
    
    private String description;
}