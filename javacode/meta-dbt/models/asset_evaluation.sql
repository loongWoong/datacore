{% snapshot asset_evaluation_snapshot %}

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
    asset_id::{{ dbt.type_integer() }} AS asset_id,
    evaluation_date::{{ dbt.type_date() }} AS evaluation_date,
    asset_level::{{ dbt.type_string() }} AS asset_level,
    usage_frequency::{{ dbt.type_string() }} AS usage_frequency,
    data_freshness::{{ dbt.type_string() }} AS data_freshness,
    business_value::{{ dbt.type_decimal() }} AS business_value,
    technical_quality::{{ dbt.type_decimal() }} AS technical_quality,
    security_level::{{ dbt.type_decimal() }} AS security_level,
    overall_score::{{ dbt.type_decimal() }} AS overall_score,
    evaluator::{{ dbt.type_string() }} AS evaluator,
    evaluation_comment::{{ dbt.type_string() }} AS evaluation_comment,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'asset_evaluation') }}

{% endsnapshot %}