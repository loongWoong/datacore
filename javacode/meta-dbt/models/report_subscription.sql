{% snapshot report_subscription_snapshot %}

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
    subscriber::{{ dbt.type_string() }} AS subscriber,
    subscription_type::{{ dbt.type_string() }} AS subscription_type,
    subscription_config::{{ dbt.type_string() }} AS subscription_config,
    is_active::{{ dbt.type_integer() }} AS is_active,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'report_subscription') }}

{% endsnapshot %}