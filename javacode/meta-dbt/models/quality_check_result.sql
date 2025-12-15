{% snapshot quality_check_result_snapshot %}

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
    rule_id::{{ dbt.type_integer() }} AS rule_id,
    table_name::{{ dbt.type_string() }} AS table_name,
    column_name::{{ dbt.type_string() }} AS column_name,
    check_date::{{ dbt.type_date() }} AS check_date,
    check_status::{{ dbt.type_string() }} AS check_status,
    actual_value::{{ dbt.type_decimal() }} AS actual_value,
    expected_value::{{ dbt.type_decimal() }} AS expected_value,
    deviation::{{ dbt.type_decimal() }} AS deviation,
    failed_records::{{ dbt.type_string() }} AS failed_records,
    execution_time::{{ dbt.type_integer() }} AS execution_time,
    error_message::{{ dbt.type_string() }} AS error_message,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'quality_check_result') }}

{% endsnapshot %}