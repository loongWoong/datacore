{% snapshot lineage_config_snapshot %}

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
    config_key::{{ dbt.type_string() }} AS config_key,
    config_value::{{ dbt.type_string() }} AS config_value,
    config_type::{{ dbt.type_string() }} AS config_type,
    description::{{ dbt.type_string() }} AS description,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'lineage_config') }}

{% endsnapshot %}