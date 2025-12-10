package com.dataplatform.lineage.entity.relationships;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@Data
@RelationshipProperties
public class ExecutedByRelation {
    
    @Id
    @GeneratedValue
    private Long id;
}