{% snapshot asset_tag_snapshot %}

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
    tag_code::{{ dbt.type_string() }} AS tag_code,
    tag_name::{{ dbt.type_string() }} AS tag_name,
    tag_description::{{ dbt.type_string() }} AS tag_description,
    tag_category::{{ dbt.type_string() }} AS tag_category,
    tag_color::{{ dbt.type_string() }} AS tag_color,
    creator::{{ dbt.type_string() }} AS creator,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'asset_tag') }}

{% endsnapshot %}