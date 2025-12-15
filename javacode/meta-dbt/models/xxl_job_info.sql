{% snapshot xxl_job_info_snapshot %}

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
    job_desc::{{ dbt.type_string() }} AS job_desc,
    add_time::{{ dbt.type_timestamp() }} AS add_time,
    update_time::{{ dbt.type_timestamp() }} AS update_time,
    author::{{ dbt.type_string() }} AS author,
    alarm_email::{{ dbt.type_string() }} AS alarm_email,
    schedule_type::{{ dbt.type_string() }} AS schedule_type,
    schedule_conf::{{ dbt.type_string() }} AS schedule_conf,
    misfire_strategy::{{ dbt.type_string() }} AS misfire_strategy,
    executor_route_strategy::{{ dbt.type_string() }} AS executor_route_strategy,
    executor_handler::{{ dbt.type_string() }} AS executor_handler,
    executor_param::{{ dbt.type_string() }} AS executor_param,
    executor_block_strategy::{{ dbt.type_string() }} AS executor_block_strategy,
    executor_timeout::{{ dbt.type_integer() }} AS executor_timeout,
    executor_fail_retry_count::{{ dbt.type_integer() }} AS executor_fail_retry_count,
    glue_type::{{ dbt.type_string() }} AS glue_type,
    glue_source::{{ dbt.type_string() }} AS glue_source,
    glue_remark::{{ dbt.type_string() }} AS glue_remark,
    glue_updatetime::{{ dbt.type_timestamp() }} AS glue_updatetime,
    child_jobid::{{ dbt.type_string() }} AS child_jobid,
    trigger_status::{{ dbt.type_integer() }} AS trigger_status,
    trigger_last_time::{{ dbt.type_integer() }} AS trigger_last_time,
    trigger_next_time::{{ dbt.type_integer() }} AS trigger_next_time
FROM {{ source('dataplatform', 'xxl_job_info') }}

{% endsnapshot %}