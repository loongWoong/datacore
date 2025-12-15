{% snapshot asset_info_snapshot %}

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
    asset_code::{{ dbt.type_string() }} AS asset_code,
    asset_name::{{ dbt.type_string() }} AS asset_name,
    asset_name_cn::{{ dbt.type_string() }} AS asset_name_cn,
    asset_description::{{ dbt.type_string() }} AS asset_description,
    asset_type::{{ dbt.type_string() }} AS asset_type,
    business_domain::{{ dbt.type_string() }} AS business_domain,
    data_source::{{ dbt.type_string() }} AS data_source,
    owner::{{ dbt.type_string() }} AS owner,
    department::{{ dbt.type_string() }} AS department,
    schema_name::{{ dbt.type_string() }} AS schema_name,
    table_name::{{ dbt.type_string() }} AS table_name,
    column_name::{{ dbt.type_string() }} AS column_name,
    sensitivity_level::{{ dbt.type_string() }} AS sensitivity_level,
    asset_status::{{ dbt.type_string() }} AS asset_status,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'asset_info') }}

{% endsnapshot %}