{% snapshot quality_report_snapshot %}

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
    report_date::{{ dbt.type_date() }} AS report_date,
    table_name::{{ dbt.type_string() }} AS table_name,
    overall_score::{{ dbt.type_decimal() }} AS overall_score,
    completeness_rate::{{ dbt.type_decimal() }} AS completeness_rate,
    accuracy_rate::{{ dbt.type_decimal() }} AS accuracy_rate,
    consistency_rate::{{ dbt.type_decimal() }} AS consistency_rate,
    uniqueness_rate::{{ dbt.type_decimal() }} AS uniqueness_rate,
    timeliness_rate::{{ dbt.type_decimal() }} AS timeliness_rate,
    total_records::{{ dbt.type_integer() }} AS total_records,
    failed_rules::{{ dbt.type_integer() }} AS failed_rules,
    warning_rules::{{ dbt.type_integer() }} AS warning_rules,
    passed_rules::{{ dbt.type_integer() }} AS passed_rules,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'quality_report') }}

{% endsnapshot %}