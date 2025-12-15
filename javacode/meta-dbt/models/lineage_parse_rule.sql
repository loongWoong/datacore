{% snapshot lineage_parse_rule_snapshot %}

{{
    config(
      target_database='dataplatform',
      target_schema='snapshots',
      unique_key='id',
      strategy='timestamp',
      updated_at='updated_at',
    )
}}

SELECT
    id::{{ dbt.type_integer() }} AS id,
    rule_name::{{ dbt.type_string() }} AS rule_name,
    rule_type::{{ dbt.type_string() }} AS rule_type,
    source_pattern::{{ dbt.type_string() }} AS source_pattern,
    target_pattern::{{ dbt.type_string() }} AS target_pattern,
    transform_type::{{ dbt.type_string() }} AS transform_type,
    description::{{ dbt.type_string() }} AS description,
    is_enabled::{{ dbt.type_integer() }} AS is_enabled,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'lineage_parse_rule') }}

{% endsnapshot %}