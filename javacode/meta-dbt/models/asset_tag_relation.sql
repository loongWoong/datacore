{% snapshot asset_tag_relation_snapshot %}

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
    tag_id::{{ dbt.type_integer() }} AS tag_id,
    created_time::{{ dbt.type_timestamp() }} AS created_time
FROM {{ source('dataplatform', 'asset_tag_relation') }}

{% endsnapshot %}