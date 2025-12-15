{% snapshot xxl_job_log_report_snapshot %}

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
    trigger_day::{{ dbt.type_timestamp() }} AS trigger_day,
    running_count::{{ dbt.type_integer() }} AS running_count,
    suc_count::{{ dbt.type_integer() }} AS suc_count,
    fail_count::{{ dbt.type_integer() }} AS fail_count,
    update_time::{{ dbt.type_timestamp() }} AS update_time
FROM {{ source('dataplatform', 'xxl_job_log_report') }}

{% endsnapshot %}