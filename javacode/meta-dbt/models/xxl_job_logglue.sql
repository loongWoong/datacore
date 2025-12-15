{% snapshot xxl_job_logglue_snapshot %}

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
    job_id::{{ dbt.type_integer() }} AS job_id,
    glue_type::{{ dbt.type_string() }} AS glue_type,
    glue_source::{{ dbt.type_string() }} AS glue_source,
    glue_remark::{{ dbt.type_string() }} AS glue_remark,
    add_time::{{ dbt.type_timestamp() }} AS add_time,
    update_time::{{ dbt.type_timestamp() }} AS update_time
FROM {{ source('dataplatform', 'xxl_job_logglue') }}

{% endsnapshot %}