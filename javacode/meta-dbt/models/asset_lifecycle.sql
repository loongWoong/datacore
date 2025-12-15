{% snapshot asset_lifecycle_snapshot %}

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
    lifecycle_event::{{ dbt.type_string() }} AS lifecycle_event,
    event_description::{{ dbt.type_string() }} AS event_description,
    operator::{{ dbt.type_string() }} AS operator,
    operated_time::{{ dbt.type_timestamp() }} AS operated_time
FROM {{ source('dataplatform', 'asset_lifecycle') }}

{% endsnapshot %}