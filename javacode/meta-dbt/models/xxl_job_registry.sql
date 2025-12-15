{% snapshot xxl_job_registry_snapshot %}

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
    registry_group::{{ dbt.type_string() }} AS registry_group,
    registry_key::{{ dbt.type_string() }} AS registry_key,
    registry_value::{{ dbt.type_string() }} AS registry_value,
    update_time::{{ dbt.type_timestamp() }} AS update_time
FROM {{ source('dataplatform', 'xxl_job_registry') }}

{% endsnapshot %}