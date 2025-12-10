package com.dataplatform.lineage.entity.nodes;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Node("TableNode")
public class TableNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("tableName")
    private String tableName;
    
    @Property("schemaName")
    private String schemaName;
    
    @Property("fullName")
    private String fullName;
    
    @Property("tableType")
    private String tableType;
    
    @Property("description")
    private String description;
    
    @Property("businessDomain")
    private String businessDomain;
    
    @Property("dataLayer")
    private String dataLayer;
}