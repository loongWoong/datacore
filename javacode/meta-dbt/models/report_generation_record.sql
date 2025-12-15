{% snapshot report_generation_record_snapshot %}

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
    report_id::{{ dbt.type_integer() }} AS report_id,
    report_code::{{ dbt.type_string() }} AS report_code,
    report_name::{{ dbt.type_string() }} AS report_name,
    generation_status::{{ dbt.type_string() }} AS generation_status,
    start_time::{{ dbt.type_timestamp() }} AS start_time,
    end_time::{{ dbt.type_timestamp() }} AS end_time,
    execution_time::{{ dbt.type_integer() }} AS execution_time,
    file_path::{{ dbt.type_string() }} AS file_path,
    file_size::{{ dbt.type_integer() }} AS file_size,
    error_message::{{ dbt.type_string() }} AS error_message,
    generated_by::{{ dbt.type_string() }} AS generated_by,
    generated_params::{{ dbt.type_string() }} AS generated_params,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'report_generation_record') }}

{% endsnapshot %}