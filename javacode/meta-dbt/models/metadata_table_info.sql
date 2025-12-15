{% snapshot metadata_table_info_snapshot %}

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
    schema_name::{{ dbt.type_string() }} AS schema_name,
    table_name::{{ dbt.type_string() }} AS table_name,
    table_name_cn::{{ dbt.type_string() }} AS table_name_cn,
    table_description::{{ dbt.type_string() }} AS table_description,
    business_domain::{{ dbt.type_string() }} AS business_domain,
    data_source::{{ dbt.type_string() }} AS data_source,
    update_frequency::{{ dbt.type_string() }} AS update_frequency,
    owner::{{ dbt.type_string() }} AS owner,
    data_layer::{{ dbt.type_string() }} AS data_layer,
    record_count::{{ dbt.type_integer() }} AS record_count,
    last_update_time::{{ dbt.type_timestamp() }} AS last_update_time,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'metadata_table_info') }}

{% endsnapshot %}