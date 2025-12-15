{% snapshot metadata_version_info_snapshot %}

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
    version_number::{{ dbt.type_string() }} AS version_number,
    version_description::{{ dbt.type_string() }} AS version_description,
    metadata_content::{{ dbt.type_string() }} AS metadata_content,
    created_by::{{ dbt.type_string() }} AS created_by,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'metadata_version_info') }}

{% endsnapshot %}