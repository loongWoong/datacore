{% snapshot xxl_job_log_snapshot %}

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
    job_group::{{ dbt.type_integer() }} AS job_group,
    job_id::{{ dbt.type_integer() }} AS job_id,
    executor_address::{{ dbt.type_string() }} AS executor_address,
    executor_handler::{{ dbt.type_string() }} AS executor_handler,
    executor_param::{{ dbt.type_string() }} AS executor_param,
    executor_sharding_param::{{ dbt.type_string() }} AS executor_sharding_param,
    executor_fail_retry_count::{{ dbt.type_integer() }} AS executor_fail_retry_count,
    trigger_time::{{ dbt.type_timestamp() }} AS trigger_time,
    trigger_code::{{ dbt.type_integer() }} AS trigger_code,
    trigger_msg::{{ dbt.type_string() }} AS trigger_msg,
    handle_time::{{ dbt.type_timestamp() }} AS handle_time,
    handle_code::{{ dbt.type_integer() }} AS handle_code,
    handle_msg::{{ dbt.type_string() }} AS handle_msg,
    alarm_status::{{ dbt.type_integer() }} AS alarm_status
FROM {{ source('dataplatform', 'xxl_job_log') }}

{% endsnapshot %}