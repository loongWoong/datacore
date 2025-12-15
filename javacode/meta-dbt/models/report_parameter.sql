{% snapshot report_parameter_snapshot %}

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
    param_name::{{ dbt.type_string() }} AS param_name,
    param_label::{{ dbt.type_string() }} AS param_label,
    param_type::{{ dbt.type_string() }} AS param_type,
    is_required::{{ dbt.type_integer() }} AS is_required,
    default_value::{{ dbt.type_string() }} AS default_value,
    param_options::{{ dbt.type_string() }} AS param_options,
    param_order::{{ dbt.type_integer() }} AS param_order,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'report_parameter') }}

{% endsnapshot %}