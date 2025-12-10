package com.dataplatform.lineage.entity.nodes;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Node("ColumnNode")
public class ColumnNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("columnName")
    private String columnName;
    
    @Property("dataType")
    private String dataType;
    
    @Property("description")
    private String description;
    
    @Property("isPrimaryKey")
    private Boolean isPrimaryKey;
}