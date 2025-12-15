# DBT血缘采集测试SQL文件

这个目录包含了用于测试dbt血缘采集功能的SQL文件。这些文件模拟了真实的业务场景，可以帮助验证dbt在处理复杂SQL查询时的血缘关系识别能力。

## 文件列表

1. `01_create_asset_view.sql` - 创建资产总览视图，整合资产基本信息和评估信息
2. `02_create_quality_report.sql` - 创建数据质量报告视图，关联质量检查结果和规则
3. `03_create_lineage_analysis.sql` - 创建数据血缘分析视图，展示表之间的依赖关系
4. `04_create_scheduler_job_stats.sql` - 创建调度任务统计视图，分析任务执行情况
5. `05_create_business_report.sql` - 创建业务报表视图，整合多个维度的数据
6. `06_create_metadata_summary.sql` - 创建元数据汇总视图，展示各层数据资产情况
7. `07_create_lineage_path.sql` - 创建血缘路径分析存储过程，用于查找表的上下游关系
8. `08_create_asset_lifecycle_analysis.sql` - 创建资产生命周期分析视图，追踪资产从创建到废弃的全过程
9. `09_create_quality_trend.sql` - 创建质量趋势分析视图，展示数据质量随时间的变化
10. `10_create_report_dashboard.sql` - 创建报表仪表板视图，整合各类关键指标

## 使用方法

这些SQL文件可以用于以下测试场景：

1. **血缘关系识别测试** - 验证dbt能否正确识别复杂的JOIN、子查询和聚合操作
2. **视图血缘追踪** - 测试视图与其基础表之间的血缘关系
3. **存储过程分析** - 验证dbt对存储过程中涉及的表的操作识别能力
4. **跨表依赖分析** - 检查多表关联查询的血缘关系识别准确性

## 注意事项

- 这些SQL文件已根据实际的数据库表结构进行了调整
- 文件中使用的表名和字段名与 `dataplatform_full_schema.sql` 中定义的结构保持一致
- 某些视图依赖于其他视图，因此需要按顺序创建
- 运行这些SQL前请确保有足够的权限创建视图和存储过程