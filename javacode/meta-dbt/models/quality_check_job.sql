{% snapshot quality_check_job_snapshot %}

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
    job_name::{{ dbt.type_string() }} AS job_name,
    job_description::{{ dbt.type_string() }} AS job_description,
    rule_ids::{{ dbt.type_string() }} AS rule_ids,
    cron_expression::{{ dbt.type_string() }} AS cron_expression,
    is_enabled::{{ dbt.type_integer() }} AS is_enabled,
    last_execute_time::{{ dbt.type_timestamp() }} AS last_execute_time,
    next_execute_time::{{ dbt.type_timestamp() }} AS next_execute_time,
    creator::{{ dbt.type_string() }} AS creator,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'quality_check_job') }}

{% endsnapshot %}