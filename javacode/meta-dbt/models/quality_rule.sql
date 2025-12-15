{% snapshot quality_rule_snapshot %}

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
    rule_code::{{ dbt.type_string() }} AS rule_code,
    rule_name::{{ dbt.type_string() }} AS rule_name,
    rule_description::{{ dbt.type_string() }} AS rule_description,
    rule_type::{{ dbt.type_string() }} AS rule_type,
    table_name::{{ dbt.type_string() }} AS table_name,
    column_name::{{ dbt.type_string() }} AS column_name,
    check_expression::{{ dbt.type_string() }} AS check_expression,
    expected_value::{{ dbt.type_string() }} AS expected_value,
    threshold::{{ dbt.type_decimal() }} AS threshold,
    severity_level::{{ dbt.type_string() }} AS severity_level,
    is_enabled::{{ dbt.type_integer() }} AS is_enabled,
    creator::{{ dbt.type_string() }} AS creator,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'quality_rule') }}

{% endsnapshot %}