{% snapshot report_config_snapshot %}

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
    report_code::{{ dbt.type_string() }} AS report_code,
    report_name::{{ dbt.type_string() }} AS report_name,
    report_description::{{ dbt.type_string() }} AS report_description,
    report_type::{{ dbt.type_string() }} AS report_type,
    data_source_type::{{ dbt.type_string() }} AS data_source_type,
    data_source_config::{{ dbt.type_string() }} AS data_source_config,
    template_file_path::{{ dbt.type_string() }} AS template_file_path,
    output_format::{{ dbt.type_string() }} AS output_format,
    schedule_config::{{ dbt.type_string() }} AS schedule_config,
    is_enabled::{{ dbt.type_integer() }} AS is_enabled,
    creator::{{ dbt.type_string() }} AS creator,
    created_time::{{ dbt.type_timestamp() }} AS created_time,
    updated_time::{{ dbt.type_timestamp() }} AS updated_time
FROM {{ source('dataplatform', 'report_config') }}

{% endsnapshot %}