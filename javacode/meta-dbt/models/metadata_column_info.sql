{% snapshot metadata_column_info_snapshot %}

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
    table_id::{{ dbt.type_integer() }} AS table_id,
    column_name::{{ dbt.type_string() }} AS column_name,
    column_name_cn::{{ dbt.type_string() }} AS column_name_cn,
    column_description::{{ dbt.type_string() }} AS column_description,
    data_type::{{ dbt.type_string() }} AS data_type,
    is_primary_key::{{ dbt.type_integer() }} AS is_primary_key,
    is_nullable::{{ dbt.type_integer() }} AS is_nullable,
    column_order::{{ dbt.type_integer() }} AS column_order,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'metadata_column_info') }}

{% endsnapshot %}